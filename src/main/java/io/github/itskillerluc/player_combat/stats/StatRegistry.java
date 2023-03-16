package io.github.itskillerluc.player_combat.stats;

import io.github.itskillerluc.player_combat.PlayerCombat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StatRegistry {
    public static final DeferredRegister<ResourceLocation> STATS = DeferredRegister.create(Registry.CUSTOM_STAT_REGISTRY, PlayerCombat.MODID);
    public static final DeferredRegister<StatType<?>> STAT_TYPE = DeferredRegister.create(ForgeRegistries.STAT_TYPES, PlayerCombat.MODID);

    public static final RegistryObject<StatType<BlockEntityType<?>>> TEST = STAT_TYPE.register("test", () -> new StatType<>(Registry.BLOCK_ENTITY_TYPE));
    public static final RegistryObject<ResourceLocation> POINTS = STATS.register("points", () -> new ResourceLocation(PlayerCombat.MODID, "points"));
}
