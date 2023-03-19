package io.github.itskillerluc.player_combat.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.itskillerluc.player_combat.config.ServerConfig;
import io.github.itskillerluc.player_combat.stats.StatRegistry;
import io.github.itskillerluc.player_combat.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;

import java.util.*;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PlayerCombatCommand {
    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext pContext, String cmd) {
        LiteralCommandNode<CommandSourceStack> mainCommand = pDispatcher.register(literal(cmd)
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
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> givePoint(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "targets").stream().map(Entity::getStringUUID).toList()))))

                                .then(argument("target", StringArgumentType.word())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> givePoint(context, IntegerArgumentType.getInteger(context, "amount"), ImmutableList.of(StringArgumentType.getString(context, "target")))))))

                        .then(literal("remove")
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> removePoint(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "targets").stream().map(Entity::getStringUUID).toList()))))

                                .then(argument("target", StringArgumentType.word())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> removePoint(context, IntegerArgumentType.getInteger(context, "amount"), ImmutableList.of(StringArgumentType.getString(context,"target")))))))

                        .then(literal("get")
                                .then(argument("target", EntityArgument.player())
                                        .executes(context -> getStats(context, EntityArgument.getPlayer(context, "target").getStringUUID())))

                                .then(argument("target", StringArgumentType.word())
                                        .executes(context -> getStats(context, StringArgumentType.getString(context, "target"))))

                        .then(literal("set")
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "targets").stream().map(Entity::getStringUUID).toList())))

                                        .then(literal("from")
                                                .then(argument("target", EntityArgument.player())
                                                        .executes(context -> setPoint(context, Utils.getStat(EntityArgument.getPlayer(context, "target").getUUID(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get())), EntityArgument.getPlayers(context, "targets").stream().map(Entity::getStringUUID).toList())))

                                                .then(argument("target", StringArgumentType.word())
                                                        .executes(context -> setPoint(context, getStat(context, StringArgumentType.getString(context, "target")), ImmutableList.of(StringArgumentType.getString(context, "target")))))))

                                .then(argument("target", StringArgumentType.word())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "targets").stream().map(Entity::getStringUUID).toList())))

                                        .then(literal("from")
                                                .then(argument("target", EntityArgument.player())
                                                        .executes(context -> setPoint(context, Utils.getStat(EntityArgument.getPlayer(context, "target").getUUID(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get())), ImmutableList.of(StringArgumentType.getString(context, "targets")))))

                                                .then(argument("target", StringArgumentType.word())
                                                        .executes(context -> setPoint(context, getStat(context, StringArgumentType.getString(context, "target")), ImmutableList.of(StringArgumentType.getString(context, "target")))))))


                                .then(argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "amount"), ImmutableList.of(context.getSource().getPlayerOrException().getStringUUID()))))

                                .then(literal("from")
                                        .then(argument("target", EntityArgument.player())
                                                .executes(context -> setPoint(context, Utils.getStat(EntityArgument.getPlayer(context, "target").getUUID(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get())), ImmutableList.of(context.getSource().getPlayerOrException().getStringUUID()))))

                                        .then(argument("target", StringArgumentType.word())
                                                .executes(context -> setPoint(context, getStat(context, StringArgumentType.getString(context, "target")), ImmutableList.of(context.getSource().getPlayerOrException().getStringUUID()))))))


                        .then(literal("reset")
                                .executes(context -> resetPoints(context, ImmutableList.of(context.getSource().getPlayerOrException().getStringUUID())))

                                .then(literal("everyone")
                                        .executes(PlayerCombatCommand::resetAll))

                                .then(literal("everyoneoffline")
                                        .executes(PlayerCombatCommand::resetAllOffline))

                                .then(argument("targets", EntityArgument.players())
                                        .executes(context -> resetPoints(context, EntityArgument.getPlayers(context, "targets").stream().map(Entity::getStringUUID).toList())))

                                .then(argument("targets", StringArgumentType.word())
                                        .executes(context -> resetPoints(context, ImmutableList.of(StringArgumentType.getString(context, "targets"))))))

                        .then(literal("bonus")
                                .then(argument("first", IntegerArgumentType.integer())
                                        .then(argument("second", IntegerArgumentType.integer())
                                                .then(argument("third", IntegerArgumentType.integer())
                                                        .executes(context -> bonusReward(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), -1, -1))

                                                        .then(argument("threshold", IntegerArgumentType.integer())
                                                                .then(argument("thresholdamount", IntegerArgumentType.integer())
                                                                        .executes(context -> bonusReward(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), IntegerArgumentType.getInteger(context, "threshold"), IntegerArgumentType.getInteger(context, "thresholdamount")))))))))))

                .then(literal("leaderboard")
                        .executes(context -> getLeaderBoard(context, 0))

                        .then(argument("page", IntegerArgumentType.integer())
                                .executes(context -> getLeaderBoard(context, IntegerArgumentType.getInteger(context, "page")))))

                .then(literal("bounty")
                        .then(literal("set")
                                .then(argument("target", EntityArgument.player())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> setBounty(context, EntityArgument.getPlayer(context, "target").getStringUUID(), IntegerArgumentType.getInteger(context, "amount")))))

                                .then(argument("target", StringArgumentType.word())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> setBounty(context, StringArgumentType.getString(context, "target"), IntegerArgumentType.getInteger(context, "amount"))))))

                        .then(literal("list")
                                .executes(context -> listBounty(context, 0))

                                .then(argument("page", IntegerArgumentType.integer())
                                        .executes(context -> listBounty(context, IntegerArgumentType.getInteger(context, "page"))))))
        );
    }

    private static int getLeaderBoard(CommandContext<CommandSourceStack> context, int page){
        MinecraftServer server = context.getSource().getServer();
        ArrayList<Map.Entry<UUID, ServerStatsCounter>> entries = new ArrayList<>(server.getPlayerList().stats.entrySet());
        context.getSource().sendSystemMessage(Component.literal("--------Leaderboard--------").withStyle(ChatFormatting.GOLD));


        for (int i = 10 * page; i < Math.min(page * 10 + 10, entries.size()); i++) {
            Map.Entry<UUID, ServerStatsCounter> uuidServerStatsCounterEntry = entries.get(i);

            int finalI = i + 1;
            server.getProfileCache().get(uuidServerStatsCounterEntry.getKey()).ifPresentOrElse(profile ->
                    context.getSource().sendSystemMessage(Component.literal(finalI + ". ").withStyle(ChatFormatting.GREEN).append(Component.literal(profile.getName() + ": ").withStyle(ChatFormatting.AQUA)).append(Component.literal(String.valueOf(uuidServerStatsCounterEntry.getValue().getValue(Stats.CUSTOM.get(StatRegistry.POINTS.get())))).withStyle(ChatFormatting.WHITE))), () -> LogManager.getLogger().warn("Couldn't find player"));
        }
        context.getSource().sendSystemMessage(Component.literal(String.format("---------Page (%d/%d)---------", page + 1, (entries.size() / 10) +1)).withStyle(ChatFormatting.GOLD));
        return 0;
    }

    private static int givePoint(CommandContext<CommandSourceStack> context, int points, Collection<String> players){
        for (String player : players) {
            try {
                return Utils.addStat(UUID.fromString(player), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), points);
            } catch (IllegalArgumentException ignore) {
                return Utils.addStat(player, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), points);
            }
        }
        return 0;
    }

    private static int removePoint(CommandContext<CommandSourceStack> context, int points, Collection<String> players){
        return givePoint(context, -points, players);
    }
    private static int setPoint(CommandContext<CommandSourceStack> context, int points, Collection<String> players){
        for (String player : players) {
            try {
                return Utils.setStat(UUID.fromString(player), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), points);
            } catch (IllegalArgumentException ignore) {
                return Utils.setStat(player, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), points);
            }
        }
        return 0;
    }

    private static int payRewards(CommandContext<CommandSourceStack> context, int first, int second, int third, int threshold, int thresholdAmount){
        return 0;
    }

    private static int resetPoints(CommandContext<CommandSourceStack> context, Collection<String> players){
        for (String player : players) {
            try {
                return Utils.setStat(UUID.fromString(player), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), ServerConfig.DEFAULT_POINTS.get());
            } catch (IllegalArgumentException ignore) {
                return Utils.setStat(player, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), ServerConfig.DEFAULT_POINTS.get());
            }
        }
        return 0;
    }

    private static int bonusReward(CommandContext<CommandSourceStack> context, int first, int second, int third, int threshold, int thresholdAmount){
        return payRewards(context, first, second, third, threshold, thresholdAmount) & resetAll(context);
    }

    private static int setBounty(CommandContext<CommandSourceStack> context, String target, int amount){
        return 0;
    }

    private static int listBounty(CommandContext<CommandSourceStack> context, int page){
        return 0;
    }

    public static int resetAll(CommandContext<CommandSourceStack> context){
        for (ServerPlayer player : context.getSource().getLevel().players()) {
            return Utils.setStat(player.getUUID(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), ServerConfig.DEFAULT_POINTS.get()) &
                    Utils.setStat(player.getUUID(), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.MOB_POINTS.get()), 0);
        }
        return 0;
    }

    public static int resetAllOffline(CommandContext<CommandSourceStack> context){
        for (UUID uuid : context.getSource().getServer().getPlayerList().stats.keySet()) {
            return Utils.setStat(uuid, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()), ServerConfig.DEFAULT_POINTS.get()) &
                    Utils.setStat(uuid, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.MOB_POINTS.get()), 0);
        }
        return 0;
    }

    private static int getStat(CommandContext<CommandSourceStack> context, String player){
        try {
            return Utils.getStat(UUID.fromString(player), context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()));
        } catch (IllegalArgumentException ignore) {
            return Utils.getStat(player, context.getSource().getLevel(), Stats.CUSTOM.get(StatRegistry.POINTS.get()));
        }
    }

    private static int getStats(CommandContext<CommandSourceStack> context, String player){
        context.getSource().sendSystemMessage(Component.literal(String.valueOf(getStat(context, player))));
        return Math.min(getStat(context, player), 1);
    }
}
