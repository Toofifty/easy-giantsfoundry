package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.MetalBarSource;
import com.toofifty.easygiantsfoundry.enums.MetalBarType;
import lombok.Getter;
import lombok.Value;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class MetalBarCounter
{
	private final Map<Integer, Map<MetalBarType, CountsBySource>> index = new HashMap<>(); // container id -> bar counts

	@Inject
	private EasyGiantsFoundryConfig config;

	@Getter
	private boolean seenBank = false;

	public int get(MetalBarType type)
	{
		int count = 0;
		for (Map<MetalBarType, CountsBySource> counts : index.values())
		{
			count += counts.get(type).sum(config);
		}

		return count;
	}

	public void clear()
	{
		index.clear();
		seenBank = false;
	}

	public void put(ItemContainer container)
	{
		int tinOre = 0;
		int copperOre = 0;
		int ironOre = 0;

		if (container.getId() == InventoryID.BANK)
		{
			seenBank = true;
		}

		Map<MetalBarType, CountsBySource> counts = newCounts();
		for (Item item : container.getItems())
		{
			MetalBarValues.Record record = MetalBarValues.get(item.getId());
			if (record == null)
			{
				continue;
			}

			// ore special cases:
			// * add bronze bars equal to min(tin, copper)
			//   * there is an edge case here where it won't sum quite right multiple item containers but I don't really
			//     care to code for that right now
			// * iron ore counts for both iron and steel
			switch (item.getId())
			{
				case ItemID.TIN_ORE:
				case ItemID.Cert.TIN_ORE:
					tinOre += item.getQuantity();
					break;
				case ItemID.COPPER_ORE:
				case ItemID.Cert.COPPER_ORE:
					copperOre += item.getQuantity();
					break;
				case ItemID.IRON_ORE:
				case ItemID.Cert.IRON_ORE:
					ironOre += item.getQuantity();
					break;
				default:
					counts.compute(record.getType(), (k, v) ->
							v.add(record.getSource(), record.getValue() * item.getQuantity()));
			}
		}

		int finalTinOre = tinOre;
		int finalCopperOre = copperOre;
		int finalIronOre = ironOre;
		counts.compute(MetalBarType.BRONZE, (k, v) ->
				v.add(MetalBarSource.ORE, Math.min(finalTinOre, finalCopperOre)));
		counts.compute(MetalBarType.IRON, (k, v) ->
				v.add(MetalBarSource.ORE, finalIronOre));
		counts.compute(MetalBarType.STEEL, (k, v) ->
				v.add(MetalBarSource.ORE, finalIronOre));

		index.put(container.getId(), counts);
	}

	private static Map<MetalBarType, CountsBySource> newCounts()
	{
		Map<MetalBarType, CountsBySource> counts = new HashMap<>();
		counts.put(MetalBarType.BRONZE, CountsBySource.empty());
		counts.put(MetalBarType.IRON, CountsBySource.empty());
		counts.put(MetalBarType.STEEL, CountsBySource.empty());
		counts.put(MetalBarType.MITHRIL, CountsBySource.empty());
		counts.put(MetalBarType.ADAMANT, CountsBySource.empty());
		counts.put(MetalBarType.RUNITE, CountsBySource.empty());
		return counts;
	}

	@Value
	private static class CountsBySource
	{
		int ores, bars, equipment;

		public static CountsBySource empty()
		{
			return new CountsBySource(0, 0, 0);
		}

		public CountsBySource add(MetalBarSource source, int count)
		{
			switch (source)
			{
				case ORE:
					return new CountsBySource(ores + count, bars, equipment);
				case BAR:
					return new CountsBySource(ores, bars + count, equipment);
				case EQUIPMENT:
					return new CountsBySource(ores, bars, equipment + count);
				default:
					return this;
			}
		}

		public int sum(EasyGiantsFoundryConfig config)
		{
			int sum = config.countOre() ? ores : 0;
			sum += config.countBars() ? bars : 0;
			sum += config.countEquipment() ? equipment : 0;
			return sum;
		}
	}
}
