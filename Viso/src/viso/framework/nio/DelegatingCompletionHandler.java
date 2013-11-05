package viso.framework.nio;

import java.nio.channels.CompletionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public abstract class DelegatingCompletionHandler<OR, OA, IR, IA> extends
		FutureTask<OR> implements CompletionHandler<IR, IA> {

	/** The associated outer handler. */
	private final CompletionHandler<OR, OA> outerHandler;

	/**
     * The attachment for this {@code IoFuture}.  This field is
     * {@code volatile} to match the attachment implementation in
     * {@link java.nio.channels.SelectionKey}.
     */
    private volatile OA attachment;
	
	/** The lock to synchronize on when accessing innerFuture. */
	private final Object lock = new Object();

	/**
	 * The inner future associated with the current computation, or {@code null}
	 * if the inner computation is not underway.
	 */
	private Future<IR> innerFuture = null;

	/**
	 * Creates an instance for the specified attachment and handler.
	 * 
	 * @param outerAttachment
	 *            the attachment for the outer future; may be {@code null}
	 * @param outerHandler
	 *            the handler to notify or {@code null}
	 */
	public DelegatingCompletionHandler(OA outerAttachment,
			CompletionHandler<OR, OA> outerHandler) {
		//并不进行计算,只是用到done()时 去调用 outerHandler.completed 的特性.
		super(new FailingCallable<OR>());
		this.outerHandler = outerHandler;
		this.attachment = outerAttachment;
	}

	/* -- Implement CompletionHandler -- */

	/**
	 * Invoked when an inner computation has completed. This method calls
	 * {@link #implCompleted}, and calls {@link #setException} on the future if
	 * that method throws an exception.
	 * 
	 * @param innerResult
	 *            the result of the inner computation
	 */
	public final void completed(IR innerReturn, IA innerAttachment) {
		synchronized (lock) {//有了该lock就不用担心 在 implCompleted 继续注册自己时 可能会产生的同步问题
			if (!isDone()) {
				try {
					if(implCompleted(innerReturn, innerAttachment)) {
						set(null);
					}
				} catch (ExecutionException e) {
					setException(e.getCause());
				} catch (Throwable t) {
					setException(t);
				}
			}
		}
	}

	/* -- Other public methods -- */

	/**
	 * This method should not be called.
	 * 
	 * @see #start
	 */
	@Override
	public final void run() {
		throw new UnsupportedOperationException(
				"The run method is not supported");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 
	 * This implementation cancels the current future, if any.
	 */
	@Override
	public final boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (lock) {
			if (isDone()) {
				return false;
			}
			boolean success = (innerFuture == null) ? true : innerFuture
					.cancel(mayInterruptIfRunning);
			if (success) {
				success = super.cancel(false);
				assert success;
			}
			return success;
		}
	}

	/**
	 * Starts the computation and returns a future representing the result of
	 * the computation.
	 * 
	 * @return a future representing the result of the computation
	 */
	public final Future<OR> start() {
		synchronized (lock) {
			if (!isDone()) {
				try {
					if (implStart()) {
						set(null);
					}
				} catch (Throwable t) {
					setException(t);
				}
			}
			return this;
		}
	}

	/* -- Protected methods -- */

	/**
	 * Starts the computation, returning a future for managing the inner
	 * computation or {@code null} to indicate that the computation is
	 * completed. Any exception thrown will terminate the computation.
	 * 
	 * @return has finished
	 */
	protected abstract boolean implStart();

	/**
	 * Called when the delegated computation completes. The implementation
	 * should return a new future if there is more computation to perform, or
	 * else {@code null} to indicate that the operation has completed. Any
	 * exception thrown will terminate the computation. If an {@link
	 * ExecutionException} is thrown, then its cause will be used.
	 * 
	 * @param innerResult
	 *            the result of the delegated computation
	 * @return a future for managing continued compuation, or {@code null} to
	 *         specify that the computation is done
	 * @throws Exception
	 *             if the computation failed
	 */
	protected abstract boolean implCompleted(IR innerReturn, IA innerAttachment)
			throws Exception;

	/**
	 * Called when the computation is completed, which occurs when {@link
	 * #implCompleted} returns {@code null} or throws an exception, or when the
	 * outer future is cancelled.
	 * <p>
	 * 
	 * This implementation runs the outer completion handler. Subclasses that
	 * override this method should make sure to call this method by calling
	 * {@code super.done()}.
	 */
	@Override
	protected void done() {
		synchronized (lock) {
			innerFuture = null;
			if (outerHandler != null) {
				try {
					outerHandler.completed(this.get(), attachment);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					setException(e);
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					setException(e);
				}
			}
		}
	}

	/* -- Private methods and classes -- */

	/** Implements a {@code Callable} that fails if called. */
	private static final class FailingCallable<V> implements Callable<V> {
		FailingCallable() {
		}

		public V call() {
			throw new AssertionError();
		}
	}
}
