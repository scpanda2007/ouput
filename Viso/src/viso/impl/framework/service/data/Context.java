package viso.impl.framework.service.data;

import java.util.logging.Logger;

import viso.framework.service.Transaction;
import viso.framework.service.TransactionListener;
import viso.framework.service.store.DataStore;
import viso.util.tools.LoggerWrapper;


/** Stores information for a specific transaction. */
final class Context extends TransactionContext implements TransactionListener {

    /** The logger for the data service class. */
    private static final LoggerWrapper logger =
	new LoggerWrapper(Logger.getLogger(DataServiceImpl.class.getName()));

    /** The data service. */
    private final DataServiceImpl service;

    /** The data store. */
    final DataStore store;

    /** The transaction. */
    final Transaction txn;

    /**
     * The number of operations to skip between checks of the consistency of
     * the reference table.
     */
    private final int debugCheckInterval;

    /** Whether to detect modifications. */
    final boolean detectModifications;

    /** Controls serializing classes. */
    final ClassSerialization classSerial;

    /**
     * The number of operations performed -- used to determine when to make
     * checks on the reference table.
     */
    private int count = 0;

    /**
     * Stores information about managed references.  This field is logically
     * part of the ManagedReferenceImpl class.
     */
    final ReferenceTable refs;

    /**
     * A map that records all managed objects that are currently having
     * ManagedObjectRemoval.removingObject called on them, to detect recursion,
     * or null.  Uses identity comparison to avoid confusion by value-based
     * equals methods.
     */
    private IdentityHashMap<ManagedObjectRemoval, Boolean> removing = null;

    /** Creates an instance of this class. */
    Context(DataServiceImpl service,
	    DataStore store,
	    Transaction txn,
	    int debugCheckInterval,
	    boolean detectModifications,
	    ClassesTable classesTable,
	    boolean trackStaleObjects)
    {
	super(txn);
	assert service != null && store != null && txn != null &&
	    classesTable != null;
	this.service = service;
	this.store = store;
	this.txn = txn;
	this.debugCheckInterval = debugCheckInterval;
	this.detectModifications = detectModifications;
	refs = new ReferenceTable(trackStaleObjects);
	classSerial = classesTable.createClassSerialization(this.txn);
	txn.registerListener(this);
	if (logger.isLoggable(Level.FINER)) {
	    logger.log(Level.FINER, "join tid:{0,number,#}, thread:{1}",
		       getTxnId(), Thread.currentThread().getName());
	}
    }

    /* -- Methods for obtaining references -- */

    /** Obtains the reference associated with the specified object. */
    <T> ManagedReferenceImpl<T> getReference(T object) {
	return ManagedReferenceImpl.getReference(this, object);
    }

    /**
     * Finds the existing reference associated with the specified object,
     * returning null if it is not found.  Throws ObjectNotFoundException if
     * the object has been removed.
     */
    <T> ManagedReferenceImpl<T> findReference(T object) {
	return ManagedReferenceImpl.findReference(this, object);
    }

    /**
     * Finds the existing reference associated with the specified object,
     * returning null if it is not found or has been removed.
     */
    <T> ManagedReferenceImpl<T> safeFindReference(T object) {
	return ManagedReferenceImpl.safeFindReference(this, object);
    }

    /** Obtains the reference associated with the specified ID. */
    ManagedReferenceImpl<?> getReference(long oid) {
	return ManagedReferenceImpl.getReference(this, oid);
    }

    /* -- Methods for bindings -- */

    /** Obtains the object associated with the specified internal name. */
    ManagedObject getBinding(String internalName, boolean forUpdate) {
	long id = store.getBinding(txn, internalName);
	assert id >= 0 : "Object ID must not be negative";
	ManagedObject result;
	if (forUpdate) {
	    result = (ManagedObject) getReference(id).getForUpdate(false);
	} else {
	    result = (ManagedObject) getReference(id).get(false);
	}
	store.setBindingDescription(txn, internalName, result);
	return result;
    }

    /** Sets the object associated with the specified internal name. */
    void setBinding(String internalName, Object object) {
	store.setBindingDescription(txn, internalName, object);
	store.setBinding(txn, internalName, getReference(object).oid);
    }

    /** Removes the object associated with the specified internal name. */
    void removeBinding(String internalName) {
	store.removeBinding(txn, internalName);
    }

