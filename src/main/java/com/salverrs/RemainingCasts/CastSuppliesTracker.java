package com.salverrs.RemainingCasts;

import com.salverrs.RemainingCasts.Events.BoltsEnchanted;
import com.salverrs.RemainingCasts.Events.RunesChanged;
import com.salverrs.RemainingCasts.Model.RuneChanges;
import com.salverrs.RemainingCasts.Model.SpellInfo;
import com.salverrs.RemainingCasts.Util.RuneIds;
import com.salverrs.RemainingCasts.Util.SpellIds;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.PostClientTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class CastSuppliesTracker {

    private static final int[] RUNE_POUCH_RUNE_VARBITS = {
            Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3, Varbits.RUNE_POUCH_RUNE4
    };

    private static final int[] RUNE_POUCH_AMOUNT_VARBITS = {
            Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3, Varbits.RUNE_POUCH_AMOUNT4
    };
    private static final int FOUNTAIN_OF_RUNES_VARBIT = 4145;

    final private Map<Integer, Integer> runeCount = new HashMap<>();
    final private Set<Integer> unlimitedRunes = new HashSet<>();
    private RuneChanges lastChanges;
    private boolean active = false;
    private boolean requiresPostUpdate = false;

    @Inject
    private Client client;
    @Inject
    private EventBus eventBus;

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        if (!active || !isRelevantItemContainer(event.getItemContainer()))
            return;

        updateRuneCount();
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        if (!active || requiresPostUpdate || !isRelevantVarbit(event.getVarbitId()))
            return;

        requiresPostUpdate = true;
    }

    @Subscribe
    public void onPostClientTick(PostClientTick event)
    {
        if (!requiresPostUpdate)
            return;

        updateRuneCount();
        requiresPostUpdate = false;
    }

    public void start()
    {
        active = true;
        requiresPostUpdate = true;
    }

    public void stop()
    {
        requiresPostUpdate = false;
        active = false;
    }

    public Map<Integer, Integer> forceUpdateRuneCount()
    {
        updateRuneCount();
        return runeCount;
    }

    public Map<Integer, Integer> getLastRuneCount()
    {
        return runeCount;
    }

    private void updateRuneCount()
    {
        final Map<Integer, Integer> lastCount, changes;
        lastCount = new HashMap<>(runeCount);

        updateCurrentRuneCount();
        changes = calculateRuneChangesMap(lastCount, runeCount);

        if (changes.size() != 0)
        {
            lastChanges = new RuneChanges(changes, runeCount, unlimitedRunes);
            eventBus.post(new RunesChanged(lastChanges));

            final SpellInfo enchant = getEnchantSpellCast(changes);
            if (enchant != null)
                eventBus.post(new BoltsEnchanted(enchant, lastChanges));
        }
    }

    public void updateCurrentRuneCount()
    {
        resetRuneCount();
        updateContainerItems();
        checkGlobalVarbits();
    }

    private void updateContainerItems()
    {
        unlimitedRunes.clear();

        final ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
        final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);

        final Item[] inventoryItems = inventory != null ? inventory.getItems() : new Item[] {};
        final Item[] equipmentItems = equipment != null ? equipment.getItems() : new Item[] {};

        Arrays.stream(inventoryItems).forEach(i -> updateInventoryItem(i.getId(), i.getQuantity()));
        Arrays.stream(equipmentItems).forEach(i -> updateEquipmentItems(i.getId(), i.getQuantity()));
    }

    private void updateInventoryItem(int itemId, int quantity)
    {
        if (itemId == ItemID.RUNE_POUCH || itemId == ItemID.RUNE_POUCH_L)
        {
            updateRunePouchItems(false);
            return;
        }
        else if (itemId == ItemID.DIVINE_RUNE_POUCH || itemId == ItemID.DIVINE_RUNE_POUCH_L)
        {
            updateRunePouchItems(true);
            return;
        }
        else if (itemId == ItemID.RUNE_POUCH_23650) // LMS Rune pouch
        {
            addLMSRunePouch();
            return;
        }
        else if (itemId == ItemID.RUNE_POUCH_27086) // Emir's Arena Rune pouch (assumed to contain all, unable to get specific runes yet)
        {
            addUnlimitedRunes();
            return;
        }

        final Integer[] runes = RuneIds.getRuneIds(itemId);
        if (runes != null)
            Arrays.stream(runes).forEach(r -> updateRuneCount(r, quantity));

        final int ingredient = RuneIds.getIngredientId(itemId);
        if (ingredient != -1)
            updateRuneCount(ingredient, quantity);

        if (RuneIds.isEnchantProduct(itemId))
            updateRuneCount(itemId, quantity);

    }

    private void updateEquipmentItems(int itemId, int quantity)
    {
        if (RuneIds.isEnchantProduct(itemId))
            updateRuneCount(itemId, quantity);

        final Integer[] runes = RuneIds.getRuneIdsFromEquipment(itemId);
        if (runes != null)
        {
            Arrays.stream(runes).forEach(r -> {
                unlimitedRunes.add(r);
                updateRuneCount(r, Integer.MAX_VALUE);
            });
        }

        final Integer[] items = RuneIds.getItemIdsFromEquipment(itemId);
        if (items != null)
        {
            Arrays.stream(items).forEach(i -> {
                updateRuneCount(i, quantity);
            });
        }
    }

    private void checkGlobalVarbits()
    {
        final boolean nearFountain = client.getVarbitValue(FOUNTAIN_OF_RUNES_VARBIT) == 1;

        if (nearFountain)
            addUnlimitedRunes();
    }

    private void updateRunePouchItems(boolean isDivine)
    {
        final EnumComposition runepouchEnum = client.getEnum(EnumID.RUNEPOUCH_RUNE);
        final int pouchSize = isDivine ? 4 : 3;

        for (int i = 0; i < pouchSize; i++)
        {
            final int id = client.getVarbitValue(RUNE_POUCH_RUNE_VARBITS[i]);
            final int quantity = client.getVarbitValue(RUNE_POUCH_AMOUNT_VARBITS[i]);

            if (id == 0 || quantity <= 0)
                continue;

            final int runeId = runepouchEnum.getIntValue(id);
            updateRuneCount(runeId, quantity);
        }
    }

    private void addLMSRunePouch()
    {
        runeCount.put(ItemID.WATER_RUNE, Integer.MAX_VALUE);
        runeCount.put(ItemID.DEATH_RUNE, Integer.MAX_VALUE);
        runeCount.put(ItemID.BLOOD_RUNE, Integer.MAX_VALUE);
        runeCount.put(ItemID.SOUL_RUNE, Integer.MAX_VALUE);
    }

    private void addUnlimitedRunes()
    {
        final Set<Integer> allRuneIds = RuneIds.getAllRuneIds();
        allRuneIds.forEach(id -> runeCount.put(id, Integer.MAX_VALUE));
    }

    private void updateRuneCount(int runeId, int quantity)
    {
        final Integer currentValue = runeCount.getOrDefault(runeId, null);
        final long expectedValue = currentValue != null ? (long)currentValue + (long)quantity : (long)quantity;

        if (expectedValue >= Integer.MAX_VALUE)
        {
            runeCount.put(runeId, Integer.MAX_VALUE);
        }
        else
        {
            runeCount.put(runeId, (int)expectedValue);
        }
    }

    private Map<Integer, Integer> calculateRuneChangesMap(Map<Integer, Integer> oldRunes, Map<Integer, Integer> newRunes)
    {
        final Map<Integer, Integer> changeMap = new HashMap<>();
        final Set<Integer> runeKeys = new HashSet<>(oldRunes.keySet());
        runeKeys.addAll(newRunes.keySet());

        for (Integer runeId : runeKeys)
        {
            final int oldQuantity = oldRunes.getOrDefault(runeId, 0);
            final int newQuantity = newRunes.getOrDefault(runeId, 0);
            final int change = newQuantity - oldQuantity;

            if (change != 0)
                changeMap.put(runeId, change);
        }

        return changeMap;
    }

    private SpellInfo getEnchantSpellCast(Map<Integer, Integer> changes)
    {
        for (int itemId : changes.keySet())
        {
            final int change = changes.get(itemId);
            final SpellInfo enchant = SpellIds.getSpellByProduct(itemId, change);

            if (enchant != null)
                return enchant;
        }

        return null;
    }

    private void resetRuneCount()
    {
        runeCount.clear();
    }

    private boolean isRelevantItemContainer(ItemContainer container)
    {
        return container == client.getItemContainer(InventoryID.INVENTORY)
            || container == client.getItemContainer(InventoryID.EQUIPMENT);
    }

    private boolean isRelevantVarbit(int varbitId)
    {
        return varbitId == FOUNTAIN_OF_RUNES_VARBIT ||
            Arrays.stream(RUNE_POUCH_RUNE_VARBITS).anyMatch(v -> v == varbitId) ||
            Arrays.stream(RUNE_POUCH_AMOUNT_VARBITS).anyMatch(v -> v == varbitId);
    }
}
