package com.salverrs.RemainingCasts.Model;

import lombok.Getter;

@Getter
public class TooltipDetails {
    private String spellName;
    private String spellLevel;

    public TooltipDetails(String name, String level)
    {
        this.spellName = name;
        this.spellLevel = level;
    }

}
