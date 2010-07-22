package com.sc2mod.andromeda.test;

/**
 * Emulates Galaxy's "fixed" data type.
 * @author XPilot
 */
public class Fixed {
	public static void main(String[] args) {
		Fixed f1 = fromDecimal(1.0 + 1.0 / FRACTION_FACTOR);
		Fixed f2 = fromDecimal(2.0 / FRACTION_FACTOR);
		
		test(f1, f2);
	}
	
	public static void test(Fixed f1, Fixed f2) {
		System.out.println("f1 = " + f1);
		
		System.out.println("f2 = " + f2);
		
		System.out.println("f1 + f2 = " + sum(f1, f2));
		System.out.println("f1 - f2 = " + difference(f1, f2));
		System.out.println("f1 * f2 = " + product(f1, f2));
		System.out.println("f1 / f2 = " + quotient(f1, f2));
		System.out.println("f1 % f2 = " + modulus(f1, f2));
	}
	
	private static final int FRACTION_BITS = 12;
	private static final int FRACTION_FACTOR = 1 << FRACTION_BITS;
	
	private long value;
	
	private Fixed(long value) {
		this.value = value;
	}
	
	public boolean isValid() {
		return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
	}
	
	public static Fixed parseFixed(String s) {
		return fromDecimal(Double.parseDouble(s));
	}
	
	public int decimalDigits() {
		int i = 0;
		while(i < FRACTION_BITS) {
			if((value & (1 << i)) != 0) break;
			i++;
		}
		return FRACTION_BITS - i;
	}
	
	@Override
	public String toString() {
		return String.format("%." + Math.max(decimalDigits(), 1) + "f", (double)value / FRACTION_FACTOR);
	}
	
	public static int toInt(Fixed f) {
		return (int)(f.value >> FRACTION_BITS);
	}
	
	public static Fixed fromInt(int i) {
		long value = i;
		return new Fixed(value << FRACTION_BITS);
	}
	
	public static Fixed fromDecimal(double d) {
		long value = (long)(d * FRACTION_FACTOR);
		return new Fixed(value);
	}
	
	public static Fixed sum(Fixed f1, Fixed f2) {
		return new Fixed(f1.value + f2.value);
	}
	
	public static Fixed difference(Fixed f1, Fixed f2) {
		return new Fixed(f1.value - f2.value);
	}
	
	public static Fixed product(Fixed f1, Fixed f2) {
		long value = f1.value * f2.value;
		value = value >> FRACTION_BITS;
		return new Fixed(value);
	}
	
	public static Fixed quotient(Fixed f1, Fixed f2) {
		long value = f1.value << FRACTION_BITS;
		value /= f2.value;
		return new Fixed(value);
	}
	
	public static Fixed modulus(Fixed f1, Fixed f2) {
		long value = f1.value % f2.value;
		return new Fixed(value);
	}
}
