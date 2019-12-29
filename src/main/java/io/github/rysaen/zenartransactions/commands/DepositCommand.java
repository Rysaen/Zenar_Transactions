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
import org.spongepowered.api.text.format.TextStyles;

import io.github.rysaen.zenartransactions.util.TransactionUtil;


public class DepositCommand implements CommandExecutor {

	private static CommandSpec spec;

	private static final Text commandDescriptionL = Text.builder()
			.append(Text.of("Ti permette di depositare gli zenar che hai nell'inventario sul conto virtuale. Utiizzando l'opzione "))
			.append(Text.of(TextColors.GOLD, "-p"))
			.append(Text.of(" (o nel formato esteso "))
			.append(Text.of(TextColors.GOLD, "--peek"))
			.append(Text.of(") è possibile ottenere la stima degli zenar trasportati "))
			.append(Text.of(TextStyles.ITALIC, "senza"))
			.append(Text.of(" effettuare l'operazione di deposito."))
			.build();

	public static CommandSpec build() {
		if(spec == null)
			spec = CommandSpec.builder()
			.description(Text.of("Deposita gli zenar che stai trasportando sul conto virtuale."))
			.extendedDescription(commandDescriptionL)
			.executor(new DepositCommand())
			.arguments(GenericArguments.flags()
					.flag("p", "-peek")
					.buildWith(GenericArguments.none())
					)
			.build()
			;
		return spec;
	}

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
			} else if(TransactionUtil.deposit(player))
				CommandResult.success();
		}
		return CommandResult.empty();
	}

}
