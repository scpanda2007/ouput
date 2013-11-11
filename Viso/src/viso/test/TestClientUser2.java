package viso.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import viso.impl.framework.service.net.TcpTransport;
import viso.util.tools.MessageBuffer;

public class TestClientUser2 {
	
	AsynchronousSocketChannel server;
	/** ·þÎñÆ÷µØÖ· */
	InetSocketAddress is_addr;
	
	ByteBuffer buf = ByteBuffer.allocate(1024);
	
	public TestClientUser2(String host, int port){
		is_addr = new InetSocketAddress(host, port);
	}
	
	public void connect() throws IOException{
		server = AsynchronousSocketChannel.open();
		server.connect(is_addr, null, new CompletionHandler<Void, Void>(){

			@Override 
			public void completed(Void arg0,Void arg1){// TODO Auto-generated method stub
				MessageBuffer msg = new MessageBuffer(1024);
				msg.putByte((byte)0);
				msg.putString(" yes i am connected to the server");
				msg.flip();
				int size_ = msg.remaining();
				ByteBuffer buffer = ByteBuffer.allocate(2+size_);
				buffer.putShort((short)size_).put(msg.getBytes(size_)).flip();
				server.write(buffer.asReadOnlyBuffer());
				buf.clear();
				server.read(buf, null, new ReaderHandler());
			}

			@Override 
			public void failed(Throwable arg0,Void arg1){// TODO Auto-generated method stub
			
			}
			
		});
	}
	
	public class ReaderHandler implements CompletionHandler<Integer, Void>{

		@Override
		public void completed(Integer arg0, Void arg1) {
			// TODO Auto-generated method stub
			System.out.println(" haha i have read from server : "+new String(buf.array(),0,buf.position()));
			try {
				server.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void failed(Throwable arg0, Void arg1) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void close() throws IOException{
		if(server.isOpen()){
			server.close();
		}
	}
	
	public static void main(String[] args) {
		TestClientUser2 client = new TestClientUser2("127.0.0.1", TcpTransport.DEFAULT_PORT);
		try {
			client.connect();
			Thread.sleep(10000);
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
