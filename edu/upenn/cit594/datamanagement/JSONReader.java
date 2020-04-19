package edu.upenn.cit594.datamanagement;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.upenn.cit594.data.ParkingViolation;

public class JSONReader extends Reader{

protected String parkingFileName;
	
	public JSONReader(String fileName) {
		this.parkingFileName = fileName;
	}
	
	@Override
	public List<ParkingViolation> read() {
		List<ParkingViolation> violations = new ArrayList<>();
		JSONParser parser = new JSONParser();
		
		try {
			Object obj  = parser.parse(new FileReader(parkingFileName));
			JSONArray joArray = new JSONArray();
			joArray.add(obj);
			JSONArray array = (JSONArray) joArray.get(0);
			for (int i = 0; i < array.size(); i++) {
				JSONObject jo = (JSONObject) array.get(i);
				String state = (String) jo.get("state");
				int fine = (int) jo.get("fine");
				int zipCode = (int) jo.get("zip_code");
				violations.add(new ParkingViolation(fine, state, zipCode));

			}
		} catch (Exception e) {
			throw new IllegalStateException("File Not Found Or Cannot Read");
		}
		return violations;
	}
}
