package ca.naln1.rainflake.warpstaffs.common;

import net.minecraft.item.Rarity;

public enum StaffTypes {
    /*
    WOOD_STAFF(16, 5, 100, Rarity.COMMON),
    STONE_STAFF(16, 3, 50, Rarity.COMMON),
    IRON_STAFF(32, 3, 10, Rarity.UNCOMMON),
    DIAMOND_STAFF(64, 1, 1, Rarity.RARE),
    STAR_STAFF(256, 1, 0, Rarity.EPIC),
    FURNO_STAFF(32, 5, 0, Rarity.RARE),
    ;
     */
    WOOD_STAFF(16, 5, 100, 3, Rarity.COMMON),
    STONE_STAFF(16, 3, 50, 3, Rarity.COMMON),
    IRON_STAFF(32, 3, 10, 2, Rarity.UNCOMMON),
    DIAMOND_STAFF(64, 1, 1, 1, Rarity.RARE),
    STAR_STAFF(256, 1, 0, 0, Rarity.EPIC),
    FURNO_STAFF(32, 5, 0, 2, Rarity.UNCOMMON),
    ;

    int distance;
    double cool_down;
    int use_dmg;
    int player_dmg;
    Rarity rarity;

    StaffTypes(int distance, double cool_down, int use_dmg, int player_dmg, Rarity rarity) {
        this.distance = distance;
        this.cool_down = cool_down;
        this.use_dmg = use_dmg;
        this.player_dmg = player_dmg;
        this.rarity = rarity;
    }

    public int getDistance() {
        return distance;
    }

    public double getCool_down() {
        return cool_down;
    }

    public int getUse_dmg() {
        return use_dmg;
    }

    public int getPlayer_dmg() {
        return player_dmg;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
