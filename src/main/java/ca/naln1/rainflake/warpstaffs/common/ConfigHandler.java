package ca.naln1.rainflake.warpstaffs.common;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec COMMON;

    public static final ForgeConfigSpec.BooleanValue shouldDamagePlayer;
    //public static final ForgeConfigSpec.IntValue maxStarDist;

    static {
        String desc;
        BUILDER.comment("Common config settings").push("common");

        //desc = "star staff max distance";
        //maxStarDist = BUILDER.comment(desc).defineInRange("maxStarDist", 256, 16, 1024)

        desc = "should staffs hurt player on use";
        shouldDamagePlayer = BUILDER.comment(desc).define("shouldDamagePlayer", false);


        BUILDER.pop();
        COMMON = BUILDER.build();
    }
}
