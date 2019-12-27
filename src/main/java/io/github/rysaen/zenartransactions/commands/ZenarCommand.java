package io.github.rysaen.zenartransactions.commands;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class ZenarCommand {
	
	private static CommandSpec spec;

	public static CommandSpec build() {
		if(spec == null) {
			spec = CommandSpec.builder()
				.description(Text.of("Mette a disposizione una serie di sotto-comandi specifici per le transazioni tra zenar fisici e conto virtuale."))
				.child(WithdrawCommand.build(), "withdraw")
				.child(DepositCommand.build(), "deposit")
				.build()
			;

		}
		return spec;
	}

}
