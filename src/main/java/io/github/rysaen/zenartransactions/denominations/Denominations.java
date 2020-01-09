package io.github.rysaen.zenartransactions.denominations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

public final class Denominations {

	private static List<Denomination> table = new ArrayList<>();
	private static QueryOperation<?>[] queries = null;
	private static Collection<String> definitions = null;
	private static Map<Integer,Denomination> map = null;

	private static boolean _qoutdateflag = false;
	private static boolean _defoutdateflag = false;
	private static boolean _mapoutdateflag = false;

	/**
	 * Returns the number of denominations currently registered.
	 * @return the number of denominations present.
	 */
	public static int count() {
		return table.size();
	}

	/**
	 * Returns the definition associated with each denomination. Used internally
	 * to supply the identifiers list when needed.
	 * @return a {@link Collection} containing the denominations ids.
	 */
	public static Collection<String> getDefinitions() {
		if(_defoutdateflag) {
			definitions = table.stream()
					.map(x -> { return x.getName(); })
					.collect(Collectors.toList())
			;
			_defoutdateflag = false;
		}
		return definitions;
	}

	/**
	 * Returns the {@link Denomination} associated with the given Item ID.
	 * @param itemid of the denomination.
	 * @return the associated {@link Denomination} on success, null otherwise.
	 */
	public static Denomination getDenominationByItemId(String itemid) {
		int i;
		for(i = 0; i < table.size(); ++i)
			if(table.get(i).getItemId().equals(itemid))
				return table.get(i);
		return null;
	}

	/**
	 * Returns the internal ID of the denomination associated with the given
	 * name.
	 * @param name
	 * @return the internal index of the associated denomination, -1 otherwise.
	 */
	public static int getDenominationIdByName(String name) {
		int i;
		for(i = 0; i < table.size(); ++i)
			if(table.get(i).getName().equals(name))
				return i;
		return -1;
	}

	/**
	 * Returns a map containing the id associated with a denomination in the
	 * internal table, and the denomination itself.
	 * @return a {@link Map} of ids and denominations.
	 */
	public static Map<Integer,Denomination> getDenominations() {
		if(_mapoutdateflag) {
			map = IntStream.range(0, table.size())
					.boxed()
					.collect(Collectors.toMap(i -> i, table::get))
			;
			_mapoutdateflag = false;
		}
		return map;
	}

	/**
	 * Returns an array of {@link QueryOperation}s used to filter the items from
	 * the inventory associated with a denomination.
	 * @return an array of {@link QueryOperation}s.
	 */
	public static QueryOperation<?>[] getQueryOperations() {
		if(_qoutdateflag) {
			queries = new QueryOperation<?>[table.size()];
			for(
					int i = 0;
					i < table.size();
					queries[i] = QueryOperationTypes.ITEM_TYPE.of(Sponge.getRegistry().getType(ItemType.class, table.get(i).getItemId()).get()),
					++i
			);
			_qoutdateflag = false;
		}
		return queries;
	}

	/**
	 * Adds the denominations to the internal register
	 * @param d A {@link Denomination} instance.
	 * @return true on success, false otherwise.
	 */
	public static boolean supply(Denomination d) {
		if(table.contains(d))
			return false;
		int i;
		for(i = 0; (i < table.size()) && (table.get(i).getValue() > d.getValue()); ++i);
		table.add(i, d);
		// Update flags
		_qoutdateflag = true;
		_defoutdateflag = true;
		_mapoutdateflag = true;
		return true;
	}

	/**
	 * Registers a new denomination with the specified values.
	 * @param id
	 * @param value
	 * @param itemid used by the denomination.
	 * @param displayName
	 * @param lore
	 * @return true on success, false otherwise.
	 */
	public static boolean supply(String id, int value, String itemid, Text displayName, Text ...lore) {
		return Denominations.supply(new Denomination(id, value, itemid, displayName, lore));
	}

	private Denominations() {}
}
