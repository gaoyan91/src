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
	private Map<String, Integer> intResults = new HashMap<>(); // TODO: need UI to Print interger not double
	private Map<String, Map> resultsMap = new HashMap<>();
	private Map<String, Integer> doubleResults = new HashMap<>(); // TODO: Maybe simplified?
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

	public void process() { // TODO: delete ?

	}

	public int getTotalPop() { // 1, TODO:
		if (intResults.containsKey("1")) {
			return intResults.get("1");
		} else {
			int totalPop = 0;
			for (Entry<Integer, Integer> entry : popMap.entrySet()) {
				totalPop += entry.getValue();
			}
			intResults.put("1", totalPop);
			return totalPop;
		}
	}

	public Map getFinePerCapita() {
		if (resultsMap.containsKey("2")) {
			return resultsMap.get("2");
		} else {
			Map<Integer, Double> zipFine = new TreeMap<>();
			for (ParkingViolation p : violations) { // set up fine per zipcode hashmap
				if (zipFine.containsKey(p.getZipCode())) {
					zipFine.put(p.getZipCode(), zipFine.get(p.getZipCode()) + (double) p.getFine());
				} else {
					zipFine.put(p.getZipCode(), (double) p.getFine());
				}
			}
//			DecimalFormat df = new DecimalFormat("0.0000"); // move these code to UI ?
//			df.setRoundingMode(RoundingMode.DOWN);
//			for (Map.Entry<Integer, Double> each : zipFine.entrySet()) {
//				if (popMap.containsKey(each.getKey())) {
//					int popNumber = popMap.get(each.getKey());
//					each.setValue(each.getValue() / popNumber);
//					System.out.println(each.getKey() + "\t" + each.getValue() + "\t" + df.format(each.getValue())); // format
//				}
//			}
			resultsMap.put("2", zipFine);
			return zipFine;
		}
	}

	public int getAverageMarketValue(Integer zipcode) { // TODO: use extract function 3
		if (intResults.containsKey("3")) {
			return intResults.get("3");
		} else {
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
			intResults.put("3", averageValue);
			return averageValue;
		}
	}

	public int getAverageLivableArea(Integer zipcode) { // TODO: use extract// function 4
		if (intResults.containsKey("4")) {
			return intResults.get("4");
		} else {
			int count = 0;
			Double totalArea = 0.0;
			int averageValue = 0;
			for (Property p : propertyValues) {
				if (p.getZipCode() == zipcode) {
					count++;
					totalArea += p.getTotalLivableArea();
				}
			}
			if (count == 0) {
				System.out.println(averageValue);
			} else {
				averageValue = (int) (totalArea / count);
				System.out.println(averageValue);
			}
			intResults.put("4", averageValue);
			return averageValue;
		}
	}

	public int getMarketPerCapita(Integer zipcode) { // 5 check truncated
		if (intResults.containsKey("5")) {
			return intResults.get("5");
		} else {
			int popInZip;
			int valueInZipcode;
			int totalValue = 0;
			if (popMap.get(zipcode) != null) {
				popInZip = popMap.get(zipcode);
				for (Property p : propertyValues) {
					if (p.getZipCode() == zipcode) {
						totalValue += p.getMarketValue();
					}
				}
				valueInZipcode = totalValue / popInZip;
			} else {
				valueInZipcode = 0;
			}
			intResults.put("5", valueInZipcode);
			return valueInZipcode;
		}
	}

	public double getLivableAreaPerCapitaOfMaxFineArea() { // 6: find the total livable area per capita where has the highest fine per capita.
		if (doubleResults.containsKey("6")) {
			return doubleResults.get("6");
		} else {
			Double maxFine = Double.NEGATIVE_INFINITY;
			int maxFineZipcode = 0;
			int populationInZipcode;
			int totalLivableArea = 0;
			double totalLivableAreaPerCapita;
			Map finesAllZipcode = getFinePerCapita();
			for(Map.Entry<Integer, Integer> each: popMap.entrySet()) {
				int zipcode = each.getKey();
				if(finesAllZipcode.get(each.getKey()) != null) {
					Double currentFine = (Double) finesAllZipcode.get(each.getKey());
					if (currentFine > maxFine) {
						maxFine = currentFine;
						maxFineZipcode = zipcode;
					}
				}
			}
			populationInZipcode = popMap.get(maxFineZipcode);
			for (Property p : propertyValues) {
				if (p.getZipCode() == maxFineZipcode) {
					totalLivableArea += p.getTotalLivableArea();
				}
			}
			totalLivableAreaPerCapita = (double)totalLivableArea / populationInZipcode;
			System.out.println(totalLivableArea);
			System.out.println(populationInZipcode);
			//doubleResults.put("6", totalLivableAreaPerCapita);
			return totalLivableAreaPerCapita;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Processor p = new Processor("csv", "parking.csv", "pp.csv", "population.txt", "log.txt");
//		System.out.print(p.getTotalPop());
//		p.getFinePerCapita();
//		p.getAverageMarketValue(19148);
		System.out.println(p.getLivableAreaPerCapitaOfMaxFineArea()); 
		System.out.println(Integer.parseInt("sss"));
	}

}
