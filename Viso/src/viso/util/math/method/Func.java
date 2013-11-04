package viso.util.math.method;

public class Func {
	public static double invSqrt(double number){
		assert number != 0.0d;
		return Math.sqrt(1.0d/number);
	}
}
