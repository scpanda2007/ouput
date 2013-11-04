package viso.com.ogre.trans;

import java.util.List;

import viso.com.table.Table;
import viso.util.math.matrix.Matrix4;

public class Matrix4Converter {
	public static Matrix4 buildMatrix4(List<Double> list){
		Matrix4 matrix4x4 = new Matrix4();
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				matrix4x4.m[j][i] = list.get(i*4+j);
			}
		}
		return matrix4x4;
	}
	
	public static Table buildOgreBoneMatrixTable(List<Double> list){
		Matrix4 matrix4x4 = buildMatrix4(list);
		return buildOgreBoneMatrixTable(matrix4x4);
	}
	
	public static Table buildOgreBoneMatrixTable(Matrix4 matrix4x4){
		return (new OgreBoneMatrix4(matrix4x4)).buildTable();
	}
}
