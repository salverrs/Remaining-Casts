package com.salverrs.RemainingCasts.Infobox;

import com.salverrs.RemainingCasts.Model.SpellCost;
import com.salverrs.RemainingCasts.Model.SpellInfo;
import com.salverrs.RemainingCasts.Util.CastUtils;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Map;

public class RemainingCastsInfoBox extends InfoBox
{
    private SpellInfo spellInfo;
    private SpellCost spellCost;
    private int remainingCasts;
    private long lastUpdated;
    private boolean shouldShorten;

    public RemainingCastsInfoBox(SpellInfo spellInfo, Map<Integer, Integer> runeCount, BufferedImage image, boolean shorten, Plugin plugin)
    {
        super(image, plugin);
        this.spellInfo = spellInfo;
        this.spellCost = spellInfo.getSpellCost();
        this.lastUpdated = Instant.now().getEpochSecond();
        this.remainingCasts = this.spellCost.getRemainingCasts(runeCount);
        this.shouldShorten = shorten;
    }

    @Override
    public String getText()
    {
        return getRemainingCastsString();
    }

    @Override
    public String getTooltip()
    {
        return spellInfo.getName() + " - " + remainingCasts + " remaining casts";
    }

    @Override
    public Color getTextColor()
    {
        return Color.white;
    }

    public void update(Map<Integer, Integer> runeCount)
    {
        update(runeCount, false);
    }

    public void update(Map<Integer, Integer> runeCount, boolean wasCast)
    {
        remainingCasts = spellCost.getRemainingCasts(runeCount);

        if (wasCast)
            lastUpdated = Instant.now().getEpochSecond();
    }

    public long getActiveTime()
    {
        return (Instant.now().getEpochSecond() - lastUpdated);
    }

    private String getRemainingCastsString()
    {
        if (remainingCasts == -1)
            return "N/A";

        return shouldShorten ? CastUtils.getShortenedAmount(remainingCasts) : Integer.toString(remainingCasts);
    }

}
