package com.juanelsuper.minecoin.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class AbstractPacket<T> {
	
	protected T data;
	
	
	public abstract void toBytes(PacketBuffer buf);
	
	public AbstractPacket(PacketBuffer buf) {
		this.data = this.readPacket(buf);
	}

    public AbstractPacket(T data) {
        this.data = data;
    }
    
    public abstract T readPacket(PacketBuffer buf);
    
    @OnlyIn(Dist.CLIENT)
    public void handleClient(Supplier<NetworkEvent.Context> ctx) {
    	
    } 
    public abstract void handleServer(ServerPlayerEntity player);
    
    
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
    	if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
    		handleClient(ctx);
    	}
    	else {
    		ServerPlayerEntity player = ctx.get().getSender();
    		if(player != null) {
    			ctx.get().enqueueWork(
	            () -> {
	            	handleServer(player);
	            });
    		}
    	}
    	ctx.get().setPacketHandled(true);
    }
    
    protected void sync(ServerPlayerEntity player) {
    	if(player != null) {
    		Network.INSTANCE.sendTo(this, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    	}
    }
    

}
