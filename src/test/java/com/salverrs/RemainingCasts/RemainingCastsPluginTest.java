package com.salverrs.RemainingCasts;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RemainingCastsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RemainingCastsPlugin.class);
		RuneLite.main(args);
	}
}