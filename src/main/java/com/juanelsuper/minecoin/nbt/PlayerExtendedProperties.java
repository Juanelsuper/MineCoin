package com.juanelsuper.minecoin.nbt;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerExtendedProperties {
	
	@CapabilityInject(PlayerBalanceProperties.class)
    public static Capability<PlayerBalanceProperties> BALANCE;
	
	public static LazyOptional<PlayerBalanceProperties> getBalance(PlayerEntity player){
		return player.getCapability(BALANCE);
	}
}
