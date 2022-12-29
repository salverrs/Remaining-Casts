package com.salverrs.RemainingCasts.Model;

import lombok.Getter;

@Getter
public class SpellInfo {
    private String name;
    private int spriteId;
    private SpellCost spellCost;
    private SpellProduct[] spellProducts;

    public SpellInfo(String name, int spriteId, SpellCost spellCost, SpellProduct... products)
    {
        this(name, spriteId, spellCost);
        this.spellProducts = products;
    }

    public SpellInfo(String name, int spriteId, SpellCost spellCost)
    {
        this.name = name;
        this.spriteId = spriteId;
        this.spellCost = spellCost;
    }

}
