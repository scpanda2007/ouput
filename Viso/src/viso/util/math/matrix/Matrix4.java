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

		Matrix3 matQ = new Matrix3();
		Vector3 vecU = new Vector3();
		m3x3.QDUDecomposition(matQ, scale, vecU); 
		
		result = "\n matQ is :\n "+matQ.toString();
		orientation.build(matQ);
		position.build(m[0][3], m[1][3], m[2][3]);
		
		return result;
	}

}
