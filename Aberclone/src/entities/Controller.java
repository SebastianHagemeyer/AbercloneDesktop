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
	private int WIDTH;
	
	public Controller(Player p, Camera c, int HEIGHT, int WIDTH){
		this.p = p;
		this.c = c;
		this.HEIGHT = HEIGHT;
		this.WIDTH = WIDTH;
	}
	
	public void tick(){
		for (int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			tempObject.tick(object);
		}
		p.tick();
	}
	
	public void render(Graphics g){
		int py = p.getY();
		int cy = -c.getY();
		for (int i = cy; i <= cy+HEIGHT+140; i++){
			for (int ii = 0; ii < object.size(); ii++){
				tempObject = object.get(ii);
				int y = tempObject.getY();
				
				if(y == i){
					if(tempObject.getX() >= -c.getX()){
						if(tempObject.getX() <= -c.getX()+WIDTH){
							tempObject.render(g);	
						}
					}
				}
			}
			
			if(py == i){
				p.render(g);
			}
		}
	}
	
	public void addObject (GameObject object){
		this.object.add(object);
	}
	public void removeObject (GameObject object){
		this.object.remove(object);
	}
}
