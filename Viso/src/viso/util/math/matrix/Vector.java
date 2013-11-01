package viso.util.math.matrix;

public class Vector {
	
	public Double m[];
	public Vector(int vectNum){
		m = new Double[vectNum];
	}
	
	public void setValue(int col, Object obj){
		if(!(obj instanceof Double)){
			throw new IllegalStateException(" ���Ͳ�ƥ�� ");
		}
		if(col<0 || col>=m.length){
			throw new IllegalStateException(" ����Խ�� ");
		}
		m[col] = new Double((Double)obj); 
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
