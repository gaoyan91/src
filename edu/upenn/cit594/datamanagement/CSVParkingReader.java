package edu.upenn.cit594.datamanagement;

import java.io.File;
import java.util.*;

import edu.upenn.cit594.data.ParkingViolation;


public class CSVParkingReader extends Reader{
	
protected String parkingFileName;
	
	public CSVParkingReader(String parkingFileName) {
		this.parkingFileName = parkingFileName;
	}
	
	@Override
	public List<ParkingViolation> read() {
		List<ParkingViolation> violations = new ArrayList<>();
		Scanner in = null;
		try {
			File file = new File(parkingFileName);
			if (!file.exists() || !file.canRead()) {
				throw new Exception("File Not Found Or Cannot Read");
			}
			in = new Scanner(file);
			while (in.hasNext()) {
				String parkingInfo = in.nextLine();
				String[] parking = parkingInfo.split(",");
				if (parking.length != 7) {
					continue;
				}
				String state = parking[4];
				String fine = parking[1];
				String zipCode = parking[6];
				if (isNumber(state) || !isNumber(fine) || !isNumber(zipCode)
					|| zipCode.length() < 5) {
					continue;
				}
				violations.add(new ParkingViolation(Integer.parseInt(fine), state, Integer.parseInt(zipCode)));
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			in.close();
		}
		return violations;
	}
}

	