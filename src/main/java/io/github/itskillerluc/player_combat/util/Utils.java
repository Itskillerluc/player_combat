package io.github.itskillerluc.player_combat.util;

import io.github.itskillerluc.player_combat.capabilities.AttachBountyCapability;
import io.github.itskillerluc.player_combat.networking.RegisterPackets;
import io.github.itskillerluc.player_combat.networking.SyncBountyPacket;
import io.github.itskillerluc.player_combat.networking.SyncPointsPacket;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class Utils {
    public static Map<UUID, Integer> SYNCHED_POINTS = new HashMap<>();
    public static Map<UUID, Integer> SYNCHED_BOUNTY = new HashMap<>();

    @Nullable
    private static ServerStatsCounter getStats(UUID uuid, Level level){
        if (level.getServer() == null){
            return null;
        }
        return level.getServer().getPlayerList().stats.get(uuid);
    }

    @Nullable
    private static ServerStatsCounter getStats(String username, Level level){
        if (level.getServer() == null){
            return null;
        }
        AtomicReference<ServerStatsCounter> toReturn = new AtomicReference<>();
        level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                toReturn.set(getStats(profile.getId(), level)), () -> LogManager.getLogger().error("Couldn't find user with username: " + username));
        return toReturn.get();
    }

    @Nullable
    public static int getStatOffline(UUID uuid, Level level, Stat<?> stat){
        if (level.getServer() == null){
            return 0;
        }
        var stats = getStats(uuid, level);
        return stats == null ? 0 : stats.getValue(stat);
    }

    @Nullable
    public static int getStatOffline(String username, Level level, Stat<?> stat){
        if (level.getServer() == null){
            return 0;
        }
        var stats = getStats(username, level);
        return stats == null ? 0 : stats.getValue(stat);
    }

    private static int setStatOffline(UUID uuid, Level level, Stat<?> stat, int value){
        if (level.getServer() != null){
            getStats(uuid, level).stats.put(stat, value);
            sync(level);
            return 1;
        }
        return 0;
    }

    private static int setStatOffline(String username, Level level, Stat<?> stat, int value){
        if (level.getServer() != null) {
            AtomicInteger success = new AtomicInteger();
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                    success.set(setStatOffline(profile.getId(), level, stat, value)), () -> {success.set(0); LogManager.getLogger().error("Couldn't find user with username: " + username);});
            sync(level);
            return success.get();
        }
        return 0;
    }

    public static int addStat(UUID uuid, Level level, Stat<?> stat, int value){
        return setStat(uuid, level, stat, getStat(uuid, level, stat) + value);
    }

    public static int addStat(String username, Level level, Stat<?> stat, int value){
        if (level.getServer() != null) {
            AtomicInteger success = new AtomicInteger();
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                    success.set(addStat(profile.getId(), level, stat, value)), () -> {success.set(0);LogManager.getLogger().error("Couldn't find user with username: " + username);});
            return success.get();
        }
        return 0;
    }

    public static int setStat(UUID uuid, Level level, Stat<?> stat, int value){
        Player player = level.getPlayerByUUID(uuid);
        if (player == null){
            return setStatOffline(uuid, level, stat, value);
        } else {
            player.resetStat(stat);
            player.awardStat(stat, value);
            sync(level);
            return 1;
        }
    }

    public static int setStat(String username, Level level, Stat<?> stat, int value){
        if (level.getServer() != null) {
            AtomicInteger success = new AtomicInteger();
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                    success.set(setStat(profile.getId(), level, stat, value)), () -> {success.set(0);LogManager.getLogger().error("Couldn't find user with username: " + username);});
            return success.get();
        }
        return 0;
    }

    public static int getStat(UUID uuid, Level level, Stat<?> stat) {
        Player player = level.getPlayerByUUID(uuid);
        return player == null ? getStatOffline(uuid, level, stat) : ((ServerPlayer) player).getStats().getValue(stat);
    }

    public static int getStat(String username, Level level, Stat<?> stat) {
        if (level.getServer() != null) {
            AtomicInteger toReturn = new AtomicInteger();
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile -> {
                toReturn.set(getStat(profile.getId(), level, stat));
            }, () -> LogManager.getLogger().error("Couldn't find user with username: " + username));
            return toReturn.get();
        }
        return 0;
    }

    public static void sync(@NotNull Level level) {
        Map<UUID, Integer> pointMap = new HashMap<>();
        for (Player player : level.getServer().getPlayerList().getPlayers()) {
            Map.Entry<UUID, ServerStatsCounter> uuidServerStatsCounterEntry = Map.entry(player.getUUID(), level.getServer().getPlayerList().getPlayerStats(player));
            pointMap.put(uuidServerStatsCounterEntry.getKey(), uuidServerStatsCounterEntry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())));
        }
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            RegisterPackets.sendToPlayer(new SyncPointsPacket(pointMap), player);
        }
        pointMap.clear();
        for (Player player : level.getServer().getPlayerList().getPlayers()) {
            level.getCapability(AttachBountyCapability.INSTANCE).resolve().ifPresent(cap -> pointMap.put(player.getUUID(), cap.getBounty(player.getUUID())));
        }
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            RegisterPackets.sendToPlayer(new SyncBountyPacket(pointMap), player);
        }
    }
}
