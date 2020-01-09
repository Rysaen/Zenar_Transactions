package io.github.rysaen.zenartransactions.denominations;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

/**
 * Represents an association between a specific item and a denomination. Every
 * instance of this class is considered _Immutable_.
 * @author rysaen
 *
 */
public class Denomination {

	private String name;
	private int value;
	private String itemid;
	private String tostring = null;
	private Text displayName;
	private List<Text> lore;
	private ItemStackSnapshot snapshot = null;

	public Denomination(
			@Nonnull String name, 
			int value, 
			@Nonnull String itemid, 
			@Nullable Text displayName, 
			@Nullable Text[] loreLines) 
	{
		this.name = name;
		this.value = value;
		this.itemid = itemid;
		this.displayName = displayName;
		this.lore = null;
		if(loreLines != null) {
			this.lore = new LinkedList<>();
			for(Text ll : loreLines) this.lore.add(ll);
		}
	}
	
	/**
	 * Returns the display name, or null if not present.
	 * @return a string representing the display name, or null if not present.
	 */
	public Text getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * Returns lore lines associated with the denomination. May return null if
	 * no lore lines are defined.
	 * @return A stream of strings representing the lore lines, or null if no
	 * lore lines are found.
	 */
	public Stream<Text> getLore() {
		if(lore != null)
			return this.lore.stream();
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Denomination) {
			Denomination d = (Denomination)o;
			return (d.name.equals(name) || (d.value == value) || d.itemid.equals(itemid));
		}
		return false;
	}

	/**
	 * Returns the itemid associated with the denomination.
	 * @return a String containing the denomination item id.
	 */
	public String getItemId() {
		return itemid;
	}

	/**
	 * Returns the denomination name. Be wary that the given name is NOT the one
	 * displayed on the item. This is used internally or as a string required by
	 * the -(-d)enomination flag on withdraw command.
	 * 
	 * @return a String containing the denomination name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value.
	 * @return Integer representing the value.
	 */
	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		if(tostring == null)
			tostring = (new StringBuilder())
				.append('[')
				.append(name)
				.append(", ")
				.append(value)
				.append(", ")
				.append(itemid)
				.append(']')
				.toString();
		return tostring;
	}

	/**
	 * Returns the denomination ItemType optional.
	 * @return optional of ItemType.
	 */
	public Optional<ItemType> getItemType() {
		return Sponge.getRegistry().getType(ItemType.class, this.getItemId());
	}
	
	/**
	 * Returns an itemstack snapshot associated with the denomination.
	 * @return an itemstack snapshot.
	 */
	public ItemStackSnapshot getItemStackSnapshot() {
		if(snapshot == null)
			snapshot = ItemStack.builder()
				.itemType(this.getItemType().get())
				.add(Keys.DISPLAY_NAME, this.getDisplayName())
				.add(Keys.ITEM_LORE, lore)
				.quantity(1)
				.build()
				.createSnapshot();
		return snapshot;
	}
}
