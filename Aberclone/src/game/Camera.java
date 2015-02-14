package game;

import entities.Player;

public class Camera {
	private int x = 0;
	private int y = 0;
	private int HEIGHT;
	private int WIDTH;
	private int WORLDHEIGHT;
	private int WORLDWIDTH;
	
	Camera(int WIDTH, int HEIGHT, int WORLDWIDTH, int WORLDHEIGHT){
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.WORLDWIDTH = WORLDWIDTH;
		this.WORLDHEIGHT = WORLDHEIGHT;
	}
	
	public void tick(Player player){

		x = (int) (-player.getX() + WIDTH/2 );
		y = (int) (-player.getY() + HEIGHT/2 );
		if (!(x < 0)){
			x = 0;
		}
		if (!(y < 0)){
			y = 0;
		}
		if(y - HEIGHT < -WORLDHEIGHT){
			y = -WORLDHEIGHT + HEIGHT;
		}
		if(x - WIDTH < -WORLDWIDTH){
			x = -WORLDWIDTH + WIDTH;
		}
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}
