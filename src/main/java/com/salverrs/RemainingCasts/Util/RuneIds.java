package com.salverrs.RemainingCasts.Util;

import net.runelite.api.ItemID;

import java.util.*;

public class RuneIds {

    private static final Map<Integer, Integer[]> RuneMap = new HashMap<>();
    private static final Map<Integer, Integer[]> StaffMap = new HashMap<>();
    private static final Map<Integer, Integer> TomeMap = new HashMap<>();
    private static final Map<Integer, Integer> IngredientMap = new HashMap<>();
    private static final List<Integer> EnchantProducts = new ArrayList<>();

    public static Set<Integer> getAllRuneIds()
    {
        return RuneMap.keySet();
    }

    public static Integer[] getRuneIds(int runeId)
    {
        return RuneMap.getOrDefault(runeId, null);
    }

    public static int getIngredientId(int itemId)
    {
        return IngredientMap.getOrDefault(itemId, -1);
    }

    public static boolean isEnchantProduct(int itemId)
    {
        return EnchantProducts.contains(itemId);
    }

    public static Integer[] getItemIdsFromEquipment(int equipmentId)
    {
        if (StaffMap.containsKey(equipmentId))
        {
            final Set<Integer> staffRuneIds = new HashSet<>();
            final Integer[] equipmentRuneIds = StaffMap.get(equipmentId);
            for (int id : equipmentRuneIds)
                staffRuneIds.addAll(Arrays.asList(RuneMap.getOrDefault(id, new Integer[] { id })));

            return staffRuneIds.toArray(new Integer[0]);
        }
        else if (TomeMap.containsKey(equipmentId))
        {
            return RuneMap.getOrDefault(TomeMap.get(equipmentId), null);
        }
        return null;
    }

