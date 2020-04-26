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
	private Map<String, Integer> popMap;

	private int cachedTotalPop;
	private Map<String, Double> cachedFinePerCapita;
	private Map<String, Integer> cachedAverageMarketValue;
	private Map<String, Integer> cachedAverageLivableArea;
	private Map<String, Integer> cachedtotalMarketValue;
	private Map<String, Integer> cachedtotalLivableArea;
	private Map<String, Integer> cachedMarketValPerCapita;
	private double cachedFineofMaxLivableAreaPerCapitaZipCode;

	public Processor(String parkingFileFormat, String parkingFileName, String propertyFileName, String popFileName) {
		ReaderFactory rf = new ReaderFactory();
		this.parkingReader = rf.getReader(parkingFileFormat, parkingFileName);
		this.propertyReader = rf.getPropertyReader(propertyFileName);
		this.popReader = rf.getPopReader(popFileName);
		this.violations = (List<ParkingViolation>) parkingReader.read();
		this.propertyValues = (List<Property>) propertyReader.read();
		this.popMap = (Map<String, Integer>) popReader.read();
		cachedTotalPop = -1;
		cachedFinePerCapita = new TreeMap<>();
		cachedAverageMarketValue = new HashMap<>();
		cachedAverageLivableArea = new HashMap<>();
		cachedtotalMarketValue = new HashMap<>();
		cachedtotalLivableArea = new HashMap<>();
		cachedMarketValPerCapita = new HashMap<>();
		cachedFineofMaxLivableAreaPerCapitaZipCode = -0.1;
	}

	public int getTotalPop() {
		if (cachedTotalPop > 0) {
			System.out.println("i am here");
			return cachedTotalPop;
		}
		int totalPop = 0;
		for (Entry<String, Integer> entry : popMap.entrySet()) {
			totalPop += entry.getValue();
		}
		cachedTotalPop = totalPop;
		return totalPop;
	}

	public Map<String, Double> getFinesPerCapita() {
		if (!cachedFinePerCapita.isEmpty()) {
			System.out.println("i am here");
			return cachedFinePerCapita;
		}
		Map<String, Double> zipFine = new HashMap<>();
		Map<String, Double> zipFinePerCapita = new TreeMap<>();
		for (ParkingViolation p : violations) {
			if (!p.getState().equals("PA")) {
				continue;
			}
			String zip = p.getZipCode();
			int fine = p.getFine();
			if (zipFine.containsKey(zip)) {
				zipFine.put(zip, zipFine.get(zip) + fine);
			} else {
				zipFine.put(zip, (double) fine);
			}
		}
		for (Entry<String, Double> entry : zipFine.entrySet()) {
			String zip = entry.getKey();
			double fine = entry.getValue();
			if (fine == 0 || !popMap.containsKey(zip) || popMap.get(zip) == 0) {
				continue;
			}
			if (popMap.containsKey(entry.getKey())) {
				int pop = popMap.get(zip);
				double ave = fine / pop;
				zipFinePerCapita.put(zip, ave);
			}
		}
		cachedFinePerCapita = new TreeMap<>(zipFinePerCapita);
		return zipFinePerCapita;
	}

	public int getAverageMarketValue(String zipCode) {
		if (cachedAverageMarketValue.containsKey(zipCode)) {
			System.out.println("i am here");
			return cachedAverageMarketValue.get(zipCode);
		}
		int[] marketValue = getAverageValue(zipCode, new MarketValProcessor());
		int totalMarketVal = marketValue[0];
		int aveMarketValue = marketValue[1];
		cachedtotalMarketValue.put(zipCode, totalMarketVal);
		cachedAverageMarketValue.put(zipCode, aveMarketValue);
		return aveMarketValue;
	}

	public int getAverageLivableArea(String zipCode) {
		if (cachedAverageLivableArea.containsKey(zipCode)) {
			System.out.println("i am here");
			return cachedAverageLivableArea.get(zipCode);
		}
		if (!isValidZipCode(zipCode)) {
			return 0;
		}
		int[] livableArea = getAverageValue(zipCode, new LivableAreaProcessor());
		int totalArea = livableArea[0];
		int aveArea = livableArea[1];
		cachedtotalLivableArea.put(zipCode, totalArea);
		cachedAverageLivableArea.put(zipCode, aveArea);
		return aveArea;
	}

	private int[] getAverageValue(String zipCode, TotalValProcessor pro) {
		int[] ret = new int[2];
		if (!isValidZipCode(zipCode)) {
			ret[0] = 0;
			ret[1] = 0;
			return ret;
		} else {
			zipCode = zipCode.substring(0, 5);
		}
		int totalVal = 0;
		int count = 0;
		for (Property p : propertyValues) {
			if (p.getZipCode().contentEquals(zipCode)) {
				totalVal = pro.getTotalVal(p, totalVal);
				count++;
			}
		}
		ret[0] = totalVal;
		if (count == 0) {
			ret[1] = 0;
		} else {
			ret[1] = totalVal / count;
		}
		return ret;
	}

	public int getMarketValPerCapita(String zipCode) {
		if (!isValidZipCode(zipCode)) {
			return 0;
		}
		if (!popMap.containsKey(zipCode) || popMap.get(zipCode) == 0) {
			return 0;
		}
		if (cachedMarketValPerCapita.containsKey(zipCode)) {
			System.out.println("i am here");
			return cachedMarketValPerCapita.get(zipCode);
		}
		int totalValue = 0;
		int population = popMap.get(zipCode);
		if (cachedtotalMarketValue.containsKey(zipCode)) {
			totalValue = cachedtotalMarketValue.get(zipCode);
		} else {
			for (Property p : propertyValues) {
				if (p.getZipCode().equals(zipCode)) {
					totalValue += p.getMarketValue();
				}
			}
		}
		int valuePerCapita = totalValue / population;
		cachedMarketValPerCapita.put(zipCode, valuePerCapita);
		return valuePerCapita;
	}
	
	public double getFineofMinLivableAreaPerCapitaZipCode() { 
		//find the total fine of the ZIP Code with the largest livable area per capita
		if (cachedFineofMaxLivableAreaPerCapitaZipCode >= 0) {
			System.out.println("i am here");
			return cachedFineofMaxLivableAreaPerCapitaZipCode;
		}
		// Get the aggregated fines for each ZIP Code
		Map<String, Double> finesAllZipcode;
		if (!cachedFinePerCapita.isEmpty()) {
			finesAllZipcode = cachedFinePerCapita;
		} else {
			finesAllZipcode = getFinesPerCapita();
			cachedFinePerCapita = finesAllZipcode;
		}
		// Calculate the total livable area for each ZIP Code that are existed in all 3 input data files
		Map<String, Double> totalLivableAreaMap = new HashMap<>();
		for (Property p : propertyValues) {
			String zip = p.getZipCode();
			if (!popMap.containsKey(zip) || popMap.get(zip) == 0 || !finesAllZipcode.containsKey(zip)) {
				continue;
			}
			if (cachedtotalLivableArea.containsKey(zip)) {
				totalLivableAreaMap.put(zip, (double) cachedtotalLivableArea.get(zip));
			}
			if (totalLivableAreaMap.containsKey(zip)) {
				totalLivableAreaMap.put(zip, totalLivableAreaMap.get(zip) + p.getTotalLivableArea());
			} else {
				totalLivableAreaMap.put(zip, p.getTotalLivableArea());
			}
		}
		//Calculate the livable area per capita for each ZIP Code that are existed in all 3 input data files
		Map<String, Double> livableAreaPerCapitaMap = new HashMap<>();
		for (Entry<String, Double> entry : totalLivableAreaMap.entrySet()) {
			String zip = entry.getKey();
			int pop = popMap.get(zip);
			double livableAreaPerCapita = entry.getValue() / pop;
			livableAreaPerCapitaMap.put(zip, livableAreaPerCapita);
		}
		// Find the ZIP Code associated with the minimum livable area per capita
		double minLivableArea = Double.MAX_VALUE;
		String minLivableAreaZip = "";
		for (Entry<String, Double> entry : livableAreaPerCapitaMap.entrySet()) {
			if (entry.getValue() < minLivableArea) {
				minLivableArea = entry.getValue();
				minLivableAreaZip = entry.getKey();
			}
		}
		// Find the total fines of that ZIP Code
		return finesAllZipcode.get(minLivableAreaZip);
	}
	
	private boolean isValidZipCode(String zipCode) {
		if (zipCode.length() < 5) {
			return false;
		}
		for (int i = 0; i < 5; i++) {
			if (!Character.isDigit(zipCode.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
