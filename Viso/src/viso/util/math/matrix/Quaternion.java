package viso.util.math.matrix;

import viso.util.math.method.Func;

public class Quaternion {
	
	public Quaternion(){}
	public Quaternion(Matrix3 m3x3){ build(m3x3);}
	public void build(Matrix3 m3x3){
		FromRotationMatrix(m3x3);
	}
	public Quaternion(double w, double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Double x,y,z,w;
	
	static int s_iNext[] = new int[]{ 1, 2, 0 };
	
	////////////////////////// Äæ×ª ///////////////////////////////
	
	public void FromAngleAxis(final double rfAngle, final Vector3 rkAxis) {
		// assert: axis[] is unit length
		//
		// The quaternion representing the rotation is
		// q = cos(A/2)+sin(A/2)*(x*i+y*j+z*k)
		double fHalfAngle = 0.5 * rfAngle;
		double fSin = Math.sin(fHalfAngle);
		w = Math.cos(fHalfAngle);
		x = fSin * rkAxis.getX();
		y = fSin * rkAxis.getY();
		z = fSin * rkAxis.getZ();
	}
	
	public Quaternion Inverse()
    {
        double fNorm = w*w+x*x+y*y+z*z;
        if ( fNorm > 0.0d )
        {
            double fInvNorm = 1.0d/fNorm;
            return new Quaternion(w*fInvNorm,-x*fInvNorm,-y*fInvNorm,-z*fInvNorm);
        }
        else
        {
            // return an invalid result to flag the error
            return null;
        }
    }
	
	void ToRotationMatrix (Matrix3 kRot)
    {
        double fTx  = x+x;
        double fTy  = y+y;
        double fTz  = z+z;
        double fTwx = fTx*w;
        double fTwy = fTy*w;
        double fTwz = fTz*w;
        double fTxx = fTx*x;
        double fTxy = fTy*x;
        double fTxz = fTz*x;
        double fTyy = fTy*y;
        double fTyz = fTz*y;
        double fTzz = fTz*z;

        kRot.m[0][0] = 1.0d-(fTyy+fTzz);
        kRot.m[0][1] = fTxy-fTwz;
        kRot.m[0][2] = fTxz+fTwy;
        kRot.m[1][0] = fTxy+fTwz;
        kRot.m[1][1] = 1.0d-(fTxx+fTzz);
        kRot.m[1][2] = fTyz-fTwx;
        kRot.m[2][0] = fTxz-fTwy;
        kRot.m[2][1] = fTyz+fTwx;
        kRot.m[2][2] = 1.0d-(fTxx+fTyy);
    }

	//////////////////////// Ë³×ª /////////////////////////////
	
	public void FromRotationMatrix (final Matrix3 kRot)
    {
        // Algorithm in Ken Shoemake's article in 1987 SIGGRAPH course notes
        // article "Quaternion Calculus and Fast Animation".
        double fTrace = kRot.m[0][0]+kRot.m[1][1]+kRot.m[2][2];
        double fRoot;

        if ( fTrace > 0.0d )
        {
            // |w| > 1/2, may as well choose w > 1/2
            fRoot = Math.sqrt(fTrace + 1.0d);  // 2w
            w = 0.5d*fRoot;
            fRoot = 0.5d/fRoot;  // 1/(4w)
            x = (kRot.m[2][1]-kRot.m[1][2])*fRoot;
            y = (kRot.m[0][2]-kRot.m[2][0])*fRoot;
            z = (kRot.m[1][0]-kRot.m[0][1])*fRoot;
        }
        else
        {
            // |w| <= 1/2
            int i = 0;
            if ( kRot.m[1][1] > kRot.m[0][0] )
                i = 1;
            if ( kRot.m[2][2] > kRot.m[i][i] )
                i = 2;
            int j = s_iNext[i];
            int k = s_iNext[j];

            fRoot = Math.sqrt(kRot.m[i][i]-kRot.m[j][j]-kRot.m[k][k] + 1.0d);
            setValue(i,0.5f*fRoot);
            fRoot = 0.5f/fRoot;
            w = (kRot.m[k][j]-kRot.m[j][k])*fRoot;
            setValue(j,(kRot.m[j][i]+kRot.m[i][j])*fRoot);
            setValue(k, (kRot.m[k][i]+kRot.m[i][k])*fRoot);
        }
    }
	
	private void setValue(int idx, double value){
		switch(idx){
		case 1:y = value;break;
		case 2:z = value;break;
		case 0:x = value;break;
		}
	}

	
	public double ToAngleAxis(Vector3 rkAxis)
    {
        // The quaternion representing the rotation is
        //   q = cos(A/2)+sin(A/2)*(x*i+y*j+z*k)
		double fSqrLength = x*x+y*y+z*z;
        double rfAngle;
        if ( fSqrLength > 0.0d )
        {
            rfAngle = 2.0d*Math.acos(w);
            double fInvLength = Func.invSqrt(fSqrLength);
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
        
        return rfAngle;
    }
	
	//////////////////////////////////////////////////////////////
	
	Vector3 mul(final Vector3 v)
    {
		// nVidia SDK implementation
		Vector3 uv, uuv;
		Vector3 qvec = new Vector3(x, y, z);
		uv = qvec.crossProduct(v);
		uuv = qvec.crossProduct(uv);
		uv = uv.mul(2.0d * w);
		uuv = uuv.mul(2.0f);

		return v.add(uv).add(uuv);

    }
	
	public String toString(){
		return "[x:"+x+" y:"+y+" z:"+z+" w:"+w+"]";
	}


}
