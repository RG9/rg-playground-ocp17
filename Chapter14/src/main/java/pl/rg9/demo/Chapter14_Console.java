package pl.rg9.demo;

import java.io.Console;

/**
 * Running:  java src/main/java/pl/rg9/demo/Chapter14_Console.java
 */
public class Chapter14_Console {

	public static void main(String[] args) {
		Console console = System.console();

		console.writer().println("Enter name:");
		console.writer().flush();

		var name = console.readLine();
		console.format("Hello %s!%n", name);

		var pwd = console.readPassword("Enter password:");

		if (pwd[0] != '0') {
			console.format("Not permitted");
		} else {
			console.format("Welcome!%n");
		}
	}

}
