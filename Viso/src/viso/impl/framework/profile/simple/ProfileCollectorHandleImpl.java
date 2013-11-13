package viso.impl.framework.profile.simple;

import viso.framework.auth.Identity;
import viso.framework.kernel.KernelRunnable;
import viso.framework.profile.AccessedObjectsDetail;
import viso.framework.profile.ProfileCollector;
import viso.framework.profile.ProfileParticipantDetail;
import viso.framework.profile.TransactionListenerDetail;

public class ProfileCollectorHandleImpl implements ProfileCollectorHandle{

	public ProfileCollectorHandleImpl(ProfileCollectorImpl profileCollector) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void finishTask(int tryCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishTask(int tryCount, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProfileCollector getCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void noteTransactional(byte[] transactionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyNodeIdAssigned(long nodeId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyThreadAdded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyThreadRemoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTask(KernelRunnable task, Identity owner,
			long scheduledStartTime, int readyCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(TransactionListenerDetail listenerDetail) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addParticipant(ProfileParticipantDetail participantDetail) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAccessedObjectsDetail(AccessedObjectsDetail detail) {
		// TODO Auto-generated method stub
		
	}
	
}