package io.github.rysaen.zenartransactions.denomination;

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
import org.spongepowered.api.text.serializer.TextSerializers;

public class Denomination {

	private String name;
	private int value;
	private String itemid;
	private String tostring;
	private Text displayName;
	private List<Text> lore;
	private ItemStackSnapshot snapshot = null;

	public Denomination(@Nonnull String name, int value, @Nonnull String itemid, @Nullable String displayName, @Nullable List<String> loreLines) {
		this.name = name;
		this.value = value;
		this.itemid = itemid;
		tostring = (new StringBuilder())
				.append("Denomination: { name=")
				.append(name)
				.append(", value=")
				.append(value)
				.append(", itemid=")
				.append(itemid)
				.append(" }")
				.toString();
		this.displayName = Text.of(TextSerializers.FORMATTING_CODE.deserialize(displayName));
		this.lore = null;
		if (loreLines != null) {
			this.lore = new LinkedList<>();
			for (String ll : loreLines) {
				this.lore.add(Text.of(TextSerializers.FORMATTING_CODE.deserialize(ll)));
			}
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

	public String getItemId() {
		return itemid;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return tostring;
	}

	public Optional<ItemType> getItemType() {
		return Sponge.getRegistry().getType(ItemType.class, this.getItemId());
	}
	
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
