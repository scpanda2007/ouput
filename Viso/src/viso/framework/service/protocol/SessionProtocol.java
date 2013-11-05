package viso.framework.service.protocol;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.util.Set;

import viso.app.Delivery;

public interface SessionProtocol extends Channel {

	/**
	 * Reasons why a server disconnects a session.
	 */
	enum DisconnectReason {
		/** The application explicitly terminates a session. */
		TERMINATION,
		/** The server preempts a session because of a duplicate login. */
		PREEMPTION
	};

	/**
	 * Returns a set containing the delivery guarantees supported by
	 * this protocol.  The returned set is serializable.
	 *
	 * @return	a set containing the supported delivery guarantees
	 */
	Set<Delivery> getDeliveries();

	/**
	 * Returns the maximum length, in bytes, of the buffers passed as the
	 * {@code message} parameters to the
	 * {@link #sessionMessage sessionMessage} and
	 * {@link #channelMessage channelMessage} methods.
	 * 
	 * @return the maximum message length
	 */
	int getMaxMessageLength();

	/**
	 * Sends the associated client the specified {@code message} in a
	 * manner that satisfies the specified {@code delivery} guarantee. 
	 *
	 * <p>When possible, the message should be delivered using the most
	 * efficient means (e.g., protocol and transport) to satisfy the
	 * delivery guarantee.  However, a stronger delivery guarantee may be
	 * used to deliver the message if this protocol only supports
	 * stronger delivery guarantees.  If this protocol is not able to
	 * satisfy the specified delivery guarantee (e.g., only supports weaker
	 * delivery guarantees than the one specified), then a {@link
	 * DeliveryNotSupportedException} will be thrown.
	 *
	 * <p>The {@code ByteBuffer} is not modified and may be reused
	 * immediately after this method returns.  Changes made to the buffer
	 * after this method returns will have no effect on the message sent to
	 * the client by this invocation.
	 * 
	 * @param	message a message
	 * @param	delivery the delivery guarantee
	 * 
	 * @throws	IllegalArgumentException if the {@code message} size is
	 *          greater than {@link #getMaxMessageLength}
	 * @throws	IllegalStateException if the associated session was
	 *		requested to suspend messages
	 * @throws	DeliveryNotSupportedException if the specified {@code
	 *		delivery} guarantee cannot be satisfied by this protocol
	 * @throws	IOException if an I/O error occurs
	 */
	void sessionMessage(ByteBuffer message, Delivery delivery)
			throws IOException;

	/**
	 * Notifies the associated client that it is joined to the channel
	 * with the specified {@code name} and {@code channelId}.  This
	 * notification to the client must be delivered reliably.
	 *
	 * @param	name a channel name
	 * @param	channelId the channel's ID
	 * @param	delivery the channel's delivery guarantee
	 *
	 * @throws	IllegalStateException if the associated session was
	 *		requested to suspend messages (explicitly or due to
	 *		relocation) 
	 * @throws	DeliveryNotSupportedException if the specified {@code
	 *		delivery} guarantee cannot be satisfied by this protocol
	 * @throws	IOException if an I/O error occurs
	 */
	void channelJoin(String name, BigInteger channelId, Delivery delivery)
			throws IOException;

	/**
	 * Notifies the associated client that it is no longer a member of
	 * the channel with the specified {@code channelId}.  This
	 * notification to the client must be delivered reliably.
	 *
	 * @param	channelId a channel ID
	 * 
	 * @throws	IllegalStateException if the associated session was
	 *		requested to suspend messages (explicitly or due to
	 *		relocation) 
	 * @throws	IOException if an I/O error occurs
	 */
	void channelLeave(BigInteger channelId) throws IOException;

	/**
	 * Sends the associated client the specified channel {@code message}
	 * for the channel with the specified {@code channelId} in a manner
	 * that satisfies the specified {@code delivery} guarantee.
	 *
	 * <p>When possible, the message should be delivered using the most
	 * efficient means (e.g., protocol and transport) to satisfy the
	 * delivery guarantee.  However, a stronger delivery guarantee may be
	 * used to deliver the message if this protocol only supports
	 * stronger delivery guarantees.  If this protocol is not able to
	 * satisfy the specified delivery guarantee (e.g., only supports weaker
	 * delivery guarantees than the one specified), then a {@link
	 * DeliveryNotSupportedException} will be thrown.
	 * 
	 * <p>The {@code ByteBuffer} is not modified and may be reused
	 * immediately after this method returns.  Changes made to the buffer
	 * after this method returns will have no effect on the message sent to
	 * the client by this invocation.
	 *
	 * @param	channelId a channel ID
	 * @param	message a channel message
	 * @param	delivery the channel's delivery guarantee
	 *
	 * @throws	IllegalArgumentException if the {@code message} size is
	 *          greater than {@link #getMaxMessageLength}
	 * @throws	IllegalStateException if the associated session was
	 *		requested to suspend messages (explicitly or due to
	 *		relocation) 
	 * @throws	DeliveryNotSupportedException if the specified {@code
	 *		delivery} guarantee cannot be satisfied by this protocol
	 * @throws	IOException if an I/O error occurs
	 */
	void channelMessage(BigInteger channelId, ByteBuffer message,
			Delivery delivery) throws IOException;

	/**
	 * Disconnects the associated session for the specified {@code reason}.
	 * The protocol may send a message to the associated client indicating
	 * the reason for the disconnection, or the protocol may close the
	 * connection immediately.  Any underlying connection(s) should be
	 * closed in a timely fashion.
	 *
	 * @param	reason	the reason for disconnection
	 * 
	 * @throws	IOException if an I/O error occurs
	 */
	void disconnect(DisconnectReason reason) throws IOException;
}