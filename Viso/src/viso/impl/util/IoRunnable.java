package viso.impl.util;

import java.io.IOException;

/**
 * A task to encapsulate IO-related operations to be executed within the
 * context of {@link AbstractService#runIoTask AbstractService.runIoTask}.
 */
public interface IoRunnable {

    /**
     * Runs IO-related operations to be executed within the context of
     * {@link AbstractService#runIoTask AbstractService.runIoTask}.
     *
     * @throws	IOException if an IOException occurs while running this
     *		method 
     */
   void run() throws IOException;
}

