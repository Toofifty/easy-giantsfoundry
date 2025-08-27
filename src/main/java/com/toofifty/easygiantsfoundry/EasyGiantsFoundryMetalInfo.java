package com.toofifty.easygiantsfoundry;

import com.toofifty.easygiantsfoundry.enums.Location;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;

class EasyGiantsFoundryMetalInfo extends OverlayPanel
{
	private final Rectangle giantsFoundryArea = new Rectangle(3354, 11478, 24, 25);

	private final Client client;
	private final EasyGiantsFoundryConfig config;

	private final HashMap<Integer, Integer> bronzeBarValues = new HashMap<>();
	private final HashMap<Integer, Integer> ironBarValues = new HashMap<>();
	private final HashMap<Integer, Integer> steelBarValues = new HashMap<>();
	private final HashMap<Integer, Integer> mithrilBarValues = new HashMap<>();
	private final HashMap<Integer, Integer> adamBarValues = new HashMap<>();
	private final HashMap<Integer, Integer> runeBarValues = new HashMap<>();
	protected int bronzeBars;
	protected int copperOre;
	protected int tinOre;
	protected int ironBars;
	protected int steelBars;
	protected int mithrilBars;
	protected int adamantiteBars;
	protected int runiteBars;

	protected ItemContainer bank;
	protected ItemContainer inventory;

	@Inject
	private EasyGiantsFoundryMetalInfo(final Client client, final EasyGiantsFoundryPlugin plugin, final EasyGiantsFoundryConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.BOTTOM_RIGHT);
		this.client = client;
		this.config = config;
		
		InitializeEquipmentValues();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if ((config.location() == Location.GIANTS_FOUNDRY && !TileIsInFoundry(client.getLocalPlayer().getWorldLocation()))
				|| config.location() == Location.NOWHERE)
			return null;

