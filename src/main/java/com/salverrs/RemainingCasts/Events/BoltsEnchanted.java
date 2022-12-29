package com.salverrs.RemainingCasts.Events;

import com.salverrs.RemainingCasts.Model.RuneChanges;
import com.salverrs.RemainingCasts.Model.SpellInfo;
import lombok.Getter;

import java.util.Map;

@Getter
public class BoltsEnchanted {
    private SpellInfo enchantSpell;

    private RuneChanges changes;

    public BoltsEnchanted(SpellInfo enchantSpell, RuneChanges changes)
    {
        this.enchantSpell = enchantSpell;
        this.changes = changes;
    }
}
