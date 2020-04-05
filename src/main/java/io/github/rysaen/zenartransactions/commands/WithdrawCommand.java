package io.github.rysaen.zenartransactions.commands;

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
import io.github.rysaen.zenartransactions.denomination.Denominations;
import io.github.rysaen.zenartransactions.util.TransactionUtil;


public class WithdrawCommand implements CommandExecutor  {

	private static CommandSpec spec;

	private static final Text commandDescriptionL = Text.builder()
			.append(Text.of("Ti permette di prelevare zenar fisici dal tuo conto virtuale. Se è specificato solo il valore da ritirare, ti verrà automaticamente assegnato il quantitativo di zenar corrispondente. In caso tu voglia ritirare uno specifico taglio, puoi utilizzare l'opzione  "))
			.append(Text.of(TextColors.GOLD, "-d"))
			.append(Text.of(" (o esteso "))
			.append(Text.of(TextColors.GOLD, "--denomination"))
			.append(Text.of(") specificando il nome del taglio (tra quelli disponibili) e il numero degli zenar di quel taglio da ritirare."))
			.build();

	public static CommandSpec build() {
		if(spec == null) {
			spec = CommandSpec.builder()
					.description(Text.of("Ritira zenar fisici dal conto virtuale."))
					.extendedDescription(commandDescriptionL)
					.executor(new WithdrawCommand())
					.arguments(GenericArguments.flags()
							.valueFlag(GenericArguments.onlyOne(GenericArguments.choices(Text.of("taglio"), () -> { return Denominations.getDefinitions(); }, (x) -> { return x; }, true)), "d", "-denomination")
							.buildWith(GenericArguments.onlyOne(GenericArguments.integer(Text.of("valore"))))
							)
					.build();
			;
		}
		return spec;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(src instanceof Player) {
			final Player player = (Player)src;
			final int amount = args.<Integer>getOne("valore").get();
			if(args.hasAny("taglio")) {
				// Denomination flag
				if(TransactionUtil.withdraw(player, args.<String>getOne("taglio").get(), amount))
					return CommandResult.success();
			} else // Raw amount
			if(TransactionUtil.withdraw(player, amount))
				return CommandResult.success();
		}
		return CommandResult.empty();
	}

}
