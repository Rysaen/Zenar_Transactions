package io.github.rysaen.zenartransactions.commands;

import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class ZenarCommand {
	
	private static CommandSpec spec;

	public static CommandSpec build() {
		if(spec == null) {
			spec = CommandSpec.builder()
				.description(Text.of("Zenar")) // TODO Zenar command description
				.child(WithdrawCommand.build(), "withdraw")
				.child(DepositCommand.build(), "deposit")
				.build()
			;

		}
		return spec;
	}

}
