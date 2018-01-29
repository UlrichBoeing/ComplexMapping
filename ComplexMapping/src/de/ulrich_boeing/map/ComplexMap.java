package de.ulrich_boeing.map;

/**
 * The class ComplexMap is created by a string with one or more (mathematical)
 * operations.<br>
 * It maps input to output values according to this string.
 * 
 * @author Ulrich Böing
 *
 */

class ComplexMap extends Map {
	Mapping[] mappings;
	float sumWeight = 0;

	ComplexMap(String str) {
		super();
		set(str);
	}

	private void set(String str) {
		str = prepareString(str);
		String[] arr = str.split("\\s*&\\s*");

		int countMappings = arr.length;
		mappings = new Mapping[countMappings];
		for (int i = 0; i < countMappings; i++) {
			mappings[i] = new Mapping(arr[i]);
			sumWeight += mappings[i].weight;
		}
	}

	public float get(float x) {
		x = input.normalize(x);
		float sum = 0;
		for (Mapping mapping : mappings) {
			sum += mapping.map(x);
		}
		return output.deNormalize(sum / sumWeight);
	}

	boolean isFast() {
		int numSteps = numSteps();
		return numSteps <= 3 && numSteps == numFastSteps();
	}

	int numSteps() {
		int numSteps = 0;
		for (Mapping mapping : mappings) {
			numSteps += mapping.steps.length;
		}
		return numSteps;
	}

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
	 * @return A trimmed, lower case string with no <br>
	 *         empty operations (e.g. '> >' or '& >') <br>
	 *         and at least one "x".
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
		str += " input: " + input.toString() + "\n";
		str += " output: " + output.toString() + "\n";
		return str;
	}

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
