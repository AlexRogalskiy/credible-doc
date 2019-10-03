package com.credibledoc.log.labelizer.hint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewYearHint {
	public static final int OLDEST_YEAR = 1980;
	public static final int ACTUAL_YEAR = Calendar.getInstance().get(Calendar.YEAR);
	public static final int SHORT_OLD_YEAR = 80;
	DateFormat shortActualYear = new SimpleDateFormat("yy");
	public static final int SHORT_ACTUAL_YEAR = Calendar.getInstance().get(Calendar.YEAR) % 100;
	public static final int SHORT_ZERO_YEAR = 00; // year 2000 in short version
	public static final int SHORT_HELPFULL_YEAR = 100; // year 2000 in helpfull version for nineteens years

	public static void main(String[] args) {
		String input = "28.2.2019 11:45:00.123 1234567654 abcde 28.2.2018 11:46:00.124";
		String output = "wwwwwddddwddwwwwddwwwwwwwwwwwwwwwwwwwwwwwwwwwddddwddwwwwddwwww";
		Integer lastInputValue = (input.length() - 1);
		StringBuffer result = new StringBuffer();
		StringBuffer context = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			Integer valueOfInput = i;
			controlLastValueExit(output, result, lastInputValue, valueOfInput);
			Character character = input.charAt(i);
			System.out.println(character + ":" + Character.isDigit(character));
			if (!Character.isDigit(character)) {
				result.append("w");
				context.delete(0, context.length()); // zajišťuje, že context nebude počítat s hodnotami, které byli v
				// předešlém zkoumaném čísle
			} else {
				context.append(character);
				if (context.length() == 1) {
					nextCharControl(valueOfInput, input, context, result);
				}
				if (context.length() == 2 || context.length() == 4) {
					if (context.length() == 2) {
						nextCharControl(valueOfInput, input, context, result);
					}
					if (context.length() == 4) {
						nextCharControl(valueOfInput, input, context, result);
					}
				} else {
					if (context.length() > 4) {
						while (Character.isDigit(character)) {
							context.append(character);
							i++;
							character = input.charAt(i);
							System.out.println(character + ":" + Character.isDigit(character));
							result.append("w");
						}
					}
				}
			}
		}
	}

	private static void nextCharControl(Integer valueOfInput, String input, StringBuffer context, StringBuffer result) {
		int c = valueOfInput;
		c++;
		Character nextChar = input.charAt(c);
		if (!Character.isDigit(nextChar)) {
			if (context.length() != 1) {
				dateControl(context, result);
			} else {
				result.append("w");
			}
		}

	}

	private static void dateControl(StringBuffer context, StringBuffer result) {
		Integer contextResult = Integer.valueOf(context.toString());
		boolean isDate = isDate(contextResult);
		if (isDate == true) {
			for (int d = 0; d < context.length(); d++) {
				result.append("d"); // píše "d" pouze pro případ, který odpovídá metodě isDate
			}
		} else {
			for (int w = 0; w < context.length(); w++) {
				result.append("w"); // píše "d" pouze pro případ, který odpovídá metodě isDate
			}
		}
	}

	private static boolean isDate(Integer contextResult) {
		if ((contextResult >= OLDEST_YEAR && contextResult <= ACTUAL_YEAR)
				|| (contextResult >= SHORT_ZERO_YEAR && contextResult <= SHORT_ACTUAL_YEAR)
				|| (contextResult >= SHORT_OLD_YEAR && contextResult < SHORT_HELPFULL_YEAR)) {
			return true;
		}
		return false;
	}

	private static void controlLastValueExit(String output, StringBuffer result, Integer lastInputValue, Integer valueOfInput) {
		if (valueOfInput == lastInputValue) {
			System.out.println(output); // Pro ověření že výstup sedí
			System.out.println(result.toString());
			System.out.println(result.toString().equals(output));
			System.exit(0);
		}
	}

}
