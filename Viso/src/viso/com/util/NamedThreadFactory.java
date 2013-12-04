package viso.com.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import static viso.com.util.Objects.checkNull;

/**
 * �����ɵ��̼߳������ֱ�ʶ
 * */
public class NamedThreadFactory implements ThreadFactory{
	
	private String prefix;//����ǰ׺
	
	private AtomicInteger counter;//������

	/**
	 * 	@param prefix �߳�����ǰ׺ ���ɵ��߳��� prefix + ���� �ķ�ʽ��ʾ
	 * */
	public NamedThreadFactory(String prefix){
		checkNull("Thread Name", prefix);
		this.prefix = prefix;
		this.counter = new AtomicInteger(0);
	}
	
	/**
	 * ����һ����Ψһ��ʶ����ָ�����Ƶ��߳�
	 * */
	@Override
	public Thread newThread(Runnable arg0) {
		// TODO Auto-generated method stub
		return new Thread(arg0, prefix + counter.getAndIncrement());
	}

}
