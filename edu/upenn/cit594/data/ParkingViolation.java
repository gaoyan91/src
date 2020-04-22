package edu.upenn.cit594.data;

public class ParkingViolation {
	int fine, zipCode; // private ? 2. is fine a integer or double
	String description, state; // private ?

	public ParkingViolation(int fine, String state, int zipCode) {
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

	public int getZipCode() {
		return zipCode;
	}

}
