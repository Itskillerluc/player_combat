package io.github.itskillerluc.player_combat.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PlayerCombatCommand {
    public static void register(CommandDispatcher<CommandSourceStack> pDispatcher, CommandBuildContext pContext) {
        var mainCommand = pDispatcher.register(literal("pc")
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
                                                .executes(context -> givePoint(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "targets"))))))
                        .then(literal("remove")
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> removePoint(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "targets"))))))
                        .then(literal("set")
                                .then(argument("targets", EntityArgument.players())
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "targets"))))
                                        .then(literal("from")
                                                .then(argument("target", EntityArgument.player())
                                                        //TODO: get the points
                                                        .executes(context -> setPoint(context, 2, EntityArgument.getPlayers(context, "targets"))))))
                                .then(argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> setPoint(context, IntegerArgumentType.getInteger(context, "amount"), ImmutableList.of(context.getSource().getPlayerOrException()))))
                                .then(literal("from")
                                                .then(argument("target", EntityArgument.player())
                                                        //TODO: get the points
                                                        .executes(context -> setPoint(context, 3, ImmutableList.of(context.getSource().getPlayerOrException()))))))
                        .then(literal("reset")
                                .executes(context -> resetPoints(context, ImmutableList.of(context.getSource().getPlayerOrException())))
                                .then(literal("everyone")
                                        .executes(PlayerCombatCommand::resetAll))
                                .then(argument("targets", EntityArgument.players())
                                        .executes(context -> resetPoints(context, EntityArgument.getPlayers(context, "targets")))))
                        .then(literal("bonus")
                                .then(argument("first", IntegerArgumentType.integer())
                                        .then(argument("second", IntegerArgumentType.integer())
                                                .then(argument("third", IntegerArgumentType.integer())
                                                        .executes(context -> bonusReward(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), -1, -1))
                                                        .then(argument("threshold", IntegerArgumentType.integer())
                                                                .then(argument("thresholdamount", IntegerArgumentType.integer())
                                                                        .executes(context -> bonusReward(context, IntegerArgumentType.getInteger(context, "first"), IntegerArgumentType.getInteger(context, "second"), IntegerArgumentType.getInteger(context, "third"), IntegerArgumentType.getInteger(context, "threshold"), IntegerArgumentType.getInteger(context, "thresholdamount"))))))))))
                .then(literal("leaderboard")
                        .executes(context -> getLeaderBoard(context, 0))
                        .then(argument("page", IntegerArgumentType.integer())
                                .executes(context -> getLeaderBoard(context, IntegerArgumentType.getInteger(context, "page")))))
        );
        pDispatcher.register(literal("playercombat")
                .requires(p -> p.hasPermission(2)).redirect(mainCommand));
    }

    private static int getLeaderBoard(CommandContext<?> context, int page){
        return 0;
    }

    private static int givePoint(CommandContext<?> context, int points, Collection<ServerPlayer> players){
        return 0;
    }

    private static int removePoint(CommandContext<?> context, int points, Collection<ServerPlayer> players){
        return 0;
    }
    private static int setPoint(CommandContext<?> context, int points, Collection<ServerPlayer> players){
        return 0;
    }

    private static int payRewards(CommandContext<?> context, int first, int second, int third, int threshold, int thresholdAmount){
        return 0;
    }

    private static int resetPoints(CommandContext<?> context, Collection<ServerPlayer> players){
        return 0;
    }

    private static int bonusReward(CommandContext<?> context, int first, int second, int third, int threshold, int thresholdAmount){
        return payRewards(context, first, second, third, threshold, thresholdAmount) & resetAll(context);
    }

    public static int resetAll(CommandContext<?> context){
        return 0;
    }
}
