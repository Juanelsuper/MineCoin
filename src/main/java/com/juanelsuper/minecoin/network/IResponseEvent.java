package com.juanelsuper.minecoin.network;

public interface IResponseEvent<T> {
	void excecute(AbstractPacket<T> packet);
}
