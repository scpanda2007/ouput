package viso.test.framework.service.net;

import java.math.BigInteger;

import viso.framework.auth.Identity;
import viso.framework.kernel.ComponentRegistry;
import viso.framework.service.protocol.ProtocolListener;
import viso.framework.service.protocol.RequestCompletionHandler;
import viso.framework.service.protocol.SessionProtocol;
import viso.framework.service.protocol.SessionProtocolHandler;
import viso.impl.framework.service.net.TcpTransport;
import viso.impl.framework.service.protocol.SimpleVisoProtocolAcceptor;
import viso.test.framework.util.TestProperties;
import junit.framework.TestCase;

public class TestProtocolAccepter extends TestCase {

	private volatile SimpleVisoProtocolAcceptor acceptor;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public final void testClose() {
		try {
			acceptor = new SimpleVisoProtocolAcceptor(TestProperties
					.getProperties(), null, null);
			acceptor.accept(new ProtocolListener() {

				@Override
				public void relocatedSession(
						BigInteger relocationKey,
						SessionProtocol protocol,
						RequestCompletionHandler<SessionProtocolHandler> completionHandler) {
					// TODO Auto-generated method stub

				}

				@Override
				public void newLogin(
						Identity identity,
						SessionProtocol protocol,
						RequestCompletionHandler<SessionProtocolHandler> completionHandler) {
					// TODO Auto-generated method stub

				}

			});
			Thread.sleep(30000);//立即停止时， acceptor 还在调用异步读取，会抛一个异常
			acceptor.close(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			acceptor.close();
		}
	}
	
//	public final void testCreate(){
//		try {
//			acceptor = new SimpleVisoProtocolAcceptor(TestProperties.getProperties());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			acceptor.close();
//		}
//	}
//	
//	public final void testAccept(){
//		try {
//			acceptor = new SimpleVisoProtocolAcceptor(TestProperties.getProperties());
//			acceptor.accept(new ProtocolListener() {
//				
//				@Override
//				public void relocatedSession(BigInteger relocationKey,
//						SessionProtocol protocol,
//						RequestCompletionHandler<SessionProtocolHandler> completionHandler) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void newLogin(Identity identity, SessionProtocol protocol,
//						RequestCompletionHandler<SessionProtocolHandler> completionHandler) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//			});
//			
//			Thread.sleep(30000);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			acceptor.close();
//		}
//	}

}
