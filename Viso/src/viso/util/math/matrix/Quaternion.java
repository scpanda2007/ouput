package viso.util.math.matrix;

public class Quaternion {
	public Quaternion(){}
	public Quaternion(Matrix3 m3x3){}
	public void build(Matrix3 m3x3){}
	
	public double ToAngleAxis(Vector3 rkAxis)
    {
        // The quaternion representing the rotation is
        //   q = cos(A/2)+sin(A/2)*(x*i+y*j+z*k)
		double x = 1.0f;
		double y = 1.0f;
		double z = 0.5f;//TODO: what is this
		double fSqrLength = x*x+y*y+z*z;
        double rfAngle;
        
        if ( fSqrLength > 0.0 )
        {
        	Double w = 0.0d;//TODO; what is this?
            rfAngle = 2.0*Math.cos(w);//TODO: error
            double fInvLength = Math.sqrt(fSqrLength);
            rkAxis.m[0] = x*fInvLength;
            rkAxis.m[1] = y*fInvLength;
            rkAxis.m[2] = z*fInvLength;
        }
        else
        {
            // angle is 0 (mod 2*pi), so any axis will do
            rfAngle = 0.0d;
            rkAxis.m[0] = 1.0d;
            rkAxis.m[1] = 0.0d;
            rkAxis.m[2] = 0.0d;
        }
        
        return rfAngle;//TODO: ´íÎóµÄ¸³Öµ
    }

}
