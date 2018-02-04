package de.ulrich_boeing.map;

import de.ulrich_boeing.map.Range.RepeatRange;

/**
 * The class Map is the abstract base class of the classes ComplexMap and
 * Graph.<br>
 * It allows both package private classes to be handled in a similar way and
 * defines variables and methods common to these two classes. Common variables
 * are i.a. the input, output and ratio-Range and a targetMap.
 * 
 * @author Ulrich Böing
 *
 */

abstract public class Map {
	private Map targetMap;
	private Range input, output, ratioRange;

	/**
	 * The Map constructor is only called by the ComplexMap and Graph
	 * constructor.<br>
	 * It initializes all ranges with (0,1).
	 */
	Map() {
		input = new Range(0, 1);
		output = new Range(0, 1);
		ratioRange = new Range(0, 1);
	}

	/**
	 * Get the mapped value for x.<br>
	 * 
	 * @param x
	 *            The value to map.
	 * @return The mapped value.
	 */
	public float map(float x) {
		x = input.normalize(x);
		return output.deNormalize(normMap(x));
	}

	// An abstract method overwritten in ComplexMap and Graph.
	abstract float normMap(float x);

	/**
	 * Get the mapped value for x.
	 * 
	 * @param x
	 *            The value to map.
	 * @param ratio
	 *            The ratio between main and target map.
	 *            <ul>
	 *            <li>0 -> Map.map(x)</li>
	 *            <li>1 -> targetMap.map(x))</li>
	 *            </ul>
	 * @return The mapped value.
	 */
	public float map(float x, float ratio) {
		float y1 = map(x);
		if (targetMap == null) {
			System.err.println("No targetMap defined, parameter ratio is ignored.");
			return y1;
		} else {
			float y2 = targetMap.map(x);
			return y1 + ratioRange.normalize(ratio) * (y2 - y1);
		}
	}

	/**
	 * Creates a map whose precision and therefore type is automatically detected.
	 * 
	 * @param str
	 *            The map-defining string
	 * @return The created map.
	 */
	public static Map create(String str) {
		ComplexMap complexMap = new ComplexMap(str);
		// if calculation of the ComplexMap is faster than the calculation of the Graph
		if (complexMap.isFast()) {
			return complexMap;
		} else {
			return create(complexMap, Graph.defaultPrecision);
		}
	}

	/**
	 * Creates a map whose precision and therefore type is given.
	 * 
	 * @param str
	 *            The map-defining string
	 * @param precision
	 *            The precision of the approximation.
	 * @return The created map.
	 */
	public static Map create(String str, Precision precision) {
		ComplexMap complexMap = new ComplexMap(str);
		return create(complexMap, precision);
	}

	/**
	 * Creates a map based on a ComplexMap with a given precision.
	 * 
	 * @param complexMap
	 *            The ComplexMap
	 * @param precision
	 *            The precision of the approximation.
	 * @return The new created Map of type ComplexMap or Graph
	 */
	private static Map create(ComplexMap complexMap, Precision precision) {
		if (precision == Precision.Highest) {
			return complexMap;
		} else {
			return new Graph(complexMap, precision.resolution);
		}
	}

	/**
	 * Creates a target map which type (ComplexMap, Graph) is automatically
	 * detected.
	 * 
	 * @param str
	 *            The map-defining string.
	 * @return The object the method is called on. (Not the target map).
	 */
	public Map setTargetMap(String str) {
		targetMap = Map.create(str);
		targetMap.setRange(input, output);
		return this;
	}

	/**
	 * Creates a map and assigns it to targetMap.
	 * 
	 * @param str
	 *            The map-defining string.
	 * @param precision
	 *            The precision of the mapped result.
	 * @return The object the method is called on. (Not the target map).
	 */
	public Map setTargetMap(String str, Precision precision) {
		targetMap = Map.create(str, precision);
		targetMap.setRange(input, output);
		return this;
	}

	private void setRange(Range input, Range output) {
		this.input = input;
		this.output = output;
	}

	public Map setRange(float inputStart, float inputEnd, float outputStart, float outputEnd, RepeatRange inputRepeat) {
		input.repeat = inputRepeat;
		return setRange(inputStart, inputEnd, outputStart, outputEnd);
	}

	public Map setRange(float inputStart, float inputEnd, float outputStart, float outputEnd) {
		input.set(inputStart, inputEnd);
		output.set(outputStart, outputEnd);
		return this;
	};

	public Map setRatioRange(float start, float end) {
		ratioRange.set(start, end);
		return this;
	}

	public float getInputStart() {
		return input.getStart();
	}

	public float getInputEnd() {
		return input.getEnd();
	}

	/**
	 * Map.getDeviation() gets the deviation of values between map and
	 * targetMap.<br>
	 * It calculates n=samples values of map and targetMap, and detects the maximum
	 * difference and calculates the average.
	 * 
	 * @param samples
	 *            The number of values of map and targetMap that are retrieved.
	 * @return A string containing the results.
	 */
	public String getDeviation(int samples) {
		if (targetMap == null) {
			return ("targetMap is necessary to calculate deviation.");
		} else {
			float maxDif = 0;
			float sumDif = 0;
			float step = input.getRange() / samples;
			for (float x = getInputStart(); x <= getInputEnd(); x += step) {
				float y1 = map(x);
				float y2 = targetMap.map(x);
				float dif = Math.abs(y2 - y1);
				sumDif += dif;
				if (dif > maxDif) {
					maxDif = dif;
				}
			}
			float averageDif = sumDif / (samples + 1);
			averageDif /= output.getRange();
			maxDif /= output.getRange();
			return "averageDif = " + averageDif + "; maxDif = " + maxDif;
		}
	}

	/**
	 * Measures the performance of Map and targetMap and writes it to System.out
	 * 
	 * @param iterations
	 *            The number of calculations to perform.
	 */
	public void getPerformance(int iterations) {
		System.out.println("Elapsed time Map " + iterations + " : " + getMapPerformance(this, iterations) / 1000);
		if (targetMap != null) {
			System.out.println(
					"Elapsed time targetMap " + iterations + " : " + getMapPerformance(targetMap, iterations) / 1000);
		}
	}

	/**
	 * Gets the performance of a single map.
	 * @param map The map to measure.
	 * @param iterations The number of calculations to perform.
	 * @return The execution time in nanoseconds.
	 */
	private long getMapPerformance(Map map, int iterations) {
		float value = input.getStart() + input.getRange() / 2;
		long start = System.nanoTime();
		for (int i = 0; i <= iterations; i++) {
			map.map(value);
		}
		long end = System.nanoTime();
		return end - start;
	}

	@Override
	public String toString() {
		String str = " input: " + input.toString() + "\n";
		str += " output: " + output.toString() + "\n";
		if (targetMap != null) {
			str += "targetMap " + targetMap.toString();
		}
		return str;
	}

}
