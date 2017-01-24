package nighttimedriver.chat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;

/**
 * A class with helpful class (static) members.
 * 
 * @author NighttimeDriver50000
 * 
 */
public class Util {

	/**
	 * The port used for Chat's client-server communication. If this changes at
	 * any point, then all versions after the change will not be compatible with
	 * versions before the change. Value: {@value}
	 */
	public static final int PORT = 6447;

	/**
	 * Creates a formatted string using the specified format string and
	 * arguments.
	 * <p>
	 * The locale always used is the one returned by Locale.getDefault().
	 * </p>
	 * 
	 * @param format
	 *            A format string as described in <a href=
	 *            "http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html#syntax"
	 *            >Format string syntax</a>.
	 * @param args
	 *            Arguments referenced by the format specifiers in the format
	 *            string. If there are more arguments than format specifiers,
	 *            the extra arguments are ignored. The number of arguments is
	 *            variable and may be zero. The maximum number of arguments is
	 *            limited by the maximum dimension of a Java array as defined by
	 *            The Java Virtual Machine Specification. The behaviour on a
	 *            null argument depends on the conversion.
	 * @return The formatted string
	 * @throws IllegalFormatException
	 *             If a format string contains an illegal syntax, a format
	 *             specifier that is incompatible with the given arguments,
	 *             insufficient arguments given the format string, or other
	 *             illegal conditions. For specification of all possible
	 *             formatting errors, see the Details section of the Formatter
	 *             class specification.
	 * @throws NullPointerException
	 *             If the format is null
	 */
	public static String format(final String format, final Object... args) {
		final StringWriter stringWriter = new StringWriter();
		new PrintWriter(stringWriter, true).format(format, args);
		return stringWriter.toString();
	}

	/**
	 * Returns a new string that is a copy of the given string, excluding all of
	 * the given characters.
	 * 
	 * @param s
	 *            the string to remove the breaks from
	 * @param chars
	 *            the characters to remove
	 * @return a copy of <code>s</code>, excluding all of the given characters
	 */
	public static String removeChars(final String s, final Character... chars) {
		if (s == null || chars == null || chars.length < 1)
			return s;
		final List<Character> charList = new ArrayList<Character>(
				Arrays.asList(Util.boxArray(s.toCharArray())));
		final List<Character> blackList = Arrays.asList(chars);
		if (charList.removeAll(blackList))
			return new String(unboxArray(charList.toArray(new Character[0])));
		return s;
	}

	/**
	 * Returns a new string that is a copy of the given string, excluding all of
	 * the given characters.
	 * 
	 * @param s
	 *            the string to remove the breaks from
	 * @param chars
	 *            the characters to remove
	 * @return a copy of <code>s</code>, excluding all of the given characters
	 */
	public static String removeChars(final String s, final char[] chars) {
		if (chars == null)
			return s;
		return removeChars(s, boxArray(chars));
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Byte[] boxArray(final byte[] bytes) {
		final Byte[] byteBox = new Byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			byteBox[i] = bytes[i];
		return byteBox;
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Short[] boxArray(final short[] shorts) {
		final Short[] shortBox = new Short[shorts.length];
		for (int i = 0; i < shorts.length; i++)
			shortBox[i] = shorts[i];
		return shortBox;
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Integer[] boxArray(final int[] ints) {
		final Integer[] intBox = new Integer[ints.length];
		for (int i = 0; i < ints.length; i++)
			intBox[i] = ints[i];
		return intBox;
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Long[] boxArray(final long[] longs) {
		final Long[] longBox = new Long[longs.length];
		for (int i = 0; i < longs.length; i++)
			longBox[i] = longs[i];
		return longBox;
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Float[] boxArray(final float[] floats) {
		final Float[] floatBox = new Float[floats.length];
		for (int i = 0; i < floats.length; i++)
			floatBox[i] = floats[i];
		return floatBox;
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Double[] boxArray(final double[] doubles) {
		final Double[] doubleBox = new Double[doubles.length];
		for (int i = 0; i < doubles.length; i++)
			doubleBox[i] = doubles[i];
		return doubleBox;
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Boolean[] boxArray(final boolean[] booleans) {
		final Boolean[] booleanBox = new Boolean[booleans.length];
		for (int i = 0; i < booleans.length; i++)
			booleanBox[i] = booleans[i];
		return booleanBox;
	}

	/**
	 * boxes an array of primitives into their boxed type
	 */
	public static Character[] boxArray(final char[] chars) {
		final Character[] charBox = new Character[chars.length];
		for (int i = 0; i < chars.length; i++)
			charBox[i] = chars[i];
		return charBox;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static byte[] unboxArray(final Byte[] bytes) {
		final byte[] byteArray = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++)
			byteArray[i] = bytes[i];
		return byteArray;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static short[] unboxArray(final Short[] shorts) {
		final short[] shortArray = new short[shorts.length];
		for (int i = 0; i < shorts.length; i++)
			shortArray[i] = shorts[i];
		return shortArray;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static int[] unboxArray(final Integer[] ints) {
		final int[] intArray = new int[ints.length];
		for (int i = 0; i < ints.length; i++)
			intArray[i] = ints[i];
		return intArray;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static long[] unboxArray(final Long[] longs) {
		final long[] longArray = new long[longs.length];
		for (int i = 0; i < longs.length; i++)
			longArray[i] = longs[i];
		return longArray;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static float[] unboxArray(final Float[] floats) {
		final float[] floatArray = new float[floats.length];
		for (int i = 0; i < floats.length; i++)
			floatArray[i] = floats[i];
		return floatArray;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static double[] unboxArray(final Double[] doubles) {
		final double[] doubleArray = new double[doubles.length];
		for (int i = 0; i < doubles.length; i++)
			doubleArray[i] = doubles[i];
		return doubleArray;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static boolean[] unboxArray(final Boolean[] booleans) {
		final boolean[] booleanArray = new boolean[booleans.length];
		for (int i = 0; i < booleans.length; i++)
			booleanArray[i] = booleans[i];
		return booleanArray;
	}

	/**
	 * unboxes an array of boxed types into their primitives
	 */
	public static char[] unboxArray(final Character[] chars) {
		final char[] charArray = new char[chars.length];
		for (int i = 0; i < chars.length; i++)
			charArray[i] = chars[i];
		return charArray;
	}

}
