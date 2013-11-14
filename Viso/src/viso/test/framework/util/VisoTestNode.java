package viso.test.framework.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import viso.framework.kernel.ComponentRegistry;
import viso.framework.kernel.NodeType;
import viso.framework.service.TransactionProxy;
import viso.impl.framework.kernel.KernelShutdownController;
import viso.impl.framework.kernel.StandardProperties;

import static viso.test.framework.util.UtilProperties.createProperties;

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
	
	/**
     * Returns the default properties for a server node, useful for
     * adding additional properties as required.
     */
    public static Properties getDefaultProperties(String appName, 
                                           VisoTestNode serverNode,
                                           Class<?> listenerClass) 
        throws Exception
    {
        // The SgsTestNode currently starts single node (in a network config
        // for the data store) or an app node.  If a core server node is
        // desired, it's best to set the property explicitly.
        boolean isServerNode = serverNode == null;
        String nodeType = 
            isServerNode ? 
            NodeType.singleNode.toString() : 
            NodeType.appNode.toString();

//        int requestedDataPort =
//            isServerNode ?
//            getNextUniquePort() :
//            getDataServerPort((DataServiceImpl) serverNode.getDataService());

//        int requestedWatchdogPort =
//            isServerNode ?
//            getNextUniquePort() :
//            ((WatchdogServiceImpl) serverNode.getWatchdogService()).
//	    	getServer().getPort();

//        int requestedNodeMapPort =
//            isServerNode ?
//            getNextUniquePort() :
//            getNodeMapServerPort(serverNode.getNodeMappingServer());

        String dir = System.getProperty("java.io.tmpdir") +
                                File.separator + appName;

        // The node mapping service requires at least one full stack
        // to run properly (it will not assign identities to a node
        // without an app listener).   Most tests only require a single
        // node, so we provide a simple app listener if the test doesn't
        // care about one.
//        if (listenerClass == null) {
//            listenerClass = DummyAppListener.class;
//        }
        
        Properties retProps = createProperties(
            StandardProperties.APP_NAME, appName,
            StandardProperties.APP_ROOT, dir,
            StandardProperties.NODE_TYPE, nodeType,
            StandardProperties.SERVER_HOST, "localhost"
//            viso.impl.framework.service.net.TcpTransport.LISTEN_PORT_PROPERTY,
//                String.valueOf(getNextUniquePort()),
//            StandardProperties.APP_LISTENER, listenerClass.getName(),
//            "com.sun.sgs.impl.service.data.store.DataStoreImpl.directory",
//                dir + ".db",
//            "com.sun.sgs.impl.service.data.store.net.server.port", 
//                String.valueOf(requestedDataPort),
// "com.sun.sgs.impl.service.data.DataServiceImpl.data.store.class",
// "com.sun.sgs.impl.service.data.store.net.DataStoreClient",
// "com.sun.sgs.impl.service.watchdog.server.port",
// String.valueOf(requestedWatchdogPort),
// "com.sun.sgs.impl.service.channel.server.port",
// String.valueOf(getNextUniquePort()),
// "com.sun.sgs.impl.service.session.server.port",
// String.valueOf(getNextUniquePort()),
// "com.sun.sgs.impl.service.nodemap.client.port",
// String.valueOf(getNextUniquePort()),
// "com.sun.sgs.impl.service.watchdog.client.port",
// String.valueOf(getNextUniquePort()),
// "com.sun.sgs.impl.service.watchdog.server.renew.interval", "1500",
// "com.sun.sgs.impl.service.nodemap.server.port",
// String.valueOf(requestedNodeMapPort),
// LPADriver.GRAPH_CLASS_PROPERTY, "None",
// "com.sun.sgs.impl.service.nodemap.remove.expire.time", "1000",
// "com.sun.sgs.impl.service.task.continue.threshold", "10"
        );

        return retProps;
    }
	
}
