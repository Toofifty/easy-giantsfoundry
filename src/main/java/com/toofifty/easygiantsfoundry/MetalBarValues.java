package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.MetalBarSource;
import com.toofifty.easygiantsfoundry.enums.MetalBarType;
import lombok.Value;
import net.runelite.api.gameval.ItemID;

import java.util.HashMap;

public class MetalBarValues
{
	private static final HashMap<Integer, Record> values = new HashMap<>();

	static
	{
		// tin, copper, and iron are included here so the counter doesn't ignore them, but their actual counts are
		// handled as special cases
		putOre(ItemID.TIN_ORE, MetalBarType.BRONZE);
		putOre(ItemID.COPPER_ORE, MetalBarType.BRONZE);
		putOre(ItemID.IRON_BAR, MetalBarType.IRON);
		putOre(ItemID.MITHRIL_ORE, MetalBarType.MITHRIL);
		putOre(ItemID.ADAMANTITE_ORE, MetalBarType.ADAMANT);
		putOre(ItemID.RUNITE_ORE, MetalBarType.RUNITE);
		putOre(ItemID.Cert.TIN_ORE, MetalBarType.BRONZE);
		putOre(ItemID.Cert.COPPER_ORE, MetalBarType.BRONZE);
		putOre(ItemID.Cert.IRON_BAR, MetalBarType.IRON);
		putOre(ItemID.Cert.MITHRIL_ORE, MetalBarType.MITHRIL);
		putOre(ItemID.Cert.ADAMANTITE_ORE, MetalBarType.ADAMANT);
		putOre(ItemID.Cert.RUNITE_ORE, MetalBarType.RUNITE);

		putBar(ItemID.BRONZE_BAR, MetalBarType.BRONZE);
		putBar(ItemID.IRON_BAR, MetalBarType.IRON);
		putBar(ItemID.STEEL_BAR, MetalBarType.STEEL);
		putBar(ItemID.MITHRIL_BAR, MetalBarType.MITHRIL);
		putBar(ItemID.ADAMANTITE_BAR, MetalBarType.ADAMANT);
		putBar(ItemID.RUNITE_BAR, MetalBarType.RUNITE);
		putBar(ItemID.Cert.BRONZE_BAR, MetalBarType.BRONZE);
		putBar(ItemID.Cert.IRON_BAR, MetalBarType.IRON);
		putBar(ItemID.Cert.STEEL_BAR, MetalBarType.STEEL);
		putBar(ItemID.Cert.MITHRIL_BAR, MetalBarType.MITHRIL);
		putBar(ItemID.Cert.ADAMANTITE_BAR, MetalBarType.ADAMANT);
		putBar(ItemID.Cert.RUNITE_BAR, MetalBarType.RUNITE);

		putEquipment(ItemID.BRONZE_SCIMITAR, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.BRONZE_LONGSWORD, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.BRONZE_FULL_HELM, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.BRONZE_SQ_SHIELD, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.BRONZE_CLAWS, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.BRONZE_WARHAMMER, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.BRONZE_BATTLEAXE, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.BRONZE_CHAINBODY, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.BRONZE_KITESHIELD, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.BRONZE_2H_SWORD, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.BRONZE_PLATELEGS, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.BRONZE_PLATESKIRT, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.BRONZE_PLATEBODY, MetalBarType.BRONZE, 4);
		putEquipment(ItemID.Cert.BRONZE_SCIMITAR, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.Cert.BRONZE_LONGSWORD, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.Cert.BRONZE_FULL_HELM, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.Cert.BRONZE_SQ_SHIELD, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.Cert.BRONZE_CLAWS, MetalBarType.BRONZE, 1);
		putEquipment(ItemID.Cert.BRONZE_WARHAMMER, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.Cert.BRONZE_BATTLEAXE, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.Cert.BRONZE_CHAINBODY, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.Cert.BRONZE_KITESHIELD, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.Cert.BRONZE_2H_SWORD, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.Cert.BRONZE_PLATELEGS, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.Cert.BRONZE_PLATESKIRT, MetalBarType.BRONZE, 2);
		putEquipment(ItemID.Cert.BRONZE_PLATEBODY, MetalBarType.BRONZE, 4);

		putEquipment(ItemID.IRON_SCIMITAR, MetalBarType.IRON, 1);
		putEquipment(ItemID.IRON_LONGSWORD, MetalBarType.IRON, 1);
		putEquipment(ItemID.IRON_FULL_HELM, MetalBarType.IRON, 1);
		putEquipment(ItemID.IRON_SQ_SHIELD, MetalBarType.IRON, 1);
		putEquipment(ItemID.IRON_CLAWS, MetalBarType.IRON, 1);
		putEquipment(ItemID.IRON_WARHAMMER, MetalBarType.IRON, 2);
		putEquipment(ItemID.IRON_BATTLEAXE, MetalBarType.IRON, 2);
		putEquipment(ItemID.IRON_CHAINBODY, MetalBarType.IRON, 2);
		putEquipment(ItemID.IRON_KITESHIELD, MetalBarType.IRON, 2);
		putEquipment(ItemID.IRON_2H_SWORD, MetalBarType.IRON, 2);
		putEquipment(ItemID.IRON_PLATELEGS, MetalBarType.IRON, 2);
		putEquipment(ItemID.IRON_PLATESKIRT, MetalBarType.IRON, 2);
		putEquipment(ItemID.IRON_PLATEBODY, MetalBarType.IRON, 4);
		putEquipment(ItemID.Cert.IRON_SCIMITAR, MetalBarType.IRON, 1);
		putEquipment(ItemID.Cert.IRON_LONGSWORD, MetalBarType.IRON, 1);
		putEquipment(ItemID.Cert.IRON_FULL_HELM, MetalBarType.IRON, 1);
		putEquipment(ItemID.Cert.IRON_SQ_SHIELD, MetalBarType.IRON, 1);
		putEquipment(ItemID.Cert.IRON_CLAWS, MetalBarType.IRON, 1);
		putEquipment(ItemID.Cert.IRON_WARHAMMER, MetalBarType.IRON, 2);
		putEquipment(ItemID.Cert.IRON_BATTLEAXE, MetalBarType.IRON, 2);
		putEquipment(ItemID.Cert.IRON_CHAINBODY, MetalBarType.IRON, 2);
		putEquipment(ItemID.Cert.IRON_KITESHIELD, MetalBarType.IRON, 2);
		putEquipment(ItemID.Cert.IRON_2H_SWORD, MetalBarType.IRON, 2);
		putEquipment(ItemID.Cert.IRON_PLATELEGS, MetalBarType.IRON, 2);
		putEquipment(ItemID.Cert.IRON_PLATESKIRT, MetalBarType.IRON, 2);
		putEquipment(ItemID.Cert.IRON_PLATEBODY, MetalBarType.IRON, 4);

		putEquipment(ItemID.STEEL_SCIMITAR, MetalBarType.STEEL, 1);
		putEquipment(ItemID.STEEL_LONGSWORD, MetalBarType.STEEL, 1);
		putEquipment(ItemID.STEEL_FULL_HELM, MetalBarType.STEEL, 1);
		putEquipment(ItemID.STEEL_SQ_SHIELD, MetalBarType.STEEL, 1);
		putEquipment(ItemID.STEEL_CLAWS, MetalBarType.STEEL, 1);
		putEquipment(ItemID.STEEL_WARHAMMER, MetalBarType.STEEL, 2);
		putEquipment(ItemID.STEEL_BATTLEAXE, MetalBarType.STEEL, 2);
		putEquipment(ItemID.STEEL_CHAINBODY, MetalBarType.STEEL, 2);
		putEquipment(ItemID.STEEL_KITESHIELD, MetalBarType.STEEL, 2);
		putEquipment(ItemID.STEEL_2H_SWORD, MetalBarType.STEEL, 2);
		putEquipment(ItemID.STEEL_PLATELEGS, MetalBarType.STEEL, 2);
		putEquipment(ItemID.STEEL_PLATESKIRT, MetalBarType.STEEL, 2);
		putEquipment(ItemID.STEEL_PLATEBODY, MetalBarType.STEEL, 4);
		putEquipment(ItemID.Cert.STEEL_SCIMITAR, MetalBarType.STEEL, 1);
		putEquipment(ItemID.Cert.STEEL_LONGSWORD, MetalBarType.STEEL, 1);
		putEquipment(ItemID.Cert.STEEL_FULL_HELM, MetalBarType.STEEL, 1);
		putEquipment(ItemID.Cert.STEEL_SQ_SHIELD, MetalBarType.STEEL, 1);
		putEquipment(ItemID.Cert.STEEL_CLAWS, MetalBarType.STEEL, 1);
		putEquipment(ItemID.Cert.STEEL_WARHAMMER, MetalBarType.STEEL, 2);
		putEquipment(ItemID.Cert.STEEL_BATTLEAXE, MetalBarType.STEEL, 2);
		putEquipment(ItemID.Cert.STEEL_CHAINBODY, MetalBarType.STEEL, 2);
		putEquipment(ItemID.Cert.STEEL_KITESHIELD, MetalBarType.STEEL, 2);
		putEquipment(ItemID.Cert.STEEL_2H_SWORD, MetalBarType.STEEL, 2);
		putEquipment(ItemID.Cert.STEEL_PLATELEGS, MetalBarType.STEEL, 2);
		putEquipment(ItemID.Cert.STEEL_PLATESKIRT, MetalBarType.STEEL, 2);
		putEquipment(ItemID.Cert.STEEL_PLATEBODY, MetalBarType.STEEL, 4);

		putEquipment(ItemID.MITHRIL_SCIMITAR, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.MITHRIL_LONGSWORD, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.MITHRIL_FULL_HELM, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.MITHRIL_SQ_SHIELD, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.MITHRIL_CLAWS, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.MITHRIL_WARHAMMER, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.MITHRIL_BATTLEAXE, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.MITHRIL_CHAINBODY, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.MITHRIL_KITESHIELD, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.MITHRIL_2H_SWORD, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.MITHRIL_PLATELEGS, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.MITHRIL_PLATESKIRT, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.MITHRIL_PLATEBODY, MetalBarType.MITHRIL, 4);
		putEquipment(ItemID.Cert.MITHRIL_SCIMITAR, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.Cert.MITHRIL_LONGSWORD, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.Cert.MITHRIL_FULL_HELM, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.Cert.MITHRIL_SQ_SHIELD, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.Cert.MITHRIL_CLAWS, MetalBarType.MITHRIL, 1);
		putEquipment(ItemID.Cert.MITHRIL_WARHAMMER, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.Cert.MITHRIL_BATTLEAXE, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.Cert.MITHRIL_CHAINBODY, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.Cert.MITHRIL_KITESHIELD, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.Cert.MITHRIL_2H_SWORD, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.Cert.MITHRIL_PLATELEGS, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.Cert.MITHRIL_PLATESKIRT, MetalBarType.MITHRIL, 2);
		putEquipment(ItemID.Cert.MITHRIL_PLATEBODY, MetalBarType.MITHRIL, 4);

		putEquipment(ItemID.ADAMANT_SCIMITAR, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.ADAMANT_LONGSWORD, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.ADAMANT_FULL_HELM, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.ADAMANT_SQ_SHIELD, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.ADAMANT_CLAWS, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.ADAMNT_WARHAMMER, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.ADAMANT_BATTLEAXE, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.ADAMANT_CHAINBODY, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.ADAMANT_KITESHIELD, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.ADAMANT_2H_SWORD, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.ADAMANT_PLATELEGS, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.ADAMANT_PLATESKIRT, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.ADAMANT_PLATEBODY, MetalBarType.ADAMANT, 4);
		putEquipment(ItemID.Cert.ADAMANT_SCIMITAR, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.Cert.ADAMANT_LONGSWORD, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.Cert.ADAMANT_FULL_HELM, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.Cert.ADAMANT_SQ_SHIELD, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.Cert.ADAMANT_CLAWS, MetalBarType.ADAMANT, 1);
		putEquipment(ItemID.Cert.ADAMNT_WARHAMMER, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.Cert.ADAMANT_BATTLEAXE, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.Cert.ADAMANT_CHAINBODY, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.Cert.ADAMANT_KITESHIELD, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.Cert.ADAMANT_2H_SWORD, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.Cert.ADAMANT_PLATELEGS, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.Cert.ADAMANT_PLATESKIRT, MetalBarType.ADAMANT, 2);
		putEquipment(ItemID.Cert.ADAMANT_PLATEBODY, MetalBarType.ADAMANT, 4);

		putEquipment(ItemID.RUNE_SCIMITAR, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.RUNE_LONGSWORD, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.RUNE_FULL_HELM, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.RUNE_SQ_SHIELD, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.RUNE_CLAWS, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.RUNE_WARHAMMER, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.RUNE_BATTLEAXE, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.RUNE_CHAINBODY, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.RUNE_KITESHIELD, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.RUNE_2H_SWORD, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.RUNE_PLATELEGS, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.RUNE_PLATESKIRT, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.RUNE_PLATEBODY, MetalBarType.RUNITE, 4);
		putEquipment(ItemID.Cert.RUNE_SCIMITAR, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.Cert.RUNE_LONGSWORD, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.Cert.RUNE_FULL_HELM, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.Cert.RUNE_SQ_SHIELD, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.Cert.RUNE_CLAWS, MetalBarType.RUNITE, 1);
		putEquipment(ItemID.Cert.RUNE_WARHAMMER, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.Cert.RUNE_BATTLEAXE, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.Cert.RUNE_CHAINBODY, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.Cert.RUNE_KITESHIELD, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.Cert.RUNE_2H_SWORD, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.Cert.RUNE_PLATELEGS, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.Cert.RUNE_PLATESKIRT, MetalBarType.RUNITE, 2);
		putEquipment(ItemID.Cert.RUNE_PLATEBODY, MetalBarType.RUNITE, 4);
	}

	@Value
	public static class Record
	{
		MetalBarType type;
		MetalBarSource source;
		int value;
	}

	public static Record get(int id)
	{
		return values.get(id);
	}

	private static void putOre(int id, MetalBarType type)
	{
		values.put(id, new Record(type, MetalBarSource.ORE, 1));
	}

	private static void putBar(int id, MetalBarType type)
	{
		values.put(id, new Record(type, MetalBarSource.BAR, 1));
	}

	private static void putEquipment(int id, MetalBarType type, int value)
	{
		values.put(id, new Record(type, MetalBarSource.EQUIPMENT, value));
	}

	private MetalBarValues()
	{
	}
}
