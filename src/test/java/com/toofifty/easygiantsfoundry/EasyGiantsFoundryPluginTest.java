package com.toofifty.easygiantsfoundry;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EasyGiantsFoundryPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(EasyGiantsFoundryPlugin.class);
        RuneLite.main(args);
    }
}