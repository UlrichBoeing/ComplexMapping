package de.ulrich_boeing.map;

/**
 * The class Mapping is a sequence of steps,
 * the output of one step is the input of the subsequent step.<br>
 * A Mapping is built with a string in the form "triangle > invert > exp 3".<br><br>
 * 
 *  If more than one mapping is combined in a ComplexMap weight is needed.<br>
 * Input for the normMap method must be between 0-1 and so must be output.
 *
 */
class Mapping {
	Step[] steps;
	float weight;

	Mapping(String str) {
		build(str);
	}

	private void build(String str) {
		String[] arr = str.split("\\s*>\\s*");

		int countSteps = arr.length;
		steps = new Step[countSteps];
		for (int i = 0; i < countSteps; i++) {
			steps[i] = new Step(arr[i]);
		}
		setWeight();
	}

	private void setWeight() {
		weight = 1;
		
		for (int i = 0; i < steps.length; i++) {
			if (steps[i].type.equals(StepType.weight)) {
				if (i == steps.length - 1) {
					weight = steps[i].p[0];
				} else {
					String msg = "Weight must be the last step of a mapping: '" + toString() + "'";
					throw new RuntimeException(msg);
				}
			}

		}
	}
	
	protected float normMap(float x) {
		for (Step step : steps) {
			x = step.normMap(x);
		}
		return x;
	}

	@Override
	public String toString() {
		String str = "";
		for (int i = 0; i < steps.length; i++) {
			str += steps[i].toString();
			if (i < steps.length - 1) {
				str += " > ";
			}
		}
		return str;
	}
}
