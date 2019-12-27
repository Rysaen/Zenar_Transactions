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

import io.github.rysaen.zenartransactions.denominations.Denominations;
import io.github.rysaen.zenartransactions.util.TransactionUtil;


public class WithdrawCommand implements CommandExecutor  {
	
	private static CommandSpec spec;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(src instanceof Player) {
			final Player player = (Player)src;
			final int amount = args.<Integer>getOne("valore").get();
			if(args.hasAny("taglio")) {
				// Denomination flag
				if(TransactionUtil.withdraw(player, args.<String>getOne("taglio").get(), amount))
					return CommandResult.success();
			} else {
				// Raw amount
				if(TransactionUtil.withdraw(player, amount))
					return CommandResult.success();
			}
		}
		return CommandResult.empty();
	}

	public static CommandSpec build() {
		if(spec == null) {
			spec = CommandSpec.builder()
				.description(Text.of("Withdraw")) // TODO Withdraw Description
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

}
