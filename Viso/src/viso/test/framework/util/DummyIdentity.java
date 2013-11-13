package viso.test.framework.util;

import viso.framework.auth.Identity;

public class DummyIdentity implements Identity {
	
	String name;
	
	public DummyIdentity(String name){
		this.name = name;
		if(name==null) this.name = "WHQ";
	}
	
	public DummyIdentity(){
		this(null);
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void notifyLoggedIn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyLoggedOut() {
		// TODO Auto-generated method stub

	}

}
