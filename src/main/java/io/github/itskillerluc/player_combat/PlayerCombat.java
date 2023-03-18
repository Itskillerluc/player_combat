package io.github.itskillerluc.player_combat;

import io.github.itskillerluc.player_combat.config.ServerConfig;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;


@Mod(PlayerCombat.MODID)
public class PlayerCombat
{
    public static final String MODID = "player_combat";

    public PlayerCombat()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        StatRegistry.STATS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC, "player_combat-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }
}
