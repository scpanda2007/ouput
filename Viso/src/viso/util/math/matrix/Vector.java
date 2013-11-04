package viso.util.math.matrix;

public class Vector {
	
	public double m[];
	public Vector(int vectNum){
		m = new double[vectNum];
	}
	
	public Vector(Vector another){
		m = new double[another.m.length];
		for(int i=0;i<m.length;i++){
			m[i] = another.m[i];
		}
	}
	
	public void setValue(int col, Object obj){
		if(!(obj instanceof Double)){
			throw new IllegalStateException(" 类型不匹配 ");
		}
		if(col<0 || col>=m.length){
			throw new IllegalStateException(" 索引越界 ");
		}
		m[col] = (Double)obj; 
	}
	
	public void selfMul(double factor){
		for(int i=0;i<m.length;i++){
			m[i] *= factor;
		}
	}
	
	public void selfAdd(double factor){
		for(int i=0;i<m.length;i++){
			m[i] += factor;
		}
	}
	
	public void selfAdd(Vector another){
		assert this.m.length == another.m.length;
		for(int i=0;i<m.length;i++){
			m[i] += another.m[i];
		}
	}
	
	public void selfDel(double factor){
		selfAdd(0.0d-factor);
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(int i=0;i<m.length;i++){
			if(i>0)buffer.append(",");
			buffer.append(""+m[i]);
		}
		buffer.append("]");
		return buffer.toString();
	}
	
}
