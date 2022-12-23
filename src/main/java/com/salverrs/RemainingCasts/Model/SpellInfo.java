package com.salverrs.RemainingCasts.Model;

import lombok.Getter;

public class SpellInfo {
    @Getter
    private String name;
    @Getter
    private int spriteId;
    @Getter
    private SpellCost spellCost;

    public SpellInfo(String name, int spriteId, SpellCost spellCost)
    {
        this.name = name;
        this.spriteId = spriteId;
        this.spellCost = spellCost;
    }
}
