package viso.game.framework.net;

import java.util.Properties;

public class ProtocolAcceptor {
	
	Transport transport;
	
	public ProtocolAcceptor(Properties property){
		transport = new Transport(property);
	}
	
	public void accept(ConnectionHandler listener){
		transport.accept(listener);
	}
}
