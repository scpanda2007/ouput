package viso.com.ogre.trans;

import viso.com.table.Table;
import viso.util.math.matrix.Matrix4;
import viso.util.math.matrix.Quaternion;
import viso.util.math.matrix.Vector3;

public class OgreBoneMatrix4 {
	
	public final Vector3 position = new Vector3();
	public double angle;
	public final Vector3 axis = new Vector3();
	public final Vector3 scale = new Vector3();
	public final Quaternion orientation = new Quaternion();
	
	OgreBoneMatrix4(Matrix4 matrix4x4){
		build(matrix4x4);
	}
	
	OgreBoneMatrix4(){
		
	}
	
	public String build(Matrix4 matrix4x4){
		String desc = matrix4x4.decomposition(position, scale, orientation);
		angle = orientation.ToAngleAxis(axis);
		return desc;
	}
	
	public Matrix4 toMatrix4(){
		Matrix4 matrix4x4 = new Matrix4();
//		System.out.println(" old : "+orientation.toString());
//		orientation.FromAngleAxis(angle, axis); 
//		System.out.println(" new : "+orientation.toString());
		matrix4x4.makeInverseTransform(position, scale, orientation);
		return matrix4x4;
	}
	
	Table buildTable(){
		
		Table table = Table.createTable("boneInfo");
		Table postionTable = table.MapAndCreateTable("position");
		postionTable.MapFloat("x", new Float(this.position.m[0]));
		postionTable.MapFloat("y", new Float(this.position.m[1]));
		postionTable.MapFloat("z", new Float(this.position.m[2]));
		
		Table rotationTable = table.MapAndCreateTable("rotation");
		rotationTable.MapFloat("angle", new Float(this.angle));
		rotationTable.MapFloat("x", new Float(this.axis.m[0]));
		rotationTable.MapFloat("y", new Float(this.axis.m[1]));
		rotationTable.MapFloat("z", new Float(this.axis.m[2]));

		return table;
	}
}
