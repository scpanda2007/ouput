package viso.framework.kernel;


/**
 * This is a general registry interface used to provide access to a collection
 * of components by type. It is used by the kernel during startup to
 * configure system components and <code>Service</code>s.
 */
public interface ComponentRegistry extends Iterable<Object>
{

    /**
     * Returns a component from the registry, matched based on the given
     * type. If there are no matches, or there is more than one possible
     * match, then an exception is thrown.
     *
     * @param <T> the type of the component
     * @param type the <code>Class</code> of the requested component
     *
     * @return the requested component
     *
     * @throws MissingResourceException if the requested component is not
     *                                  available, or if there is more
     *                                  than one matching component
     */
    <T> T getComponent(Class<T> type);

}

