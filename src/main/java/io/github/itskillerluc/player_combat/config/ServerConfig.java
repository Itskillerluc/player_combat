package io.github.itskillerluc.player_combat.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> MONSTER_DAMAGE_TO_POINT;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEATH_POINT_PENALTY;
    public static final ForgeConfigSpec.ConfigValue<Integer> DEFAULT_POINTS;
    public static final ForgeConfigSpec.ConfigValue<Integer> PODIUM_DEATH_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<String> REWARD_ITEM;

    static {
        BUILDER.push("Configs for Player Combat");

        MONSTER_DAMAGE_TO_POINT = BUILDER.comment("How much damage to a monster equals 1 point.")
                .define("Monster Damage To Points", 200);

        DEATH_POINT_PENALTY = BUILDER.comment("How much points you will lose upon death.")
                        .define("Death Penalty", 10);

        DEFAULT_POINTS = BUILDER.comment("The default amount of points a new player gets, and reset resets it to.")
                        .define("Default Points", 0);

        REWARD_ITEM = BUILDER.comment("The item that gets used for the reward payment.")
                        .define("Reward Item", "minecraft:gold_nugget");

        PODIUM_DEATH_MODIFIER = BUILDER.comment("The amount of points the first second and third player lose extra ontop of the death point penalty.")
                        .define("Leader Death Modifier", 5);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
