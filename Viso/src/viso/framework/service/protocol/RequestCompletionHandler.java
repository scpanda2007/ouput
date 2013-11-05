package viso.framework.service.protocol;

import java.util.concurrent.Future;

/**
 * A completion handler for a request carried out by a {@link
 * SessionProtocolHandler}.
 *
 * @param <V> the type of the request's result
 */
public interface RequestCompletionHandler<V> {
    
    /**
     * Notifies this handler that the request associated with this
     * handler is complete with the specified {@code result}.
     *
     * @param	result a future containing the result of the
     * 		request 
     */
    void completed(Future<V> result);
}
