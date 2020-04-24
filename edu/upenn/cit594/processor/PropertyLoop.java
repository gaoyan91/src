package edu.upenn.cit594.processor;

import java.util.List;

import edu.upenn.cit594.data.Property;

public interface PropertyLoop {
	public int loopProperty(List<Property> propertyList, int zipcode);
}
