package org.educoins.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FormatToScientifc {

	public static String format(BigDecimal x, int scale) {
		NumberFormat formatter = new DecimalFormat("0.0E0");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		formatter.setMinimumFractionDigits(scale);
		return formatter.format(x);
	}
	
	public static String format(int x, int scale) {
		NumberFormat formatter = new DecimalFormat("0.0E0");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		formatter.setMinimumFractionDigits(scale);
		return formatter.format(x);
	}
	
	public static String format(Sha256Hash x, int scale) {
		NumberFormat formatter = new DecimalFormat("0.0E0");
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		formatter.setMinimumFractionDigits(scale);
		return formatter.format(x.toBigInteger());
	}
}
