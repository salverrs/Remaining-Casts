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

    private int tooltipWidgetId = -1;
    private boolean active = false;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private CastSuppliesTracker runeCountTracker;
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
        for (MenuEntry entry : entries)
        {
            final Widget widget = entry.getWidget();
            if (widget == null)
                continue;

            final String spellName = Text.removeFormattingTags(widget.getName());
            final SpellInfo spellInfo = SpellIds.getSpellByName(spellName);

            if (spellInfo != null)
            {
                final String target = entry.getTarget();
                final Map<Integer, Integer> runeCount = runeCountTracker.getLastRuneCount();
                final int numCasts = spellInfo.getSpellCost().getRemainingCasts(runeCount);
                final String casts = "(" + getRemainingCastsString(numCasts) + ")";

                if (target.endsWith(casts))
                    continue;

                entry.setTarget(target + " " + casts);
                continue;
            }
        }
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event)
    {
        if (!active || !config.enableSpellTooltip() || event.getScriptId() != 2622)
            return;

        final int tooltipWidgetArgIndex = 5;
        final ScriptEvent scriptEvent = event.getScriptEvent();
        final Object[] args = scriptEvent.getArguments();

        tooltipWidgetId = (int)args[tooltipWidgetArgIndex];
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event)
    {
        if (!active || !config.enableSpellTooltip())
            return;

        if (event.getScriptId() != 2622 || tooltipWidgetId == -1)
            return;

        clientThread.invoke(() ->
        {
            Widget widget = client.getWidget(tooltipWidgetId);
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

            final SpellInfo spellInfo = SpellIds.getSpellByName(details.getSpellName());
            if (spellInfo == null)
                return;

            final SpellCost spellCost = spellInfo.getSpellCost();
            final int numCasts = spellCost.getRemainingCasts(runeCountTracker.getLastRuneCount());

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
