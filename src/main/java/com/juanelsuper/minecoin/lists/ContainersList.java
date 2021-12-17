package com.juanelsuper.minecoin.lists;

import com.juanelsuper.minecoin.inventory.ATMContainer;

import net.minecraft.inventory.container.ContainerType;

public class ContainersList {
	public static final ContainerType<ATMContainer> ATM_CONTAINER = new ContainerType<ATMContainer>(ATMContainer::new);
}
