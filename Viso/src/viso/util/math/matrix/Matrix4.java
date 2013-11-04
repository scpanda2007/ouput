package viso.util.math.matrix;

public class Matrix4 extends Matrix {

	public Matrix4(){
		super(4, 4);
	}
	
	@SuppressWarnings("unused")
	private Matrix4(int rowNum, int colNum) {
		super(rowNum, colNum);
		// TODO Auto-generated constructor stub
	}
	
	////////////////////// Ë³×ª ////////////////////////////
	
	public void extract3x3Matrix(Matrix3 m3x3)
    {
        m3x3.m[0][0] = m[0][0];
        m3x3.m[0][1] = m[0][1];
        m3x3.m[0][2] = m[0][2];
        m3x3.m[1][0] = m[1][0];
        m3x3.m[1][1] = m[1][1];
        m3x3.m[1][2] = m[1][2];
        m3x3.m[2][0] = m[2][0];
        m3x3.m[2][1] = m[2][1];
        m3x3.m[2][2] = m[2][2];
    }
	
	public String decomposition(Vector3 position, Vector3 scale, Quaternion orientation)
	{
		String result;
		assert(isAffine());

		Matrix3 m3x3 = new Matrix3();
		extract3x3Matrix(m3x3);
//		System.out.println("decomposition m3x3::"+m3x3.toString());
		Matrix3 matQ = new Matrix3();
		Vector3 vecU = new Vector3();
		m3x3.QDUDecomposition(matQ, scale, vecU); 
//		System.out.println("matQ rot3x3::"+matQ.toString());
		result = "\n matQ is :\n "+matQ.toString();
		orientation.build(matQ);
		position.build(m[0][3], m[1][3], m[2][3]);
		
		return result;
	}
	
	////////////////////////// Äæ×ª //////////////////////////////////
	
	public void makeInverseTransform(final Vector3 position, final Vector3 scale, final Quaternion orientation)
    {
        // Invert the parameters
        Vector3 invTranslate = position.mul(-1.0d);
        
        Vector3 invScale = new Vector3(1.0d / scale.getX(), 1.0d / scale.getY(), 1.0d / scale.getZ());
        Quaternion invRot = orientation.Inverse();

        // Because we're inverting, order is translation, rotation, scale
        // So make translation relative to scale & rotation
        invTranslate = invRot.mul(invTranslate); // rotate
        invTranslate = invTranslate.crossProduct(invScale); // scale

        // Next, make a 3x3 rotation matrix
        Matrix3 rot3x3 = new Matrix3();
        invRot.ToRotationMatrix(rot3x3);
        System.out.println("rot3x3 is ::"+rot3x3.toString());
        
        // Set up final matrix with scale, rotation and translation
        m[0][0] = invScale.getX() * rot3x3.m[0][0]; 
        m[0][1] = invScale.getX() * rot3x3.m[0][1]; 
        m[0][2] = invScale.getX() * rot3x3.m[0][2]; 
        m[0][3] = invTranslate.getX();
        
        m[1][0] = invScale.getY() * rot3x3.m[1][0];
        m[1][1] = invScale.getY() * rot3x3.m[1][1]; 
        m[1][2] = invScale.getY() * rot3x3.m[1][2]; 
        m[1][3] = invTranslate.getY();
        
        m[2][0] = invScale.getZ() * rot3x3.m[2][0]; 
        m[2][1] = invScale.getZ() * rot3x3.m[2][1]; 
        m[2][2] = invScale.getZ() * rot3x3.m[2][2]; 
        m[2][3] = invTranslate.getZ();		

        // No projection term
        m[3][0] = 0; 
        m[3][1] = 0; 
        m[3][2] = 0; 
        m[3][3] = 1;
    }


}
