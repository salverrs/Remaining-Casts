package com.salverrs.RemainingCasts.Model;

import lombok.Getter;
import net.runelite.api.ItemID;

import java.util.*;

@Getter
public class SpellCost {
    private int[] runes;
    private int[] quantities;

    public SpellCost(int[] runeIds, int[] quantities)
    {
        if (runeIds.length != quantities.length)
            throw new IndexOutOfBoundsException();

        this.runes = runeIds;
        this.quantities = quantities;
    }

    public boolean matchesCost(Map<Integer, Integer> runeCount, Set<Integer> unlimitedRunes)
    {
        for (int i = 0; i < runes.length; i++)
        {
            final int targetRuneId = runes[i];
            if (!unlimitedRunes.contains(targetRuneId) && (!runeCount.containsKey(targetRuneId) || runeCount.get(targetRuneId) < quantities[i]))
                return false;
        }

        return true;
    }

    public int getRemainingCasts(Map<Integer, Integer> runeCount)
    {
        final List<Integer> castsPerRune = new ArrayList<Integer>();
        for (int i = 0; i < runes.length; i++)
        {
            final int targetRuneId = runes[i];
            if (runeCount.containsKey(targetRuneId))
            {
                final int available = runeCount.get(targetRuneId);
                final int cost = quantities[i];
                castsPerRune.add(Math.floorDiv(available, cost));
            }
            else
            {
                return 0;
            }
        }

        return Collections.min(castsPerRune);
    }

}

