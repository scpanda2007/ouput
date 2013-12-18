package viso.sbeans.framework.store.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.junit.Test;

import viso.sbeans.framework.store.data.DataObject;

class TestObject implements Serializable, DataObject{
	private static final long serialVersionUID = 1L;
	private int a;
	private String test;
	public TestObject(int a, String test){
		this.a = a;
		this.test = test;
	}
	
	@Override
	public String toString(){
		return "a:"+a+" string:"+test;
	}
}

class TestObject2 implements Serializable, DataObject{
	private static final long serialVersionUID = 1L;
	private int a;
	private String test;
	public TestObject2(int a, String test){
		this.a = a;
		this.test = test;
	}
	
	@Override
	public String toString(){
		return "a:"+a+" string:"+test;
	}
}

public class TestObjectIOStream {
	
	@Test(expected=ClassCastException.class)
	public void testIO(){
		ObjectStreamClass streamClass = ObjectStreamClass.lookup(TestObject2.class);
		TestObject test = new TestObject(12345,"abcd");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			TestObjectOutputStream oos = new TestObjectOutputStream(baos);
			oos.writeObject(test);
			oos.flush();
			byte in[] = baos.toByteArray();
			ObjectInputStream ois = new TestObjectInputStream(new ByteArrayInputStream(in),streamClass);
			TestObject test2 = (TestObject)ois.readObject();
			System.out.println("TestObject:"+test2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class TestObjectInputStream extends ObjectInputStream{

		ObjectStreamClass objectStreamClass;
		
		public TestObjectInputStream(InputStream in,ObjectStreamClass osc) throws IOException {
			super(in);
			// TODO Auto-generated constructor stub
			this.objectStreamClass = osc;
			
		}
		
		protected ObjectStreamClass readClassDescriptor() throws IOException{
			this.readByte();
			return objectStreamClass;
		}
		
	}
	
	private class TestObjectOutputStream extends ObjectOutputStream{

		public TestObjectOutputStream(OutputStream out) throws IOException {
			super(out);
			// TODO Auto-generated constructor stub
			AccessController.doPrivileged(
					new PrivilegedAction<Void>() {
					    public Void run() {
						enableReplaceObject(true);
						return null;
					    }
					});
		}
		
		protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException{
			this.writeByte(1);
		}
		
		protected Object replaceObject(Object object) throws IOException {
			return object;
		}
		
	}
	
}
