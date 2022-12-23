package com.salverrs.RemainingCasts.Util;

public class CastUtils {

    public static String getShortenedAmount(double amount)
    {
        if (amount > 1000000)
        {
            return String.format("%.1f", amount / 1000000) + "m";
        }
        else if (amount > 1000)
        {
            return String.format("%.1f", amount / 1000) + "k";
        }
        else
        {
            return Integer.toString((int)amount);
        }
    }
}



