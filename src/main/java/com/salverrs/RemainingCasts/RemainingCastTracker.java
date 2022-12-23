package com.salverrs.RemainingCasts;

import com.salverrs.RemainingCasts.Events.RunesChanged;
import com.salverrs.RemainingCasts.Infobox.RemainingCastsInfoBox;
import com.salverrs.RemainingCasts.Model.RuneChanges;
import com.salverrs.RemainingCasts.Model.SpellFilterOption;
import com.salverrs.RemainingCasts.Util.SpellIds;
import com.salverrs.RemainingCasts.Model.SpellInfo;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.SpriteID;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.util.*;

@Singleton
public class RemainingCastTracker {

    private Map<SpellInfo, RemainingCastsInfoBox> castBoxes = new HashMap<>();
    private Queue<SpellInfo> spellQueue = new LinkedList<>();
    private Map<Integer, Integer> runeCount;
    private boolean active = false;
    private boolean otherItemContainerOpen = false;
    private int lastCastSpriteId = -1;
    private String lastCastSpellName;
    private Plugin plugin;

    @Inject
    private Client client;
    @Inject
    private RuneCountTracker runeCountTracker;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject RemainingCastsConfig config;

    @Subscribe
    public void onClientTick(ClientTick tick)
    {
        if (!active || !config.enableInfoboxes())
            return;

        checkCastBoxExpiry();
    }

    @Subscribe
    public void onRunesChanged(RunesChanged event)
    {
        if (!active || !config.enableInfoboxes())
            return;

        final RuneChanges changes = event.getChanges();
        SpellInfo spellInfo = getLastSpellCasted(changes);

        if (otherItemContainerOpen) // Ignore all item deposits
            spellInfo = null;

        runeCount = changes.getCurrentRunes();
        updateCastBoxes(spellInfo);

        if (spellInfo == null || isFiltered(spellInfo))
            return;

        processCast(spellInfo);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!active || !config.enableInfoboxes())
            return;

        final MenuEntry entry = event.getMenuEntry();
        final String option = entry.getOption();
        final String target = entry.getTarget();

        if (!option.equals("Cast") && !target.contains("Teleport"))
            return;

        final Widget widget = entry.getWidget();
        final String spellName = Text.removeFormattingTags(target);

        if (SpellIds.getSpellByName(spellName) != null)
            lastCastSpellName = spellName;

        if (widget == null)
            return;

