package ca.naln1.rainflake.warpstaffs.gui;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import ca.naln1.rainflake.warpstaffs.WarpStaffs;

public class WarpStaffsContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, WarpStaffs.MODID);

    /*
    public static final RegistryObject<ContainerType<FurnaceStaffContainer>> FURNO_STAFF =
            CONTAINERS.register("furno_staff", () -> FurnaceStaffContainer::create);
    */
    public static final RegistryObject<ContainerType<FurnaceStaffContainer>> FURNO_STAFF =
            CONTAINERS.register("furno_staff", () -> IForgeContainerType.create(((windowId, inv, data) -> {
                return new FurnaceStaffContainer(windowId, inv.player);
            })));
}
