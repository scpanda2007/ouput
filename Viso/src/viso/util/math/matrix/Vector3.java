package viso.util.math.matrix;

public class Vector3 extends Vector{

	public Vector3(){
		super(3);
	}
	
	public Vector3(Object o1, Object o2, Object o3){
		super(3);
		build(o1,o2,o3);
	}
	
	public void build(Object o1, Object o2, Object o3){
		setValue(0, o1);
		setValue(0, o2);
		setValue(0, o3);
	}
	
	@SuppressWarnings("unused")
	private Vector3(int vectNum) {
		super(vectNum);
		// TODO Auto-generated constructor stub
	}

}
