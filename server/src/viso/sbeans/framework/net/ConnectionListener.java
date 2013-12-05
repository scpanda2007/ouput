package viso.sbeans.framework.net;

public interface ConnectionListener {
	public void newConnection(AsynchronousMessageChannel channel) throws Exception;
	public void shutdown();
}
