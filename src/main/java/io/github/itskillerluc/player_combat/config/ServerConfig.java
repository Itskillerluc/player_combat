package io.github.itskillerluc.player_combat.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> MONSTER_DAMAGE_TO_POINT;

    static {
        BUILDER.push("Configs for Player Combat");

        MONSTER_DAMAGE_TO_POINT = BUILDER.comment("How much damage to a monster equals 1 point.")
                .define("Monster Damage To Points", 50);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
