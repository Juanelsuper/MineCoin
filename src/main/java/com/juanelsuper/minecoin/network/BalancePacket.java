package com.juanelsuper.minecoin.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import java.util.function.Supplier;

import com.juanelsuper.minecoin.MineCoin;
import com.juanelsuper.minecoin.inventory.ATMContainer;
import com.juanelsuper.minecoin.nbt.PlayerBalanceProperties;
import com.juanelsuper.minecoin.nbt.PlayerExtendedProperties;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class BalancePacket extends AbstractPacket<Integer> {
	
	
	public BalancePacket(PacketBuffer buf) {
		super(buf);
	}
	public BalancePacket(Integer data) {
		super(data);
	}
	
	@Override
	public Integer readPacket(PacketBuffer buf) {
		return buf.readInt();
	}
	@Override
	public void toBytes(PacketBuffer buf) {
        buf.writeInt(data);
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleClient(Supplier<Context> ctx) {
		Minecraft minecraft = Minecraft.getInstance();
		PlayerEntity player = minecraft.player;
    	PlayerExtendedProperties.getBalance(player).ifPresent(balance -> {
    		balance.set(this.data);
		});
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
		PlayerExtendedProperties.getBalance(player).ifPresent(balance -> {
			Container container = player.containerMenu;
			if(!(container instanceof ATMContainer)) {
				return;
			}
			ATMContainer atm = (ATMContainer)container;
    		if(data < 0) {
    			balance.add(data);
    			if(data < 0) {
    				atm.withdrawCoins(data*-1);
    			}
    		}
    		getBalance(balance, player);
		});
	}
	public void getBalance(PlayerBalanceProperties balance, ServerPlayerEntity player) {
		this.data = balance.get();
		MineCoin.LOGGER.info("$" + this.data + " is the new balance of " + player.getName().getString());
		this.sync(player);
	}
}
