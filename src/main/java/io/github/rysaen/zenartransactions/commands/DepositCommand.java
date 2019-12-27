package io.github.rysaen.zenartransactions.commands;

import java.math.BigDecimal;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import io.github.rysaen.zenartransactions.util.TransactionUtil;


public class DepositCommand implements CommandExecutor {

	private static CommandSpec spec;
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(src instanceof Player) {
			final Player player = (Player)src;
			final BigDecimal amount = TransactionUtil.inventoryPeek(player);
			if(args.hasAny("p")) {
				player.sendMessage(Text.builder()
						.append(Text.of("Stai trasportando un totale di "))
						.append(Text.of(TextColors.GOLD, amount))
						.append(Text.of(" zenar."))
						.build()
				);
				return CommandResult.success();
			} else {
				if(TransactionUtil.deposit(player))
					CommandResult.success();
			}
		}
		return CommandResult.empty();
	}

	public static CommandSpec build() {
		if(spec == null) {
			spec = CommandSpec.builder()
				.description(Text.of("Deposit")) // TODO Deposit command description
				.executor(new DepositCommand())
				.arguments(GenericArguments.flags()
					.flag("p", "-peek")
					.buildWith(GenericArguments.none())
				)
				.build()
			;
		}
		return spec;
	}

}
