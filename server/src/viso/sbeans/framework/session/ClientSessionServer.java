package viso.sbeans.framework.session;

/**
 * 客户端会话处理服务，分离出来将来打算拓展成远程调用，所以这里的函数尽量简单够用
 * */
public interface ClientSessionServer {
	public void send(byte sesseionId[], byte message[]);
}
