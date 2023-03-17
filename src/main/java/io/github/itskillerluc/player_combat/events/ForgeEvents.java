package io.github.itskillerluc.player_combat.events;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.capabilities.AttachDamageTrackCapability;
import io.github.itskillerluc.player_combat.capabilities.DamageTrackCapability;
import io.github.itskillerluc.player_combat.commands.PlayerCombatCommand;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE, modid = PlayerCombat.MODID)
public class ForgeEvents {
    @SubscribeEvent
    static void registerCommands(RegisterCommandsEvent event){
        PlayerCombatCommand.register(event.getDispatcher(), event.getBuildContext(), "pc");
        PlayerCombatCommand.register(event.getDispatcher(), event.getBuildContext(), "playercombat");
    }

    @SubscribeEvent
    static void playerJoin(final PlayerEvent.ItemCraftedEvent event){
        event.getEntity().awardStat(StatRegistry.POINTS.get());
        event.getEntity().awardStat(StatRegistry.TEST.get().get(BlockEntityType.FURNACE));
    }

    @SubscribeEvent
    static void AttachEntityCaps(final AttachCapabilitiesEvent<Entity> event){
        AttachDamageTrackCapability.attach(event);
    }

    @SubscribeEvent
    static void damageEvent(final LivingDamageEvent event){
        Entity attacker = event.getSource().getEntity();
        if (event.getEntity().getLevel().isClientSide() || !(attacker instanceof Player) || !(event.getEntity() instanceof Enemy || event.getEntity() instanceof Player)){
            return;
        }
        attacker.getCapability(AttachDamageTrackCapability.INSTANCE).ifPresent(cap -> {
            cap.addDamage(attacker.getUUID(), event.getAmount());
        });
    }

    @SubscribeEvent
    static void deathEvent(final LivingDeathEvent event) {
        if (event.getEntity() instanceof Player || !(event.getEntity() instanceof Enemy)){
            return;
        }
        event.getEntity().getCapability(AttachDamageTrackCapability.INSTANCE).resolve().ifPresentOrElse(cap -> {

        }, () -> LogManager.getLogger().error("Couldn't find DamageTrackCapability"));
    }

    @SubscribeEvent
    static void healEvent(final LivingHealEvent event){

    }
}
