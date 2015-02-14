package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import util.ObjectId;

public abstract class GameObject {
	
	protected double x,y;
	protected ObjectId id;
	protected double SCALE;
	protected BufferedImage image;
	public Rectangle rect;
	public String name;
	public int xV;
	public int yV;
	
	public GameObject(double x, double y, ObjectId id, double SCALE, BufferedImage image){
		this.x = x;
		this.y = y;
		this.id = id;
		this.SCALE = SCALE;
		this.image = image;
	}
	
	public abstract void tick(LinkedList<GameObject> object);
	public abstract void render(Graphics g);
	
	public abstract double getY();
	public abstract ObjectId getId();
	public String getName(){
		return name;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
	public void setxV(int xV){
		this.xV = xV;
	}
	public void setyV(int yV){
		this.yV = yV;
	}
}
