package com.salverrs.RemainingCasts;

import com.salverrs.RemainingCasts.Events.BoltsEnchanted;
import com.salverrs.RemainingCasts.Events.RunesChanged;
import com.salverrs.RemainingCasts.Infobox.RemainingCastsInfoBox;
import com.salverrs.RemainingCasts.Model.RuneChanges;
import com.salverrs.RemainingCasts.Model.SpellFilterOption;
import com.salverrs.RemainingCasts.Util.SpellIds;
import com.salverrs.RemainingCasts.Model.SpellInfo;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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
    private Map<Integer, Integer> runeCount = new HashMap<>();
    private boolean active = false;
    private boolean initUpdate = false;
    private boolean otherItemContainerOpen = false;
    private int lastCastSpriteId = -1;
    private String lastCastSpellName;
    private Plugin plugin;

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Notifier notifier;
    @Inject
    private ChatMessageManager chatManager;
    @Inject
    private CastSuppliesTracker suppliesTracker;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private InfoBoxManager infoBoxManager;
    @Inject RemainingCastsConfig config;


    @Subscribe
    public void onClientTick(ClientTick tick)
    {
        if (!active)
            return;

        checkCastBoxExpiry();
    }

    @Subscribe
    public void onRunesChanged(RunesChanged event)
    {
        if (!active || (!config.enableInfoboxes() && !config.useChatWarnings()))
            return;

        final RuneChanges changes = event.getChanges();
        SpellInfo spellInfo = getLastSpellCasted(changes);

        if (otherItemContainerOpen) // Ignore all item deposits
            spellInfo = null;

        runeCount = changes.getCurrentRunes();

        if (config.enableInfoboxes())
            updateCastBoxes(spellInfo);

        if (config.useChatWarnings())
            updateWarnings(spellInfo);

        if (!config.enableInfoboxes() || spellInfo == null || isFiltered(spellInfo))
            return;

        processCast(spellInfo);
    }

    @Subscribe
    public void onBoltsEnchanted(BoltsEnchanted event)
    {
        if (!active || (!config.enableInfoboxes() && !config.useChatWarnings()))
            return;

        final RuneChanges changes = event.getChanges();
        SpellInfo enchant = event.getEnchantSpell();

        if (!matchesSpellInfo(enchant, changes) || otherItemContainerOpen)
            enchant = null;

        runeCount = changes.getCurrentRunes();

        if (config.enableInfoboxes())
            updateCastBoxes(enchant);

        if (config.useChatWarnings())
            updateWarnings(enchant);

        if (!config.enableInfoboxes() || enchant == null || isFiltered(enchant))
            return;

        processCast(enchant);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!active)
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
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (!active || initUpdate)
            return;

        runeCount = suppliesTracker.forceUpdateRuneCount(); // Force initial rune count update

        if (config.enableInfoboxes())
        {
            updatePinnedSpells();
            updateCastBoxes(null);
        }

        initUpdate = true;
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

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        if (!configChanged.getGroup().equals(RemainingCastsPlugin.CONFIG_GROUP))
            return;

        if (configChanged.getKey().equals("enableInfoboxes"))
        {
            stop();
            start(plugin);
        }
        else
        {
            if (config.enableInfoboxes())
                updatePinnedSpells();
        }
    }

    public void start(Plugin plugin)
    {
        active = true;
        this.plugin = plugin;

        if (config.enableInfoboxes())
            updatePinnedSpells();
    }

    public void stop()
    {
        active = false;
        initUpdate = false;
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

    private void updateWarnings(SpellInfo recentCast)
    {
        if (recentCast == null)
            return;

        final List<Integer> thresholds = new ArrayList<>();
        final List<String> thresholdStrings = Text.fromCSV(config.chatWarningThresholds());
        thresholdStrings.forEach(ts -> thresholds.add(tryParseInt(ts, -1)));
        Collections.sort(thresholds);

        if (thresholds.size() == 0)
            return;

        final int remaining = recentCast.getSpellCost().getRemainingCasts(runeCount);

        for (int val : thresholds)
        {
            if (remaining == val)
            {
                final String msgContent = recentCast.getName() + " has " + val + " casts remaining.";
                final String msg = new ChatMessageBuilder()
                        .append(ChatColorType.HIGHLIGHT)
                        .append(msgContent)
                        .build();

                chatManager.queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .name(RemainingCastsPlugin.CONFIG_GROUP)
                        .runeLiteFormattedMessage(msg)
                        .build());

                if (config.useChatWarningNotifications())
                    notifier.notify(msgContent);

                break;
            }

        }

    }

    private void updateCastBoxes(SpellInfo recentCast)
    {
        final int threshold = config.infoBoxThreshold();
        final List<SpellInfo> toRemove = new ArrayList<>();

        if (recentCast != null && castBoxes.containsKey(recentCast))
        {
            final RemainingCastsInfoBox infoBox = castBoxes.get(recentCast);
            infoBox.update(runeCount, true);
        }

        castBoxes.values().forEach(c ->
        {
            if (threshold != 0 && c.getRemainingCasts() > threshold)
                toRemove.add(c.getSpellInfo());
            else
                c.update(runeCount);
        });

        toRemove.forEach(this::removeCastBox);
    }

    private void processCast(SpellInfo spellInfo)
    {
        if (!castBoxes.containsKey(spellInfo))
            createRemainingCastsBox(spellInfo);
    }

    private RemainingCastsInfoBox createRemainingCastsBox(SpellInfo spellInfo)
    {
        final BufferedImage sprite = config.showInfoBoxSprites()
                ? spriteManager.getSprite(spellInfo.getSpriteId(), 0)
                : getSpellBookSprite();

        final int threshold = config.infoBoxThreshold();
        final int remainingCasts = spellInfo.getSpellCost().getRemainingCasts(runeCount);
        if (threshold != 0 && remainingCasts > threshold)
            return null;

        final RemainingCastsInfoBox infoBox = new RemainingCastsInfoBox(spellInfo, runeCount, sprite, config.shortenCastAmounts(), false, plugin);
        infoBoxManager.addInfoBox(infoBox);
        castBoxes.put(spellInfo, infoBox);
        spellQueue.add(spellInfo);

        checkCastBoxLimit();

        return infoBox;
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
            if (!box.isPinned() && box.getActiveTime() > expirySeconds)
                expired.add(spellInfo);
        }

        expired.forEach(this::removeCastBox);
    }

    private void updatePinnedSpells()
    {
        final String pinned = config.pinnedSpells();
        final List<String> spellNames = Text.fromCSV(pinned);

        clientThread.invoke(() ->
        {
            castBoxes.values().forEach(cb -> cb.setPinned(false));

            if (spellNames.size() == 0)
                return;

            spellNames.forEach(name ->
            {
                final SpellInfo info = SpellIds.getSpellByName(name);
                if (info == null)
                    return;

                RemainingCastsInfoBox box = castBoxes.getOrDefault(info, null);
                if (box != null)
                {
                    box.setPinned(true);
                }
                else
                {
                    box = createRemainingCastsBox(info);
                    if (box != null)
                        box.setPinned(true);
                }
            });
        });
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
            return spellNames.stream().anyMatch(s -> spellInfo.getName().equalsIgnoreCase(s));
        }
        else if (config.spellFilterOption() == SpellFilterOption.WHITELIST)
        {
            return spellNames.stream().noneMatch(s -> spellInfo.getName().equalsIgnoreCase(s));
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

    public int tryParseInt(String value, int defaultVal) {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return defaultVal;
        }
    }


}
