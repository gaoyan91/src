package edu.upenn.cit594;

import edu.upenn.cit594.processor.Processor;
import edu.upenn.cit594.ui.CommandLineUserInterface;

public class Main {

	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("Runtime Argument Number Error");
			System.exit(0);
		}

		String parkingFileFormat = args[0];
		if (!parkingFileFormat.equals("csv") && !parkingFileFormat.equals("json")) {
			System.out.println("Parking File Format Error");
			System.exit(0);
		}
		String parkingFileName = args[1];
		String propertyFileName = args[2];
		String popFileName = args[3];
		String logFileName = args[4];
		
		Processor processor = new Processor(parkingFileFormat, parkingFileName, propertyFileName, popFileName, logFileName);
		CommandLineUserInterface ui = new CommandLineUserInterface(processor);
		ui.start();
		
	}

}
