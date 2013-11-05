package viso.framework.service.protocol.simple;

public class SimpleSgsProtocol {
	public static final int VERSION = 100;
	public static final int MAX_MESSAGE_LENGTH = 4096;
	public static final int MAX_PAYLOAD_LENGTH = 10000;
	public static final byte SESSION_MESSAGE = 1;
	public static final byte CHANNEL_JOIN = 2;
	public static final byte CHANNEL_LEAVE = 3;
	public static final byte CHANNEL_MESSAGE = 4;
	public static final byte LOGIN_SUCCESS = 5;
	public static final byte LOGIN_REDIRECT = 6;
	public static final byte LOGIN_FAILURE = 7;
	public static final byte LOGOUT_SUCCESS = 8;
	public static final byte SUSPEND_MESSAGES = 9;
	public static final byte RESUME_MESSAGES = 10;
	public static final byte RELOCATE_SUCCESS = 11;
	public static final byte RELOCATE_FAILURE = 12;
	public static final byte RELOCATE_REQUEST = 13;
	public static final byte SUSPEND_MESSAGES_COMPLETE = 14;
	public static final byte RELOCATE_NOTIFICATION = 15;
	public static final byte LOGIN_REQUEST = 16;
	public static final byte LOGOUT_REQUEST = 17;
}
