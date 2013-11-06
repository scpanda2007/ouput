package viso.test.framework.service.net;

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

	public final void testCreate(){
		transport = new TcpTransport(TestProperties.getProperties());
		shutDown();
	}
	
	public final void testAccept() {
		transport = new TcpTransport(TestProperties.getProperties());
		transport.accept(new TestConnectionHandler());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		shutDown();
	}
	
	private class TestConnectionHandler implements ConnectionHandler{

		@Override
		public void newConnection(AsynchronousByteChannel channel)
				throws Exception {
			// TODO Auto-generated method stub
			
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