package com.salverrs.RemainingCasts.Infobox;

import com.salverrs.RemainingCasts.Model.SpellCost;
import com.salverrs.RemainingCasts.Model.SpellInfo;
import com.salverrs.RemainingCasts.RemainingCastsConfig;
import com.salverrs.RemainingCasts.Util.CastUtils;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Map;

@Getter
public class RemainingCastsInfoBox extends InfoBox
{
    private final SpellInfo spellInfo;
    private final SpellCost spellCost;
    private RemainingCastsConfig config;
    private int remainingCasts;
    private long lastUpdated;
    @Setter
    private boolean isPinned;

    public RemainingCastsInfoBox(SpellInfo spellInfo, Map<Integer, Integer> runeCount, BufferedImage image, Plugin plugin, RemainingCastsConfig config)
    {
        super(image, plugin);
        this.spellInfo = spellInfo;
        this.spellCost = spellInfo.getSpellCost();
        this.lastUpdated = Instant.now().getEpochSecond();
        this.remainingCasts = this.spellCost.getRemainingCasts(runeCount);
        this.config = config;
    }

    @Override
    public String getText()
    {
        return getRemainingCastsString();
    }

    @Override
    public String getTooltip()
    {
        return spellInfo.getName() + " - " + remainingCasts + (remainingCasts != 1 ? " casts " : " cast ") + "remaining";
    }

    @Override
    public Color getTextColor()
    {
        return config.infoBoxTextColor();
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

        return config.shortenCastAmounts() ? CastUtils.getShortenedAmount(remainingCasts) : Integer.toString(remainingCasts);
    }

}
