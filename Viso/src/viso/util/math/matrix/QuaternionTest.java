package viso.util.math.matrix;

import junit.framework.TestCase;

public class QuaternionTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFromAngleAxis() {
//		fail("Not yet implemented");
		Quaternion qua = new Quaternion();
		Vector3 rkAxis = new Vector3(0.0, -1.0000000000000002d,0.0);
		double angle = 1.5907976603682872d;
		qua.FromAngleAxis(angle, rkAxis);
		System.out.println("call FromAngleAxis");
		System.out.println("qua is : "+qua.toString());
		System.out.println("rkAxis is : "+rkAxis.toString());
		System.out.println("angle is : "+angle+"\n");

	}

	public void testToAngleAxis() {
//		fail("Not yet implemented");
		Quaternion qua = new Quaternion(0.7,0.0,-0.7,0.0);
		Vector3 rkAxis = new Vector3();
		double angle = qua.ToAngleAxis(rkAxis);
		System.out.println("call ToAngleAxis");
		System.out.println("qua is : "+qua.toString());
		System.out.println("rkAxis is : "+rkAxis.toString());
		System.out.println("angle is : "+angle+"\n");
	}

}
