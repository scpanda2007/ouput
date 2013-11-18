package viso.impl.framework.service.data.store.bdb;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.Transaction;

import static com.sleepycat.db.OperationStatus.SUCCESS;
import static com.sleepycat.db.OperationStatus.NOTFOUND;
import static com.sleepycat.db.OperationStatus.KEYEXIST;

import viso.framework.service.store.db.DbCursor;
import viso.framework.service.store.db.DbDatabaseException;

/** Provides a cursor implementation using Berkeley DB. */
public class BdbCursor implements DbCursor {

	/** The Berkeley DB cursor. */
	private final Cursor cursor;

	/** An entry containing the current key if isCurrent is true. */
	private DatabaseEntry keyEntry = new DatabaseEntry();

	/** An entry containing the current value if isCurrent is true. */
	private DatabaseEntry valueEntry = new DatabaseEntry();

	/** Whether the data in keyEntry and valueEntry is valid. */
	private boolean isCurrent = false;

	/**
	 * Creates an instance of this class.
	 * 
	 * @param db
	 *            the Berkeley DB database
	 * @param txn
	 *            the Berkeley DB transaction
	 * @throws TransactionAbortedException
	 *             if the transaction should be aborted due to timeout or
	 *             conflict
	 * @throws DbDatabaseException
	 *             if an unexpected database problem occurs
	 */
	BdbCursor(Database db, Transaction txn) {
		try {
			cursor = db.openCursor(txn, null);
		} catch (DatabaseException e) {
			throw BdbEnvironment.convertException(e, true);
		}
	}

	/** {@inheritDoc} */
	public byte[] getKey() {
		return isCurrent ? BdbDatabase.convertData(keyEntry.getData()) : null;
	}

	/** {@inheritDoc} */
	public byte[] getValue() {
		return isCurrent ? BdbDatabase.convertData(valueEntry.getData()) : null;
	}

	/** {@inheritDoc} */
	public boolean findFirst() {
		try {
			OperationStatus status = cursor
					.getFirst(keyEntry, valueEntry, null);
			if (status == SUCCESS) {
				isCurrent = true;
				return true;
			} else if (status == NOTFOUND) {
				return false;
			} else {
				throw new DbDatabaseException("Operation failed: " + status);
			}
		} catch (DatabaseException e) {
			throw BdbEnvironment.convertException(e, true);
		}
	}

	/** {@inheritDoc} */
	public boolean findNext() {
		try {
			OperationStatus status = cursor.getNext(keyEntry, valueEntry, null);
			if (status == SUCCESS) {
				isCurrent = true;
				return true;
			} else if (status == NOTFOUND) {
				return false;
			} else {
				throw new DbDatabaseException("Operation failed: " + status);
			}
		} catch (DatabaseException e) {
			throw BdbEnvironment.convertException(e, true);
		}
	}

	/** {@inheritDoc} */
	public boolean findNext(byte[] key) {
		DatabaseEntry searchEntry = new DatabaseEntry(key);
		try {
			OperationStatus status = cursor.getSearchKeyRange(searchEntry,
					valueEntry, null);
			if (status == SUCCESS) {
				keyEntry = searchEntry;
				isCurrent = true;
				return true;
			} else if (status == NOTFOUND) {
				return false;
			} else {
				throw new DbDatabaseException("Operation failed: " + status);
			}
		} catch (DatabaseException e) {
			throw BdbEnvironment.convertException(e, true);
		}
	}

	/** {@inheritDoc} */
	public boolean findLast() {
		try {
			OperationStatus status = cursor.getLast(keyEntry, valueEntry, null);
			if (status == SUCCESS) {
				isCurrent = true;
				return true;
			} else if (status == NOTFOUND) {
				return false;
			} else {
				throw new DbDatabaseException("Operation failed: " + status);
			}
		} catch (DatabaseException e) {
			throw BdbEnvironment.convertException(e, true);
		}
	}

	/** {@inheritDoc} */
	public boolean putNoOverwrite(byte[] key, byte[] value) {
		try {
			DatabaseEntry putKeyEntry = new DatabaseEntry(key);
			DatabaseEntry putValueEntry = new DatabaseEntry(value);
			OperationStatus status = cursor.putNoOverwrite(putKeyEntry,
					putValueEntry);
			if (status == SUCCESS) {
				isCurrent = true;
				keyEntry = putKeyEntry;
				valueEntry = putValueEntry;
				return true;
			} else if (status == KEYEXIST) {
				return false;
			} else {
				throw new DbDatabaseException("Operation failed: " + status);
			}
		} catch (DatabaseException e) {
			throw BdbEnvironment.convertException(e, true);
		}
	}

	/** {@inheritDoc} */
	public void close() {
		try {
			cursor.close();
		} catch (DatabaseException e) {
			throw BdbEnvironment.convertException(e, true);
		}
	}
}
