package viso.impl.framework.service.store.net;

import java.io.IOException;

import viso.impl.framework.service.data.store.BindingValue;

/**
 * An abstract class that implements the non-network parts of the client side
 * of an experimental network protocol, not currently used, for implementing
 * DataStoreServer without using RMI.
 */
abstract class DataStoreProtocolClient implements DataStoreServer {

	/** Creates an instance of this class. */
	DataStoreProtocolClient() {
	}

	/** The protocol handler for the current thread. */
	private final ThreadLocal<DataStoreProtocol> handler = new ThreadLocal<DataStoreProtocol>();

	/**
	 * Creates a protocol handler.
	 *
	 * @return	the protocol handler
	 * @throws	IOException if an I/O failure occurs
	 */
	abstract DataStoreProtocol createHandler() throws IOException;

	/**
	 * Gets the protocol handler for the current thread.
	 *
	 * @return	the protocol handler for the current thread
	 * @throws	IOException if an I/O failure occurs
	 */
	DataStoreProtocol getHandler() throws IOException {
		DataStoreProtocol h = handler.get();
		if (h == null) {
			h = createHandler();
			handler.set(h);
		}
		return h;
	}

	/* -- Implement DataStoreServer -- */

	/** {@inheritDoc} */
	public long newNodeId() throws IOException {
		return getHandler().newNodeId();
	}

	/** {@inheritDoc} */
	public long createObject(long tid) throws IOException {
		return getHandler().createObject(tid);
	}

	/** {@inheritDoc} */
	public void markForUpdate(long tid, long oid) throws IOException {
		getHandler().markForUpdate(tid, oid);
	}

	/** {@inheritDoc} */
	public byte[] getObject(long tid, long oid, boolean forUpdate)
			throws IOException {
		return getHandler().getObject(tid, oid, forUpdate);
	}

	/** {@inheritDoc} */
	public void setObject(long tid, long oid, byte[] data) throws IOException {
		getHandler().setObject(tid, oid, data);
	}

	/** {@inheritDoc} */
	public void setObjects(long tid, long[] oids, byte[][] dataArray)
			throws IOException {
		getHandler().setObjects(tid, oids, dataArray);
	}

	/** {@inheritDoc} */
	public void removeObject(long tid, long oid) throws IOException {
		getHandler().removeObject(tid, oid);
	}

	/** {@inheritDoc} */
	public BindingValue getBinding(long tid, String name) throws IOException {
		return getHandler().getBinding(tid, name);
	}

	/** {@inheritDoc} */
	public BindingValue setBinding(long tid, String name, long oid)
			throws IOException {
		return getHandler().setBinding(tid, name, oid);
	}

	/** {@inheritDoc} */
	public BindingValue removeBinding(long tid, String name) throws IOException {
		return getHandler().removeBinding(tid, name);
	}

	/** {@inheritDoc} */
	public String nextBoundName(long tid, String name) throws IOException {
		return getHandler().nextBoundName(tid, name);
	}

	/** {@inheritDoc} */
	public int getClassId(long tid, byte[] classInfo) throws IOException {
		return getHandler().getClassId(tid, classInfo);
	}

	/** {@inheritDoc} */
	public byte[] getClassInfo(long tid, int classId) throws IOException {
		return getHandler().getClassInfo(tid, classId);
	}

	/** {@inheritDoc} */
	public long nextObjectId(long tid, long oid) throws IOException {
		return getHandler().nextObjectId(tid, oid);
	}

	/** {@inheritDoc} */
	public long createTransaction(long timeout) throws IOException {
		return getHandler().createTransaction(timeout);
	}

	/** {@inheritDoc} */
	public boolean prepare(long tid) throws IOException {
		return getHandler().prepare(tid);
	}

	/** {@inheritDoc} */
	public void commit(long tid) throws IOException {
		getHandler().commit(tid);
	}

	/** {@inheritDoc} */
	public void prepareAndCommit(long tid) throws IOException {
		getHandler().prepareAndCommit(tid);
	}

	/** {@inheritDoc} */
	public void abort(long tid) throws IOException {
		getHandler().abort(tid);
	}
}