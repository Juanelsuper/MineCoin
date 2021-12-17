package com.juanelsuper.minecoin.inventory;

import javax.annotation.Nullable;

import com.juanelsuper.minecoin.lists.ContainersList;
import com.juanelsuper.minecoin.nbt.PlayerExtendedProperties;
import com.juanelsuper.minecoin.network.BalancePacket;
import com.juanelsuper.minecoin.network.InventoryChangePacket;
import com.juanelsuper.minecoin.network.Network;
import com.juanelsuper.minecoin.utils.CoinableItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ATMContainer extends Container{
	
	public static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent("container.atm");
	

    
    private Slot inSlot, out50, out10, out1; //TODO: make out an array
    @Nullable
    private Integer currentBalance;
    
	public ATMContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.NULL);
    }

	public ATMContainer(int id, PlayerInventory playerInventory, IWorldPosCallable world) {
		super(ContainersList.ATM_CONTAINER, id);
		loadBalance(playerInventory.player);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
        inSlot = addSlot(new Slot(playerInventory, 36, 116, 41) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return CoinableItem.getAcceptedItems().contains(stack.getItem());
			}
        });
        out50 = addSlot(new Slot(playerInventory, 37, 98, 7) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
        });
        out10 = addSlot(new Slot(playerInventory, 38, 116, 7){
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
        });
        out1 = addSlot(new Slot(playerInventory, 39, 134, 7){
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
        });
	}
	
	
	@Override
	public boolean clickMenuButton(PlayerEntity player, int id) {
		System.out.println(id);
		return super.clickMenuButton(player, id);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
	
	public void loadBalance(PlayerEntity player) {
		if(player.level.isClientSide()) {
			Network.INSTANCE.sendToServer(new BalancePacket(0));
        }
	}
	public int getBalance(PlayerEntity player) {
		//if(this.currentBalance == null) { //TODO: optimize this
			PlayerExtendedProperties.getBalance(player).ifPresent(balance -> {
				currentBalance = balance.get();
			});
			//return 0;
		//}
		return this.currentBalance;
	}
	
	@OnlyIn(Dist.CLIENT)
	private void changeBalance(int amount) {
		if(this.currentBalance != null && amount != 0) {
			if(amount > 0) {
				Network.INSTANCE.sendToServer(new InventoryChangePacket(inSlot.getItem()));
			}
			else {
				Network.INSTANCE.sendToServer(new BalancePacket(amount));
			}
			this.currentBalance += amount;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void deposit() { //TODO: remove items from server
		if(inSlot.hasItem()) {
			int aux_count = inSlot.getItem().getCount();
			int amount = depositCoins();
			int temp = inSlot.getItem().getCount();
			inSlot.getItem().setCount(aux_count);
			this.changeBalance(amount);
			inSlot.getItem().setCount(temp);
		}
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public void withdraw(int amount) { //TODO: add items from server
		if(amount <= 3200 && this.currentBalance >= amount && this.outIsEmpty()) {
			this.changeBalance(amount*-1);
			this.withdrawCoins(amount);
		}
	}

	public void withdrawCoins(int amount) {
		ItemStack[] coins = CoinableItem.getCoins(amount);
		out50.set(coins[0]);
		out10.set(coins[1]);
		out1.set(coins[2]);
	}
	
	
	public int depositCoins() {
		return CoinableItem.convertToEconomy(inSlot.getItem());
	}
	
	
	public void dropItemFromSlot(PlayerEntity player, Slot slot) {
		if(slot.hasItem()) {
			player.drop(slot.getItem(), false);
		}
		slot.set(ItemStack.EMPTY);
	}
	
	@Override
	public void removed(PlayerEntity player) {
		dropItemFromSlot(player, inSlot);
		dropItemFromSlot(player, out50);
		dropItemFromSlot(player, out10);
		dropItemFromSlot(player, out1);
	}
	
	private boolean isCustomSlot(Slot slot) {
		return slot == this.inSlot || slot == this.out1 || slot == this.out10 || slot == this.out50;
	}
	
	public boolean outIsEmpty() {
		return !this.out1.hasItem() && !this.out10.hasItem() && !this.out50.hasItem();
	}
	
	private boolean setItemInSlot(Slot previous_slot, Slot newSlot) {
		ItemStack item = previous_slot.getItem();
		if(newSlot.hasItem()) {
			ItemStack currentItem = newSlot.getItem();
			if(currentItem.getItem() == item.getItem()) {
				int currentItemCount = currentItem.getCount();
				int amount = Math.min(currentItemCount + item.getCount(), currentItem.getMaxStackSize());
				currentItem.setCount(amount);
				item.setCount(item.getCount() - (amount -currentItemCount));
			}
		}
		else {
			previous_slot.set(ItemStack.EMPTY);
			newSlot.set(item);
			return true;
		}
		return item.isEmpty();
	}
	
	private void moveToFirstEmptySlot(Slot current_slot) {
		for(Slot slot: this.slots) {
			if(!isCustomSlot(slot) && slot.hasItem() && setItemInSlot(current_slot, slot)) {
				return;
			}
		}
		for(Slot slot: this.slots) {
			if(!isCustomSlot(slot) && !slot.hasItem() && setItemInSlot(current_slot, slot)) {
				return;
			}
		}
	}
	
	@Override
	public ItemStack clicked(int slot_id, int button_number, ClickType type, PlayerEntity player) {
		if(type == ClickType.QUICK_MOVE) {
			Slot selectedSlot = this.getSlot(slot_id);
			ItemStack item = this.getSlot(slot_id).getItem();
			if(!isCustomSlot(selectedSlot)) {
				if(CoinableItem.getAcceptedItems().contains(item.getItem())) {
					setItemInSlot(selectedSlot, this.inSlot);
				}
			}
			else {
				moveToFirstEmptySlot(selectedSlot);
			}
			return item;
		}
		return super.clicked(slot_id, button_number, type, player);
	}
	
	
	
}