    static
    {
        // Regular runes
        RuneMap.put(ItemID.AIR_RUNE, new Integer[] {ItemID.AIR_RUNE});
        RuneMap.put(ItemID.WATER_RUNE, new Integer[] {ItemID.WATER_RUNE});
        RuneMap.put(ItemID.EARTH_RUNE, new Integer[] {ItemID.EARTH_RUNE});
        RuneMap.put(ItemID.FIRE_RUNE, new Integer[] {ItemID.FIRE_RUNE});
        RuneMap.put(ItemID.MIND_RUNE, new Integer[] {ItemID.MIND_RUNE});
        RuneMap.put(ItemID.BODY_RUNE, new Integer[] {ItemID.BODY_RUNE});
        RuneMap.put(ItemID.COSMIC_RUNE, new Integer[] {ItemID.COSMIC_RUNE});
        RuneMap.put(ItemID.CHAOS_RUNE, new Integer[] {ItemID.CHAOS_RUNE});
        RuneMap.put(ItemID.NATURE_RUNE, new Integer[] {ItemID.NATURE_RUNE});
        RuneMap.put(ItemID.LAW_RUNE, new Integer[] {ItemID.LAW_RUNE});
        RuneMap.put(ItemID.DEATH_RUNE, new Integer[] {ItemID.DEATH_RUNE});
        RuneMap.put(ItemID.ASTRAL_RUNE, new Integer[] {ItemID.ASTRAL_RUNE});
        RuneMap.put(ItemID.BLOOD_RUNE, new Integer[] {ItemID.BLOOD_RUNE});
        RuneMap.put(ItemID.SOUL_RUNE, new Integer[] {ItemID.SOUL_RUNE});
        RuneMap.put(ItemID.WRATH_RUNE, new Integer[] {ItemID.WRATH_RUNE});

        // Regular runes (variations)
        RuneMap.put(ItemID.AIR_RUNE_6422, new Integer[] {ItemID.AIR_RUNE});
        RuneMap.put(ItemID.AIR_RUNE_7558, new Integer[] {ItemID.AIR_RUNE});
        RuneMap.put(ItemID.AIR_RUNE_9693, new Integer[] {ItemID.AIR_RUNE});
        RuneMap.put(ItemID.AIR_RUNE_11688, new Integer[] {ItemID.AIR_RUNE});
        RuneMap.put(ItemID.AIR_RUNE_NZ, new Integer[] {ItemID.AIR_RUNE});

        RuneMap.put(ItemID.WATER_RUNE_6424, new Integer[] {ItemID.WATER_RUNE});
        RuneMap.put(ItemID.WATER_RUNE_7556, new Integer[] {ItemID.WATER_RUNE});
        RuneMap.put(ItemID.WATER_RUNE_9691, new Integer[] {ItemID.WATER_RUNE});
        RuneMap.put(ItemID.WATER_RUNE_11687, new Integer[] {ItemID.WATER_RUNE});
        RuneMap.put(ItemID.WATER_RUNE_NZ, new Integer[] {ItemID.WATER_RUNE});

        RuneMap.put(ItemID.EARTH_RUNE_6426, new Integer[] {ItemID.EARTH_RUNE});
        RuneMap.put(ItemID.EARTH_RUNE_9695, new Integer[] {ItemID.EARTH_RUNE});
        RuneMap.put(ItemID.EARTH_RUNE_11689, new Integer[] {ItemID.EARTH_RUNE});
        RuneMap.put(ItemID.EARTH_RUNE_NZ, new Integer[] {ItemID.EARTH_RUNE});

        RuneMap.put(ItemID.FIRE_RUNE_6428, new Integer[] {ItemID.FIRE_RUNE});
        RuneMap.put(ItemID.FIRE_RUNE_7554, new Integer[] {ItemID.FIRE_RUNE});
        RuneMap.put(ItemID.FIRE_RUNE_9699, new Integer[] {ItemID.FIRE_RUNE});
        RuneMap.put(ItemID.FIRE_RUNE_11686, new Integer[] {ItemID.FIRE_RUNE});
        RuneMap.put(ItemID.FIRE_RUNE_NZ, new Integer[] {ItemID.FIRE_RUNE});

        RuneMap.put(ItemID.MIND_RUNE_6436, new Integer[] {ItemID.MIND_RUNE});
        RuneMap.put(ItemID.MIND_RUNE_9697, new Integer[] {ItemID.MIND_RUNE});
        RuneMap.put(ItemID.MIND_RUNE_11690, new Integer[] {ItemID.MIND_RUNE});

        RuneMap.put(ItemID.BODY_RUNE_6438, new Integer[] {ItemID.BODY_RUNE});
        RuneMap.put(ItemID.BODY_RUNE_11691, new Integer[] {ItemID.BODY_RUNE});

        RuneMap.put(ItemID.COSMIC_RUNE_11696, new Integer[] {ItemID.COSMIC_RUNE});

        RuneMap.put(ItemID.CHAOS_RUNE_6430, new Integer[] {ItemID.CHAOS_RUNE});
        RuneMap.put(ItemID.CHAOS_RUNE_7560, new Integer[] {ItemID.CHAOS_RUNE});
        RuneMap.put(ItemID.CHAOS_RUNE_11694, new Integer[] {ItemID.CHAOS_RUNE});
        RuneMap.put(ItemID.CHAOS_RUNE_NZ, new Integer[] {ItemID.CHAOS_RUNE});

        RuneMap.put(ItemID.NATURE_RUNE_11693, new Integer[] {ItemID.NATURE_RUNE});

        RuneMap.put(ItemID.LAW_RUNE_6434, new Integer[] {ItemID.LAW_RUNE});
        RuneMap.put(ItemID.LAW_RUNE_11695, new Integer[] {ItemID.LAW_RUNE});

        RuneMap.put(ItemID.DEATH_RUNE_6432, new Integer[] {ItemID.DEATH_RUNE});
        RuneMap.put(ItemID.DEATH_RUNE_11692, new Integer[] {ItemID.DEATH_RUNE});
        RuneMap.put(ItemID.DEATH_RUNE_NZ, new Integer[] {ItemID.DEATH_RUNE});

        RuneMap.put(ItemID.ASTRAL_RUNE_11699, new Integer[] {ItemID.ASTRAL_RUNE});

        RuneMap.put(ItemID.BLOOD_RUNE_11697, new Integer[] {ItemID.BLOOD_RUNE});
        RuneMap.put(ItemID.BLOOD_RUNE_NZ, new Integer[] {ItemID.BLOOD_RUNE});

        RuneMap.put(ItemID.SOUL_RUNE_11698, new Integer[] {ItemID.SOUL_RUNE});

        RuneMap.put(ItemID.WRATH_RUNE_22208, new Integer[] {ItemID.WRATH_RUNE});

        // Combo runes
        RuneMap.put(ItemID.MIST_RUNE, new Integer[] {ItemID.AIR_RUNE, ItemID.WATER_RUNE});
        RuneMap.put(ItemID.DUST_RUNE, new Integer[] {ItemID.AIR_RUNE, ItemID.EARTH_RUNE});
        RuneMap.put(ItemID.MUD_RUNE, new Integer[] {ItemID.WATER_RUNE, ItemID.EARTH_RUNE});
        RuneMap.put(ItemID.SMOKE_RUNE, new Integer[] {ItemID.AIR_RUNE, ItemID.FIRE_RUNE});
        RuneMap.put(ItemID.STEAM_RUNE, new Integer[] {ItemID.WATER_RUNE, ItemID.FIRE_RUNE});
        RuneMap.put(ItemID.LAVA_RUNE, new Integer[] {ItemID.EARTH_RUNE, ItemID.FIRE_RUNE});

        // Staffs
        StaffMap.put(ItemID.STAFF_OF_AIR, new Integer[] {ItemID.AIR_RUNE });
        StaffMap.put(ItemID.STAFF_OF_WATER, new Integer[] {ItemID.WATER_RUNE});
        StaffMap.put(ItemID.STAFF_OF_EARTH, new Integer[] {ItemID.EARTH_RUNE});
        StaffMap.put(ItemID.STAFF_OF_FIRE, new Integer[] {ItemID.FIRE_RUNE});

        StaffMap.put(ItemID.AIR_BATTLESTAFF, new Integer[] {ItemID.AIR_RUNE});
        StaffMap.put(ItemID.WATER_BATTLESTAFF, new Integer[] {ItemID.WATER_RUNE});
        StaffMap.put(ItemID.EARTH_BATTLESTAFF, new Integer[] {ItemID.EARTH_RUNE});
        StaffMap.put(ItemID.FIRE_BATTLESTAFF, new Integer[] {ItemID.FIRE_RUNE});

        StaffMap.put(ItemID.MYSTIC_AIR_STAFF, new Integer[] {ItemID.AIR_RUNE});
        StaffMap.put(ItemID.MYSTIC_WATER_STAFF, new Integer[] {ItemID.WATER_RUNE});
        StaffMap.put(ItemID.MYSTIC_EARTH_STAFF, new Integer[] {ItemID.EARTH_RUNE});
        StaffMap.put(ItemID.MYSTIC_FIRE_STAFF, new Integer[] {ItemID.FIRE_RUNE});

        StaffMap.put(ItemID.MIST_BATTLESTAFF, new Integer[] {ItemID.MIST_RUNE});
        StaffMap.put(ItemID.DUST_BATTLESTAFF, new Integer[] {ItemID.DUST_RUNE});
        StaffMap.put(ItemID.MUD_BATTLESTAFF, new Integer[] {ItemID.MUD_RUNE});
        StaffMap.put(ItemID.SMOKE_BATTLESTAFF, new Integer[] {ItemID.SMOKE_RUNE});
        StaffMap.put(ItemID.STEAM_BATTLESTAFF, new Integer[] {ItemID.STEAM_RUNE});
        StaffMap.put(ItemID.LAVA_BATTLESTAFF, new Integer[] {ItemID.LAVA_RUNE});

        StaffMap.put(ItemID.MYSTIC_MIST_STAFF, new Integer[] {ItemID.MIST_RUNE});
        StaffMap.put(ItemID.MYSTIC_DUST_STAFF, new Integer[] {ItemID.DUST_RUNE});
        StaffMap.put(ItemID.MYSTIC_MUD_STAFF, new Integer[] {ItemID.MUD_RUNE});
        StaffMap.put(ItemID.MYSTIC_SMOKE_STAFF, new Integer[] {ItemID.SMOKE_RUNE});
        StaffMap.put(ItemID.MYSTIC_STEAM_STAFF, new Integer[] {ItemID.STEAM_RUNE});
        StaffMap.put(ItemID.MYSTIC_LAVA_STAFF, new Integer[] {ItemID.LAVA_RUNE});

        StaffMap.put(ItemID.BRYOPHYTAS_STAFF, new Integer[] {ItemID.NATURE_RUNE});

        // Required staffs
        StaffMap.put(ItemID.IBANS_STAFF, new Integer[] {ItemID.IBANS_STAFF});
        StaffMap.put(ItemID.IBANS_STAFF_U, new Integer[] {ItemID.IBANS_STAFF});
        StaffMap.put(ItemID.SLAYERS_STAFF, new Integer[] {ItemID.SLAYERS_STAFF});
        StaffMap.put(ItemID.SLAYERS_STAFF_E, new Integer[] {ItemID.SLAYERS_STAFF});
        StaffMap.put(ItemID.SARADOMIN_STAFF, new Integer[] {ItemID.SARADOMIN_STAFF});
        StaffMap.put(ItemID.GUTHIX_STAFF, new Integer[] {ItemID.GUTHIX_STAFF});
        StaffMap.put(ItemID.VOID_KNIGHT_MACE, new Integer[] {ItemID.GUTHIX_STAFF});
        StaffMap.put(ItemID.VOID_KNIGHT_MACE_L, new Integer[] {ItemID.GUTHIX_STAFF});
        StaffMap.put(ItemID.ZAMORAK_STAFF, new Integer[] {ItemID.ZAMORAK_STAFF});

        StaffMap.put(ItemID.STAFF_OF_THE_DEAD, new Integer[] {ItemID.SLAYERS_STAFF, ItemID.ZAMORAK_STAFF});
        StaffMap.put(ItemID.STAFF_OF_THE_DEAD_23613, new Integer[] {ItemID.SLAYERS_STAFF, ItemID.ZAMORAK_STAFF});
        StaffMap.put(ItemID.TOXIC_STAFF_OF_THE_DEAD, new Integer[] {ItemID.SLAYERS_STAFF, ItemID.ZAMORAK_STAFF});
        StaffMap.put(ItemID.TOXIC_STAFF_UNCHARGED, new Integer[] {ItemID.SLAYERS_STAFF, ItemID.ZAMORAK_STAFF});
        StaffMap.put(ItemID.STAFF_OF_LIGHT, new Integer[] {ItemID.SLAYERS_STAFF, ItemID.SARADOMIN_STAFF});
        StaffMap.put(ItemID.STAFF_OF_BALANCE, new Integer[] {ItemID.SLAYERS_STAFF, ItemID.GUTHIX_STAFF});

        // Tomes
        TomeMap.put(ItemID.TOME_OF_WATER, ItemID.WATER_RUNE);
        TomeMap.put(ItemID.TOME_OF_FIRE, ItemID.TOME_OF_FIRE);

        // Ingredients
        IngredientMap.put(ItemID.UNPOWERED_ORB, ItemID.UNPOWERED_ORB);
        IngredientMap.put(ItemID.OPAL_BOLTS, ItemID.OPAL_BOLTS);
        IngredientMap.put(ItemID.JADE_BOLTS, ItemID.JADE_BOLTS);
        IngredientMap.put(ItemID.PEARL_BOLTS, ItemID.PEARL_BOLTS);
        IngredientMap.put(ItemID.TOPAZ_BOLTS, ItemID.TOPAZ_BOLTS);
        IngredientMap.put(ItemID.SAPPHIRE_BOLTS, ItemID.SAPPHIRE_BOLTS);
        IngredientMap.put(ItemID.EMERALD_BOLTS, ItemID.EMERALD_BOLTS);
        IngredientMap.put(ItemID.RUBY_BOLTS, ItemID.RUBY_BOLTS);
        IngredientMap.put(ItemID.DIAMOND_BOLTS, ItemID.DIAMOND_BOLTS);
        IngredientMap.put(ItemID.DRAGONSTONE_BOLTS, ItemID.DRAGONSTONE_BOLTS);
        IngredientMap.put(ItemID.ONYX_BOLTS, ItemID.ONYX_BOLTS);
        IngredientMap.put(ItemID.OPAL_DRAGON_BOLTS, ItemID.OPAL_BOLTS);
        IngredientMap.put(ItemID.JADE_DRAGON_BOLTS, ItemID.JADE_BOLTS);
        IngredientMap.put(ItemID.PEARL_DRAGON_BOLTS, ItemID.PEARL_BOLTS);
        IngredientMap.put(ItemID.TOPAZ_DRAGON_BOLTS, ItemID.TOPAZ_BOLTS);
        IngredientMap.put(ItemID.SAPPHIRE_DRAGON_BOLTS, ItemID.SAPPHIRE_BOLTS);
        IngredientMap.put(ItemID.EMERALD_DRAGON_BOLTS, ItemID.EMERALD_BOLTS);
        IngredientMap.put(ItemID.RUBY_DRAGON_BOLTS, ItemID.RUBY_BOLTS);
        IngredientMap.put(ItemID.DIAMOND_DRAGON_BOLTS, ItemID.DIAMOND_BOLTS);
        IngredientMap.put(ItemID.DRAGONSTONE_DRAGON_BOLTS, ItemID.DRAGONSTONE_BOLTS);
        IngredientMap.put(ItemID.ONYX_DRAGON_BOLTS, ItemID.ONYX_BOLTS);

        // Products (Enchant Crossbow only)
        EnchantProducts.add(ItemID.OPAL_BOLTS_E);
        EnchantProducts.add(ItemID.JADE_BOLTS_E);
        EnchantProducts.add(ItemID.PEARL_BOLTS_E);
        EnchantProducts.add(ItemID.TOPAZ_BOLTS_E);
        EnchantProducts.add(ItemID.SAPPHIRE_BOLTS_E);
        EnchantProducts.add(ItemID.EMERALD_BOLTS_E);
        EnchantProducts.add(ItemID.RUBY_BOLTS_E);
        EnchantProducts.add(ItemID.DIAMOND_BOLTS_E);
        EnchantProducts.add(ItemID.DRAGONSTONE_BOLTS_E);
        EnchantProducts.add(ItemID.ONYX_BOLTS_E);
        EnchantProducts.add(ItemID.OPAL_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.JADE_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.PEARL_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.TOPAZ_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.SAPPHIRE_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.EMERALD_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.RUBY_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.DIAMOND_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.DRAGONSTONE_DRAGON_BOLTS_E);
        EnchantProducts.add(ItemID.ONYX_DRAGON_BOLTS_E);

    }

}