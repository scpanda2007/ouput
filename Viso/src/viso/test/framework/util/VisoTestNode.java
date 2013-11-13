package viso.test.framework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;
import java.util.Properties;

import viso.framework.kernel.TaskScheduler;
import viso.framework.profile.ProfileCollectorHandle;
import viso.test.framework.profile.DummyProfileCollectorHandle;

import org.junit.*;

public class VisoTestNode {
	/** 程序标识 */
	String appName;
	/** 不可见 需要加载的register类 */
	private LinkedHashSet<Object> components = new LinkedHashSet<Object>();
	/** 已提供加载的动态模块 */
	private static ArrayList<String> properties = new ArrayList<String>();
	
	private static final String TaskSchedulerClassImplPath = "viso.impl.framework.kernel.TaskSchedulerImpl";
	
	private Object taskScheduler;
	/** taskschedule shutdown */
    private static Method taskScheduleShutdownMethod;
	
    private static Constructor<?> taskSchedulerCtor;
    
	static{
		try {
			/** TaskSchedulerClassImpl */
			Class<?> taskSchedulerClass = Class.forName(TaskSchedulerClassImplPath);
			taskSchedulerCtor = taskSchedulerClass.getDeclaredConstructor(Properties.class, 
					ProfileCollectorHandle.class);
			taskScheduleShutdownMethod = taskSchedulerClass.getDeclaredMethod("shutdown");
			taskSchedulerCtor.setAccessible(true);
			taskScheduleShutdownMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public VisoTestNode(){
		this("VisoTestNode");
	}
	
	public VisoTestNode(String appName){
		this.appName = appName;
		try {
			taskScheduler = taskSchedulerCtor.newInstance(TestProperties.getProperties(), new DummyProfileCollectorHandle());
			components.add(taskScheduler);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public <T> T getInstance(Class<T> klass){
		
		Object matchObj = null;
		
		for (Object obj : components) {
			if (klass.isAssignableFrom(obj.getClass())) {
				if (matchObj != null) {
					throw new MissingResourceException("More than one "
							+ "matching component", klass.getName(), null);
				}
				matchObj = obj;
			}
			
		}
		
		// if no matches were found, it's an error
        if (matchObj == null) {
            throw new MissingResourceException("No matching components",
            		klass.getName(), null);
        }
		
		return klass.cast(matchObj);
	}
	
	public void shutdown(){
		try {
			taskScheduleShutdownMethod.invoke(taskScheduler);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRun(){
		try{
			TaskScheduler taskScheduler = (new VisoTestNode("TestTaskScheduleImpl")).getInstance(TaskScheduler.class);
			assert taskScheduler!= null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
