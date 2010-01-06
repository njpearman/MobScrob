/**
 * 
 */
package mobscrob.util;

/**
 * @author Neill
 * 
 */
public class StringUtil {

	private StringUtil() {
	}

	public static CompareResult compare(String value, String compare) {
		if (value == null && compare == null) {
			return CompareResult.EQUALS;
		} else if (value == null) {
			return CompareResult.LESS_THAN;
		} else if (compare == null) {
			return CompareResult.GREATER_THAN;
		}

		byte[] valueBytes = value.toLowerCase().getBytes();
		byte[] compareBytes = compare.toLowerCase().getBytes();
		CompareResult result = CompareResult.EQUALS;

		for (int i = 0; i < compareBytes.length; i++) {
			if (valueBytes.length < i) {
				return result;
			}
			result = compare(valueBytes[i], compareBytes[i]);
			if (CompareResult.GREATER_THAN.equals(result)
					|| CompareResult.LESS_THAN.equals(result)) {
				return result;
			} else {
				result = CompareResult.EQUALS;
			}
		}

		return result;
	}

	public static CompareResult compare(byte value, byte compare) {
		if (compare < value) {
			return CompareResult.LESS_THAN;
		} else if (compare > value) {
			return CompareResult.GREATER_THAN;
		}
		return CompareResult.EQUALS;
	}

	public static class CompareResult {
		public static CompareResult LESS_THAN = new CompareResult(-1);
		public static CompareResult EQUALS = new CompareResult(0);
		public static CompareResult GREATER_THAN = new CompareResult(1);

		private int val;

		private CompareResult(int val) {
			this.val = val;
		}

		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof CompareResult)) {
				return false;
			}
			return ((CompareResult) obj).val == this.val;
		}
	}

}
