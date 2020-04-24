package edu.upenn.cit594.ui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import edu.upenn.cit594.processor.Processor;

public class CommandLineUserInterface {
	Processor pro;
	
	public CommandLineUserInterface(Processor pro) {
		this.pro = pro;
	}

	public void start() {
		try {
			while (true) {
				System.out.println("Please specify the action to be performed");
				Scanner in = new Scanner(System.in);
				if (in.hasNext()) {
					int action = Integer.parseInt(in.next());
					if (action < 1 || action > 6) {
						System.out.println("Invalid action");
						System.exit(0);
					}
					switch(action) {
						case 1:
							displayTotalpop();
							break;
						case 2:
							displayFinePerCapita();
							break;
						case 3:
							displayAveMarketValue();
							break;
						case 4:
							displayAveLivableArea();
							break;
						case 5:
							displayMarketValuePerCapita();
							break;
					}
				}
			}
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void displayTotalpop() {
		System.out.println("Here is the total population: " + pro.getTotalPop());
	}
	
	private void displayFinePerCapita() {
		Map<String, Double> map = pro.getFinesPerCapita();
		System.out.println("Here is the fine per capita");
		DecimalFormat df = new DecimalFormat("0.0000");
		df.setRoundingMode(RoundingMode.DOWN);
		for (Entry<String, Double> entry : map.entrySet()) {
			System.out.println(entry.getKey() + " " + df.format(entry.getValue()));
		}
	}
	
	private void displayAveMarketValue() {
		String zip = requestZipCode();
		System.out.println("Here is the average market value: " + pro.getAverageMarketValue(zip));
		
	}
	
	private void displayAveLivableArea() {
		String zip = requestZipCode();
		System.out.println("Here is the average livable area: " + pro.getAverageLivableArea(zip));
	}
	
	private void displayMarketValuePerCapita() {
		String zip = requestZipCode();
		int a = pro.getMarketValPerCapita(zip);
		System.out.println("Here is the average market value per capita: " + pro.getMarketValPerCapita(zip));
	}
	
	private String requestZipCode() {
		System.out.println("Please provide a valid zipcode");
		Scanner s = new Scanner(System.in);
		return s.next();
	}
	
}
