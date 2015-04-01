package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import util.ObjectId;

public abstract class GameObject {
	
	protected int x,y;
	protected ObjectId id;
	protected double SCALE;
	protected BufferedImage image;
	public Rectangle rect;
	public String name;
	public int xV;
	public int yV;
	public String say;
	
	public GameObject(int x, int y, ObjectId id, double SCALE, BufferedImage image){
		this.x = x;
		this.y = y;
		this.id = id;
		this.SCALE = SCALE;
		this.image = image;
	}
	
	public abstract void tick(LinkedList<GameObject> object);
	public abstract void render(Graphics g);
	
	public abstract int getY();
	public abstract ObjectId getId();
	
	public String getName(){
		return name;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	public void setxV(int xV){
		this.xV = xV;
	}
	public void setyV(int yV){
		this.yV = yV;
	}

	public void say(String say) {
		this.say = say;
	}

	public int getX() {
		return x;
	}
}
