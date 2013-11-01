package viso.util.math.matrix;

public class Matrix {
	
	public Double m[][];
	
	public Matrix(int rowNum, int colNum){
		m = new Double[rowNum][colNum];
	}
	
	protected boolean isAffine(){
		return true;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for(int i=0;i<m.length;i++){
			if(i>0)buffer.append("\n");
			for(int j=0;j<m[i].length;j++){
				if(j>0)buffer.append(",");
				buffer.append(""+m[i][j]);
			}
		}
		buffer.append("]\n");
		return buffer.toString();
	}
}
