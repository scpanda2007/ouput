package viso.com.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import static viso.com.util.Objects.checkNull;

public class NamedThreadFactory implements ThreadFactory{

	private AtomicInteger counter;
	
	private final String name;
	
	public NamedThreadFactory(String name){
		checkNull("name",name);
		this.name = name;
		counter = new AtomicInteger(0);
	}
	
	@Override
	public Thread newThread(Runnable arg0) {
		// TODO Auto-generated method stub
		return new Thread(arg0,name+"--"+counter.getAndIncrement());
	}

}
