package com.juanelsuper.minecoin.nbt;

import net.minecraft.nbt.CompoundNBT;

public class PlayerBalanceProperties {
	
	private int balance;
	
	public void set(int amount) {
		balance = amount;
	}
	public void add(int amount) {
		balance += amount;
	}
	public void remove(int amount) {
		balance -= amount;
	}
	public int get() {
		return balance;
	}
	
	public void saveNBTData(CompoundNBT compound) {
        compound.putInt("balance", get());
    }
	public void loadNBTData(CompoundNBT nbt) {
		set(nbt.getInt("balance"));
	}

}
