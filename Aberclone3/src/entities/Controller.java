package entities;

import game.Camera;

import java.awt.Graphics;
import java.util.LinkedList;

public class Controller {
	public LinkedList<GameObject> object = new LinkedList<GameObject>();
	
	private GameObject tempObject;
	private Player p;
	private Camera c;
	private int HEIGHT;
	
	public Controller(Player p, Camera c, int HEIGHT){
		this.p = p;
		this.c = c;
		this.HEIGHT = HEIGHT;
	}
	
	public void tick(){
		for (int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			tempObject.tick(object);
		}
		p.tick();
	}
	
	public void render(Graphics g){
		double py = p.getY();
		double cy = c.getY();
		for (int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			double y = tempObject.getY();
			if(-y < cy-10){
				if(y <= py){
					tempObject.render(g);
				}
			}
		}
		
		p.render(g);
		
		for (int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			double y = tempObject.getY();
			if((cy-HEIGHT-100 < -y)){
				if(tempObject.getY() >= py+1){
					tempObject.render(g);
				}
			}
		}
	}
	
	public void addObject (GameObject object){
		this.object.add(object);
	}
}
