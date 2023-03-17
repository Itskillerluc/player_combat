package io.github.itskillerluc.player_combat.util;

import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class Utils {
    @Nullable
    public static ServerStatsCounter getStats(UUID uuid, Level level){
        if (level.getServer() == null){
            return null;
        }
        return level.getServer().getPlayerList().stats.get(uuid);
    }

    @Nullable
    public static ServerStatsCounter getStats(String username, Level level){
        if (level.getServer() == null){
            return null;
        }
        AtomicReference<ServerStatsCounter> toReturn = new AtomicReference<>();
        level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                toReturn.set(getStats(profile.getId(), level)), () -> LogManager.getLogger().error("Couldn't find user with username: " + username));
        return toReturn.get();
    }

    @Nullable
    public static Integer getStat(UUID uuid, Level level, Stat<?> stat){
        if (level.getServer() == null){
            return null;
        }
        var stats = getStats(uuid, level);
        return stats == null ? null : stats.getValue(stat);
    }

    @Nullable
    public static Integer getStat(String username, Level level, Stat<?> stat){
        if (level.getServer() == null){
            return null;
        }
        var stats = getStats(username, level);
        return stats == null ? null : stats.getValue(stat);
    }

    public static void setStat(UUID uuid, Level level, Stat<?> stat, int value){
        if (level.getServer() != null){
            getStats(uuid, level).stats.put(stat, value);
        }
    }

    public static void setStat(String username, Level level, Stat<?> stat, int value){
        if (level.getServer() != null) {
            level.getServer().getProfileCache().get(username).ifPresentOrElse(profile ->
                getStats(profile.getId(), level).stats.put(stat, value), () -> LogManager.getLogger().error("Couldn't find user with username: " + username));
        }
    }
}
