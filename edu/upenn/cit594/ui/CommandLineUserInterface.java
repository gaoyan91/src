package edu.upenn.cit594.ui;

import java.util.Scanner;

import edu.upenn.cit594.processor.Processor;

public class CommandLineUserInterface {
	Processor pro;
	
	public CommandLineUserInterface(Processor pro) {
		this.pro = pro;
	}

	public void start() {
		System.out.println("Please specify the action to be performed");
		try {
			Scanner in = new Scanner(System.in);
			if (in.hasNext()) {
				int action = Integer.parseInt(in.next());
				if (action < 1 || action > 6) {
					System.out.println("Invalid action");
					System.exit(0);
				}
				if (action == 1) {
					displayTotalpop();
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void displayTotalpop() {
		System.out.print("Here is the total population: " + pro.getTotalPop());
	}
}
