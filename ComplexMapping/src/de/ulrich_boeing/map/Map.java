package de.ulrich_boeing.map;

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
	Range input, output, ratioRange;

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
	 * (An abstract method overwritten in ComplexMap and Graph.)
	 * 
	 * @param x
	 *            The value to map.
	 * @return The mapped value.
	 */
	public abstract float get(float x);

	/**
	 * Get the mapped value for x.
	 * 
	 * @param x
	 *            The value to map.
	 * @param ratio
	 *            The ratio between main and target map.<br>
	 *            (0 -> Map.get(x) <br>
	 *            1 -> targetMap.get(x))
	 * @return The mapped value.
	 */

	public float get(float x, float ratio) {
		float y1 = get(x);
		if (targetMap == null) {
			System.err.println("No targetMap defined, parameter ratio is ignored.");
			return y1;
		} else {
			float y2 = targetMap.get(x);
			return y1 + ratioRange.normalize(ratio) * (y2 - y1);
		}
	}

	/**
	 * Creates a map whose type is automatically detected.
	 * 
	 * @param str
	 *            The map-defining string
	 * @return The created map.
	 */
	public static Map create(String str) {
		ComplexMap complexMap = new ComplexMap(str);
		if (complexMap.isFast()) {
			return complexMap;
		} else {
			return create(complexMap, Precision.High);
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
		targetMap.setRange(input.getStart(), input.getEnd(), output.getStart(), output.getEnd());
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
		targetMap.setRange(input.getStart(), input.getEnd(), output.getStart(), output.getEnd());
		return this;
	}

	public Map setRange(float inputStart, float inputEnd, float outputStart, float outputEnd) {
		if (targetMap != null)
			targetMap.setRange(inputStart, inputEnd, outputStart, outputEnd);

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

	public String getDeviation(int samples) {
		if (targetMap == null) {
			return ("targetMap is necessary to calculate deviation.");
		} else {
			float maxDif = 0;
			float sumDif = 0;
			float step = input.getRange() / samples;
			for (float x = getInputStart(); x <= getInputEnd(); x += step) {
				float y1 = get(x);
				float y2 = targetMap.get(x);
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

	public void getPerformance(int iterations) {
		System.out.println("Elapsed time Map " + iterations + " : " + getMapPerformance(this, iterations) / 1000);
		if (targetMap != null) {
			System.out.println(
					"Elapsed time targetMap " + iterations + " : " + getMapPerformance(targetMap, iterations) / 1000);
		}
	}

	private long getMapPerformance(Map map, int iterations) {
		float value = input.getStart() + input.getRange() / 2;
		long start = System.nanoTime();
		for (int i = 0; i <= iterations; i++) {
			map.get(value);
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
