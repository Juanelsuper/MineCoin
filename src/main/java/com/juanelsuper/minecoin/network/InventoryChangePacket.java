package com.juanelsuper.minecoin.network;

import com.juanelsuper.minecoin.inventory.ATMContainer;
import com.juanelsuper.minecoin.nbt.PlayerExtendedProperties;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class InventoryChangePacket extends AbstractPacket<ItemStack> {

	public InventoryChangePacket(ItemStack data) {
		super(data);
	}
	public InventoryChangePacket(PacketBuffer buf) {
		super(buf);
	}

	@Override
	public void toBytes(PacketBuffer buf) {
		buf.writeItem(data);
		
	}

	@Override
	public ItemStack readPacket(PacketBuffer buf) {
		return buf.readItem();
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
		PlayerExtendedProperties.getBalance(player).ifPresent(balance -> {
			Container container = player.containerMenu;
			if(container instanceof ATMContainer) {
				ATMContainer atm = (ATMContainer)container;
				int amount = atm.depositCoins();
				balance.add(amount);
				BalancePacket returningPacket = new BalancePacket(0);
				returningPacket.getBalance(balance, player);
			}
		});
		
	}

}
