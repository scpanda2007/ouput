package viso.sbeans.framework.net;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import viso.com.util.NamedThreadFactory;
import viso.sbeans.framework.net.TcpTransport.ConnectionListener;
import viso.sbeans.framework.protocol.SessionProtocolAcceptor;

/**服务端口底层网络协议接收器*/
public class ProtocolAcceptor {
	
	TcpTransport transport;
	ExecutorService executor;
	
	public ProtocolAcceptor(Properties property){
		transport = new TcpTransport(property);
		executor = Executors.newFixedThreadPool(4, new NamedThreadFactory("acceptor"));
	}
	
	public void accept(SessionProtocolAcceptor listener){
		transport.accept(new ConnectionListenerImpl(listener));
	}
	
	public void scheduledNonTransactionTask(Runnable task){
		executor.submit(task);
	}
	
	public void close(){
		if(transport!=null){
			transport.shutdown();
		}
	}
	
	private class ConnectionListenerImpl implements ConnectionListener{
		
		SessionProtocolAcceptor listener;
		public ConnectionListenerImpl(SessionProtocolAcceptor listener){
			this.listener = listener;
		}
		
		@Override
		public void newConnection(AsynchronousMessageChannel channel)
				throws Exception {
			// TODO Auto-generated method stub
			new ProtocolHandler(ProtocolAcceptor.this, listener, channel);
		}

		@Override
		public void shutdown() {
			// TODO Auto-generated method stub
			// do something if the tcptransport is shutdown.
		}
		
	}
}
