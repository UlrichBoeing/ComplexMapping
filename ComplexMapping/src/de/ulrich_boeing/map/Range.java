package de.ulrich_boeing.map;

/**
 * A Range is a range of float values between start and end.<br>
 * A Range can
 * <ul>
 * <li>Translate it's range to 0-1 (normalize)</li>
 * <li>Translate the range 0-1 to start-end (deNormalize)</li>
 * <li>It can limit values to that start-end range.</li>
 * <li>It can repeat the range by translating a value outside the range to the
 * corresponding value inside the range.<br>
 * With the enum RepeatRange you can specify to repeat only before min,
 * repeat only after max, repeat on both sides or don't repeat at all.
 * </ul>
 * 
 * @author Ulrich Böing
 *
 */
public class Range {
	public enum RepeatRange {
		No, Min, Max, Both
	};

	private float start;
	private float end;
	private float range;
	private float min;
	private float max;

	RepeatRange repeat;
	// One ErrorAction for all Range-objects
	public static ErrorAction onExceedRange;

	public Range(float start, float end) {
		set(start, end);
		repeat = RepeatRange.Both;
	}

	public void set(float start, float end) {
		this.start = start;
		this.end = end;
		range = end - start;
		min = Math.min(start, end);
		max = Math.max(start, end);
	}

	public boolean equal(float start, float end) {
		return (this.start == start && this.end == end);
	}

	float checkRange(float x) {
		if (x < min) {
			errorHandling(x);
			if (repeat == RepeatRange.Both || repeat == RepeatRange.Min) {
				int n = (int) ((min - x) / Math.abs(range)) + 1;
				x += n * Math.abs(range);
			} else {
				x = min;
			}
		} else if (x > max) {
			errorHandling(x);
			if (repeat == RepeatRange.Both || repeat == RepeatRange.Max) {
				int n = (int) ((x - max) / Math.abs(range)) + 1;
				x -= n * Math.abs(range);
			} else {
				x = max;
			}
		}
		return x;
	}

	public float normalize(float x) {
		x = checkRange(x);
		return (x - start) / range;
	}

	public float deNormalize(float x) {
		x = start + x * range;
		return checkRange(x);
	}

	public float getStart() {
		return start;
	}

	public float getEnd() {
		return end;
	}

	public float getRange() {
		return range;
	}

	private void errorHandling(float x) {
		if (onExceedRange == ErrorAction.ErrorMsg) {
			System.err.println(getErrorMsg(x));
		} else if (onExceedRange == ErrorAction.Exception) {
			throw new RuntimeException(getErrorMsg(x));
		}
	}

	private String getErrorMsg(float x) {
		return "Value '" + x + "' is outside " + toString() + ".";

	}

	@Override
	public String toString() {
		return "range from '" + start + "' to '" + end + "'";
	}
}
