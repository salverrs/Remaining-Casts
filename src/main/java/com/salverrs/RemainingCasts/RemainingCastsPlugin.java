package com.salverrs.RemainingCasts;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Remaining Casts",
	description = "Displays the number of casts remaining for your spells.",
	tags = {"remaining","casts","magic","spell","rune","tracker"}
)
public class RemainingCastsPlugin extends Plugin
{
	public static final String CONFIG_GROUP = "RemainingCasts";

	@Inject
	private RemainingCastsConfig config;
	@Inject
	private EventBus eventBus;
	@Inject
	private CastSuppliesTracker runeCountTracker;
	@Inject
	private RemainingCastTracker castTracker;
	@Inject
	private TooltipCastUpdater tooltipUpdater;

	@Override
	protected void startUp() throws Exception
	{
		eventBus.register(runeCountTracker);
		eventBus.register(castTracker);
		eventBus.register(tooltipUpdater);

		runeCountTracker.start();
		castTracker.start(this);
		tooltipUpdater.start();
	}

	@Override
	protected void shutDown() throws Exception
	{
		eventBus.unregister(runeCountTracker);
		eventBus.register(castTracker);
		eventBus.register(tooltipUpdater);

		runeCountTracker.stop();
		castTracker.stop();
		tooltipUpdater.stop();
	}


	@Provides
	RemainingCastsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RemainingCastsConfig.class);
	}
}
