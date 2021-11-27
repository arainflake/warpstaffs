package ca.naln1.rainflake.warpstaffs;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftGame;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.plaf.basic.BasicComboBoxUI;

import ca.naln1.rainflake.warpstaffs.common.ConfigHandler;
import ca.naln1.rainflake.warpstaffs.common.StaffTypes;
import ca.naln1.rainflake.warpstaffs.common.Utilities;
import ca.naln1.rainflake.warpstaffs.common.item.FurnoStaffItem;
import ca.naln1.rainflake.warpstaffs.common.item.FurnoStaffItemNew;
import ca.naln1.rainflake.warpstaffs.common.item.WarpStaffItem;
import ca.naln1.rainflake.warpstaffs.gui.FurnaceStaffContainer;
import ca.naln1.rainflake.warpstaffs.gui.FurnaceStaffScreen;
import ca.naln1.rainflake.warpstaffs.gui.WarpStaffsContainerTypes;
import ca.naln1.rainflake.warpstaffs.network.FuelerPacket;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WarpStaffs.MODID)
public class WarpStaffs {

    public static final String MODID = "warpstaffs";
    public static final Logger LOGGER = LogManager.getLogger();
    public static SimpleChannel network;
    private final NonNullList<KeyBinding> keyBinds = NonNullList.create();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    //make custom item group, for creative tab
    public static ItemGroup warp_group = new ItemGroup(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.DIAMOND);
        }
    };

    public WarpStaffs() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //register items
        ITEMS.register("item_wood_staff", () -> new WarpStaffItem(StaffTypes.WOOD_STAFF));
        ITEMS.register("item_stone_staff", () -> new WarpStaffItem(StaffTypes.STONE_STAFF));
        ITEMS.register("item_iron_staff", () -> new WarpStaffItem(StaffTypes.IRON_STAFF));
        ITEMS.register("item_diamond_staff", () -> new WarpStaffItem(StaffTypes.DIAMOND_STAFF));
        ITEMS.register("item_star_staff", () -> new WarpStaffItem(StaffTypes.STAR_STAFF));
        ITEMS.register("item_furno_staff", () -> new FurnoStaffItemNew(StaffTypes.FURNO_STAFF));
        //ITEMS.register("item_furno_staff", () -> new FurnoStaffItem(StaffTypes.FURNO_STAFF));

        ITEMS.register(modEventBus);

        WarpStaffsContainerTypes.CONTAINERS.register(modEventBus);

        //registers our config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON);

        // Register the setup method for modloading
        modEventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        modEventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        modEventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        modEventBus.addListener(this::doClientStuff);


        // Register method for listening for keybind clicks
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);

        //MinecraftForge.EVENT_BUS.addListener(Utilities::warpDestination);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        //can register capabilities here
        CapabilityManager.INSTANCE.register(IItemHandler.class, FurnaceStaffContainer.ITEM_HANDLER_CAPABILITY, () -> new ItemStackHandler(1));
        //AttachCapabilitiesEvent<ItemStack> capabilitiesEvent = new AttachCapabilitiesEvent<ItemStack>(WarpStaffItem.class,obj);
        //requires client and server to both have the mod
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> "1", s -> true, s -> true);
        network.registerMessage(0, FuelerPacket.class, FuelerPacket::toBytes, FuelerPacket::new, FuelerPacket::handle);
    }

    private void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.keyBinds.get(0).consumeClick()){
            PlayerEntity player = Minecraft.getInstance().player;

            /*
            CompoundNBT nbt = player.serializeNBT();
            LOGGER.info("player old nbt: " + nbt.toString());
            //nbt.putBoolean("distTraveled", true);
            nbt.putInt("foodLevel", 10);
            player.deserializeNBT(nbt);

            LOGGER.info("player new nbt: " + player.serializeNBT().toString());
            LOGGER.info(player.serializeNBT().getAllKeys().toString());
            */

            player.sendMessage(ITextComponent.nullToEmpty("keybind clicked"), UUID.randomUUID());
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        //register keybinds and screens here

        ScreenManager.register(WarpStaffsContainerTypes.FURNO_STAFF.get(), FurnaceStaffScreen::new);
        this.keyBinds.add(0, new KeyBinding("Show Travel Dist", -1, MODID));
        ClientRegistry.registerKeyBinding(this.keyBinds.get(0));

        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("warpstaffs", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // register a new item here


            LOGGER.info("HELLO from Register Item");
        }
    }
}
