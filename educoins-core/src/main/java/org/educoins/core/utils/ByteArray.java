package org.educoins.core.utils;


import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class ByteArray {

	/**
	 * Converts a short value into a byte array.
	 * 
	 * @param value
	 *            The short value to convert.
	 * @return The byte array of the short value.
	 */
	public static final byte[] convertFromShort(short value) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Short.BYTES);
		byteBuffer.putShort(value);
		byte[] byteArray = byteBuffer.array();
		return byteArray;
	}

	/**
	 * Converts a byte array into a short value.
	 * 
	 * @param byteArray
	 *            The byte array to convert.
	 * @return The short value of the byte array.
	 */
	public static final short convertToShort(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		short value = byteBuffer.getShort();
		return value;
	}

	/**
	 * Converts an integer value into a byte array.
	 * 
	 * @param value
	 *            The integer value to convert.
	 * @return The byte array of the integer value.
	 */
	public static final byte[] convertFromInt(int value) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES);
		byteBuffer.putInt(value);
		byte[] byteArray = byteBuffer.array();
		return byteArray;
	}

	/**
	 * Converts a byte array into an integer value.
	 * 
	 * @param byteArray
	 *            The byte array to convert.
	 * @return The integer value of the byte array.
	 */
	public static final int convertToInt(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		int value = byteBuffer.getInt();
		return value;
	}

	/**
	 * Converts a long integer value into a byte array.
	 * 
	 * @param value
	 *            The long integer value to convert.
	 * @return The byte array of the long integer value.
	 */
	public static final byte[] convertFromLong(long value) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
		byteBuffer.putLong(value);
		byte[] byteArray = byteBuffer.array();
		return byteArray;
	}

	/**
	 * Converts a byte array into a long integer value.
	 * 
	 * @param byteArray
	 *            The byte array to convert.
	 * @return The long integer value of the byte array.
	 */
	public static final long convertToLong(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		long value = byteBuffer.getLong();
		return value;
	}

	/**
	 * Converts a float value into a byte array.
	 * 
	 * @param value
	 *            The float value to convert.
	 * @return The float value of the byte array.
	 */
	public static final byte[] convertFromFloat(float value) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Float.BYTES);
		byteBuffer.putFloat(value);
		byte[] byteArray = byteBuffer.array();
		return byteArray;
	}

	/**
	 * Converts a byte array into a float value.
	 * 
	 * @param byteArray
	 *            The byte array to convert.
	 * @return The float value of the byte array.
	 */
	public static final float convertToFloat(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		float value = byteBuffer.getFloat();
		return value;
	}

	/**
	 * Converts a double value into a byte array.
	 * 
	 * @param value
	 *            The double value to convert.
	 * @return The double value of the byte array.
	 */
	public static final byte[] convertFromDouble(double value) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
		byteBuffer.putDouble(value);
		byte[] byteArray = byteBuffer.array();
		return byteArray;
	}

	/**
	 * Converts a byte array into a double value.
	 * 
	 * @param byteArray
	 *            The byte array to convert.
	 * @return The double value of the byte array.
	 */
	public static final double convertToDouble(byte[] byteArray) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		double value = byteBuffer.getDouble();
		return value;
	}

	/**
	 * Converts a decimal string representation of a value into a byte array.
	 * 
	 * @param value
	 *            The value to convert.
	 * @return The byte array of the value.
	 */
	public static final byte[] convertFromString(String value) {
		return convertFromString(value, 16);
	}

	/**
	 * Converts a string representation of a value with the given radix into a
	 * byte array.
	 * 
	 * @param value
	 *            The value to convert.
	 * @param radix
	 *            The radix to be used.
	 * @return The byte array of the value.
	 */
	public static final byte[] convertFromString(String value, int radix) {
		BigInteger convertedValue = new BigInteger(value, radix);
		byte[] byteArray = convertedValue.toByteArray();
		if (byteArray.length > 1 && byteArray[0] == 0){
			byteArray = Arrays.copyOfRange(byteArray, 1, byteArray.length);
		}
		return byteArray;
	}

	/**
	 * Converts a byte array into a string representation of the value in the
	 * decimal format.
	 * 
	 * @param byteArray
	 *            The byte array to convert.
	 * @return The string representation of the value of the byte array.
	 */
	public static final String convertToString(byte[] byteArray) {
		return ByteArray.convertToString(byteArray, 16);
	}

	/**
	 * Converts a byte array into a string representation of the value in the
	 * given radix.
	 * 
	 * @param byteArray
	 *            The byte array to convert.
	 * @param radix
	 *            The radix to be used.
	 * @return The string representation of the value of the byte array.
	 */
	public static final String convertToString(byte[] byteArray, int radix) {
		BigInteger value = new BigInteger(1, byteArray);
		String stringRepresenation = value.toString(radix);
		return stringRepresenation;
	}

	/**
	 * Compares two byte arrays.
	 * 
	 * @param x
	 *            The first byte array.
	 * @param y
	 *            The second byte array.
	 * @return 0 if the byte arrays are equal, 1 if the first byte array is
	 *         greater or -1 if the second byte array is greater.
	 */
	public final static int compare(byte[] x, byte[] y) {
		if (x == null && y == null) {
			return 0;
		} else if (y == null) {
			return 1;
		} else if (x == null) {
			return -1;
		}

		int indexX = 0;
		int indexY = 0;
		int maxIndex = Math.min(x.length, y.length);

		if (x.length > y.length){
			for(byte c : Arrays.copyOfRange(x, 0, x.length - y.length)){
				if (c != 0)
					return 1;
			}
			indexX = x.length - y.length;
		}
		if (x.length < y.length){
			for(byte c : Arrays.copyOfRange(y, 0, y.length - x.length)){
				if (c != 0)
					return -1;
			}
			indexY = y.length - x.length;
		}
		for (int i = 0; i < maxIndex; i++){
			int a = Byte.compare(x[i+indexX], y[i+indexY]);
			if (a != 0) return Integer.compare(a, 0);
		}
		return 0;
	}

	/**
	 * Concatenates byte arrays.
	 * 
	 * @param byteArrays
	 *            The byte arrays to concatenate.
	 * @return The concatenated byte array.
	 */
	public static final byte[] concatByteArrays(byte[]... byteArrays) {
		int length = 0;
		for (byte[] bArray : byteArrays) {
			length += bArray.length;
		}
		byte[] byteArray = new byte[length];
		int index = 0;
		for (byte[] bArray : byteArrays) {
			System.arraycopy(bArray, 0, byteArray, index, bArray.length);
			index += bArray.length;
		}
		return byteArray;
	}
}
