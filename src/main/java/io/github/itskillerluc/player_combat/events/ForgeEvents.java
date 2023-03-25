package io.github.itskillerluc.player_combat.events;

import io.github.itskillerluc.player_combat.PlayerCombat;
import io.github.itskillerluc.player_combat.capabilities.AttachBountyCapability;
import io.github.itskillerluc.player_combat.capabilities.AttachDamageTrackCapability;
import io.github.itskillerluc.player_combat.commands.PlayerCombatCommand;
import io.github.itskillerluc.player_combat.config.ServerConfig;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import io.github.itskillerluc.player_combat.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE, modid = PlayerCombat.MODID)
public class ForgeEvents {
    @SubscribeEvent
    static void registerCommands(RegisterCommandsEvent event){
        PlayerCombatCommand.register(event.getDispatcher(), "pc");
        PlayerCombatCommand.register(event.getDispatcher(), "playercombat");
    }

    @SubscribeEvent
    static void playerJoin(final PlayerEvent.ItemCraftedEvent event){
        event.getEntity().awardStat(StatRegistry.POINTS.get());
    }

    @SubscribeEvent
    static void AttachEntityCaps(final AttachCapabilitiesEvent<Entity> event){
        AttachDamageTrackCapability.attach(event);
    }
    @SubscribeEvent
    static void AttachLevelCaps(final AttachCapabilitiesEvent<Level> event) {
        AttachBountyCapability.attach(event);
    }

    @SubscribeEvent
    static void damageEvent(final LivingDamageEvent event){
        Entity attacker = event.getSource().getEntity();
        if (event.getEntity().getLevel().isClientSide() || !(attacker instanceof Player) || !(event.getEntity() instanceof Enemy || event.getEntity() instanceof Player)){
            return;
        }
        event.getEntity().getCapability(AttachDamageTrackCapability.INSTANCE).ifPresent(cap -> {if (!event.getEntity().getUUID().equals(attacker.getUUID())) cap.addDamage(attacker.getUUID(), Math.min(event.getAmount(), event.getEntity().getHealth()));});
    }

    @SubscribeEvent
    static void deathEvent(final LivingDeathEvent event) {
        if (!event.getEntity().getLevel().isClientSide() && event.getEntity() instanceof Enemy){
            event.getEntity().getCapability(AttachDamageTrackCapability.INSTANCE).resolve().ifPresentOrElse(cap -> {
                for (Map.Entry<UUID, Float> uuidFloatEntry : cap.getDamageMap().entrySet()) {
                    Stat<ResourceLocation> mobStat = Stats.CUSTOM.get(StatRegistry.MOB_POINTS.get());
                    Utils.addStat(uuidFloatEntry.getKey(), event.getEntity().getLevel(), mobStat, uuidFloatEntry.getValue().intValue());
                    int value = Utils.getStat(uuidFloatEntry.getKey(), event.getEntity().getLevel(), mobStat);
                    Utils.setStat(uuidFloatEntry.getKey(), event.getEntity().getLevel(), mobStat, value % ServerConfig.MONSTER_DAMAGE_TO_POINT.get());
                    Utils.addStat(uuidFloatEntry.getKey(), event.getEntity().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), value / ServerConfig.MONSTER_DAMAGE_TO_POINT.get());
                }
            }, () -> LogManager.getLogger().error("Couldn't find DamageTrackCapability"));
        } else if (event.getEntity() instanceof Player player) {
            Stat<ResourceLocation> stat = Stats.CUSTOM.get(StatRegistry.POINTS.get());
            Utils.addStat(player.getUUID(), event.getEntity().getLevel(), stat, -ServerConfig.DEATH_POINT_PENALTY.get());

            event.getEntity().getCapability(AttachDamageTrackCapability.INSTANCE).resolve().ifPresentOrElse(cap -> {
                for (Map.Entry<UUID, Float> uuidFloatEntry : cap.getDamageMap().entrySet()) {
                    Utils.addStat(uuidFloatEntry.getKey(), event.getEntity().getLevel(), stat, uuidFloatEntry.getValue().intValue());
                }

                var lb = new ArrayList<>(cap.getDamageMap().keySet()).indexOf(event.getEntity().getUUID());
                Utils.addStat(player.getUUID(), event.getEntity().getLevel(), stat, -(ServerConfig.PODIUM_DEATH_MODIFIER.get() * (lb == 0 ? 3 : lb == 1 ? 2 : lb == 3 ? 1 : 0)));

                event.getEntity().getLevel().getCapability(AttachBountyCapability.INSTANCE).resolve().ifPresentOrElse(bounty -> {
                    if (bounty.isBounty(event.getEntity().getUUID())) {
                        cap.getDamageMap().entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue)).ifPresent(max ->
                                Utils.setStat(max.getKey(), event.getEntity().getLevel(), stat, bounty.getBounty(event.getEntity().getUUID())));
                    }
                }, () -> LogManager.getLogger().error("Couldn't find BountyCapability"));
            }, () -> LogManager.getLogger().error("Couldn't find DamageTrackCapability"));
        }

    }

    @SubscribeEvent
    static void healEvent(final LivingHealEvent event){
        if (event.getEntity().getLevel().isClientSide()){
            return;
        }
        event.getEntity().getCapability(AttachDamageTrackCapability.INSTANCE).ifPresent(cap -> cap.removeDamageEldest(event.getAmount()));
    }

    @SubscribeEvent
    static void loginEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().getLevel().isClientSide()) {
            PlayerCombatCommand.fetchRewards(((ServerLevel) event.getEntity().getLevel()), ((ServerPlayer) event.getEntity()));
            Utils.sync(event.getEntity().level);
        }
    }
}
