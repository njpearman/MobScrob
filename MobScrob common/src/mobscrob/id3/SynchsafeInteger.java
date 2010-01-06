/**
 * 
 */
package mobscrob.id3;

import java.io.IOException;

/**
 * A synch safe integer is a numeric value calculated from an array of bytes,
 * where the leading bit of each byte can only be 0 and is ignored. Hence the
 * maximum size of each byte is actually only 7 bits.
 * 
 * @author Neill
 * 
 */
public class SynchsafeInteger {

	// max value is 127
	private static final int MAX_VALUE = 0x7F;

	/**
	 * Utility class so no constructor needed
	 */
	private SynchsafeInteger() {
	}

	/**
	 * Calculates the sync safe value for the specified byte array as an int.
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static int valueOf(byte[] bytes) throws IOException {
		int val = 0;
		int[] array = new int[bytes.length];

		// first put bytes in unsigned form
		for (int i = 0; i < bytes.length; i++) {
			array[i] = 0xFF & bytes[i];
		}

		// iterate bytes, checking each byte's value and adding to val
		int factor;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > MAX_VALUE) {
				throw new IOException(
						"Maximum size for tag size byte is 127, got "
								+ array[i] + " at " + i);
			}

			factor = 7 * (array.length - i - 1);
			val |= array[i] << factor;
		}

		return val;
	}
}
