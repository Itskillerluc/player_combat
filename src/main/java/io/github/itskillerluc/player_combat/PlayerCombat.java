package io.github.itskillerluc.player_combat;

import io.github.itskillerluc.player_combat.blocks.RegisterBlocks;
import io.github.itskillerluc.player_combat.config.ServerConfig;
import io.github.itskillerluc.player_combat.items.RegisterItems;
import io.github.itskillerluc.player_combat.networking.RegisterPackets;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(PlayerCombat.MODID)
public class PlayerCombat
{
    public static final String MODID = "player_combat";

    public PlayerCombat()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        StatRegistry.STATS.register(modEventBus);
        RegisterBlocks.BLOCKS.register(modEventBus);
        RegisterBlocks.BLOCK_ENTITIES.register(modEventBus);
        RegisterItems.ITEMS.register(modEventBus);

        modEventBus.addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC, "player_combat-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        RegisterPackets.register();
    }
}
