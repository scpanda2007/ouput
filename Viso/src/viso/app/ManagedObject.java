package viso.app;

/**
 * A marker interface implemented by shared, persistent objects managed by
 * {@link DataManager}.  Classes that implement {@code ManagedObject} must also
 * implement {@link Serializable}, as should any non-managed objects they refer
 * to.  Any instances of {@code ManagedObject} that a managed object refers to
 * directly, or indirectly through non-managed objects, need to be referred to
 * through instances of {@link ManagedReference}. <p>
 *
 * Classes that implement {@code ManagedObject} should not provide {@code
 * writeReplace} or {@code readRestore} methods to designate replacement
 * objects during serialization.  Object replacement would interfere with the
 * object identity maintained by the {@code DataManager}, and is not
 * permitted. <p>
 *
 * Classes that implement {@code ManagedObject} can provide {@code readObject}
 * and {@code writeObject} methods to customize their serialization behavior,
 * but the {@code writeObject} methods should not perform calls to methods that
 * require a current transaction.
 *
 * @see		DataManager
 * @see		ManagedReference
 * @see		Serializable
 */
public interface ManagedObject { }
