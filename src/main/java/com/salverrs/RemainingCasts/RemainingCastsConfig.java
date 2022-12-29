package com.salverrs.RemainingCasts;

import com.salverrs.RemainingCasts.Model.SpellFilterOption;
import net.runelite.client.config.*;

@ConfigGroup(RemainingCastsPlugin.CONFIG_GROUP)
public interface RemainingCastsConfig extends Config
{
	@ConfigSection(
			name = "General",
			description = "Enable/disable Remaining Casts features",
			position = 0
	)
	String generalSection = "general";

	@ConfigSection(
			name = "Infoboxes",
			description = "Options relating to remaining casts infoboxes",
			position = 1
	)
	String infoboxSection = "infoboxes";

	@ConfigSection(
			name = "Chat warnings",
			description = "Options relating to remaining casts chat warnings.",
			position = 2
	)
	String warningsSection = "warnings";

	@ConfigItem(
			keyName = "enableInfoboxes",
			name = "Show infoboxes",
			description = "Show remaining casts as individual info boxes",
			section = generalSection,
			position = 0
	)
	default boolean enableInfoboxes()
	{
		return true;
	}

	@ConfigItem(
		keyName = "enableMenuTooltip",
		name = "Show on menu tooltip",
		description = "Show remaining casts on the menu action tooltip",
		section = generalSection,
		position = 1
	)
	default boolean enableMenuTooltip()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableSpellTooltip",
			name = "Show on spell tooltip",
			description = "Show remaining casts on the detailed spell tooltip",
			section = generalSection,
			position = 2
	)
	default boolean enableSpellTooltip()
	{
		return true;
	}

	@ConfigItem(
			keyName = "shortenCastAmounts",
			name = "Shorten cast amounts",
			description = "Shorten remaining cast amounts (e.g. 2512 -> 2.5k)",
			section = generalSection,
			position = 3
	)
	default boolean shortenCastAmounts()
	{
		return true;
	}

	@Units(Units.SECONDS)
	@ConfigItem(
			keyName = "infoBoxExpirySeconds",
			name = "Infobox expiry",
			description = "The number of seconds until an infobox will expire. 0 represents no expiry",
			section = infoboxSection,
			position = 4
	)
	default int infoBoxExpirySeconds()
	{
		return 15;
	}


	@ConfigItem(
			keyName = "infoBoxSpellLimit",
			name = "Maximum spells",
			description = "The maximum number of spells to track remaining casts for. 0 represents no limit",
			section = infoboxSection,
			position = 5
	)
	default int infoBoxSpellLimit()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "infoBoxThreshold",
			name = "Cast threshold",
			description = "Only show when casts remaining is less than the threshold. 0 represents no threshold.",
			section = infoboxSection,
			position = 6
	)
	default int infoBoxThreshold()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "showInfoBoxSprites",
			name = "Show spell sprites",
			description = "Show the sprite image for the spell on the infobox. If disabled, the current spell book sprite will be used instead",
			section = infoboxSection,
			position = 7
	)
	default boolean showInfoBoxSprites()
	{
		return true;
	}

	@ConfigItem(
			keyName = "spellFilterOption",
			name = "Spell filter",
			description = "Enable or disable whitelist/blacklist for spells",
			section = infoboxSection,
			position = 8
	)
	default SpellFilterOption spellFilterOption()
	{
		return SpellFilterOption.DISABLED;
	}

	@ConfigItem(
			keyName = "filterList",
			name = "Filter list",
			description = "The names of the spells you wish to whitelist/blacklist as comma separated values",
			section = infoboxSection,
			position = 9
	)
	default String filterList()
	{
		return "";
	}

	@ConfigItem(
			keyName = "pinnedSpells",
			name = "Pinned spells",
			description = "These spells will always display an infobox that will never expire.",
			section = infoboxSection,
			position = 10
	)
	default String pinnedSpells()
	{
		return "";
	}

	@ConfigItem(
			keyName = "enableChatWarnings",
			name = "Enable chat warnings",
			description = "Enable chat warnings when remaining casts reach a predefined amount.",
			section = warningsSection,
			position = 11
	)
	default boolean useChatWarnings() { return true;}

	@ConfigItem(
			keyName = "chatWarningNotification",
			name = "Enable notifications",
			description = "Enable Runelite notification alongside the chat warning.",
			section = warningsSection,
			position = 12
	)
	default boolean useChatWarningNotifications()
	{
		return true;
	}

	@ConfigItem(
			keyName = "chatWarningThresholds",
			name = "Chat warning thresholds",
			description = "The amounts at which a chat warning will be triggered.",
			section = warningsSection,
			position = 13
	)
	default String chatWarningThresholds()
	{
		return "10, 50, 100, 500";
	}



}
