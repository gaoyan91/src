package edu.upenn.cit594.processor;

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
	// Logger logger;

	public Processor(String parkingFileFormat, String parkingFileName, String propertyFileName, String popFileName,
			String logFileName) {
		ReaderFactory rf = new ReaderFactory();
		this.parkingReader = rf.getReader(parkingFileFormat, parkingFileName);
		this.propertyReader = rf.getPropertyReader(propertyFileName);
		this.popReader = rf.getPopReader(popFileName);
		this.violations = (List<ParkingViolation>) parkingReader.read();
//		for (ParkingViolation p : violations) {
//			 System.out.println(p.getFine() + " " + p.getState() + " " + p.getZipCode());
//		}
		this.propertyValues = (List<Property>) propertyReader.read();
//		for (Property p : propertyValues) {
//			System.out.println(p.getMarketValue() + " " + p.getTotalLivableArea() + " " +
//			p.getZipCode());
//		}
		this.popMap = (Map<Integer, Integer>) popReader.read();
		// this.logger = Logger.getInstance();

	}

	public void process() {

	}

	public int getTotalPop() { // 1
		int totalPop = 0;
		for (Entry<Integer, Integer> entry : popMap.entrySet()) {
			totalPop += entry.getValue();
		}
		return totalPop;
	}

	public void getFinePerCapita() { // how to use memorization ?
		Map<Integer, Double> zipFine = new TreeMap<>();
		for (ParkingViolation p : violations) { // set up fine per zipcode hashmap
			if (zipFine.containsKey(p.getZipCode())) {
				zipFine.put(p.getZipCode(), zipFine.get(p.getZipCode()) + (double) p.getFine());
			} else {
				zipFine.put(p.getZipCode(), (double) p.getFine());
			}
		}
		DecimalFormat df = new DecimalFormat("0.0000");
		df.setRoundingMode(RoundingMode.DOWN);
		for (Map.Entry<Integer, Double> each : zipFine.entrySet()) {
			if (popMap.containsKey(each.getKey())) {
				int popNumber = popMap.get(each.getKey());
				each.setValue(each.getValue() / popNumber);
				System.out.println(each.getKey() + "\t" + each.getValue() + "\t" + df.format(each.getValue())); // format
			}

		}
	}

	public int getAverageMarketValue(Integer zipcode) { // TODO: use extract function 3
		int count = 0;
		Double totalValue = 0.0;
		int averageValue = 0;
		for (Property p : propertyValues) {
			if (p.getZipCode() == zipcode) {
				count++;
				totalValue += p.getMarketValue();
			}
		}
		if (count == 0) {
			System.out.println(0);
		} else {
			averageValue = (int) (totalValue / count);
			System.out.println(averageValue);
		}
		return averageValue;
	}

	public int getAverageLivableArea(Integer zipcode, PropertyLivableAreaLoop propertyLivable) { // TODO: use extract function 4
		return propertyLivable.loopProperty(propertyValues, zipcode);
	}

	public double getMarketPerCapita(Integer zipcode) { // 5
		Double totalArea = 0.0;
		if (popMap.get(zipcode) != null) {
			for (Property p : propertyValues) {
				if (p.getZipCode() == zipcode) {
					totalArea += p.getTotalLivableArea();
				}
			}
			return totalArea / popMap.get(zipcode);
		}
		return totalArea;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Processor p = new Processor("csv", "parking.csv", "pp.csv", "population.txt", "log.txt");
//		System.out.print(p.getTotalPop());
//		p.getFinePerCapita();
//		p.getAverageMarketValue(19148);
		p.getMarketPerCapita(19148);
	}

}
