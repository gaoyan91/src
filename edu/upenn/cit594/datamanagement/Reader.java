package edu.upenn.cit594.datamanagement;

public abstract class Reader {

	public abstract Object read();
	
	public boolean isNumber(String s) {
		boolean numeric = true;
		if (s.length() < 1) {
			return false;
		}
		try {
			Double d = Double.valueOf(s);
		} catch (Exception e) {
			numeric = false;
		}
	    return numeric; 
	}
}
