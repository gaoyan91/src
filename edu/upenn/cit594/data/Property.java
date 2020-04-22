package edu.upenn.cit594.data;

public class Property{
	double marketValue;
	double totalLivableArea;
	double zipCode;
	
	public Property(double d, double e, double f) {
		this.marketValue = d;
		this.totalLivableArea = e;
		this.zipCode = f;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public double getTotalLivableArea() {
		return totalLivableArea;
	}

	public double getZipCode() {
		return zipCode;
	}

	
}
