package com.toofifty.easygiantsfoundry;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.client.ui.ColorScheme;

import javax.inject.Singleton;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

@Slf4j
@Singleton
public final class EasyGiantsFoundryHelper
{
	public static Color getHeatColor(int actions, int heat)
	{
		if (heat >= actions)
		{
			return ColorScheme.PROGRESS_COMPLETE_COLOR;
		}

		if (heat > 0)
		{
			return ColorScheme.PROGRESS_INPROGRESS_COLOR;
		}

		return ColorScheme.PROGRESS_ERROR_COLOR;
	}

	public static void renderTextLocation(Graphics2D graphics, Point point, String text, Color fg, Color bg, boolean outline)
	{
		if (bg != null)
		{
			FontMetrics fm = graphics.getFontMetrics();
			graphics.setColor(bg);
			graphics.fillRect(point.getX(), point.getY() - fm.getHeight(), fm.stringWidth(text), fm.getHeight());
		}

		graphics.setColor(Color.BLACK);
		if (outline)
		{
			graphics.drawString(text, point.getX(), point.getY() + 1);
			graphics.drawString(text, point.getX(), point.getY() - 1);
			graphics.drawString(text, point.getX() + 1, point.getY());
			graphics.drawString(text, point.getX() - 1, point.getY());
		}
		else
		{
			graphics.drawString(text, point.getX() + 1, point.getY() + 1);
		}

		graphics.setColor(fg);
		graphics.drawString(text, point.getX(), point.getY());
	}
}
