package viso.sbeans.framework.protocol;

public interface RequestCompletionHandler<V> {
	public void completed(V result);
	public void failed(Throwable t, V result);
}
