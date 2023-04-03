package io.github.itskillerluc.player_combat.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.itskillerluc.player_combat.capabilities.AttachBountyCapability;
import io.github.itskillerluc.player_combat.config.ServerConfig;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import io.github.itskillerluc.player_combat.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PlayerCombatCommand {
    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, String cmd) {
        pDispatcher.register(literal(cmd)
                .then(literal("points").requires(p -> p.hasPermission(2))
                        .then(literal("reward")
                                .then(argument("first", IntegerArgumentType.integer())
                                        .then(argument("second", IntegerArgumentType.integer())
                                                .then(argument("third", IntegerArgumentType.integer())
                                                        .executes(context -> payRewards(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), -1, -1))

                                                        .then(argument("threshold", IntegerArgumentType.integer())
                                                                .then(argument("thresholdamount", IntegerArgumentType.integer())
                                                                        .executes(context -> payRewards(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), IntegerArgumentType.getInteger(context, "threshold"), IntegerArgumentType.getInteger(context, "thresholdamount")))))))))

                        .then(literal("give")
                                .then(argument("targets", GameProfileArgument.gameProfile())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> givePoint(context, IntegerArgumentType.getInteger(context, "amount"), GameProfileArgument.getGameProfiles(context, "targets"))))))

                        .then(literal("remove")
                                .then(argument("targets", GameProfileArgument.gameProfile())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> removePoint(context, IntegerArgumentType.getInteger(context, "amount"), GameProfileArgument.getGameProfiles(context, "targets"))))))

                        .then(literal("get")
                                .then(argument("target", GameProfileArgument.gameProfile())
                                        .executes(context -> getStats(context, GameProfileArgument.getGameProfiles(context, "target")))))

                        .then(literal("set")
                                .then(argument("targets", GameProfileArgument.gameProfile())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "amount"), GameProfileArgument.getGameProfiles(context, "targets"))))

                                        .then(literal("from")
                                                .then(argument("target", GameProfileArgument.gameProfile())
                                                        .executes(context -> setPoint(context, ((int) GameProfileArgument.getGameProfiles(context, "target").stream().mapToInt(profile -> getStat(context, profile)).average().orElse(0)), GameProfileArgument.getGameProfiles(context, "targets"))))))

                                .then(argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "amount"), ImmutableList.of(context.getSource().getPlayerOrException().getGameProfile()))))

                                .then(literal("from")
                                        .then(argument("target", GameProfileArgument.gameProfile())
                                                .executes(context -> setPoint(context, ((int) GameProfileArgument.getGameProfiles(context, "target").stream().mapToInt(profile -> getStat(context, profile)).average().orElse(0)), ImmutableList.of(context.getSource().getPlayerOrException().getGameProfile()))))))

                        .then(literal("reset")
                                .executes(context -> resetPoints(context, ImmutableList.of(context.getSource().getPlayerOrException().getGameProfile())))

                                .then(literal("everyone")
                                        .executes(PlayerCombatCommand::resetAll))

                                .then(argument("targets", GameProfileArgument.gameProfile())
                                        .executes(context -> resetPoints(context, GameProfileArgument.getGameProfiles(context, "targets")))))

                        .then(literal("bonus")
                                .then(argument("first", IntegerArgumentType.integer())
                                        .then(argument("second", IntegerArgumentType.integer())
                                                .then(argument("third", IntegerArgumentType.integer())
                                                        .executes(context -> bonusReward(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), -1, -1))

                                                        .then(argument("threshold", IntegerArgumentType.integer())
                                                                .then(argument("thresholdamount", IntegerArgumentType.integer())
                                                                        .executes(context -> bonusReward(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), IntegerArgumentType.getInteger(context, "threshold"), IntegerArgumentType.getInteger(context, "thresholdamount"))))))))))
                .then(literal("leaderboard")
                        .executes(context -> getLeaderBoard(context, 1))

                        .then(argument("page", IntegerArgumentType.integer(1))
                                .executes(context -> getLeaderBoard(context, IntegerArgumentType.getInteger(context, "page")))))

                .then(literal("bounty")
                        .then(literal("set")
                                .then(argument("target", GameProfileArgument.gameProfile())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> setBounty(context, GameProfileArgument.getGameProfiles(context, "target"), IntegerArgumentType.getInteger(context, "amount"))))))

                        .then(literal("list")
                                .executes(context -> listBounty(context, 0))

                                .then(argument("page", IntegerArgumentType.integer())
                                        .executes(context -> listBounty(context, IntegerArgumentType.getInteger(context, "page")))))

                        .then(literal("clear")
                                .executes(PlayerCombatCommand::removeAllBounty)
                                .then(argument("targets", GameProfileArgument.gameProfile())
                                        .executes(context -> removeBounty(context, GameProfileArgument.getGameProfiles(context, "targets"))))
                        )
                )
        );
    }

    private static int getLeaderBoard(CommandContext<CommandSourceStack> context, int page){
        MinecraftServer server = context.getSource().getServer();
        ArrayList<Map.Entry<UUID, ServerStatsCounter>> entries = new ArrayList<>(server.getPlayerList().stats.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())))).toList());

        context.getSource().sendSystemMessage(Component.literal("--------Leaderboard--------").withStyle(ChatFormatting.GOLD));


        for (int i = 10 * (page -1); i < Math.min((page-1) * 10 + 10, entries.size()); i++) {
            Map.Entry<UUID, ServerStatsCounter> uuidServerStatsCounterEntry = entries.get(i);

            int finalI = i + 1;
            server.getProfileCache().get(uuidServerStatsCounterEntry.getKey()).ifPresentOrElse(profile ->
                    context.getSource().sendSystemMessage(Component.literal(finalI + ". ").withStyle(ChatFormatting.GREEN).append(Component.literal(profile.getName() + ": ").withStyle(ChatFormatting.AQUA)).append(Component.literal(String.valueOf(uuidServerStatsCounterEntry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())))).withStyle(ChatFormatting.WHITE))), () -> LogManager.getLogger().warn("Couldn't find player"));
        }
        context.getSource().sendSystemMessage(Component.literal(String.format("---------Page (%d/%d)---------", page, (entries.size() / 10) +1)).withStyle(ChatFormatting.GOLD));
        return 0;
    }

    private static int givePoint(CommandContext<CommandSourceStack> context, int points, Collection<GameProfile> players){
        int toReturn = 0;
        for (GameProfile player : players) {
            if (Utils.addStat(player.getId(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), points) == 1) {
                toReturn = 1;
            }
            var p = context.getSource().getServer().getPlayerList().getPlayer(player.getId());
            if (p != null) {
                p.refreshTabListName();
            }
        }
        context.getSource().sendSystemMessage(Component.literal(toReturn == 1 ? "success!" : "Failed :("));
        return toReturn;
    }

    private static int removePoint(CommandContext<CommandSourceStack> context, int points, Collection<GameProfile> players){
        return givePoint(context, -points, players);
    }
    private static int setPoint(CommandContext<CommandSourceStack> context, int points, Collection<GameProfile> players){
        int toReturn = 0;
        for (GameProfile player : players) {
            if (Utils.setStat(player.getId(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), points) == 0) {
                var p = context.getSource().getServer().getPlayerList().getPlayer(player.getId());
                if (p != null) {
                    p.refreshTabListName();
                }
                toReturn = 1;
            }
        }
        context.getSource().sendSystemMessage(Component.literal(toReturn == 1 ? "success!" : "Failed :("));
        return toReturn;
    }

    private static int payRewards(CommandContext<CommandSourceStack> context, int first, int second, int third, int threshold, int thresholdAmount){
        ArrayList<Map.Entry<UUID, ServerStatsCounter>> entries = new ArrayList<>(context.getSource().getServer().getPlayerList().stats.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())))).toList());
        int toReturn = 0;
        if (Utils.setStat(entries.get(0).getKey(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), -first) == 1 ||
                Utils.setStat(entries.get(1).getKey(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), -second) == 1 ||
                Utils.setStat(entries.get(2).getKey(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), -third) == 1){

            toReturn = 1;
        }
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<UUID, ServerStatsCounter> entry = entries.get(i);
            if (i == 0 || i == 1 || i == 2 || entry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())) < threshold){
                if (context.getSource().getLevel().players().stream().map(Entity::getUUID).anyMatch(e -> e.equals(entry.getKey()))) {
                    fetchRewards(context.getSource().getLevel(), ((ServerPlayer) Objects.requireNonNull(context.getSource().getLevel().getPlayerByUUID(entry.getKey()))));
                }
                continue;
            }
            if (Utils.setStat(entry.getKey(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), -thresholdAmount) == 1) {
                toReturn = 1;
            }

        }
        for (ServerPlayer player : context.getSource().getLevel().players()) {
            player.refreshTabListName();
        }
        context.getSource().sendSystemMessage(Component.literal(toReturn == 1 ? "success!" : "Failed :("));
        return toReturn;
    }

    private static int resetPoints(CommandContext<CommandSourceStack> context, Collection<GameProfile> players){
        int toReturn = 0;
        for (GameProfile player : players) {
            if (Utils.setStat(player.getId(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), ServerConfig.DEFAULT_POINTS.get()) == 0) {
                var p = context.getSource().getServer().getPlayerList().getPlayer(player.getId());
                if (p != null) {
                    p.refreshTabListName();
                }
                toReturn = 1;
            }
        }
        context.getSource().sendSystemMessage(Component.literal(toReturn == 1 ? "success!" : "Failed :("));
        return toReturn;
    }

    @SuppressWarnings("ConstantConditions")
    private static int bonusReward(CommandContext<CommandSourceStack> context, int first, int second, int third, int threshold, int thresholdAmount){
        PlayerList playerlist = context.getSource().getServer().getPlayerList();
        ArrayList<Map.Entry<UUID, ServerStatsCounter>> entries = new ArrayList<>(playerlist.stats.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())))).toList());
        entries.removeIf(uuid -> playerlist.getPlayer(uuid.getKey()) == null);
        boolean flag;
        Item value = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ServerConfig.REWARD_ITEM.get()));
        flag = playerlist.getPlayer(entries.get(0).getKey()).addItem(new ItemStack(value, first));
        if (!flag) {
            playerlist.getPlayer(entries.get(0).getKey()).spawnAtLocation(new ItemStack(value, first));
        }
        flag = playerlist.getPlayer(entries.get(1).getKey()).addItem(new ItemStack(value, second));
        if (!flag) {
            playerlist.getPlayer(entries.get(1).getKey()).spawnAtLocation(new ItemStack(value, second));
        }
        flag = playerlist.getPlayer(entries.get(2).getKey()).addItem(new ItemStack(value, third));
        if (!flag) {
            playerlist.getPlayer(entries.get(2).getKey()).spawnAtLocation(new ItemStack(value, third));
        }

        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<UUID, ServerStatsCounter> entry = entries.get(i);
            if (i == 0 || i == 1 || i == 2 || entry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())) < threshold){
                continue;
            }
            flag = playerlist.getPlayer(entry.getKey()).addItem(new ItemStack(value, thresholdAmount));
            if (!flag) {
                playerlist.getPlayer(entry.getKey()).spawnAtLocation(new ItemStack(value, thresholdAmount));
            }
        }
        return 1;
    }

    private static int setBounty(CommandContext<CommandSourceStack> context, Collection<GameProfile> target, int amount){
        if (context.getSource().getPlayer() == null){
            return 0;
        }
        if (getStat(context, context.getSource().getPlayer().getGameProfile()) < amount * target.size()) {
            context.getSource().getPlayer().sendSystemMessage(Component.literal("You do not have sufficient points for this. You are " + (target.size() * amount - getStat(context, context.getSource().getPlayer().getGameProfile())) + "points short."));
            return 0;
        }
        AtomicInteger toReturn = new AtomicInteger();
        for (GameProfile gameProfile : target) {
            context.getSource().getLevel().getCapability(AttachBountyCapability.INSTANCE).resolve().ifPresent(cap -> {
                    cap.setBounty(gameProfile.getId(), amount);
                    Utils.addStat(context.getSource().getPlayer().getUUID(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), -amount);
                    var player = context.getSource().getServer().getPlayerList().getPlayer(gameProfile.getId());
                    if (player != null) {
                        player.refreshTabListName();
                    }
                    toReturn.set(1);
            });
        }
        context.getSource().sendSystemMessage(Component.literal(toReturn.get() == 1 ? "success!" : "Failed :("));
        return toReturn.get();
    }

    private static int listBounty(CommandContext<CommandSourceStack> context, int page){
        if (!context.getSource().getLevel().getCapability(AttachBountyCapability.INSTANCE).isPresent()){
            return 0;
        }

        context.getSource().sendSystemMessage(Component.literal("----------Bounties----------").withStyle(ChatFormatting.GOLD));

        ArrayList<Map.Entry<UUID, Integer>> entries = new ArrayList<>(context.getSource().getLevel().getCapability(AttachBountyCapability.INSTANCE)
                .resolve().orElseThrow().getBountyList().entrySet());

        for (int i = 10 * (Math.max(0, page)); i < Math.min((page) * 10 + 10, entries.size()); i++) {
            Map.Entry<UUID, Integer> uuidServerStatsCounterEntry = entries.get(i);

            int finalI = i + 1;
            context.getSource().getServer().getProfileCache().get(uuidServerStatsCounterEntry.getKey()).ifPresentOrElse(profile ->
                    context.getSource().sendSystemMessage(Component.literal(finalI + ". ").withStyle(ChatFormatting.GREEN).append(Component.literal(profile.getName() + ": ").withStyle(ChatFormatting.AQUA)).append(Component.literal(String.valueOf(uuidServerStatsCounterEntry.getValue().intValue())).withStyle(ChatFormatting.WHITE))), () -> LogManager.getLogger().warn("Couldn't find player"));
        }
        context.getSource().sendSystemMessage(Component.literal(String.format("---------Page (%d/%d)---------", page + 1, (entries.size() / 10) +1)).withStyle(ChatFormatting.GOLD));
        return 0;
    }

    public static int resetAll(CommandContext<CommandSourceStack> context){
        for (ServerPlayer player : context.getSource().getLevel().players()) {
            player.refreshTabListName();
        }
        for (UUID uuid : context.getSource().getServer().getPlayerList().stats.keySet()) {
            return Utils.setStat(uuid, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), ServerConfig.DEFAULT_POINTS.get()) &
                    Utils.setStat(uuid, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.MOB_POINTS.get()), 0);
        }
        return 0;
    }

    private static int getStat(CommandContext<CommandSourceStack> context, GameProfile player){
        return Utils.getStat(player.getId(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()));
    }

    private static int getStats(CommandContext<CommandSourceStack> context, Collection<GameProfile> player){
        int toReturn = 0;
        for (GameProfile profile : player) {
            context.getSource().sendSystemMessage(Component.literal(profile.getName() + ": ").append(String.valueOf(getStat(context, profile))));
            if (Math.min(getStat(context, profile), 1) == 1){
                toReturn = 1;
            }
        }
        return toReturn;

    }

    private static int removeAllBounty(CommandContext<CommandSourceStack> context){
        AtomicInteger toReturn = new AtomicInteger();
        context.getSource().getLevel().getCapability(AttachBountyCapability.INSTANCE).resolve().ifPresentOrElse(cap -> {
            for (ServerPlayer player : context.getSource().getLevel().players()) {
                var p = context.getSource().getServer().getPlayerList().getPlayer(player.getUUID());
                if (p != null) {
                    p.refreshTabListName();
                }
            }
            toReturn.set(1);
            cap.getBountyList().clear();
        }, () -> toReturn.set(0));
        context.getSource().sendSystemMessage(Component.literal(toReturn.get() == 1 ? "success!" : "Failed :("));
        return toReturn.get();
    }

    private static int removeBounty(CommandContext<CommandSourceStack> context, Collection<GameProfile> players) {
        AtomicInteger toReturn = new AtomicInteger();
        int r = 0;
        for (GameProfile player : players) {
            context.getSource().getLevel().getCapability(AttachBountyCapability.INSTANCE).resolve().ifPresent(cap ->
                    toReturn.set(cap.removeBounty(player.getId())));
            if (toReturn.get() == 1){
                var p = context.getSource().getServer().getPlayerList().getPlayer(player.getId());
                if (p != null) {
                    p.refreshTabListName();
                }
                r = 1;
            }
        }
        context.getSource().sendSystemMessage(Component.literal(r == 1 ? "success!" : "Failed :("));
        return r;
    }

    public static void fetchRewards(ServerLevel level, ServerPlayer player){
        var stat = Utils.getStat(player.getUUID(), level, Stats.CUSTOM.get(StatRegistry.POINTS.get()));
        if (stat < 0){
            Item value = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ServerConfig.REWARD_ITEM.get()));
            boolean flag = player.addItem(new ItemStack(value, Math.abs(stat)));
            if (!flag) {
                player.spawnAtLocation(new ItemStack(value, Math.abs(stat)));
            }
            player.displayClientMessage(Component.literal("You have gained your Rewards!!!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
            player.displayClientMessage(Component.literal("You got " + Math.abs(stat) + " ").append(value.getDefaultInstance().getDisplayName()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);
            Utils.setStat(player.getUUID(), level, Stats.CUSTOM.get(StatRegistry.POINTS.get()), ServerConfig.DEFAULT_POINTS.get());
        }
    }
}
