package viso.com.ogre.trans;

import java.util.List;

import viso.util.math.matrix.Matrix4;

public class Matrix4Converter {
	public static Matrix4 buildMatrix4(List<Double> list){
		Matrix4 matrix4x4 = new Matrix4();
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++){
				matrix4x4.m[i][j] = new Double(list.get(i*4+j));
			}
		}
		return matrix4x4;
	}
}
