package io.github.itskillerluc.player_combat.client.events;

import io.github.itskillerluc.player_combat.PlayerCombat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE, modid = PlayerCombat.MODID, value = Dist.CLIENT)
public class ForgeEvents {
    @SubscribeEvent
    static void chatEvent(final ServerChatEvent event) {

    }
}
