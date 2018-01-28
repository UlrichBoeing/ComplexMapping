package de.ulrich_boeing.map;

class Mapping {
	Step[] steps;
	float weight;

	Mapping(String str) {
		set(str);
	}

	private void set(String str) {
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

	protected float map(float x) {
		for (Step step : steps) {
			x = step.map(x);
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
