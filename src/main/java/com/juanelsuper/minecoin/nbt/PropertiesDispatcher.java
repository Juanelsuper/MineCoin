package com.juanelsuper.minecoin.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PropertiesDispatcher implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
	
	private PlayerBalanceProperties playerBalanceProperties = new PlayerBalanceProperties();

	private LazyOptional<PlayerBalanceProperties> playerBalance = LazyOptional.of(() -> playerBalanceProperties);
	 
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == PlayerExtendedProperties.BALANCE) {
			return playerBalance.cast();
		}
		return LazyOptional.empty();
	}
	
	 @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        playerBalanceProperties.saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
    	playerBalanceProperties.loadNBTData(nbt);
    }
    

}
