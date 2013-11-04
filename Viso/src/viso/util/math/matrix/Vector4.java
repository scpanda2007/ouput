package viso.util.math.matrix;

public class Vector4 extends Vector {
	
	public Vector4(){
		super(4);
	}
	
	public Vector4(Object o1, Object o2, Object o3, Object o4){
		super(4);
		setValue(0, o1);
		setValue(1, o2);
		setValue(2, o3);
		setValue(3, o4);
	}
	
	@SuppressWarnings("unused")
	private Vector4(int vectNum) {
		super(vectNum);
		// TODO Auto-generated constructor stub
	}

}
