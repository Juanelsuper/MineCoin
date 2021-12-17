package com.juanelsuper.minecoin.network;

import com.juanelsuper.minecoin.MineCoin;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {
	 public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
        		new ResourceLocation(MineCoin.modid, "minecoin_network"), () -> "1.0", s -> true, s -> true);
        INSTANCE.registerMessage(
        		nextID(), BalancePacket.class, BalancePacket::toBytes, BalancePacket::new, BalancePacket::handle);
        INSTANCE.registerMessage(
        		nextID(), InventoryChangePacket.class, InventoryChangePacket::toBytes, InventoryChangePacket::new, InventoryChangePacket::handle);
    }

    public static SimpleChannel getInstance() {
        return INSTANCE;
    }
}
