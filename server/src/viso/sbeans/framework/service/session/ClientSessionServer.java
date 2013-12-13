package viso.sbeans.framework.service.session;

import java.math.BigInteger;

public interface ClientSessionServer {
	public void send(byte message[],BigInteger sessionid);
}
