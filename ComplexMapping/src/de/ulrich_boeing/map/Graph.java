package de.ulrich_boeing.map;

/**
 * A Graph stores the normalized results of a ComplexMap in an array. <br>
 * These values (evenly spaced on the x-axis) are connected by straight lines to
 * approximate the values of the curve.
 * 
 * <br>
 * 
 * @author Ulrich Böing
 *
 */
class Graph extends Map {

	private float[] nodes;
	private int resolution;
	static Precision defaultPrecision = Precision.High;

	private float min, max;

	Graph(String str, int resolution) {
		this(new ComplexMap(str), resolution);
	}

	Graph(ComplexMap complexMap, int resolution) {
		super();
		build(complexMap, resolution);
	}

	private void build(ComplexMap map, int resolution) {
		/*
		 * (resolution + 1) nodes are connected by resolution lines. If the last node is
		 * accessed (x == 1), Graph.normMap() needs nodes[resolution + 2]. The value of
		 * nodes[resolution + 2] doesn't have any effect because in this case (section % 1) == 0
		 */
		nodes = new float[resolution + 2];
		this.resolution = resolution;

		for (int i = 0; i <= resolution; i++) {
			float x = (float) i / resolution;
			nodes[i] = map.normMap(x);
		}
		nodes[nodes.length - 1] = nodes[nodes.length - 2];

		setMinMax();
	}

	@Override
	float normMap(float x) {
		// section is the number of the line, (section % 1) is the x-position on that
		// line
		float section = x * resolution;
		int i = (int) section;
		return nodes[i] + (section % 1) * (nodes[i + 1] - nodes[i]);
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
	}

	@Override
	public String toString() {
		String str = "Graph:\n";
		str += getNodesAsString(5);
		str += " resolution: " + resolution + " straight lines\n";
		str += " min-max: " + min + " - " + max + "\n";
		str += super.toString();
		return str;
	}

	private String getNodesAsString(int numNodes) {
		String str = "";
		float step = (float) (resolution + 1) / (float) (numNodes - 1);
		for (int i = 0; i < numNodes; i++) {
			int index = (int) (i * step);
			str += " node[" + index + "]:\t" + nodes[index] + "\n";
		}
		return str;
	}

}
