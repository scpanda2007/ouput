package viso.test.framework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import viso.framework.kernel.ComponentRegistry;
import viso.framework.service.TransactionProxy;
import viso.impl.framework.kernel.KernelShutdownController;

public class VisoTestNode {
	/** 程序标识 */
	String appName;
	/** kernel 类名*/
	private static final String kernelImplClassName = "viso.impl.framework.kernel.Kernel";
	/** kernel shutdown */
    private static Method kernelShutdownMethod;
    /** transaction proxy */
    private static Field kernelProxy;
    /** system registry */
    private static Field kernelReg;
    /** shutdown controller */
    private static Field kernelShutdownCtrl;
    /** kernel constructor */
	private static Constructor<?> kernelCtor;
	/** kernel class */
	private static Class<?> kernelClass;
    
	static{
		try {
			/** TaskSchedulerClassImpl */
			kernelClass = Class.forName(kernelImplClassName);
			kernelCtor = kernelClass.getDeclaredConstructor(Properties.class);
			kernelCtor.setAccessible(true);
			
			kernelShutdownMethod = kernelClass.getDeclaredMethod("shutdown");
			kernelShutdownMethod.setAccessible(true);
			
			kernelShutdownCtrl = kernelClass.getDeclaredField("shutdownCtrl");
			kernelShutdownCtrl.setAccessible(true);
			
			kernelReg = kernelClass.getDeclaredField("systemRegistry");
			kernelReg.setAccessible(true);
			
			kernelProxy = kernelClass.getDeclaredField("proxy");
			kernelProxy.setAccessible(true);
			
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/** framework kernel */
	private Object kernel;
	private final TransactionProxy txnProxy;
    private final ComponentRegistry systemRegistry;
    /** Shutdown controller. */
    private final KernelShutdownController shutdownCtrl;
	
	public VisoTestNode() throws Exception{
		this("VisoTestNode", null, null);
	}
	
	public VisoTestNode(String appName, Object object, Properties properties) throws Exception{
		// TODO Auto-generated constructor stub
		this.appName = appName;
		if(properties==null) {
			kernel = kernelCtor.newInstance(TestProperties.getProperties());
		}else{
			kernel = kernelCtor.newInstance(properties);
		}
		txnProxy = (TransactionProxy)kernelProxy.get(kernel);
		systemRegistry = (ComponentRegistry)kernelReg.get(kernel);
		shutdownCtrl = (KernelShutdownController)kernelShutdownCtrl.get(kernel);
	}

	public VisoTestNode(String appName) throws Exception{
		// TODO Auto-generated constructor stub
		this(appName, null, null);
	}
	
	public void shutdown(boolean clean){
		try {
			kernelShutdownMethod.invoke(kernel);
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
	
    /**
     * Returns the shutdown controller for this node.
     */
    public KernelShutdownController getShutdownCtrl() {
        return shutdownCtrl;
    }
	
	public ComponentRegistry getSystemRegistry() {
		// TODO Auto-generated method stub
		return systemRegistry;
	}

	public TransactionProxy getProxy() {
		// TODO Auto-generated method stub
		return txnProxy;
	}
	
}
