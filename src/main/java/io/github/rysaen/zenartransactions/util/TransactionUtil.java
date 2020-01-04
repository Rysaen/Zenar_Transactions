package io.github.rysaen.zenartransactions.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import io.github.rysaen.zenartransactions.ZenarLogger;
import io.github.rysaen.zenartransactions.ZenarPlugin;
import io.github.rysaen.zenartransactions.denominations.Denomination;
import io.github.rysaen.zenartransactions.denominations.Denominations;

public class TransactionUtil {
//	private static final Integer MAX_STACK_SIZE = 64;
	private static final EventContext transactionsContext = EventContext.builder()
			.add(EventContextKeys.PLUGIN, Sponge.getPluginManager().getPlugin(ZenarPlugin.ID).get())
			.build();

	private static int[] assignAutomaticDenominations(int value) {
		int[] ret = new int[Denominations.count()];
		Map<Integer, Denomination> dmap = Denominations.getDenominations();
		for(int i = 0; i < ret.length; ++i) {
			if(value <= 0) break;
			ret[i] = value/dmap.get(i).getValue();
			value = value%dmap.get(i).getValue();
		}
		return ret;
	}

	public static boolean deposit(Player player) {
		final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
		UniqueAccount acc = getPlayerAccount(player, es);

		Inventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class)).query(Denominations.getQueryOperations());
		List<ItemStack> backup = new LinkedList<>();
		Optional<ItemStack> curr;
		BigDecimal amount = BigDecimal.ZERO;

		while((curr = inv.poll()).isPresent()) {
			amount = amount.add(getTotalValue(curr.get().getType().getId(), curr.get().getQuantity()));
			backup.add(curr.get());
		}

		TransactionResult result =
				acc.deposit(es.getDefaultCurrency(), amount, Cause.of(transactionsContext, Sponge.getPluginManager().getPlugin(ZenarPlugin.ID).get()));

		if(result.getResult() == ResultType.SUCCESS) {
			player.sendMessage(Text.of("Hai depositato con successo ")
					.concat(Text.of(TextColors.GREEN, amount.toString()))
					.concat(Text.of(" zenar. Bilancio attuale: "))
					.concat(Text.of(TextColors.GOLD, acc.getBalance(es.getDefaultCurrency()))));
			ZenarLogger.get().info("Deposit > {} deposited {} zenar on his virtual account.", player.getName(), amount);
			return true;
		}

		player.sendMessage(Text.of("Non è stato possibile completare la transazione per cause non specificate. Se l'errore persiste, contattare un admin."));
		for(ItemStack is : backup)
			player.getInventory().offer(is);
		return false;
	}

	private static UniqueAccount getPlayerAccount(Player player, EconomyService es) {
		Optional<UniqueAccount> aopt = es.getOrCreateAccount(player.getUniqueId());

		if(!aopt.isPresent()) {
			player.sendMessage(Text.of(TextColors.RED, "Impossibile recuperare l'account associato."));
			ZenarLogger.get().error("Couldn't retrieve the player account for {}.", player.getName());
			return null;
		}

		return aopt.get();
	}

	private static BigDecimal getTotalValue(String itemid, int value) {
		Denomination tmp;
		if((tmp = Denominations.getDenominationByItemId(itemid)) != null)
			return BigDecimal.valueOf(tmp.getValue()).multiply(BigDecimal.valueOf(value));
		return BigDecimal.ZERO;
	}

	public static BigDecimal inventoryPeek(Player player) {
		Inventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class)).query(Denominations.getQueryOperations());
		BigDecimal amount = BigDecimal.ZERO;
		Optional<ItemStack> curr;
		for(Inventory slot : inv.slots()) {
			curr = slot.peek();
			if(curr.isPresent())
				amount = amount.add(getTotalValue(curr.get().getType().getId(),curr.get().getQuantity()));
		}
		return amount;
	}

	private static BigDecimal totalTransactionValue(int ...d) {
		BigDecimal amount = BigDecimal.ZERO;
		for(int i = 0; i < d.length; ++i)
			amount = amount.add(getTotalValue(Denominations.getDenominations().get(i).getItemId(), d[i]));
		return amount;
	}



	public static boolean withdraw(Player player, int value) {
		return withdraw(player, assignAutomaticDenominations(value));
	}

	//	private static int arraySum(int ...d) {
	//		int sum = 0;
	//		for(int i=0; i < d.length; sum += ((d[i]/MAX_STACK_SIZE) + ((d[i]%MAX_STACK_SIZE!=0)? 1 : 0)), ++i);
	//		return sum;
	//	}

	private static boolean withdraw(Player player, int ...d) {
		final EconomyService es = Sponge.getServiceManager().provideUnchecked(EconomyService.class);
		UniqueAccount acc = getPlayerAccount(player, es);
		BigDecimal amountToTransfer;

		amountToTransfer = totalTransactionValue(d);
		TransactionResult res = acc.withdraw(es.getDefaultCurrency(), amountToTransfer, Cause.of(transactionsContext, Sponge.getPluginManager().getPlugin(ZenarPlugin.ID)));
		if(res.getResult() != ResultType.SUCCESS) {
			player.sendMessage(Text.of(TextColors.RED, "Non è stato possibile completare la transazione. Se l'errore persiste, contattare un admin."));
			return false;
		}

		BigDecimal amountTransferred = BigDecimal.ZERO;
		ItemStack is;
		boolean _interrupted = false;
		Inventory playerInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
		Optional<ItemType> it;
		Denomination den;
		for(int i = 0; i < d.length; ++i)
		{
			den = Denominations.getDenominations().get(i);
			it = Sponge.getRegistry().getType(ItemType.class, den.getItemId());
			
			// Se il taglio non è presente, viene skippato e sulla console viene
			// stampato un messaggio d'errore.
			if(!it.isPresent()) {
				ZenarLogger.get().error("L'itemid {} utilizzato nella denominations {}, non è presente nel registro!", Denominations.getDenominations().get(i).getItemId(), Denominations.getDenominations().get(i).getItemId());
				continue;
			}
			
			// Building Stack
			is = ItemStack.builder().itemType(it.get()).quantity(it.get().getMaxStackQuantity()).build();
			if(den.getLore() != null) is.offer(Keys.ITEM_LORE, den.getLore().collect(Collectors.toList()));
			if(den.getDisplayName() != null) is.offer(Keys.DISPLAY_NAME, den.getDisplayName());
			
			for(int j = 0; j < (d[i]/it.get().getMaxStackQuantity()); ++j)
				if(playerInventory.canFit(is)) {
					playerInventory.offer(is.copy());
					amountTransferred = amountTransferred.add(BigDecimal.valueOf(den.getValue()*is.getQuantity()));
				}
				else {
					_interrupted = true;
					break;
				}
			if(_interrupted)
				break;
			is.setQuantity(d[i]%it.get().getMaxStackQuantity());
			if(playerInventory.canFit(is)) {
				playerInventory.offer(is.copy());
				amountTransferred = amountTransferred.add(BigDecimal.valueOf(den.getValue()*is.getQuantity()));
			} else {
				_interrupted = true;
				break;
			}
		}

		if(_interrupted) {
			acc.deposit(es.getDefaultCurrency(), amountToTransfer.subtract(amountTransferred), Cause.of(transactionsContext, Sponge.getPluginManager().getPlugin(ZenarPlugin.ID)));
			player.sendMessage(Text.of(TextColors.GRAY, "Non hai abbastanza spazio nell'inventario per prelevare l'intera somma specificata. Ti sono stati consegnati ")
					.concat(Text.of(TextColors.GOLD, amountTransferred))
					.concat(Text.of(TextColors.GRAY, " zenar (anziché " + amountToTransfer + ")")));
		} else
			player.sendMessage(Text.of("Hai ritirato ")
					.concat(Text.of(TextColors.GREEN, amountTransferred))
					.concat(Text.of(" zenar dal tuo conto virtuale (che ora ammonta a "))
					.concat(Text.of(TextColors.GOLD, acc.getBalance(es.getDefaultCurrency()))
							.concat(Text.of(" zenar)"))));
		ZenarLogger.get().info("Withdraw > {} wihdrawn {} zenar from his virtual account.", player.getName(), amountTransferred);
		return true;
	}

	public static boolean withdraw(Player player, String denomination, Integer value) {
		int[] array = new int[Denominations.count()];
		Arrays.fill(array, 0);

		int id = Denominations.getDenominationIdByName(denomination);
		if(id < 0) {
			player.sendMessage(Text.of(TextColors.RED, "Il taglio " + denomination + " non esiste. Non è stato possibile concludere l'operazione."));
			return false;
		}
		array[id] = value;

		return withdraw(player, array);
	}
}
