package io.github.itskillerluc.player_combat.events;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.commands.PlayerCombatCommand;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE, modid = PlayerCombat.MODID)
public class ForgeEvents {
    @SubscribeEvent
    static void registerCommands(RegisterCommandsEvent event){
        PlayerCombatCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    static void playerJoin(final PlayerEvent.ItemCraftedEvent event){
        event.getEntity().awardStat(StatRegistry.POINTS.get());
        event.getEntity().awardStat(StatRegistry.TEST.get().get(BlockEntityType.FURNACE));
    }

}
