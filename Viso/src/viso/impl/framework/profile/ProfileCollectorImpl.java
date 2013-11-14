package viso.impl.framework.profile;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.JMException;

import viso.framework.kernel.ComponentRegistry;
import viso.framework.profile.ProfileCollector;
import viso.framework.profile.ProfileConsumer;
import viso.framework.profile.ProfileListener;

public final class ProfileCollectorImpl implements ProfileCollector{

	// The default profiling level.  This is initially set from 
    // properties at startup.
    private ProfileLevel defaultProfileLevel;
	
	public ProfileCollectorImpl(ProfileLevel profileLevel,
			Properties appProperties, ComponentRegistry systemRegistry) {
		// TODO Auto-generated constructor stub
		defaultProfileLevel = profileLevel;
	}

	@Override
	public void addListener(ProfileListener listener, boolean canRemove) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListener(String listenerClassName) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProfileConsumer getConsumer(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ProfileConsumer> getConsumers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProfileLevel getDefaultProfileLevel() {
		// TODO Auto-generated method stub
		return defaultProfileLevel;
	}

	@Override
	public List<ProfileListener> getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRegisteredMBean(String beanName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerMBean(Object bean, String beanName) throws JMException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(ProfileListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultProfileLevel(ProfileLevel level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
}