    /** Returns the next bound name. */
    String nextBoundName(String internalName) {
	return store.nextBoundName(txn, internalName);
    }

    /* -- Methods for object IDs -- */

    /**
     * Returns the next object ID, or -1 if there are no more objects.  Does
     * not return IDs for removed objects.  Specifying -1 requests the first
     * ID.
     */
    long nextObjectId(long oid) {
	return ManagedReferenceImpl.nextObjectId(this, oid);
    }

    /* -- Methods for TransactionContext -- */

    @Override
    public boolean prepare() throws Exception {
	try {
	    isPrepared = true;
	    if (logger.isLoggable(Level.FINER)) {
		logger.log(Level.FINER,
			   "prepare tid:{0,number,#} returns true");
	    }
	    return true;
	} catch (Exception e) {
	    if (logger.isLoggable(Level.FINER)) {
		logger.logThrow(Level.FINER, e,
				"prepare tid:{0,number,#} throws", getTxnId());
	    }
	    throw e;
	}
    }

    @Override
    public void commit() {
	try {
	    isCommitted = true;
	    if (logger.isLoggable(Level.FINER)) {
		logger.log(Level.FINER, "commit tid:{0,number,#} returns",
			   getTxnId());
	    }
	} catch (RuntimeException e) {
	    if (logger.isLoggable(Level.FINER)) {
		logger.logThrow(Level.FINER, e,
				"commit tid:{0,number,#} throws", getTxnId());
	    }
	    throw e;
	}
    }

    @Override
    public void prepareAndCommit() throws Exception {
	try {
	    isCommitted = true;
	    if (logger.isLoggable(Level.FINER)) {
		logger.log(Level.FINER,
			   "prepareAndCommit tid:{0,number,#} returns",
			   getTxnId());
	    }
	} catch (RuntimeException e) {
	    if (logger.isLoggable(Level.FINER)) {
		logger.logThrow(Level.FINER, e,
				"prepareAndCommit tid:{0,number,#} throws",
				getTxnId());
	    }
	    throw e;
	}
    }

    @Override
    public void abort(boolean retryable) {
	try {
	    if (logger.isLoggable(Level.FINER)) {
		logger.log(Level.FINER, "abort tid:{0,number,#} returns",
			   getTxnId());
	    }
	} catch (RuntimeException e) {
	    if (logger.isLoggable(Level.FINER)) {
		logger.logThrow(Level.FINER, e,
				"abort tid:{0,number,#} throws", getTxnId());
	    }
	    throw e;
	}
    }

    /* -- Implement TransactionListener -- */

    /**
     * {@inheritDoc} <p>
     *
     * This implementation flushes managed references and marks the transaction
     * inactive so that we'll notice if other beforeCompletion methods attempt
     * to call the data service.
     */
    public void beforeCompletion() {
	ManagedReferenceImpl.flushAll(this);
    }

    /**
     * {@inheritDoc} <p>
     *
     * This implementation does nothing.
     */
    public void afterCompletion(boolean commit) { }

    /** {@inheritDoc} */
    public String getTypeName() {
        return Context.class.getName();
    }

    /* -- Other methods -- */

    /**
     * Checks the consistency of the reference table if the operation count
     * equals the check interval.  Throws an IllegalStateException if it
     * encounters a problem.
     */
    void maybeCheckReferenceTable() {
	if (++count > debugCheckInterval) {
	    count = 0;
	    ManagedReferenceImpl.checkAllState(this);
	}
    }

    /** Checks that the service is running or shutting down. */
    void checkState() {
	service.checkState();
    }

    /** Calls removingObject on the argument, and checks for recursion. */
    void removingObject(ManagedObjectRemoval object) {
	if (removing == null) {
	    removing = new IdentityHashMap<ManagedObjectRemoval, Boolean>();
	}
	if (removing.containsKey(object)) {
	    throw new IllegalStateException(
		"Attempt to remove object recursively: " + object);
	}
	try {
	    removing.put(object, Boolean.TRUE);
	    object.removingObject();
	} finally {
	    removing.remove(object);
	}
    }

    /** Returns the ID of the associated transaction as a BigInteger. */
    BigInteger getTxnId() {
	return new BigInteger(1, txn.getId());
    }

    /** Returns whether to delay write locking until commit time. */
    boolean optimisticWriteLocks() {
	return service.optimisticWriteLocks;
    }
}

