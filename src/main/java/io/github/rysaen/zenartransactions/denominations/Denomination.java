package io.github.rysaen.zenartransactions.denominations;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.api.text.Text;

public class Denomination {

	private String name;
	private int value;
	private String itemid;
	private String tostring;
	private Text displayName;
	private List<Text> lore;

	public Denomination(@Nonnull String name, int value, @Nonnull String itemid, @Nullable Text displayName, @Nullable Text[] loreLines) {
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
}
