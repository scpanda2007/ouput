package viso.test;

import viso.test.framework.util.TestProperties;

public class VisoTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println(" time ===> "+(System.currentTimeMillis()/1000));
//		Double a = 0.0d;
//		Double b = 0.0000000001d;
//		System.out.println(a.equals(0.0d) ? "yes":"hehe");
//		System.out.println(b.equals(0.0d) ? "yes":"hehe");
//		System.out.println(" sin(3.1415926/6) is "+Math.sin(3.1415926/6));
//		System.out.println(" asin(0.5) is "+Math.asin(0.5));
//		System.out.println(" Math.acos(0.7) is "+Math.acos(0.7));
		TestProperties.getProperties();
//		Properties properties = new Properties();
//		File file = new File(TestStandardProperties.loggerConfFile);
//		if(!file.exists()){
//			OutputStream out;
//			try {
//				file.createNewFile();
//				out = new BufferedOutputStream(new FileOutputStream(TestStandardProperties.loggerConfFile));
//				properties.put("java.util.logging.ConsoleHandler.level", "ALL");
//				properties.store(out, "create local.properties...");
//				out.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				throw new IllegalStateException("failed to create properties file");
//			}
//		}
	}

}
