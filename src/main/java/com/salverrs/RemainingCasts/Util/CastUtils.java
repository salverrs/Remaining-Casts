package com.salverrs.RemainingCasts.Util;

public class CastUtils {

    public static String getShortenedAmount(double amount)
    {
        if (amount == Integer.MAX_VALUE)
        {
            return "*";
        }
        else if (amount > 1000000000)
        {
            return String.format("%.1f", amount / 1000000000) + "b";
        }
        else if (amount > 1000000)
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

    public static String formatCastAmount(int amount)
    {
        return amount == Integer.MAX_VALUE ? "*" : Integer.toString(amount);
    }
}



