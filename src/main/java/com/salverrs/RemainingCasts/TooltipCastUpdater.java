package com.salverrs.RemainingCasts;

import com.salverrs.RemainingCasts.Model.SpellCost;
import com.salverrs.RemainingCasts.Model.SpellInfo;
import com.salverrs.RemainingCasts.Model.TooltipDetails;
import com.salverrs.RemainingCasts.Util.CastUtils;
import com.salverrs.RemainingCasts.Util.SpellIds;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class TooltipCastUpdater {

    private int mageBookTooltipWidgetId = -1;
    private int autocastTooltipWidgetId = -1;
    private boolean active = false;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private CastSuppliesTracker castSuppliesTracker;
    @Inject RemainingCastsConfig config;

    public void start()
    {
        active = true;
    }

    public void stop()
    {
        active = false;
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (!active || !config.enableMenuTooltip()|| client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen())
            return;

        final List<MenuEntry> entries = new ArrayList<>(Arrays.asList(client.getMenuEntries()));
        boolean appendToOption = false;

        if (entries.stream().anyMatch(e -> e.getOption().equals("Examine")))
            return;

        for (MenuEntry entry : entries)
        {
            final Widget widget = entry.getWidget();
            if (widget == null)
                continue;

            String spellName = Text.removeFormattingTags(widget.getName()); // Standard spell book interface
            SpellInfo spellInfo = SpellIds.getSpellByName(spellName);

            if (spellInfo == null) // Autocast fallback
            {
                spellName = Text.removeFormattingTags(entry.getOption()); // Autocast menu uses option only
                spellInfo = SpellIds.getSpellByName(spellName);
                appendToOption = true;
            }

            if (spellInfo == null)
                return;

            final Map<Integer, Integer> runeCount = castSuppliesTracker.getLastRuneCount();
            final int numCasts = spellInfo.getSpellCost().getRemainingCasts(runeCount);
            final String casts = "(" + getRemainingCastsString(numCasts) + ")";

            if (appendToOption)
            {
                final String option = entry.getOption();
                if (option.endsWith(casts))
                    continue;

                entry.setOption(option + " " + casts);
            }
            else
            {
                final String target = entry.getTarget();
                if (target.endsWith(casts))
                    continue;

                entry.setTarget(target + " " + casts);
            }

        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event)
    {
        if (!active || !config.enableSpellTooltip())
            return;

        final boolean isMageBookTooltip = event.getScriptId() == 2622; // main spell tool tip
        final boolean isAutocastTooltip = event.getScriptId() == 238; // auto cast tooltip

        if (!isMageBookTooltip && !isAutocastTooltip)
            return;

        final ScriptEvent scriptEvent = event.getScriptEvent();
        final Object[] args = scriptEvent.getArguments();
        final int tooltipWidgetArgIndex = isMageBookTooltip ? 5 : 1;

        if (isMageBookTooltip)
            mageBookTooltipWidgetId = (int)args[tooltipWidgetArgIndex];
        else if (isAutocastTooltip)
            autocastTooltipWidgetId = (int)args[tooltipWidgetArgIndex];
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event)
    {
        if (!active || !config.enableSpellTooltip())
            return;

        final int scriptId = event.getScriptId();
        if (scriptId != 2622 && scriptId != 238)
            return;

        final int widgetId = scriptId == 2622 ? mageBookTooltipWidgetId : autocastTooltipWidgetId;

        clientThread.invoke(() ->
        {
            Widget widget = client.getWidget(widgetId);
            if (widget == null)
                return;

            Widget[] children = widget.getChildren();
            if (children == null || children.length == 0)
                return;

            final Widget spellTitle = getTitleWidget(children);
            if (spellTitle == null)
                return;

            final TooltipDetails details = getSpellTooltipDetails(spellTitle);
            if (details == null)
                return;

            if (details.getSpellName().contains("Home Teleport")) // For consistency
            {
                final String newText = "[" + details.getSpellLevel() + "] " + details.getSpellName();
                spellTitle.setText(newText);
                return;
            }

            final SpellInfo spellInfo = SpellIds.getSpellByName(details.getSpellName());
            if (spellInfo == null)
                return;

            final SpellCost spellCost = spellInfo.getSpellCost();
            final int numCasts = spellCost.getRemainingCasts(castSuppliesTracker.getLastRuneCount());

            String newText = "[" + details.getSpellLevel() + "] " + details.getSpellName();
            newText += " (" + getRemainingCastsString(numCasts) + ")" ;

            spellTitle.setText(newText);
        });

    }


    private Widget getTitleWidget(Widget[] tooltipChildren)
    {
        return Arrays.stream(tooltipChildren)
                .filter(c -> c.getText().contains("Level"))
                .findFirst()
                .orElse(null);
    }

    private TooltipDetails getSpellTooltipDetails(Widget titleWidget)
    {
        final String titleText = titleWidget.getText();
        final String[] split = titleText.split(": ");
        if (split.length < 2)
            return null;

        final String name = split[1];
        final String[] levelSplit = split[0].split("Level ");
        if (levelSplit.length < 2)
            return null;

        final String level = levelSplit[1];
        return new TooltipDetails(name, level);
    }

    private String getRemainingCastsString(int numCasts)
    {
        if (numCasts == -1)
            return "N/A";

        return config.shortenCastAmounts() ? CastUtils.getShortenedAmount(numCasts) : Integer.toString(numCasts);
    }

}
