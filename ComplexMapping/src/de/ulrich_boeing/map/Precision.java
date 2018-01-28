package de.ulrich_boeing.map;

public enum Precision {
	Low(10), Normal(100), High(200), VeryHigh(1000), Highest(0); 
	
	int resolution;
	
	Precision(int resolution) {
		this.resolution = resolution;
	}

}
