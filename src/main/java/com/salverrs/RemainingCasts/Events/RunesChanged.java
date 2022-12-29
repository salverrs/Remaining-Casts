package com.salverrs.RemainingCasts.Events;

import com.salverrs.RemainingCasts.Model.RuneChanges;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RunesChanged {
    private RuneChanges changes;

    public RunesChanged(RuneChanges changes)
    {
        this.changes = changes;
    }
}
