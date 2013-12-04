package viso.test;

import java.net.MulticastSocket;
import java.nio.ByteBuffer;

import viso.test.framework.util.TestProperties;

public class VisoTest {

	public static Object lock = new Object();
	
	public static void testInterrupt(){
		
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				synchronized (lock) {
					lock.notifyAll();
					System.out.println("thread run...");
				}
				testInterruptCall();
				System.out.println("thread over...");
				// lock.notifyAll();
			}
			
		});
		t.start();
		try {
			synchronized(lock){
				lock.wait();
				System.out.println("Main Thread continue...");
				Thread.sleep(1000L);
				System.out.println("Interrupt thread...");
				t.interrupt();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testInterruptCall(){
		try {
			Thread.sleep(100000L);
			System.out.println("thread interrupt...");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Thread.currentThread().interrupt();
		}
	}
	
	public static void testBufferCompact(){
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.putInt(0);
		System.out.println(":"+buffer.position()+" :"+buffer.limit());
		buffer.putInt(0);
		System.out.println(":"+buffer.position()+" :"+buffer.limit());
		int position = buffer.position();
		buffer.position(4);
		buffer.limit(position);
		buffer.compact();
		System.out.println(":"+buffer.position()+" :"+buffer.limit());
		
		MulticastSocket multisocket;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testInterrupt();
		// TODO Auto-generated method stub
//		System.out.println(" time ===> "+(System.currentTimeMillis()/1000));
//		Double a = 0.0d;
//		Double b = 0.0000000001d;
//		System.out.println(a.equals(0.0d) ? "yes":"hehe");
//		System.out.println(b.equals(0.0d) ? "yes":"hehe");
//		System.out.println(" sin(3.1415926/6) is "+Math.sin(3.1415926/6));
//		System.out.println(" asin(0.5) is "+Math.asin(0.5));
//		System.out.println(" Math.acos(0.7) is "+Math.acos(0.7));
//		TestProperties.getProperties();
//		Properties properties = new Properties();
//		File file = new File(TestStandardProperties.loggerConfFile);
//		if(!file.exists()){
//			OutputStream out;
//			try {
//				file.createNewFile();
//				out = new BufferedOutputStream(new FileOutputStream(TestStandardProperties.loggerConfFile));
//				properties.put("java.util.logging.ConsoleHandler.level", "ALL");
//				properties.store(out, "create local.properties...");
//				out.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new IllegalStateException("failed to create properties file");
//			}
//		}
	}

}