        final int spriteId = widget.getSpriteId();
        if (SpellIds.getSpellBySpriteId(spriteId) != null)
            lastCastSpriteId = spriteId;
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event)
    {
        if (isOtherItemContainerWidget(event.getGroupId()))
        {
            otherItemContainerOpen = true;
            lastCastSpriteId = -1;
            lastCastSpellName = null;
        }
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event)
    {
        if (isOtherItemContainerWidget(event.getGroupId()))
        {
            otherItemContainerOpen = false;
        }
    }

    public void start(Plugin plugin)
    {
        active = true;
        this.plugin = plugin;
    }

    public void stop()
    {
        active = false;
        lastCastSpriteId = -1;
        lastCastSpellName = null;
        removeAllCastBoxes();
    }

    private SpellInfo getLastSpellCasted(RuneChanges changes)
    {
        SpellInfo spellInfo = lastCastSpriteId != -1 ? SpellIds.getSpellBySpriteId(lastCastSpriteId) : null;
        if (matchesSpellInfo(spellInfo, changes))
            return spellInfo;

        spellInfo = lastCastSpellName != null ? SpellIds.getSpellByName(lastCastSpellName) : null;
        if (matchesSpellInfo(spellInfo, changes))
            return spellInfo;

        spellInfo = getSpellFromAutocast();
        if (matchesSpellInfo(spellInfo, changes))
            return spellInfo;

        return null;
    }

    private boolean matchesSpellInfo(SpellInfo info, RuneChanges changes)
    {
        final Map<Integer, Integer> cost = changes.getInvertedChanges();
        return info != null && info.getSpellCost().matchesCost(cost, changes.getUnlimitedRunes());
    }

    private SpellInfo getSpellFromAutocast()
    {
        final Widget w = client.getWidget(WidgetInfo.COMBAT_SPELL_ICON);
        if (w == null)
            return null;

        final int spriteId = w.getSpriteId();
        return SpellIds.getSpellBySpriteId(spriteId);
    }

    private void updateCastBoxes(SpellInfo recentCast)
    {
        if (recentCast != null && castBoxes.containsKey(recentCast))
        {
            final RemainingCastsInfoBox infoBox = castBoxes.get(recentCast);
            infoBox.update(runeCount, true);
        }

        castBoxes.values().forEach(c -> c.update(runeCount));
    }

    private void processCast(SpellInfo spellInfo)
    {
        if (!castBoxes.containsKey(spellInfo))
            createRemainingCastsBox(spellInfo);
    }

    private void createRemainingCastsBox(SpellInfo spellInfo)
    {
        final BufferedImage sprite = config.showInfoBoxSprites()
                ? spriteManager.getSprite(spellInfo.getSpriteId(), 0)
                : getSpellBookSprite();

        final RemainingCastsInfoBox infoBox = new RemainingCastsInfoBox(spellInfo, runeCount, sprite, config.shortenCastAmounts(), plugin);
        infoBoxManager.addInfoBox(infoBox);
        castBoxes.put(spellInfo, infoBox);
        spellQueue.add(spellInfo);

        checkCastBoxLimit();
    }

    private void checkCastBoxLimit()
    {
        if (spellQueue.size() == 0 || config.infoBoxSpellLimit() <= 0)
            return;

        if (spellQueue.size() > config.infoBoxSpellLimit())
        {
            final SpellInfo oldest = spellQueue.peek();
            removeCastBox(oldest);
        }
    }

    private void checkCastBoxExpiry()
    {
        final int expirySeconds = config.infoBoxExpirySeconds();
        if (expirySeconds == 0)
            return;

        final List<SpellInfo> expired = new ArrayList<>();
        for (final SpellInfo spellInfo : castBoxes.keySet())
        {
            final RemainingCastsInfoBox box = castBoxes.get(spellInfo);
            if (box.getActiveTime() > expirySeconds)
                expired.add(spellInfo);
        }

        expired.forEach(this::removeCastBox);
    }

    private void removeCastBox(SpellInfo spellInfo)
    {
        if (!castBoxes.containsKey(spellInfo))
            return;

        final RemainingCastsInfoBox box = castBoxes.get(spellInfo);
        infoBoxManager.removeInfoBox(box);
        castBoxes.remove(spellInfo);
        spellQueue.remove(spellInfo);
    }

    private void removeAllCastBoxes()
    {
        infoBoxManager.removeIf(i -> i instanceof RemainingCastsInfoBox);
        castBoxes.clear();
        spellQueue.clear();
    }

    private boolean isFiltered(SpellInfo spellInfo)
    {
        if (config.spellFilterOption() == SpellFilterOption.DISABLED)
            return false;

        final String filterList = config.filterList();
        final List<String> spellNames = Text.fromCSV(filterList);

        if (config.spellFilterOption() == SpellFilterOption.BLACKLIST)
        {
            return spellNames.stream().anyMatch(s -> spellInfo.getName().equals(s));
        }
        else if (config.spellFilterOption() == SpellFilterOption.WHITELIST)
        {
            return spellNames.stream().noneMatch(s -> spellInfo.getName().equals(s));
        }

        return false;
    }

    private BufferedImage getSpellBookSprite()
    {
        final Widget spellBook = client.getWidget(WidgetInfo.RESIZABLE_VIEWPORT_MAGIC_ICON);
        return spellBook != null ? spriteManager.getSprite(spellBook.getSpriteId(), 0)
                : spriteManager.getSprite(SpriteID.TAB_MAGIC, 0);
    }

    private boolean isOtherItemContainerWidget(int groupId)
    {
        switch (groupId)
        {
            case WidgetID.BANK_GROUP_ID:
            case WidgetID.DEPOSIT_BOX_GROUP_ID:
            case WidgetID.TRADE_WINDOW_GROUP_ID:
            case WidgetID.GROUP_STORAGE_GROUP_ID:
                return true;
            default:
                return false;
        }
    }

}
