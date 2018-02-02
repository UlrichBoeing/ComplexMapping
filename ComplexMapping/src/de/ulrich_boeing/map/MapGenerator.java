package de.ulrich_boeing.map;

import java.util.List;
import static java.util.Arrays.asList;

public class MapGenerator {
	List<StepType> exclude;
	int numSteps, maxSteps;
	int probabilityAnd; 
	
	public MapGenerator() {
	}

	private void initParameter() {
		exclude = asList(StepType.x, StepType.constant, StepType.weight, StepType.random, StepType.squarerandom);
		maxSteps = 5;
		numSteps = (int) (Math.random() * maxSteps) + 1;
		probabilityAnd = 4;
	}
	
	public String getRandomDefString() {
		initParameter();

		String defString = "";
		for (int i = 1; i <= numSteps; i++) {
			defString += getRandomStep().toString();
			if (i < numSteps) {
				defString += getRandomSeparator();
			}
		}
		return defString;
	}
	
	private StepType getRandomStep() {
		StepType step;
		do {
			int i = (int) (Math.random() * StepType.values().length);
			step = StepType.values()[i];
		} while (exclude.contains(step));
		return step;
	}
	
	private String getRandomSeparator() {
		if (oneOf(probabilityAnd)) {
			return " & ";
		} else {
			return " > ";
		}
		
	}
	
	private static boolean oneOf(int i) {
		return (Math.random() * i) < 1;
	}
}
