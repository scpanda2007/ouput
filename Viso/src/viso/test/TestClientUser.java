package viso.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import viso.impl.framework.service.net.TcpTransport;

/**  
 * 服务端响应处理器  
 *   
 * @author Johnson Lee  
 *   
 */  
class ResponseProcessor implements Runnable {   
    private TestClientUser client;   
  
    public ResponseProcessor(TestClientUser c) {   
        super();   
        this.client = c;   
    }   
  
    @Override  
    public void run() {   
        BufferedReader br = null;   
        PrintWriter pw = null;   
        String line = null;   
        try {   
            br = new BufferedReader(new InputStreamReader(client   
                    .getInputStream()));   
            pw = new PrintWriter(System.out, true);   
            while (!client.isClosed()) {   
                line = br.readLine();   
                pw.println(line);   
            }   
        } catch (SocketException e) {   
            if (!client.isClosed()) {   
                System.err.println(e);   
            }   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
} 

public class TestClientUser {
	private InetSocketAddress svrAddress;
	private Socket svrSocket;

	public TestClientUser(String host, int port) {
		this.svrAddress = new InetSocketAddress(host, port);
	}

	public void connect() throws IOException {
		this.svrSocket = new Socket(svrAddress.getAddress(), svrAddress
				.getPort());
	}

	public boolean isClosed() {
		return this.svrSocket == null || this.svrSocket.isClosed();
	}

	public InputStream getInputStream() throws IOException {
		return this.svrSocket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return this.svrSocket.getOutputStream();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BufferedReader br = null;
		PrintWriter pw = null;
		String line = null;
		try {
			final TestClientUser c = new TestClientUser("127.0.0.1", TcpTransport.DEFAULT_PORT);
			c.connect();// 连接服务器
			try {
				br = new BufferedReader(new InputStreamReader(System.in));
				pw = new PrintWriter(c.getOutputStream(), true);
				new Thread(new ResponseProcessor(c)).start();
				while (!c.isClosed()) {
					line = br.readLine();
					pw.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null) {
						br.close();
						br = null;
					}
					if (pw != null) {
						pw.close();
						pw = null;
					}
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
