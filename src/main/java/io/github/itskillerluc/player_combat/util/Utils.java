package io.github.itskillerluc.player_combat.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class Utils {
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

    private static void setStatOffline(UUID uuid, Level level, Stat<?> stat, int value){
        if (level.getServer() != null){
            getStats(uuid, level).stats.put(stat, value);
        }
    }

    private static void setStatOffline(String username, Level level, Stat<?> stat, int value){
        if (level.getServer() != null) {
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                setStatOffline(profile.getId(), level, stat, value), () -> LogManager.getLogger().error("Couldn't find user with username: " + username));
        }
    }

    public static void addStat(UUID uuid, Level level, Stat<?> stat, int value){
        setStat(uuid, level, stat, getStat(uuid, level, stat) + value);
    }

    public static void addStat(String username, Level level, Stat<?> stat, int value){
        if (level.getServer() != null) {
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                    addStat(profile.getId(), level, stat, value), () -> LogManager.getLogger().error("Couldn't find user with username: " + username));
        }
    }

    public static void setStat(UUID uuid, Level level, Stat<?> stat, int value){
        Player player = level.getPlayerByUUID(uuid);
        if (player == null){
            setStatOffline(uuid, level, stat, value);
        } else {
            player.resetStat(stat);
            player.awardStat(stat, value);
        }
    }

    public static void setStat(String username, Level level, Stat<?> stat, int value){
        if (level.getServer() != null) {
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                    setStat(profile.getId(), level, stat, value), () -> LogManager.getLogger().error("Couldn't find user with username: " + username));
        }
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
}
