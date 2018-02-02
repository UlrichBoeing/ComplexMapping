package de.ulrich_boeing.map;

/**
 * The Step class holds a single step like "x", "invert" or "sin".<br> 
 * The array p stands for parameter, each step can have zero, one or more parameter.
 * Parameters can be separated by whitespace or comma.<br><br>
 * 
 * Input for the normMap method must be between 0-1 and so must be output.
 */
class Step {
	StepType type;
	float[] p;

	Step(String str) {
		build(str);
	}

	private void build(String str) {
		// e.g. "type value01, value 02"
		String[] arr = str.split("[\\s,]+");

		type = getStepType(arr[0]);
		p = getParameter(arr);
		p = type.evaluateParameter(p);
	}

	protected float normMap(float x) {
		return type.calculate(x, p);
	}

	private StepType getStepType(String str) {
		try {
			return StepType.valueOf(str);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("'" + str + "' is not a valid operation for Map.");
		}
	}
	
	private float[] getParameter(String[] arr) {
		tooManyParameter(arr.length - 1);

		float[] parameter = new float[type.parameterNeeded];
		for (int i = 0; i < parameter.length; i++) {
			// if a parameter is not given in the string array use default parameter
			if (i + 1 < arr.length) {
				parameter[i] = parseParameter(arr[i + 1]);
			} else {
				parameter[i] = type.defaults[i];
			}
		}
		return parameter;
	}

	private float parseParameter(String str) {
		try {
			return Float.parseFloat(str);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Parameter '" + str + "' for Step '" + type.toString()
					+ "' could not be converted into a float value.");
		}
	}
	
	private void tooManyParameter(int parameterCount) {
		if (parameterCount > type.parameterNeeded) {
			System.err.println("To many parameter: Step '" + type.name() + "' needs " + type.parameterNeeded
					+ " parameter, but " + parameterCount + " are given.");
		}
	}
	
	@Override
	public String toString() {
		String str = type.toString();
		for (int i = 0; i < p.length; i++) {
			if (i > 0) {
				str += ",";
			}
			str += " " + Float.toString(p[i]);
		}
		return str;
	}
}
