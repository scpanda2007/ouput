package viso.sbeans.framework.store.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import viso.sbeans.framework.service.session.test.TestClassCreatePrivate;
import viso.sbeans.framework.store.data.DataObject;

class TestObject implements Serializable, DataObject{
	private static final long serialVersionUID = 1L;
	protected int a;
	protected String test;
	public TestObject(int a, String test){
		this.a = a;
		this.test = test;
	}
	
	@Override
	public String toString(){
		return "a:"+a+" string:"+test;
	}
}

class TestObject3 extends TestObject{

	private int xx = 5;
	
	public TestObject3(int a, String test) {
		super(a, test);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 2L;
	
	@Override
	public String toString(){
		return "a:"+a+" string:"+test+" xx:"+xx;
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

class TestObject4 extends TestClassCreatePrivate implements Serializable, DataObject{
	private static final long serialVersionUID = 1L;
	private int a;
	private String test;
	
	public TestObject4(int a, String test){
		super(a);
		this.a = a;
		this.test = test;
	}
	
	@Override
	public String toString(){
		return "a:"+a+" string:"+test;
	}
}

public class TestObjectIOStream {
	
	ClassSerializer serializer = new ClassSerializer();
	
	@Test(expected=ClassCastException.class)
	public void testIO(){
		TestObject test = new TestObject(12345,"abcd");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		TestObjectOutputStream oos = null;
		try {
			oos = new TestObjectOutputStream(baos,test,serializer);
			oos.writeObject(test);
			oos.flush();
			byte in[] = baos.toByteArray();
			ObjectInputStream ois = new TestObjectInputStream(new ByteArrayInputStream(in),serializer);
			TestObject2 test2 = (TestObject2)ois.readObject();
			System.out.println("TestObject:"+test2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(oos!=null){
				try{
					oos.close();
				}catch(IOException e){
					
				}
			}
		}
	}
	
	@Test(expected=ClassCastException.class)
	public void testIOSub(){
		TestObject test = new TestObject(12345,"abcd");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		TestObjectOutputStream oos = null;
		try {
			oos = new TestObjectOutputStream(baos,test,serializer);
			oos.writeObject(test);
			oos.flush();
			byte in[] = baos.toByteArray();
			ObjectInputStream ois = new TestObjectInputStream(new ByteArrayInputStream(in),serializer);
			TestObject3 test2 = (TestObject3)ois.readObject();
			System.out.println("TestObject:"+test2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(oos!=null){
				try{
					oos.close();
				}catch(IOException e){
					
				}
			}
		}
	}
	
	@Test
	public void testIOSupper(){
		TestObject3 test = new TestObject3(12345,"abcd");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("TestObject actually is:"+test.toString());
		try {
			TestObjectOutputStream oos = new TestObjectOutputStream(baos,test,serializer);
			oos.writeObject(test);
			oos.flush();
			byte in[] = baos.toByteArray();
			ObjectInputStream ois = new TestObjectInputStream(new ByteArrayInputStream(in),serializer);
			TestObject test2 = (TestObject)ois.readObject();
			System.out.println("TestObject decode is :"+test2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void normalIO(){
		TestObject3 test = new TestObject3(12345,"abcd");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("TestObject actually is:"+test.toString());
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(test);
			oos.flush();
			byte in[] = baos.toByteArray();
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(in));
			TestObject3 test2 = (TestObject3)ois.readObject();
			System.out.println("TestObject decode is :"+test2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test(expected=InvalidClassException.class)
	public void privateCstorIO() throws InvalidClassException{//实验证明 序列化对于没有继承序列化的对象会调用其 无参构造函数, 不知道怎么不让这个异常抛出
		TestObject4 test = new TestObject4(12345,"abcd");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("TestObject actually is:"+test.toString());
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(test);
			oos.flush();
			byte in[] = baos.toByteArray();
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(in));
			TestObject4 test2 = (TestObject4)ois.readObject();
			System.out.println("TestObject decode is :"+test2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(e instanceof InvalidClassException){
				throw (InvalidClassException)e;
			}
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(oos!=null){
				try{
					oos.close();
				}catch(IOException e){
					
				}
			}
		}
	}
	
	@Test(expected=InvalidClassException.class)
	public void privateCstorIO2() throws InvalidClassException{//实验证明 序列化对于没有继承序列化的对象会调用其 无参构造函数
		TestObject4 test = new TestObject4(12345,"abcd");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.out.println("TestObject actually is:"+test.toString());
		TestObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new TestObjectOutputStream(baos,test,serializer);
			oos.writeObject(test);
			oos.flush();
			byte in[] = baos.toByteArray();
			ois = new TestObjectInputStream(new ByteArrayInputStream(in),serializer);
			TestObject4 test2 = (TestObject4)ois.readObject();
			System.out.println("TestObject decode is :"+test2.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(e instanceof InvalidClassException){
				throw (InvalidClassException) e;
			}else{
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(oos!=null){
				try{
					oos.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			if(ois!=null){
				try{
					ois.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public class ClassSerializer{
		Map<Integer,ObjectStreamClass> osClasses = new HashMap<Integer,ObjectStreamClass>();
		Map<ObjectStreamClass,Integer> registers = new HashMap<ObjectStreamClass,Integer>();
		AtomicInteger counter = new AtomicInteger(0);
	}
	
	private class TestObjectInputStream extends ObjectInputStream{

		ClassSerializer csl;
		
		public TestObjectInputStream(InputStream in,ClassSerializer csl) throws IOException {
			super(in);
			// TODO Auto-generated constructor stub
			this.csl = csl;
		}
		
		protected ObjectStreamClass readClassDescriptor() throws IOException{
			return csl.osClasses.get(this.readInt());
		}
		
	}
	
	private class TestObjectOutputStream extends ObjectOutputStream{

		private final DataObject topLevelObject;
		
		ClassSerializer csl;
		
		public TestObjectOutputStream(OutputStream out, DataObject topLevelObject, ClassSerializer csl) throws IOException {
			super(out);
			// TODO Auto-generated constructor stub
			AccessController.doPrivileged(
					new PrivilegedAction<Void>() {
					    public Void run() {
						enableReplaceObject(true);
						return null;
					    }
					});
			this.topLevelObject = topLevelObject;
			this.csl = csl;
		}
		
		protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException{
			int id = 0;
			if(!this.csl.registers.containsKey(desc)){
				id = this.csl.counter.getAndIncrement();
				this.csl.registers.put(desc, id);
				this.csl.osClasses.put(id, desc);
			}
			id = this.csl.registers.get(desc);
			this.writeInt(id);
		}
		
		protected Object replaceObject(Object object) throws IOException {
			if(object!=topLevelObject && object instanceof DataObject){
				throw new IOException("不能在 DataObject 中再包含DataObject实体.");
			}
			Class<?> cl = object.getClass();
			if(cl.isAnonymousClass()){
				System.out.println("发现一个匿名类");
			}
			if(cl.isLocalClass()){
				System.out.println("发现一个内部类");
			}
			return object;
		}
		
	}
	
}
