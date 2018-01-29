package de.ulrich_boeing.map;

class Graph extends Map {

	private float[] nodes;
	private int resolution;
	float ratioResolutionToInput;
	static int defaultResolution = 200;

	private float min, max;

	Graph(String str, int resolution) {
		this(new ComplexMap(str), resolution);
	}
	
	Graph(ComplexMap complexMap, int resolution) {
		super();
		build(complexMap, resolution);
		setRange(complexMap.input.getStart(), complexMap.input.getEnd(), complexMap.output.getStart(), complexMap.output.getEnd());
	}

	public Map setRange(float newInputStart, float newInputEnd, float newOutputStart, float newOutputEnd) {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = output.normalize(nodes[i]);
		}

		super.setRange(newInputStart, newInputEnd, newOutputStart, newOutputEnd);
		ratioResolutionToInput = resolution / (newInputEnd - newInputStart);

		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = output.deNormalize(nodes[i]);
		}
		return this;
	}

	public float get(float x) {
		x = input.restrictToRange(x);
		float section = (x - getInputStart()) * ratioResolutionToInput;
		int i = (int) section;
		float result = nodes[i] + (section % 1) * (nodes[i + 1] - nodes[i]);
		return output.restrictToRange(result); 
	}

	private void build(ComplexMap map, int resolution) {
		// if the last node i is accessed, Graph.get needs node i+1
		nodes = new float[resolution + 2];
		this.resolution = resolution;

		for (int i = 0; i <= resolution; i++) {
			float x = (float) i / resolution;
			nodes[i] = map.get(x);
		}
		nodes[nodes.length - 1] = nodes[nodes.length - 2];

		setMinMax();
	}

	private void setMinMax() {
		min = 1;
		max = 0;
		for (float node : nodes) {
			if (node < min) {
				min = node;
			}
			if (node > max) {
				max = node;
			}
		}
		System.out.println("Graph min-max: " + min + " - " + max);
	}
	
	
	
	
}
