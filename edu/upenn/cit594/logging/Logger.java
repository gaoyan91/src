package edu.upenn.cit594.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

	private static PrintWriter out;
	private static File file;
	private static Logger logger = null;

	public static void setFileName(String fileName) {
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Logger getLogger() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}

	public void log(String msg) {
		out.println(msg);
		out.flush();		
	}

	public void logString(String[] args) {
		String line = "";
		line += System.currentTimeMillis();
		for (String each : args) {
			line += (" " + each);
		}
		log(line);
	}

	public void logString(String string) {
		String line = "";
		line += System.currentTimeMillis();
		line += (" " + string);
		log(line);
	}
	
	public void logString(int number) {
		String line = "";
		line += System.currentTimeMillis();
		line += (" " + Integer.toString(number));
		log(line);
	}

	public static void main(String[] args) {
		Logger.setFileName("newfile.txt");
		Logger l = Logger.getLogger();
		l.log("asdfasdf");
		l.logString(args);
	}
}