package com.juanelsuper.minecoin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.juanelsuper.minecoin.lists.ItemList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CoinableItem {
	
	private static final Map<Item, CoinableItem> ACCEPTED_ITEMS = new HashMap<Item, CoinableItem>() {
		private static final long serialVersionUID = 1588106081523586016L;
	{
			put(Items.NETHERITE_INGOT, new CoinableItem(1, 10));
			put(Items.DIAMOND, new CoinableItem(1, 6));
			put(Items.GOLD_INGOT, new CoinableItem(2, 1));
			put(Items.COAL, new CoinableItem(64, 1));
			put(ItemList.BILL_50, new CoinableItem(1, 50));
			put(ItemList.BILL_10, new CoinableItem(1, 10));
			put(ItemList.COIN, new CoinableItem(1, 1));
	}};
	
	private final int amount;
	private final int conversion; //<amount> equals <conversion> of money
	
	private CoinableItem(int amount, int conversion) {
		this.amount = amount;
		this.conversion = conversion;
	}
	
	public static ItemStack[] getCoins(int amount) {// 50, 10, 1
		ItemStack[] output = new ItemStack[3];
		int _50 = (int)Math.floor(amount / 50);
		amount -= 50*_50;
		int _10 = (int)Math.floor((amount) / 10);
		amount -= 10*_10;
		output[0] = new ItemStack(ItemList.BILL_50, _50);
		output[1] = new ItemStack(ItemList.BILL_10, _10);
		output[2] = new ItemStack(ItemList.COIN, amount);
		return output;
	}
	public static int convertToEconomy(ItemStack itemStack) {
		CoinableItem coinableItem = ACCEPTED_ITEMS.get(itemStack.getItem());
		if(coinableItem != null) {
			int count = itemStack.getCount(); // = 3
			int amount = (int)Math.floor(count/coinableItem.amount);
			itemStack.setCount(count - (amount*coinableItem.amount));
			return amount * coinableItem.conversion;
		}
		return 0;
	}
	public static Set<Item> getAcceptedItems() {
		return ACCEPTED_ITEMS.keySet();
	}
}
