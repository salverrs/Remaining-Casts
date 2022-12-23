package com.salverrs.RemainingCasts.Model;

import com.google.common.primitives.Ints;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class RuneChanges {

    private Map<Integer, Integer> changes;
    private Map<Integer, Integer> currentRunes;
    private Set<Integer> unlimitedRunes;

    public RuneChanges(Map<Integer, Integer> changes, Map<Integer, Integer> currentRunes, Set<Integer> unlimitedRunes)
    {
        this.changes = changes;
        this.currentRunes = currentRunes;
        this.unlimitedRunes = unlimitedRunes;
    }

    public Map<Integer, Integer> getInvertedChanges()
    {
        Map<Integer, Integer> costMap = new HashMap<>(changes);
        costMap.replaceAll((k,v) -> v * -1);
        return costMap;
    }

}
