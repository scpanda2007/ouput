package viso.test.framework.util;

import java.util.Enumeration;
import java.util.Properties;

/** Provides properties utilities for writing tests. */
public class UtilProperties {

	/**
	 * Creates a property list with the specified keys and values, and
	 * including all current system properties that start with "viso.framework.",
	 * "java.rmi." or "je.".  Including those system properties allows
	 * supplying server properties from the command line.  It is necessary to
	 * not include all properties because some of them interfere if used for
	 * starting up another VM.
	 *
	 * @param	args an alternating list of property names and values
	 * @return	the properties
	 * @throws	IllegalArgumentException if the argument has an odd number of
	 *		elements
	 */
	public static Properties createProperties(String... args) {
		if (args.length % 2 != 0) {
			throw new IllegalArgumentException("Odd number of arguments");
		}
		Properties props = new Properties();
		for (Enumeration<?> names = System.getProperties().propertyNames(); names
				.hasMoreElements();) {
			Object name = names.nextElement();
			if (name instanceof String) {
				String property = (String) name;
				if (property.startsWith("viso.")) {
					props.setProperty(property, System.getProperty(property));
				}
			}
		}
		for (int i = 0; i < args.length; i += 2) {
			props.setProperty(args[i], args[i + 1]);
		}
		return props;
	}
}