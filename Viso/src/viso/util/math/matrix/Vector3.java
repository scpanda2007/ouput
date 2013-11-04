package viso.util.math.matrix;

public class Vector3 extends Vector{

	public Vector3(){
		super(3);
	}
	
	public Vector3(Vector3 another){
		super(another);
	}
	
	public Vector3(Object o1, Object o2, Object o3){
		super(3);
		build(o1,o2,o3);
	}
	
	public void build(Object o1, Object o2, Object o3){
		setValue(0, o1);
		setValue(1, o2);
		setValue(2, o3);
	}
	
	public double getX(){ return m[0];}
	public double getY(){ return m[1];}
	public double getZ(){ return m[2];}
	
	public Vector3 crossProduct(final Vector3 rkVector )
    {
        return new Vector3(
            this.getY() * rkVector.getZ() - getZ() * rkVector.getY(),
            getZ() * rkVector.getX() - getX() * rkVector.getZ(),
            getX() * rkVector.getY() - getY() * rkVector.getX());
    }
	
	public Vector3 mul(double factor){
		Vector3 res = new Vector3(this);
		res.selfMul(factor);
		return res;
	}
	
	public Vector3 add(double factor){
		Vector3 res = new Vector3(this);
		res.selfAdd(factor);
		return res;
	}
	
	public Vector3 add(Vector another){
		Vector3 res = new Vector3(this);
		res.selfAdd(another);
		return res;
	}
	
	@SuppressWarnings("unused")
	private Vector3(int vectNum) {
		super(vectNum);
		// TODO Auto-generated constructor stub
	}

}
