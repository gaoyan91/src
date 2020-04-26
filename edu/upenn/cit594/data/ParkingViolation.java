package edu.upenn.cit594.data;

public class ParkingViolation{
	int fine;
	String description, state, zipCode;
	
	public ParkingViolation(int fine, String state, String zipCode) {
		this.fine = fine;
		this.state = state;
		this.zipCode = zipCode;
	}
	
	public int getFine() {
		return fine;
	}
	
	public String getState() {
		return state;
	}
	
	public String getZipCode() {
		return zipCode;
	}
	
	

}
