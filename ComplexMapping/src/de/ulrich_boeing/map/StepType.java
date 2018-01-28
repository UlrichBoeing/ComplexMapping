package de.ulrich_boeing.map;;

enum StepType {

	/*
	 * simple types
	 */
	x(true, 0, new float[] {}) {
		@Override
		float calculate(float x, float[] p) {
			return x;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	invert(true, 0, new float[] {}) {
		float calculate(float x, float[] p) {
			return 1 - x;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	constant(true, 1, new float[] { 0.5f }) {
		@Override
		float calculate(float x, float[] p) {
			return p[0];
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	narrow(true, 2, new float[] { 0.2f, 0.8f }) {
		float calculate(float x, float[] p) {
			if (x < p[0]) {
				return 0;
			} else if (x > p[1]) {
				return 1;
			} else {
				return (x - p[0]) / (p[1] - p[0]);
			}
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},

	/*
	 * exponential types
	 */
	exp(false, 1, new float[] { 2 }) {
		float calculate(float x, float[] p) {
			float e = p[0];
			// rotate 180 part I
			if (e < 0) {
				x = 1 - x;
			}

			if (e != 1) {
				x = (float) Math.pow(x, Math.abs(e));
			}

			// rotate 180 part II
			if (e < 0) {
				x = 1 - x;
			}
			return x;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			if (p[0] == 0) {
				illegalArgument(p[0]);
			}
			return p;
		}
	},
	outward(false, 1, new float[] { 2 }) {
		float calculate(float x, float[] p) {
			// transform x from 0, 1 to -1, 1
			x = x * 2 - 1;

			// Exponentiate only positive values
			float y = Math.abs(x);
			y = exp.calculate(y, p);

			// Change if x is negative
			if (x < 0) {
				y = -y;
			}

			// transform y from -1, 1 to 0, 1
			y = (y + 1) / 2;

			return y;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	inward(false, 1, new float[] { 2 }) {
		@Override
		float calculate(float x, float[] p) {
			return outward.calculate(x, p);
		}

		@Override
		float[] evaluateParameter(float[] p) {
			p[0] = -p[0];
			return p;
		}
	},

	/*
	 * up-down types
	 */
	peak(true, 1, new float[] { 0.8f }) {
		float calculate(float x, float[] p) {
			if (p[0] < 0.5) {
				return 1 - Math.abs(x - p[0]) / (1 - p[0]);
			} else
				return 1 - Math.abs(p[0] - x) / p[0];
		}

		@Override
		float[] evaluateParameter(float[] p) {
			if (p[0] <= 0 || p[0] >= 1) {
				illegalArgument(p[0]);
			}
			return p;
		}
	},
	triangle(true, 1, new float[] { 0.5f }) {
		float calculate(float x, float[] p) {
			if (x < p[0]) {
				return x / p[0];
			} else
				return (1 - x) / (1 - p[0]);
		}

		@Override
		float[] evaluateParameter(float[] p) {
			if (p[0] <= 0 || p[0] >= 1) {
				illegalArgument(p[0]);
			}
			return p;
		}
	},
	trapez(true, 2, new float[] { 0.3f, 0.7f }) {
		float calculate(float x, float[] p) {
			if (x < p[0]) {
				return x / p[0];
			} else if (x > p[1]) {
				return (1 - x) / (1 - p[1]);
			} else {
				return 1;
			}
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},

	/*
	 * trigonometric functions
	 */
	sin(false, 0, new float[] {}) {
		float calculate(float x, float[] p) {
			return (float) Math.sin(Math.PI * x);
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	fullsin(false, 0, new float[] {}) {
		float calculate(float x, float[] p) {
			return 0.5f + (float) Math.sin(2 * Math.PI * x) / 2;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	cos(false, 0, new float[] {}) {
		float calculate(float x, float[] p) {
			return 0.5f + (float) Math.cos(Math.PI * x) / 2;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	fullcos(false, 0, new float[] {}) {
		float calculate(float x, float[] p) {
			return 0.5f + (float) Math.cos(2 * Math.PI * x) / 2;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},

	/*
	 * special types
	 */
	random(false, 1, new float[] { 6 }) {
		float calculate(float x, float[] p) {
			float section = x * (p.length - 2);
			int i = (int) section;
			return p[i] + (section % 1) * (p[i+1] - p[i]);
		}

		@Override
		float[] evaluateParameter(float[] p) {
			int count = (int) p[0] + 2;
			if (count < 2) {
				illegalArgument(p[0]);
			}
			p = new float[count];
			for (int i = 0; i < count; i++) {
				p[i] = (float) Math.random();
			}
			return p;
		}
	},
	squarerandom(false, 1, new float[] { 6 }) {
		float calculate(float x, float[] p) {
			float section = x * (p.length - 1);
			int i = (int) section;
			return p[i];
		}

		@Override
		float[] evaluateParameter(float[] p) {
			int count = (int) p[0] + 1;
			if (count < 2) {
				illegalArgument(p[0]);
			}
			p = new float[count];
			for (int i = 0; i < count -1; i++) {
				p[i] = (float) Math.random();
			}
			p[p.length-1] = p[p.length -2];
			return p;
		}
	},
	/*
	 * modifier
	 */
	repeat(false, 1, new float[] { 2 }) {
		float calculate(float x, float[] p) {
			return (x * p[0]) % 1;
		}

		@Override
		float[] evaluateParameter(float[] p) {
			if (p[0] == 1) {
				illegalArgument(p[0]);
			}
			return p;
		}
	},
	mirror(false, 1, new float[] { 1 }) {
		float calculate(float x, float[] p) {
			x = repeat.calculate(x, p);
			if (x < 0.5) {
				return 2 * x;
			} else {
				return (1 - x) * 2;
			}

		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	},
	weight(true, 1, new float[] { 1 }) {
		float calculate(float x, float[] p) {
			return x * p[0];
		}

		@Override
		float[] evaluateParameter(float[] p) {
			return p;
		}
	};

	final boolean fast;
	final int parameterNeeded;
	final float[] defaults;

	StepType(boolean fast, int parameterNeeded, float[] defaults) {
		this.fast = fast;
		this.parameterNeeded = parameterNeeded;
		this.defaults = defaults;
	}

	void illegalArgument(float parameter) {
		String msg = "Wrong parameter '" + parameter + "' for Step '" + name() + "'.";
		throw new IllegalArgumentException(msg);
	}

	abstract float calculate(float x, float[] p);

	abstract float[] evaluateParameter(float[] p);
}
