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
	private Map<String, Integer> cachedMarketValPerCapita;
	private double cachedLivableAreaPerCapitaOfMaxFineArea;

	// Logger logger;

	public Processor(String parkingFileFormat, String parkingFileName, String propertyFileName, String popFileName,
			String logFileName) {
		ReaderFactory rf = new ReaderFactory();
		this.parkingReader = rf.getReader(parkingFileFormat, parkingFileName);
		this.propertyReader = rf.getPropertyReader(propertyFileName);
		this.popReader = rf.getPopReader(popFileName);
		this.violations = (List<ParkingViolation>) parkingReader.read();
//		for (ParkingViolation p : violations) {
//			System.out.println(p.getFine() + " " + p.getState() + " " + p.getZipCode());
//		}
		this.propertyValues = (List<Property>) propertyReader.read();
//		for (int i = 0; i <  propertyValues.size(); i++) {
//			System.out.println(propertyValues.get(i).getMarketValue() + " " + propertyValues.get(i).getTotalLivableArea() + " " + propertyValues.get(i).getZipCode());
//		}
		this.popMap = (Map<String, Integer>) popReader.read();
		cachedTotalPop = -1;
		cachedFinePerCapita = new TreeMap<>();
		cachedAverageMarketValue = new HashMap<>();
		cachedAverageLivableArea = new HashMap<>();
		cachedMarketValPerCapita = new HashMap<>();
		cachedLivableAreaPerCapitaOfMaxFineArea = -0.1;
		// this.logger = Logger.getInstance();

	}

	public int getTotalPop() {
		if (cachedTotalPop > 0) {
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
			return cachedFinePerCapita;
		}
		Map<String, Double> zipFine = new TreeMap<>();
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
		int aveValue = getAverageValue(zipCode, new MarketValProcessor());
		cachedAverageMarketValue.put(zipCode, aveValue);
		return aveValue;
	}

	public int getAverageLivableArea(String zipCode) {
		if (cachedAverageLivableArea.containsKey(zipCode)) {
			return cachedAverageLivableArea.get(zipCode);
		}
		int aveArea = getAverageValue(zipCode, new LivableAreaProcessor());
		cachedAverageLivableArea.put(zipCode, aveArea);
		return aveArea;
	}

	private int getAverageValue(String zipCode, TotalValProcessor pro) {
		int totalVal = 0;
		int count = 0;
		for (Property p : propertyValues) {
			if (p.getZipCode().contentEquals(zipCode)) {
				totalVal = pro.getTotalVal(p, totalVal);
				count++;
			}
		}
		if (count == 0) {
			return 0;
		}
		return totalVal / count;
	}

	public int getMarketValPerCapita(String zipCode) {
		if (!popMap.containsKey(zipCode) || popMap.get(zipCode) == 0) {
			return 0;
		}
		if (cachedMarketValPerCapita.containsKey(zipCode)) {
			return cachedMarketValPerCapita.get(zipCode);
		}
		int totalValue = 0;
		int population = popMap.get(zipCode);
		for (Property p : propertyValues) {
			if (p.getZipCode().equals(zipCode)) {
				totalValue += p.getMarketValue();
			}
		}
		int valuePerCapita = totalValue / population;
		cachedMarketValPerCapita.put(zipCode, valuePerCapita);
		return valuePerCapita;
	}
	
	public double getLivableAreaPerCapitaOfMaxFineArea() { // 6: find the total livable area per capita where has the highest fine per capita.
		if (cachedLivableAreaPerCapitaOfMaxFineArea >= 0) {
			return cachedLivableAreaPerCapitaOfMaxFineArea;
		} else {
			Double maxFine = Double.NEGATIVE_INFINITY;
			String maxFineZipcode = null;
			int populationInZipcode;
			int totalLivableArea = 0;
			double totalLivableAreaPerCapita;
			Map finesAllZipcode = getFinesPerCapita();
			for(Entry<String, Integer> each: popMap.entrySet()) {
				String zipcode = each.getKey();
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
				if (p.getZipCode().equals(maxFineZipcode)) {
					totalLivableArea += p.getTotalLivableArea();
				}
			}
			totalLivableAreaPerCapita = (double)totalLivableArea / populationInZipcode;
			cachedLivableAreaPerCapitaOfMaxFineArea = totalLivableAreaPerCapita;
			return totalLivableAreaPerCapita;
		}
	}

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		Processor p = new Processor("csv", "parking.csv", "pp.csv", "population.txt", "log.txt");
////		System.out.print(p.getTotalPop());
////		System.out.print(p.getFinesPerCapita());
//		System.out.println(p.getAverageMarketValue("19148"));
//		System.out.println(p.getAverageMarketValue("19148"));
//		System.out.println(p.getMarketValPerCapita("19148"));
//		System.out.println(p.getLivableAreaPerCapitaOfMaxFineArea());
//		System.out.println(p.getLivableAreaPerCapitaOfMaxFineArea());
//		System.out.println(p.getLivableAreaPerCapitaOfMaxFineArea());
	}

}
