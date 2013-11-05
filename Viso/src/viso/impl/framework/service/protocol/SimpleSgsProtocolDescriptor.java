package viso.impl.framework.service.protocol;

import java.io.Serializable;

import viso.framework.service.net.TransportDescriptor;
import viso.framework.service.protocol.ProtocolDescriptor;

/**
 * A protocol descriptor with an underlying transport descriptor. A
 * protocol may use this class directly or extend it, overriding methods
 * for protocol and/or transport-specific needs.
 */
public class SimpleSgsProtocolDescriptor implements ProtocolDescriptor,
		Serializable {

	private static final long serialVersionUID = 1L;

	/** The transport descriptor for this protocol. */
	protected final TransportDescriptor transportDesc;

	/**
	 * Constructs an instance with the specified transport descriptor.
	 *
	 * @param	transportDesc transport descriptor
	 */
	public SimpleSgsProtocolDescriptor(TransportDescriptor transportDesc) {
		if (transportDesc == null) {
			throw new NullPointerException("null transportDesc");
		}
		this.transportDesc = transportDesc;
	}

	/** {@inheritDoc}
	 *
	 * <p>This implementation returns {@code true} if the specified {@code
	 * descriptor} is an instance of this class and this descriptor's
	 * underlying transport descriptor is compatible with the specified
	 * {@code descriptor}'s transport descriptor.
	 */
	public boolean supportsProtocol(ProtocolDescriptor descriptor) {
		if (!(descriptor instanceof SimpleSgsProtocolDescriptor)) {
			return false;
		}

		SimpleSgsProtocolDescriptor desc = (SimpleSgsProtocolDescriptor) descriptor;

		return transportDesc.supportsTransport(desc.transportDesc);
	}

	/**
	 * Return the protocol specific connection data as a byte array. The data
	 * can be used by a client to connect to a server. The format of the data
	 * may be dependent on the transport configured with this protocol.
	 * @return the connection data
	 */
	public byte[] getConnectionData() {
		return transportDesc.getConnectionData();
	}

	/**
	 * Returns a string representation of this descriptor.
	 *
	 * @return	a string representation of this descriptor
	 */
	public String toString() {
		return "SimpleSgsProtocolDescriptor[" + transportDesc.toString() + "]";
	}
}
