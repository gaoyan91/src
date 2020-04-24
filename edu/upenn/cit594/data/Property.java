package edu.upenn.cit594.data;

public class Property{
	double marketValue;
	double totalLivableArea;
	double zipCode;
	int numberOfRoom;
	
	public Property(double d, double e, double f, int numberOfRoom) {
		this.marketValue = d;
		this.totalLivableArea = e;
		this.zipCode = f;
		this.numberOfRoom = numberOfRoom;
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

	public int getNumberOfRoom() {
		return numberOfRoom;
	}

	
}
