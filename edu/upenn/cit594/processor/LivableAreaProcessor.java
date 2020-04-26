package edu.upenn.cit594.processor;

import edu.upenn.cit594.data.Property;

public class LivableAreaProcessor extends TotalValProcessor{

	@Override
	public int getTotalVal(Property p, int totalVal) {
		return totalVal += p.getTotalLivableArea();
	}

	
}
