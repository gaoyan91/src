package edu.upenn.cit594.processor;

import java.util.*;
import java.util.Map.Entry;

import edu.upenn.cit594.data.ParkingViolation;
import edu.upenn.cit594.data.Property;
import edu.upenn.cit594.datamanagement.Reader;

public class Processor {
	private Reader parkingReader;
	private Reader propertyReader;
	private Reader popReader;
	private List<ParkingViolation> violations;
	private List<Property> propertyValues;
	private Map<Integer, Integer> popMap;
	//Logger logger;
	
	public Processor(String parkingFileFormat, String parkingFileName,
			String propertyFileName, String popFileName, String logFileName) {
		ReaderFactory rf = new ReaderFactory();
		this.parkingReader = rf.getReader(parkingFileFormat, parkingFileName);
		this.propertyReader = rf.getPropertyReader(propertyFileName);
		this.popReader = rf.getPopReader(popFileName);
		this.violations = (List<ParkingViolation>) parkingReader.read();
		for (ParkingViolation p : violations) {
			System.out.println(p.getFine() + " " + p.getState() + " " + p.getZipCode());
		}
		this.propertyValues = (List<Property>) propertyReader.read();
		for (Property p : propertyValues) {
			System.out.println(p.getMarketValue() + " " + p.getTotalLivableArea() + " " + p.getZipCode());
		}
		this.popMap = (Map<Integer, Integer>) popReader.read();
		//this.logger = Logger.getInstance();
	
	}
	
	public void process() {
		
		
	}
	
	public int getTotalPop() {
		int totalPop = 0;
		for (Entry<Integer, Integer> entry: popMap.entrySet()) {
			totalPop += entry.getValue();
		}
		return totalPop;
	}

}
