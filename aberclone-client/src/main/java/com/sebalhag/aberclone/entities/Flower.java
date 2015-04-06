package com.sebalhag.aberclone.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.sebalhag.aberclone.util.ObjectId;

public class Flower extends GameObject{

	public Flower(int x, int y, ObjectId id, double SCALE,
			BufferedImage image) {
		super(x, y, id, SCALE, image);
		rect = new Rectangle((int)x,(int)(y-4*SCALE),(int)(4*SCALE),(int)(4*SCALE));
	}
	
	public void render(Graphics g){
		g.drawImage(image,(int)(x - (image.getWidth()*SCALE*4)/2), (int)(y - image.getHeight()*SCALE*4),(int)(image.getWidth()*SCALE*4 ),(int)( image.getHeight()*SCALE*4), null);
		/*Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.RED);
		g2d.draw(rect);*/
	}

	public void tick(LinkedList<GameObject> object) {
		
	}

	public int getY() {
		return y;
	}

	public ObjectId getId() {
		return id;
	}
}
