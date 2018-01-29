package de.ulrich_boeing.map;

public class Range {
	private float start;
	private float end;
	private float range;
	private float min;
	private float max;

	public static ErrorAction onExceedRange;

	public Range(float start, float end) {
		set(start, end);
//		onExceedRange = ErrorAction.ErrorMsg;
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
	
	float restrictToRange(float x) {
		if (x < min) {
			errorHandling(x);
			x = min;
		} else if (x > max) {
			errorHandling(x);
			x = max;
		}
		return x;
	}

	public float normalize(float x) {
		x = restrictToRange(x);
		return (x - start) / range;
	}

	public float deNormalize(float x) {
		x =  start + x * range;
		return restrictToRange(x);
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
		return "Range from '" + start + "' to '" + end + "'";
	}
}