package com.juanelsuper.minecoin.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class BalanceCapStorage implements Capability.IStorage<PlayerBalanceProperties>{

	@Override
	public INBT writeNBT(Capability<PlayerBalanceProperties> capability, PlayerBalanceProperties instance, Direction side) {
		CompoundNBT tag = new CompoundNBT();
		instance.saveNBTData(tag);
		return tag;
	}

	@Override
	public void readNBT(Capability<PlayerBalanceProperties> capability, PlayerBalanceProperties instance, Direction side, INBT nbt) {
		instance.loadNBTData((CompoundNBT)nbt);
	}

}
