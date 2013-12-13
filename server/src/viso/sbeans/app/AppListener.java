package viso.sbeans.app;

import viso.sbeans.framework.service.session.ClientSession;
import viso.sbeans.framework.service.session.ClientSessionListener;

public interface AppListener{
	ClientSessionListener login(ClientSession session);
}