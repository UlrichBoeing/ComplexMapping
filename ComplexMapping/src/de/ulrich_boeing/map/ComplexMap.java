package de.ulrich_boeing.map;

/**
 * The class ComplexMap is created by a string with one or more (mathematical)
 * operations. It maps input to output values according to this string.<br>
 * <br>
 * A sequence of steps is a Mapping (e.g. 'triangle > invert > exp'),<br>
 * a ComplexMap consists of one or more Mappings which are combined with an
 * '&'.<br>
 * The derived method Map.map() normalizes the input an calls normMap(). So
 * input for the normMap method is between 0-1 and so is the output.
 * 
 * 
 * @author Ulrich Böing
 *
 */

class ComplexMap extends Map {
	Mapping[] mappings;
	float sumWeight = 0;

	ComplexMap(String str) {
		super();
		build(str);
	}

	void build(String str) {
		str = prepareString(str);
		String[] arr = str.split("\\s*&\\s*");

		int countMappings = arr.length;
		mappings = new Mapping[countMappings];
		for (int i = 0; i < countMappings; i++) {
			mappings[i] = new Mapping(arr[i]);
		}
		sumWeight = getSumWeight();
	}

	private float getSumWeight() {
		sumWeight = 0;
		for (Mapping mapping : mappings) {
			sumWeight += mapping.weight;
		}
		return sumWeight;
	}

	// Input for the normMap method must be between 0-1 and so must be output.
	@Override
	float normMap(float x) {
		float sum = 0;
		for (Mapping mapping : mappings) {
			sum += mapping.normMap(x);
		}
		return sum / sumWeight;
	}

	/**
	 * A ComplexMap is calculated faster than a corresponding graph if there is a
	 * maximum of three steps and all three steps are calculated fast. All
	 * exponential and trigonometric functions are calculated slow.
	 */
	boolean isFast() {
		int numSteps = numSteps();
		return numSteps <= 3 && numSteps == numFastSteps();
	}

	/**
	 * 
	 * @return Number of steps across all mappings.
	 */
	int numSteps() {
		int numSteps = 0;
		for (Mapping mapping : mappings) {
			numSteps += mapping.steps.length;
		}
		return numSteps;
	}

	/**
	 * 
	 * @return Number of fast steps across all mappings.
	 */
	int numFastSteps() {
		int numFastSteps = 0;
		for (Mapping mapping : mappings) {
			for (Step step : mapping.steps) {
				if (step.type.fast) {
					numFastSteps++;
				}
			}
		}
		return numFastSteps;
	}

	/**
	 * A static method to prepare a map-defining string.
	 * 
	 * @param str
	 *            String to prepare.
	 * @return A trimmed, lower case string with at least one 'x' and no empty
	 *         operations: <br>
	 *         <ul>
	 *         <li>'> >' are replaced with a single '>'</li>
	 *         <li>'& >' and '> &' are replace with a single '&'</li>
	 *         </ul>
	 */
	private String prepareString(String str) {
		/*
		 * change all tabs and newLines in the form '\t' '\n' to ' '
		 */
		str = str.replaceAll("[\\t\\n]", " ");
		/*
		 * Remove all operators (>, &) and whitespace before and after the first operand
		 * (e.g. invert)
		 */
		str = str.replaceAll("^[ >&]+", "").replaceAll("[ >&]+$", "");

		/*
		 * Remove all ">>" or "> >" or combinations of these ">> >  > >" and replace
		 * with a single ">"
		 */
		str = str.replaceAll(">(\\s*>)+", ">");

		/*
		 * Remove all "& &" and "& >" and "> &" and replace with a single "&" Warning:
		 * This works only with the instruction before, otherwise all "> >" would also
		 * be replaced with "&"
		 */
		str = str.replaceAll("[&>](\\s*[&>])+", "&");

		str = str.toLowerCase();

		if (str.length() == 0) {
			str = "x";
		}
		return str;
	}

	@Override
	public String toString() {
		String str = "ComplexMap: '" + getRealDefString() + "' \n";
		str += " sumWeight = " + sumWeight + "\n";
		str += super.toString();
		return str;
	}

	/**
	 * A real definition String is a string which is prepared (e.g. no empty
	 * operations) and with specified parameters.
	 * 
	 * @return The real definition string.
	 */
	public String getRealDefString() {
		String str = "";
		for (int i = 0; i < mappings.length; i++) {
			str += mappings[i].toString();
			if (i < mappings.length - 1) {
				str += " & ";
			}
		}
		return str;
	}

}
