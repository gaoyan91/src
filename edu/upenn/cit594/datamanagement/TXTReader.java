package edu.upenn.cit594.datamanagement;

import java.io.File;
import java.util.*;

public class TXTReader extends Reader{

	protected String popFileName;
	
	public TXTReader(String fileName) {
		this.popFileName = fileName;
	}
	
	@Override
	public Map<Integer, Integer> read() {
		Map<Integer, Integer> population = new HashMap<>();
		try {
			Scanner in = new Scanner(new File(popFileName));
			while (in.hasNext()) {
				String next = in.nextLine();
				String[] popData = next.split(" ");
				String zipCode = popData[0];
				String pop = popData[1];
				if (!isNumber(zipCode) || !isNumber(pop) 
					|| zipCode.length() < 5) {
					continue;
				}
				population.put(Integer.parseInt(zipCode), Integer.parseInt(pop));
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return population;
	} 
	
	
}
