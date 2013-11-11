package viso.test.framework.service.net;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;

import viso.framework.service.net.ConnectionHandler;
import viso.impl.framework.service.net.TcpTransport;
import viso.test.framework.util.TestProperties;
import junit.framework.TestCase;

public class TestTcpTransport extends TestCase {

	private volatile TcpTransport transport;
	
	protected void setUp() throws Exception {
		super.setUp();
		transport = null;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		shutDown();
	}

//	public final void testCreate(){
//		transport = new TcpTransport(TestProperties.getProperties());
//		shutDown();
//	}
	
//	public final void testAccept() {
//		transport = new TcpTransport(TestProperties.getProperties());
//		transport.accept(new TestConnectionHandler());
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		shutDown();
//	}
	
	public final void testClose(){
		transport = new TcpTransport(TestProperties.getProperties());
		transport.accept(new TestConnectionHandler());
		shutDown();
		shutDown();
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private class TestConnectionHandler implements ConnectionHandler{

		@Override
		public void newConnection(AsynchronousByteChannel channel)
				throws Exception {
			// TODO Auto-generated method stub
			channel.write(ByteBuffer.wrap("hello this is TestConnectionHandler.".getBytes()));
		}

		@Override
		public void shutdown() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public final void shutDown(){
		if(transport!=null)
			transport.shutdown();
	}

}
