package io.github.itskillerluc.player_combat;

import io.github.itskillerluc.player_combat.stats.StatRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(PlayerCombat.MODID)
public class PlayerCombat
{
    public static final String MODID = "player_combat";

    public PlayerCombat()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        StatRegistry.STATS.register(modEventBus);
        StatRegistry.STAT_TYPE.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
