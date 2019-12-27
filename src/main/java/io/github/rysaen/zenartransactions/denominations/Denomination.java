package io.github.rysaen.zenartransactions.denominations;

import javax.annotation.Nonnull;

public class Denomination {

	private String name;
	private int value;
	private String itemid;
	private String tostring;
	
	public Denomination(@Nonnull String name, int value, @Nonnull String itemid) {
		this.name = name;
		this.value = value;
		this.itemid = itemid;
		this.tostring = (new StringBuilder())
				.append("Denomination: { name=")
				.append(name)
				.append(", value=")
				.append(value)
				.append(", itemid=")
				.append(itemid)
				.append(" }")
				.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getItemId() {
		return itemid;
	}
	
	@Override
	public String toString() {
		return tostring;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Denomination) {
			Denomination d = (Denomination)o;
			return (d.name.equals(name) || d.value == value || d.itemid.equals(itemid));
		}
		return false;
	}
}
