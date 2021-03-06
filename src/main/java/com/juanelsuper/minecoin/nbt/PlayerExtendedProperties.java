package com.juanelsuper.minecoin.nbt;

import com.juanelsuper.minecoin.MineCoin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerExtendedProperties {
	
	@CapabilityInject(PlayerBalanceProperties.class)
    public static Capability<PlayerBalanceProperties> BALANCE;
	
	public static final ResourceLocation BALANCE_LOC = MineCoin.RegistryEvents.location("balance");
	
	 public static final BalanceCapStorage BALANCE_STORAGE = new BalanceCapStorage();
	
	public static LazyOptional<PlayerBalanceProperties> getBalance(PlayerEntity player){
		return player.getCapability(BALANCE);
	}
}
