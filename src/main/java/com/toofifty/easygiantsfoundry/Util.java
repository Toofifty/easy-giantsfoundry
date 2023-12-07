package com.toofifty.easygiantsfoundry;

public class Util
{
	public static double format(double number, int decimalPlaces) {
		return Math.floor(number * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
	}
}
