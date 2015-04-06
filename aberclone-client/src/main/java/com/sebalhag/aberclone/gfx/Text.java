package com.sebalhag.aberclone.gfx;

import java.awt.Color;
import java.awt.Graphics;

public class Text {
	public static void renderText(Graphics g, String line, int x, int y, Color color){
		g.setColor(Color.black);
		g.drawString(line,x-1,y-1);
		g.drawString(line,x-1,y+1);
		g.drawString(line,x+1,y-1);
		g.drawString(line,x+1,y+1);
		g.drawString(line,x-1,y);
		g.drawString(line,x+1,y);
		g.drawString(line,x,y-1);
		g.drawString(line,x,y+1);
		g.setColor(color);
		g.drawString(line,x,y);
	}
}