        if (bank == null)
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Open bank")
                    .leftColor(Color.RED)
                    .build());

		if (bronzeBars > 0 || config.showZeroValues())
            AddMetalBarText("Bronze bars:", bronzeBars);
		if (ironBars > 0 || config.showZeroValues())
            AddMetalBarText("Iron bars:", ironBars);
		if (steelBars > 0 || config.showZeroValues())
            AddMetalBarText("Steel bars:", steelBars);
		if (mithrilBars > 0 || config.showZeroValues())
            AddMetalBarText("Mithril bars:", mithrilBars);
		if (adamantiteBars > 0 || config.showZeroValues())
            AddMetalBarText("Adamantite bars:", adamantiteBars);
		if (runiteBars > 0 || config.showZeroValues())
            AddMetalBarText("Runite bars:", runiteBars);

		return super.render(graphics);
	}

	private void AddMetalBarText(String text, int number) {
		panelComponent.getChildren().add(LineComponent.builder()
				.left(text)
				.right(Integer.toString(number))
				.build());
	}

	public void CountBars()
	{
		bronzeBars = 0;
		ironBars = 0;
		steelBars = 0;
		mithrilBars = 0;
		adamantiteBars = 0;
		runiteBars = 0;
		copperOre = 0;
		tinOre = 0;

		if (inventory != null) {
			AddMetalValues(inventory);
		}
		if (bank != null) {
			AddMetalValues(bank);
		}

		if (config.includeOre())
			bronzeBars += Math.min(copperOre, tinOre);
	}

	private void AddMetalValues(ItemContainer container) {
		for (Item item : container.getItems()) {
			if (item.getId() >= 0 && item.getQuantity() > 0) {
				if (config.includeEquipment()) {
					if (bronzeBarValues.containsKey(item.getId()))
						bronzeBars += bronzeBarValues.get(item.getId()) * item.getQuantity();
					if (ironBarValues.containsKey(item.getId()))
						ironBars += ironBarValues.get(item.getId()) * item.getQuantity();
					if (steelBarValues.containsKey(item.getId()))
						steelBars += steelBarValues.get(item.getId()) * item.getQuantity();
					if (mithrilBarValues.containsKey(item.getId()))
						mithrilBars += mithrilBarValues.get(item.getId()) * item.getQuantity();
					if (adamBarValues.containsKey(item.getId()))
						adamantiteBars += adamBarValues.get(item.getId()) * item.getQuantity();
					if (runeBarValues.containsKey(item.getId()))
						runiteBars += runeBarValues.get(item.getId()) * item.getQuantity();
				}

				if (config.includeMetalBars()) {
					if (item.getId() == ItemID.BRONZE_BAR || item.getId() == ItemID.Cert.BRONZE_BAR)
						bronzeBars += item.getQuantity();
					if (item.getId() == ItemID.IRON_BAR || item.getId() == ItemID.Cert.IRON_BAR)
						ironBars += item.getQuantity();
					if (item.getId() == ItemID.STEEL_BAR || item.getId() == ItemID.Cert.STEEL_BAR)
						steelBars += item.getQuantity();
					if (item.getId() == ItemID.MITHRIL_BAR || item.getId() == ItemID.Cert.MITHRIL_BAR)
						mithrilBars += item.getQuantity();
					if (item.getId() == ItemID.ADAMANTITE_BAR || item.getId() == ItemID.Cert.ADAMANTITE_BAR)
						adamantiteBars += item.getQuantity();
					if (item.getId() == ItemID.RUNITE_BAR || item.getId() == ItemID.Cert.RUNITE_BAR)
						runiteBars += item.getQuantity();
				}

				if (config.includeOre()) {
					if (item.getId() == ItemID.COPPER_ORE || item.getId() == ItemID.Cert.COPPER_ORE)
						copperOre +=  item.getQuantity();
					if (item.getId() == ItemID.TIN_ORE || item.getId() == ItemID.Cert.TIN_ORE)
						tinOre +=  item.getQuantity();
					if (item.getId() == ItemID.IRON_ORE || item.getId() == ItemID.Cert.IRON_ORE) {
						ironBars += item.getQuantity();
						steelBars += item.getQuantity();
					}
					if (item.getId() == ItemID.MITHRIL_ORE || item.getId() == ItemID.Cert.MITHRIL_ORE)
						mithrilBars += item.getQuantity();
					if (item.getId() == ItemID.ADAMANTITE_ORE || item.getId() == ItemID.Cert.ADAMANTITE_ORE)
						adamantiteBars += item.getQuantity();
					if (item.getId() == ItemID.RUNITE_ORE || item.getId() == ItemID.Cert.RUNITE_ORE)
						runiteBars += item.getQuantity();
				}
			}
		}
	}

	private boolean TileIsInFoundry(WorldPoint tile) {
		return giantsFoundryArea.contains(new Point(tile.getX(), tile.getY()));
	}

	private void InitializeEquipmentValues() {
		bronzeBarValues.put(ItemID.BRONZE_SCIMITAR, 1);
		bronzeBarValues.put(ItemID.BRONZE_LONGSWORD, 1);
		bronzeBarValues.put(ItemID.BRONZE_FULL_HELM, 1);
		bronzeBarValues.put(ItemID.BRONZE_SQ_SHIELD, 1);
		bronzeBarValues.put(ItemID.BRONZE_CLAWS, 1);
		bronzeBarValues.put(ItemID.BRONZE_WARHAMMER, 2);
		bronzeBarValues.put(ItemID.BRONZE_BATTLEAXE, 2);
		bronzeBarValues.put(ItemID.BRONZE_CHAINBODY, 2);
		bronzeBarValues.put(ItemID.BRONZE_KITESHIELD, 2);
		bronzeBarValues.put(ItemID.BRONZE_2H_SWORD, 2);
		bronzeBarValues.put(ItemID.BRONZE_PLATELEGS, 2);
		bronzeBarValues.put(ItemID.BRONZE_PLATESKIRT, 2);
		bronzeBarValues.put(ItemID.BRONZE_PLATEBODY, 4);
		bronzeBarValues.put(ItemID.Cert.BRONZE_SCIMITAR, 1);
		bronzeBarValues.put(ItemID.Cert.BRONZE_LONGSWORD, 1);
		bronzeBarValues.put(ItemID.Cert.BRONZE_FULL_HELM, 1);
		bronzeBarValues.put(ItemID.Cert.BRONZE_SQ_SHIELD, 1);
		bronzeBarValues.put(ItemID.Cert.BRONZE_CLAWS, 1);
		bronzeBarValues.put(ItemID.Cert.BRONZE_WARHAMMER, 2);
		bronzeBarValues.put(ItemID.Cert.BRONZE_BATTLEAXE, 2);
		bronzeBarValues.put(ItemID.Cert.BRONZE_CHAINBODY, 2);
		bronzeBarValues.put(ItemID.Cert.BRONZE_KITESHIELD, 2);
		bronzeBarValues.put(ItemID.Cert.BRONZE_2H_SWORD, 2);
		bronzeBarValues.put(ItemID.Cert.BRONZE_PLATELEGS, 2);
		bronzeBarValues.put(ItemID.Cert.BRONZE_PLATESKIRT, 2);
		bronzeBarValues.put(ItemID.Cert.BRONZE_PLATEBODY, 4);

		ironBarValues.put(ItemID.IRON_SCIMITAR, 1);
		ironBarValues.put(ItemID.IRON_LONGSWORD, 1);
		ironBarValues.put(ItemID.IRON_FULL_HELM, 1);
		ironBarValues.put(ItemID.IRON_SQ_SHIELD, 1);
		ironBarValues.put(ItemID.IRON_CLAWS, 1);
		ironBarValues.put(ItemID.IRON_WARHAMMER, 2);
		ironBarValues.put(ItemID.IRON_BATTLEAXE, 2);
		ironBarValues.put(ItemID.IRON_CHAINBODY, 2);
		ironBarValues.put(ItemID.IRON_KITESHIELD, 2);
		ironBarValues.put(ItemID.IRON_2H_SWORD, 2);
		ironBarValues.put(ItemID.IRON_PLATELEGS, 2);
		ironBarValues.put(ItemID.IRON_PLATESKIRT, 2);
		ironBarValues.put(ItemID.IRON_PLATEBODY, 4);
		ironBarValues.put(ItemID.Cert.IRON_SCIMITAR, 1);
		ironBarValues.put(ItemID.Cert.IRON_LONGSWORD, 1);
		ironBarValues.put(ItemID.Cert.IRON_FULL_HELM, 1);
		ironBarValues.put(ItemID.Cert.IRON_SQ_SHIELD, 1);
		ironBarValues.put(ItemID.Cert.IRON_CLAWS, 1);
		ironBarValues.put(ItemID.Cert.IRON_WARHAMMER, 2);
		ironBarValues.put(ItemID.Cert.IRON_BATTLEAXE, 2);
		ironBarValues.put(ItemID.Cert.IRON_CHAINBODY, 2);
		ironBarValues.put(ItemID.Cert.IRON_KITESHIELD, 2);
		ironBarValues.put(ItemID.Cert.IRON_2H_SWORD, 2);
		ironBarValues.put(ItemID.Cert.IRON_PLATELEGS, 2);
		ironBarValues.put(ItemID.Cert.IRON_PLATESKIRT, 2);
		ironBarValues.put(ItemID.Cert.IRON_PLATEBODY, 4);

		steelBarValues.put(ItemID.STEEL_SCIMITAR, 1);
		steelBarValues.put(ItemID.STEEL_LONGSWORD, 1);
		steelBarValues.put(ItemID.STEEL_FULL_HELM, 1);
		steelBarValues.put(ItemID.STEEL_SQ_SHIELD, 1);
		steelBarValues.put(ItemID.STEEL_CLAWS, 1);
		steelBarValues.put(ItemID.STEEL_WARHAMMER, 2);
		steelBarValues.put(ItemID.STEEL_BATTLEAXE, 2);
		steelBarValues.put(ItemID.STEEL_CHAINBODY, 2);
		steelBarValues.put(ItemID.STEEL_KITESHIELD, 2);
		steelBarValues.put(ItemID.STEEL_2H_SWORD, 2);
		steelBarValues.put(ItemID.STEEL_PLATELEGS, 2);
		steelBarValues.put(ItemID.STEEL_PLATESKIRT, 2);
		steelBarValues.put(ItemID.STEEL_PLATEBODY, 4);
		steelBarValues.put(ItemID.Cert.STEEL_SCIMITAR, 1);
		steelBarValues.put(ItemID.Cert.STEEL_LONGSWORD, 1);
		steelBarValues.put(ItemID.Cert.STEEL_FULL_HELM, 1);
		steelBarValues.put(ItemID.Cert.STEEL_SQ_SHIELD, 1);
		steelBarValues.put(ItemID.Cert.STEEL_CLAWS, 1);
		steelBarValues.put(ItemID.Cert.STEEL_WARHAMMER, 2);
		steelBarValues.put(ItemID.Cert.STEEL_BATTLEAXE, 2);
		steelBarValues.put(ItemID.Cert.STEEL_CHAINBODY, 2);
		steelBarValues.put(ItemID.Cert.STEEL_KITESHIELD, 2);
		steelBarValues.put(ItemID.Cert.STEEL_2H_SWORD, 2);
		steelBarValues.put(ItemID.Cert.STEEL_PLATELEGS, 2);
		steelBarValues.put(ItemID.Cert.STEEL_PLATESKIRT, 2);
		steelBarValues.put(ItemID.Cert.STEEL_PLATEBODY, 4);

		mithrilBarValues.put(ItemID.MITHRIL_SCIMITAR, 1);
		mithrilBarValues.put(ItemID.MITHRIL_LONGSWORD, 1);
		mithrilBarValues.put(ItemID.MITHRIL_FULL_HELM, 1);
		mithrilBarValues.put(ItemID.MITHRIL_SQ_SHIELD, 1);
		mithrilBarValues.put(ItemID.MITHRIL_CLAWS, 1);
		mithrilBarValues.put(ItemID.MITHRIL_WARHAMMER, 2);
		mithrilBarValues.put(ItemID.MITHRIL_BATTLEAXE, 2);
		mithrilBarValues.put(ItemID.MITHRIL_CHAINBODY, 2);
		mithrilBarValues.put(ItemID.MITHRIL_KITESHIELD, 2);
		mithrilBarValues.put(ItemID.MITHRIL_2H_SWORD, 2);
		mithrilBarValues.put(ItemID.MITHRIL_PLATELEGS, 2);
		mithrilBarValues.put(ItemID.MITHRIL_PLATESKIRT, 2);
		mithrilBarValues.put(ItemID.MITHRIL_PLATEBODY, 4);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_SCIMITAR, 1);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_LONGSWORD, 1);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_FULL_HELM, 1);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_SQ_SHIELD, 1);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_CLAWS, 1);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_WARHAMMER, 2);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_BATTLEAXE, 2);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_CHAINBODY, 2);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_KITESHIELD, 2);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_2H_SWORD, 2);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_PLATELEGS, 2);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_PLATESKIRT, 2);
		mithrilBarValues.put(ItemID.Cert.MITHRIL_PLATEBODY, 4);

		adamBarValues.put(ItemID.ADAMANT_SCIMITAR, 1);
		adamBarValues.put(ItemID.ADAMANT_LONGSWORD, 1);
		adamBarValues.put(ItemID.ADAMANT_FULL_HELM, 1);
		adamBarValues.put(ItemID.ADAMANT_SQ_SHIELD, 1);
		adamBarValues.put(ItemID.ADAMANT_CLAWS, 1);
		adamBarValues.put(ItemID.ADAMNT_WARHAMMER, 2);
		adamBarValues.put(ItemID.ADAMANT_BATTLEAXE, 2);
		adamBarValues.put(ItemID.ADAMANT_CHAINBODY, 2);
		adamBarValues.put(ItemID.ADAMANT_KITESHIELD, 2);
		adamBarValues.put(ItemID.ADAMANT_2H_SWORD, 2);
		adamBarValues.put(ItemID.ADAMANT_PLATELEGS, 2);
		adamBarValues.put(ItemID.ADAMANT_PLATESKIRT, 2);
		adamBarValues.put(ItemID.ADAMANT_PLATEBODY, 4);
		adamBarValues.put(ItemID.Cert.ADAMANT_SCIMITAR, 1);
		adamBarValues.put(ItemID.Cert.ADAMANT_LONGSWORD, 1);
		adamBarValues.put(ItemID.Cert.ADAMANT_FULL_HELM, 1);
		adamBarValues.put(ItemID.Cert.ADAMANT_SQ_SHIELD, 1);
		adamBarValues.put(ItemID.Cert.ADAMANT_CLAWS, 1);
		adamBarValues.put(ItemID.Cert.ADAMNT_WARHAMMER, 2);
		adamBarValues.put(ItemID.Cert.ADAMANT_BATTLEAXE, 2);
		adamBarValues.put(ItemID.Cert.ADAMANT_CHAINBODY, 2);
		adamBarValues.put(ItemID.Cert.ADAMANT_KITESHIELD, 2);
		adamBarValues.put(ItemID.Cert.ADAMANT_2H_SWORD, 2);
		adamBarValues.put(ItemID.Cert.ADAMANT_PLATELEGS, 2);
		adamBarValues.put(ItemID.Cert.ADAMANT_PLATESKIRT, 2);
		adamBarValues.put(ItemID.Cert.ADAMANT_PLATEBODY, 4);

		runeBarValues.put(ItemID.RUNE_SCIMITAR, 1);
		runeBarValues.put(ItemID.RUNE_LONGSWORD, 1);
		runeBarValues.put(ItemID.RUNE_FULL_HELM, 1);
		runeBarValues.put(ItemID.RUNE_SQ_SHIELD, 1);
		runeBarValues.put(ItemID.RUNE_CLAWS, 1);
		runeBarValues.put(ItemID.RUNE_WARHAMMER, 2);
		runeBarValues.put(ItemID.RUNE_BATTLEAXE, 2);
		runeBarValues.put(ItemID.RUNE_CHAINBODY, 2);
		runeBarValues.put(ItemID.RUNE_KITESHIELD, 2);
		runeBarValues.put(ItemID.RUNE_2H_SWORD, 2);
		runeBarValues.put(ItemID.RUNE_PLATELEGS, 2);
		runeBarValues.put(ItemID.RUNE_PLATESKIRT, 2);
		runeBarValues.put(ItemID.RUNE_PLATEBODY, 4);
		runeBarValues.put(ItemID.Cert.RUNE_SCIMITAR, 1);
		runeBarValues.put(ItemID.Cert.RUNE_LONGSWORD, 1);
		runeBarValues.put(ItemID.Cert.RUNE_FULL_HELM, 1);
		runeBarValues.put(ItemID.Cert.RUNE_SQ_SHIELD, 1);
		runeBarValues.put(ItemID.Cert.RUNE_CLAWS, 1);
		runeBarValues.put(ItemID.Cert.RUNE_WARHAMMER, 2);
		runeBarValues.put(ItemID.Cert.RUNE_BATTLEAXE, 2);
		runeBarValues.put(ItemID.Cert.RUNE_CHAINBODY, 2);
		runeBarValues.put(ItemID.Cert.RUNE_KITESHIELD, 2);
		runeBarValues.put(ItemID.Cert.RUNE_2H_SWORD, 2);
		runeBarValues.put(ItemID.Cert.RUNE_PLATELEGS, 2);
		runeBarValues.put(ItemID.Cert.RUNE_PLATESKIRT, 2);
		runeBarValues.put(ItemID.Cert.RUNE_PLATEBODY, 4);
	}
}
