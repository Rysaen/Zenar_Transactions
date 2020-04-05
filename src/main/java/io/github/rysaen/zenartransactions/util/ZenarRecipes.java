package io.github.rysaen.zenartransactions.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.plugin.PluginContainer;

import io.github.rysaen.zenartransactions.ZenarLogger;
import io.github.rysaen.zenartransactions.denomination.Denomination;
import io.github.rysaen.zenartransactions.denomination.Denominations;

public class ZenarRecipes {
	public static List<CraftingRecipe> processRecipes(PluginContainer plugin) {
		Map<Integer, Denomination> map = Denominations.getDenominations();
		Denomination den, denS;
		List<CraftingRecipe> ret = new LinkedList<>();
		// TODO Optimize
		// TODO Generalize?
		for(int i=map.size()-1; i>0; --i) {
			if((den = map.get(i)) != null && (denS = map.get(i-1)) != null) {
				if(denS.getValue()/den.getValue() != 8 || denS.getValue()%den.getValue()!=0) continue;
				if(!den.getItemType().isPresent() || !denS.getItemType().isPresent()) {
					ZenarLogger.get().error("Cannot retrieve ItemType for {} or {}", den.getName(), denS.getName());
					continue;
				}
				// Creating ItemStack
				ItemStack is = denS.getItemStackSnapshot().createStack();
				is.setQuantity(1);
				// Adding recipes to list
				ret.add(ShapedCraftingRecipe.builder()
					.aisle("xxx", "x x", "xxx")
					.where('x', Ingredient.of(den.getItemType().get()))
					.result(is.copy())
					.group("zenar")
					.build((denS.getName()+den.getName()+"recipe"), plugin)
				);
				ZenarLogger.get().debug("Added crafting recipe for denomination {} using {}.", denS.getName(), den.getName());
				//
				if(den.getItemType().get().getMaxStackQuantity() < 8) continue;
				is = den.getItemStackSnapshot().createStack();
				is.setQuantity(8);
				ret.add(ShapedCraftingRecipe.builder()
					.aisle("x")
					.where('x', Ingredient.of(denS.getItemType().get()))
					.result(is.copy())
					.group("zenar")
					.build((den.getName()+denS.getName()+"recipe"), plugin)
				);
				ZenarLogger.get().debug("Added crafting recipe for denomination {} using {}.", den.getName(), denS.getName());
			}
		}
		ZenarLogger.get().debug("Successfully processed {} recipes.", ret.size());
		return ret;
	}
}
