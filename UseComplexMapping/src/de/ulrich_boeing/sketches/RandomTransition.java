package de.ulrich_boeing.sketches;

import de.ulrich_boeing.map.Map;
import de.ulrich_boeing.map.MapGenerator;
import de.ulrich_boeing.map.Precision;
import de.ulrich_boeing.map.Range.RepeatRange;
import processing.core.PApplet;

public class RandomTransition extends PApplet {
	int cycleLength = 90;
	Map map, cycleText, colorMap, cycleMap;
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

		cycleMap = Map.create("narrow 0.0, 0.4 > exp 3").setRange(0, cycleLength, 0, 1);
		cycleText = Map.create(" narrow 0.4 0.99 > triangle").setRange(0, cycleLength, 0, 255);
	}

	@Override
	public void draw() {
		background(0);

		float normCyclePos = getNormCyclePos();
		for (int i = 0; i < height; i++) {
			stroke(map.map(i, normCyclePos) / 18, 0, map.map(i, normCyclePos) / 7);
			line(0, i, width, i);
		}

		stroke(255, 0, 255, 14);
		fillGraph(normCyclePos);
		noFill();
		stroke(255, 30);
		strokeWeight(1.5f);
		drawGraph(normCyclePos);
		// Show DefString
		// textSize(64);
		// fill(255, 255, 255, normCyclePos * 100 + 55);
		// textAlign(CENTER);
		// text(end, width / 2, height / 2);
		textSize(16);
		fill(255, 255, 255, cycleText.map(frameCount));
		textAlign(CENTER);
		text(end, width / 2, height - 20);

	}

	private void drawGraph(float normCyclePos) {
		beginShape();
		for (float x = 0; x < width; x++) {
			vertex(x, map.map(x, normCyclePos));
		}
		endShape();
	}

	private void fillGraph(float normCyclePos) {
		for (float x = 0; x < width; x++) {
			line(x, map.map(x, normCyclePos), x, 0);
		}
	}

	private float getNormCyclePos() {
		int cyclePos = frameCount % cycleLength;
		if (cyclePos == 0) {
			map = getRandomMap();
		}

		return cycleMap.map(cyclePos);
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
//		end = "narrow 0.6 0.9 > triangle";
//		start = end;
		Map randomMap = Map.create(start).setTargetMap(end);
		randomMap.setRange(0, width, height - 50, 15, RepeatRange.Min);
		
		System.out.println(randomMap.toString());

		// System.out.println(randomMap.getDeviation(10000));
		System.out.println("Performance new Graph:");
		randomMap.getPerformance(1000000);

		return randomMap;
	}
}