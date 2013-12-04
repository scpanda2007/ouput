package viso.com.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import static viso.com.util.Objects.checkNull;

/**
 * 给生成的线程加上名字标识
 * */
public class NamedThreadFactory implements ThreadFactory{
	
	private String prefix;//名字前缀
	
	private AtomicInteger counter;//计数器

	/**
	 * 	@param prefix 线程名称前缀 生成的线程以 prefix + 计数 的方式显示
	 * */
	public NamedThreadFactory(String prefix){
		checkNull("Thread Name", prefix);
		this.prefix = prefix;
		this.counter = new AtomicInteger(0);
	}
	
	/**
	 * 生成一个带唯一标识的有指定名称的线程
	 * */
	@Override
	public Thread newThread(Runnable arg0) {
		// TODO Auto-generated method stub
		return new Thread(arg0, prefix + counter.getAndIncrement());
	}

}
