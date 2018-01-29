package de.ulrich_boeing.sketches;

import de.ulrich_boeing.map.Map;
import de.ulrich_boeing.map.MapGenerator;
import de.ulrich_boeing.map.Precision;
import processing.core.PApplet;

public class RandomTransition extends PApplet {
	int cycleLength = 90;
	Map map, colorMap, cycleMap;
	String start, end;
	MapGenerator mapGenerator;

	public static void main(String[] args) {
		PApplet.main("de.ulrich_boeing.sketches.RandomTransition");
	}

	@Override
	public void settings() {
		size(800, 600);
	}

	@Override
	public void setup() {
		map = getRandomMap();
		
		cycleMap = Map.create("narrow 0.0, 0.6 > exp 4", Precision.VeryHigh).setRange(0, cycleLength, 0, 1);
	}

	@Override
	public void draw() {
		background(0);
		
		float normCyclePos = getNormCyclePos();
		for (int i = 0; i < height; i++) {
			stroke(32, 0, map.get(i, normCyclePos) / 7);
			line(0, i, width, i);
		}

		noFill();
		stroke(255);
		strokeWeight(1.5f);
		drawGraph(normCyclePos);
		// Show DefString
		textSize(16);
		fill(255, 255, 255, normCyclePos * 155 + 100);
		textAlign(CENTER);
		text(end, width / 2, height -20);
		
	}

	private void drawGraph(float normCyclePos) {
		beginShape();
		for (float x = 0; x < width; x++) {
			vertex(x, map.get(x, normCyclePos));
		}
		endShape();
	}
	
	private float getNormCyclePos() {
		int cyclePos = frameCount % cycleLength;
		if (cyclePos == 0) {
			map = getRandomMap();
		}
		
		return cycleMap.get(cyclePos);
	}

	private Map getRandomMap() {
		if (mapGenerator == null) {
			mapGenerator = new MapGenerator();
		}
		if (end == null) {
			end = "x";
		}
		start = end;
		end = mapGenerator.getRandomDefString();
		Map randomMap = Map.create(start, Precision.High)
				.setRange(1, width - 1, height - 50, 15).setTargetMap(end, Precision.Highest);
		
		System.out.println(randomMap.toString());

		// System.out.println(randomMap.getDeviation(10000));
		// randomMap.getPerformance(1000000);

		return randomMap;
	}
}