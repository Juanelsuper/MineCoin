package com.juanelsuper.minecoin.items;

import com.juanelsuper.minecoin.lists.ItemList;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MinecoinItemGroup extends ItemGroup{
	public MinecoinItemGroup() {
		super("minecoin");
	}
	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ItemList.COIN);
	}
}
