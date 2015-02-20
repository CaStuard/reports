package com.onboarding;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.onboarding.report.Report;

public class Main {

	private Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		Report report = new Report();
		Main main = new Main();
		System.out.println("Simple example of how this thing works");

		while (true) {
			boolean preview = true;
			int option = main.askOption("Show (1) or save (2) the report? (Exit = 0)", new int[] { 1, 2, 0 },
					"Wrong, try again");
			if (option == 0) {
				System.exit(0);
			}
			if (option == 2) {
				preview = false;
			}
			System.out.println("1. Participant Name");
			System.out.println("2. Program Description");
			System.out.println("3. Program Status");
			System.out.println("4. Start Date");
			System.out.println("0. Exit");
			option = main.askOption("Choose order method", new int[] { 1, 2, 3, 4, 0 },
					"Wrong, try again");
			switch(option) {
			case 1:
				report.generateReport(preview, "participantname");
				break;
			case 2:
				report.generateReport(preview, "programdescription");
				break;
			case 3:
				report.generateReport(preview, "programstatus");
				break;
			case 4:
				report.generateReport(preview, "startdate");
				break;
			default:
				System.exit(0);
			}
		}
	}

	public int askOption(String message, int[] validOptions, String invalidMessage) {
		int option;
		loop: while (true) {
			System.out.print(message);
			try {
				option = scanner.nextInt();
				for (int validOption : validOptions) {
					if (option == validOption) {
						break loop;
					}
				}
				System.out.println(invalidMessage);
			} catch (InputMismatchException e) {
				System.out.println(invalidMessage);
				scanner.nextLine();
			}
		}

		return option;
	}
}
