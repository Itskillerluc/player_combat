package io.github.itskillerluc.player_combat.events;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.capabilities.DamageTrackCapability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD, modid = PlayerCombat.MODID)
public class ModEvents {
    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(DamageTrackCapability.class);
    }
}
