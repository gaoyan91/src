package edu.upenn.cit594.processor;

import java.util.List;

import edu.upenn.cit594.data.Property;

public class PropertyLivableAreaLoop implements PropertyLoop {
	public int loopProperty(List<Property> propertyValues, int zipcode) {
		int count = 0;
		Double totalArea = 0.0;
		int averageValue = 0;
		for (Property p : propertyValues) {
			if (p.getZipCode() == zipcode) {
				count++;
				totalArea += p.getTotalLivableArea();
			}
		}
		if (count == 0) {
			System.out.println(averageValue);
		} else {
			averageValue = (int) (totalArea / count);
			System.out.println(averageValue);
		}
		return averageValue;
	}
}
