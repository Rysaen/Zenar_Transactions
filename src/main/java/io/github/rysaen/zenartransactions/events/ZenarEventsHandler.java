package io.github.rysaen.zenartransactions.events;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import io.github.rysaen.zenartransactions.denominations.Denominations;

public class ZenarEventsHandler {
	private static final String EnderChestString = "Ender Chest";
	
	@Listener
	public void onInventoryChange(ChangeInventoryEvent evt) {
		if (evt.getTargetInventory().getName().getId().equals(EnderChestString)) {
			for (SlotTransaction st : evt.getTransactions()) {
				if (Denominations.getDenominationByItemId(st.getFinal().getType().getId()) != null
						|| Denominations.getDenominationByItemId(st.getOriginal().getType().getId()) != null) {
					evt.setCancelled(true);
				}
			}
			System.out.println(evt);
		}
	}
}
