package com.rabbitaats.alpha_experience.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.rabbitaats.alpha_experience.domain.AlphaExperienceColor;
import com.rabbitaats.alpha_experience.domain.AlphaExperienceProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Registers and handles commands.
 */
public class AlphaExperienceCommand {

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(
      literal("aexp")
        .requires(source -> source.hasPermission(2))

        .then(literal("exp")
          .then(literal("add")
            .then(argument("targets", EntityArgument.players())
              .then(argument("color", StringArgumentType.word())
                .suggests((context, builder) -> {
                  builder.suggest("red");
                  builder.suggest("green");
                  builder.suggest("yellow");
                  return builder.buildFuture();
                })
                .then(argument("amount", IntegerArgumentType.integer())
                  .executes(context -> addAlphaExperience(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets"),
                    StringArgumentType.getString(context, "color"),
                    IntegerArgumentType.getInteger(context, "amount")
                  ))
                )
              )
            )
          )
          .then(literal("get")
            .then(argument("target", EntityArgument.player())
              .then(argument("color", StringArgumentType.word())
                .suggests((context, builder) -> {
                  builder.suggest("red");
                  builder.suggest("green");
                  builder.suggest("yellow");
                  return builder.buildFuture();
                })
                .executes(context -> getAlphaExp(
                  context.getSource(),
                  EntityArgument.getPlayer(context, "target"),
                  StringArgumentType.getString(context, "color")
                ))
              )
            )
          )
        )

        .then(literal("level")
          .then(literal("add")
            .then(argument("targets", EntityArgument.players())
              .then(argument("color", StringArgumentType.word())
                .suggests((context, builder) -> {
                  builder.suggest("red");
                  builder.suggest("green");
                  builder.suggest("yellow");
                  return builder.buildFuture();
                })
                .then(argument("amount", IntegerArgumentType.integer())
                  .executes(context -> addAlphaLevel(
                    context.getSource(),
                    EntityArgument.getPlayers(context, "targets"),
                    StringArgumentType.getString(context, "color"),
                    IntegerArgumentType.getInteger(context, "amount")
                  ))
                )
              )
            )
          )
          .then(literal("get")
            .then(argument("target", EntityArgument.player())
              .then(argument("color", StringArgumentType.word())
                .suggests((context, builder) -> {
                  builder.suggest("red");
                  builder.suggest("green");
                  builder.suggest("yellow");
                  return builder.buildFuture();
                })
                .executes(context -> getAlphaLevel(
                  context.getSource(),
                  EntityArgument.getPlayer(context, "target"),
                  StringArgumentType.getString(context, "color")
                ))
              )
            )
          )
        )
    );
  }

  private static int addAlphaExperience(
    CommandSourceStack source,
    Collection<ServerPlayer> targets,
    String colorName,
    int amount
  ) {
    AlphaExperienceColor color = AlphaExperienceColor.fromCommandName(colorName);

    for (ServerPlayer player : targets) {
      player.getCapability(AlphaExperienceProvider.ALPHA_EXPERIENCE).ifPresent(data -> {
        data.addAlphaExperience(color, amount);

        source.sendSuccess(() -> Component.literal(
          "Added " + amount + " " + color.getCommandName()
            + " AlphaExp to " + player.getName().getString()
            + ". AlphaExp: " + data.getAlphaExperience(color)
            + ", AlphaLevel: " + data.getAlphaLevel(color)
        ), true);
      });
    }

    return targets.size();
  }

  private static int getAlphaExp(
    CommandSourceStack source,
    ServerPlayer target,
    String colorName
  ) {
    AlphaExperienceColor color = AlphaExperienceColor.fromCommandName(colorName);

    target.getCapability(AlphaExperienceProvider.ALPHA_EXPERIENCE).ifPresent(data -> {
      source.sendSuccess(() -> Component.literal(
        target.getName().getString()
          + "'s " + color.getCommandName()
          + " AlphaExp: " + data.getAlphaExperience(color)
      ), false);
    });

    return 1;
  }

  private static int addAlphaLevel(
    CommandSourceStack source,
    Collection<ServerPlayer> targets,
    String colorName,
    int amount
  ) {
    AlphaExperienceColor color = AlphaExperienceColor.fromCommandName(colorName);

    for (ServerPlayer player : targets) {
      player.getCapability(AlphaExperienceProvider.ALPHA_EXPERIENCE).ifPresent(data -> {
        data.addAlphaLevel(color, amount);

        source.sendSuccess(() -> Component.literal(
          "Added " + amount + " " + color.getCommandName()
            + " AlphaLevel to " + player.getName().getString()
            + ". AlphaLevel: " + data.getAlphaLevel(color)
        ), true);
      });
    }

    return targets.size();
  }

  private static int getAlphaLevel(
    CommandSourceStack source,
    ServerPlayer target,
    String colorName
  ) {
    AlphaExperienceColor color = AlphaExperienceColor.fromCommandName(colorName);

    target.getCapability(AlphaExperienceProvider.ALPHA_EXPERIENCE).ifPresent(data -> {
      source.sendSuccess(() -> Component.literal(
        target.getName().getString()
          + "'s " + color.getCommandName()
          + " AlphaLevel: " + data.getAlphaLevel(color)
      ), false);
    });

    return 1;
  }
}