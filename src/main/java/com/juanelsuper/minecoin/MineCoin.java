package com.juanelsuper.minecoin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.juanelsuper.minecoin.blocks.ATMBlock;
import com.juanelsuper.minecoin.inventory.ATMScreen;
import com.juanelsuper.minecoin.items.MinecoinItemGroup;
import com.juanelsuper.minecoin.lists.BlockList;
import com.juanelsuper.minecoin.lists.ContainersList;
import com.juanelsuper.minecoin.lists.ItemList;
import com.juanelsuper.minecoin.nbt.PlayerBalanceProperties;
import com.juanelsuper.minecoin.nbt.PlayerExtendedProperties;
import com.juanelsuper.minecoin.nbt.PropertiesDispatcher;
import com.juanelsuper.minecoin.network.Network;

@Mod(value = MineCoin.modid)
public class MineCoin
{
	public static MineCoin instance;
	public static final String modid = "minecoin";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static ItemGroup MINECOIN_GROUP = new MinecoinItemGroup();

    public MineCoin() {
		instance = this;
		final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
		eventBus.addListener(this::clientRegistries);
		
		MinecraftForge.EVENT_BUS.register(this);
		

	}
	
	private void setup(final FMLCommonSetupEvent event) 
	{
		//OreGeneration.setupOreGeneration();
		Network.registerMessages();
		CapabilityManager.INSTANCE.register(PlayerBalanceProperties.class, PlayerExtendedProperties.BALANCE_STORAGE, PlayerBalanceProperties::new);
		MinecraftForge.EVENT_BUS.register(new ForgeEvents());
	}

	private void clientRegistries(final FMLClientSetupEvent event) 
	{
		//RenderingRegistry.registerEntityRenderingHandler(GodsHammerEntity.class,GodsHammerRender :: new);
		ScreenManager.register(ContainersList.ATM_CONTAINER, ATMScreen::new);
	}

	
	
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    @ObjectHolder(modid)
	public static class RegistryEvents
	{

		@SubscribeEvent
		public static void registerItems(final RegistryEvent.Register<Item> event)
		{
			event.getRegistry()	.registerAll
			(
					ItemList.COIN = new Item(new Item.Properties().tab(MINECOIN_GROUP)).setRegistryName(location("coin")),
					ItemList.BILL_50 = new Item(new Item.Properties().tab(MINECOIN_GROUP)).setRegistryName(location("bill_50")),
					ItemList.BILL_10 = new Item(new Item.Properties().tab(MINECOIN_GROUP)).setRegistryName(location("bill_10")),
					ItemList.ATM = new BlockItem(BlockList.ATM, new Item.Properties().tab(MINECOIN_GROUP)).setRegistryName(BlockList.ATM.getRegistryName())
					//,
			);
		}
		@SubscribeEvent
		public static void registerBlocks(final RegistryEvent.Register<Block> event)
		{
			event.getRegistry()	.registerAll
			(
					BlockList.ATM = new ATMBlock(Block.Properties.copy(Blocks.BEDROCK)).setRegistryName(location("atm"))
			);
		}
		
		 @SubscribeEvent
		    public static void onRegisterSerializers(final RegistryEvent.Register<IRecipeSerializer<?>> event) {
		        //event.getRegistry().register(RecipieTypes.CHISELING_S.setRegistryName(new ResourceLocation(ChiselMod.modid, "chiseling")));
		    }
		 
		@SubscribeEvent
		//Only launches when is a runData launch
		public static void data(GatherDataEvent event) {
			/*final DataGenerator generator = event.getGenerator();
			if (event.includeServer()) {
				generator.addProvider(new ChiselLootTableProvider(generator));
			}*/
		}
		@SubscribeEvent
	    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
	        event.getRegistry().register(ContainersList.ATM_CONTAINER.setRegistryName(modid, "container_inv"));
	    }
		
		public static ResourceLocation location(String name) 
		{
			return new ResourceLocation(modid, name);
		}
	}
	public class ForgeEvents{
		@SubscribeEvent
		public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event) {
			if(event.getObject() instanceof PlayerEntity) {
				if (!event.getObject().getCapability(PlayerExtendedProperties.BALANCE).isPresent()) {
					event.addCapability(PlayerExtendedProperties.BALANCE_LOC, new PropertiesDispatcher());
				}
			}
		}

		@SubscribeEvent
	    public void onPlayerCloned(PlayerEvent.Clone event) {
	        if (event.isWasDeath()) {;
	        	PlayerEntity deathPlayer = event.getOriginal();
	        	LazyOptional<PlayerBalanceProperties> lazyProps = PlayerExtendedProperties.getBalance(deathPlayer);
	        	lazyProps.ifPresent(oldData -> {
	                PlayerExtendedProperties.getBalance(event.getPlayer()).ifPresent(balance -> {
	                	balance.set(oldData.get());
	                });
	            });
	        	lazyProps.invalidate();
	        	
	        }
	    }
	} 
}
