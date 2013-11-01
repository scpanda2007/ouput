package viso.util.math.matrix;

public class Matrix3 extends Matrix {

	public Matrix3() {
		super(3, 3);
	}

	@SuppressWarnings("unused")
	private Matrix3(int rowNum, int colNum) {
		super(rowNum, colNum);
		// TODO Auto-generated constructor stub
	}

	public void QDUDecomposition(Matrix3 kQ, Vector3 kD, Vector3 kU) {
		// Factor M = QR = QDU where Q is orthogonal, D is diagonal,
		// and U is upper triangular with ones on its diagonal.  Algorithm uses
		// Gram-Schmidt orthogonalization (the QR algorithm).
		//
		// If M = [ m0 | m1 | m2 ] and Q = [ q0 | q1 | q2 ], then
		//
		//   q0 = m0/|m0|
		//   q1 = (m1-(q0*m1)q0)/|m1-(q0*m1)q0|
		//   q2 = (m2-(q0*m2)q0-(q1*m2)q1)/|m2-(q0*m2)q0-(q1*m2)q1|
		//
		// where |V| indicates length of vector V and A*B indicates dot
		// product of vectors A and B.  The matrix R has entries
		//
		//   r00 = q0*m0  r01 = q0*m1  r02 = q0*m2
		//   r10 = 0      r11 = q1*m1  r12 = q1*m2
		//   r20 = 0      r21 = 0      r22 = q2*m2
		//
		// so D = diag(r00,r11,r22) and U has entries u01 = r01/r00,
		// u02 = r02/r00, and u12 = r12/r11.

		// Q = rotation
		// D = scaling
		// U = shear

		// D stores the three diagonal entries r00, r11, r22
		// U stores the entries U[0] = u01, U[1] = u02, U[2] = u12

		// build orthogonal matrix Q
		Double fInvLength =  m[0][0] * m[0][0] + m[1][0] * m[1][0] + m[2][0]
				* m[2][0] ;
		//TODO: need compare 
		if (!fInvLength.equals(0.0d)) {
			fInvLength = Math.sqrt(fInvLength);
		}

		kQ.m[0][0] = m[0][0] * fInvLength ;
		kQ.m[1][0] = m[1][0] * fInvLength ;
		kQ.m[2][0] = m[2][0] * fInvLength ;

		Double fDot = kQ.m[0][0] * m[0][1] + kQ.m[1][0] * m[1][1] + kQ.m[2][0]
				* m[2][1] ;
		kQ.m[0][1] = m[0][1] - fDot * kQ.m[0][0];
		kQ.m[1][1] = m[1][1] - fDot * kQ.m[1][0];
		kQ.m[2][1] = m[2][1] - fDot * kQ.m[2][0];
		fInvLength = new Double( kQ.m[0][1] * kQ.m[0][1] + kQ.m[1][1] * kQ.m[1][1]
				+ kQ.m[2][1] * kQ.m[2][1] );

		//TODO: need compare 
		if (!fInvLength.equals(0.0d)) {
			fInvLength = Math.sqrt(fInvLength);
		}

		kQ.m[0][1] *= fInvLength;
		kQ.m[1][1] *= fInvLength;
		kQ.m[2][1] *= fInvLength;

		fDot = kQ.m[0][0] * m[0][2] + kQ.m[1][0] * m[1][2] + kQ.m[2][0]
				* m[2][2];
		kQ.m[0][2] = m[0][2] - fDot * kQ.m[0][0];
		kQ.m[1][2] = m[1][2] - fDot * kQ.m[1][0];
		kQ.m[2][2] = m[2][2] - fDot * kQ.m[2][0];
		fDot = kQ.m[0][1] * m[0][2] + kQ.m[1][1] * m[1][2] + kQ.m[2][1]
				* m[2][2];
		kQ.m[0][2] -= fDot * kQ.m[0][1];
		kQ.m[1][2] -= fDot * kQ.m[1][1];
		kQ.m[2][2] -= fDot * kQ.m[2][1];
		fInvLength =  kQ.m[0][2] * kQ.m[0][2] + kQ.m[1][2] * kQ.m[1][2]
				+ kQ.m[2][2] * kQ.m[2][2] ;

		//TODO: need compare 
		if (!fInvLength.equals(0.0d)) {
			fInvLength = Math.sqrt(fInvLength);
		}

		kQ.m[0][2] *= fInvLength;
		kQ.m[1][2] *= fInvLength;
		kQ.m[2][2] *= fInvLength;

		// guarantee that orthogonal matrix has determinant 1 (no reflections)
		Double fDet = kQ.m[0][0] * kQ.m[1][1] * kQ.m[2][2] + kQ.m[0][1]
				* kQ.m[1][2] * kQ.m[2][0] + kQ.m[0][2] * kQ.m[1][0]
				* kQ.m[2][1] - kQ.m[0][2] * kQ.m[1][1] * kQ.m[2][0]
				- kQ.m[0][1] * kQ.m[1][0] * kQ.m[2][2] - kQ.m[0][0]
				* kQ.m[1][2] * kQ.m[2][1];

		if (fDet < 0.0d) {
			for (int iRow = 0; iRow < 3; iRow++)
				for (int iCol = 0; iCol < 3; iCol++)
					kQ.m[iRow][iCol] = -kQ.m[iRow][iCol];
		}

		// build "right" matrix R
		Matrix3 kR = new Matrix3();

		kR.m[0][0] = kQ.m[0][0] * m[0][0] + kQ.m[1][0] * m[1][0] + kQ.m[2][0]
				* m[2][0];
		kR.m[0][1] = kQ.m[0][0] * m[0][1] + kQ.m[1][0] * m[1][1] + kQ.m[2][0]
				* m[2][1];
		kR.m[1][1] = kQ.m[0][1] * m[0][1] + kQ.m[1][1] * m[1][1] + kQ.m[2][1]
				* m[2][1];
		kR.m[0][2] = kQ.m[0][0] * m[0][2] + kQ.m[1][0] * m[1][2] + kQ.m[2][0]
				* m[2][2];
		kR.m[1][2] = kQ.m[0][1] * m[0][2] + kQ.m[1][1] * m[1][2] + kQ.m[2][1]
				* m[2][2];
		kR.m[2][2] = kQ.m[0][2] * m[0][2] + kQ.m[1][2] * m[1][2] + kQ.m[2][2]
				* m[2][2];

		// the scaling component
		kD.m[0] = kR.m[0][0];
		kD.m[1] = kR.m[1][1];
		kD.m[2] = kR.m[2][2];

		// the shear component
		Double fInvD0 = 1.0d / kD.m[0];
		kU.m[0] = kR.m[0][1] * fInvD0;
		kU.m[1] = kR.m[0][2] * fInvD0;
		kU.m[2] = kR.m[1][2] / kD.m[1];
	}
}
