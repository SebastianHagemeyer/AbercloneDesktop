package entities;

import game.Game;
import gfx.Animation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;

import util.ObjectId;
import util.Sprite;

public class Player{
	private double x;
	private double y;
	private int xV = 0;
	private int yV = 0;
	private double SCALE;
	private int pSpeed;
	private String name;
	private DataOutputStream out;
	private int lastKey;
	private boolean collide = false;
	
	private BufferedImage playerUp = Sprite.getSprite(4,0,8,16);
	private BufferedImage playerDown = Sprite.getSprite(3,0,8,16);
	private BufferedImage playerLeft = Sprite.getSprite(5,0,8,16);
	private BufferedImage playerRight;
	
	private BufferedImage imageUpper = playerDown;
	private BufferedImage[] walkingLeft = {Sprite.getSprite(12,1,8,8),Sprite.getSprite(12,2,8,8),Sprite.getSprite(12,3,8,8),Sprite.getSprite(12,4,8,8),Sprite.getSprite(13,1,9,8),Sprite.getSprite(13,2,9,8),Sprite.getSprite(13,3,9,8),Sprite.getSprite(13,4,9,8),Sprite.getSprite(13,5,8,8),Sprite.getSprite(13,6,8,8)};
	private Animation animationLeft = new Animation(walkingLeft, 2);
	private BufferedImage[] walkingRight = new BufferedImage[walkingLeft.length];
	private Animation animationRight;
	private BufferedImage[] walkingDown = new BufferedImage[17];
	private Animation animationDown;
	private Animation currentAnimation;
	
	
	
	public Player(double x, double y, double SCALE, String name, DataOutputStream out){
		this.x = x;
		this.y = y;
		this.SCALE = SCALE;
		this.name =  name;
		this.out = out;
		
		pSpeed = (int) (2*SCALE);
		
		AffineTransform tx;
		AffineTransformOp op;
		tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-playerLeft.getWidth(null)-1, 0);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		playerRight = op.filter(playerLeft, null);
		
		for(int i = 0; i<walkingLeft.length; i++){
			BufferedImage temp = walkingLeft[i];
			tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-temp.getWidth(null)-1, 0);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			walkingRight[i] = op.filter(temp, null);
		}
		
		animationRight = new Animation(walkingRight, 2);
		for(int i = 0; i < 17; i++){
			walkingDown[i] = Sprite.getSprite(7, i, 8, 8);
		}
		animationDown = new Animation(walkingDown, 3);
		currentAnimation = animationDown;

	}
	private void updatePos(){
		try {
			out.writeUTF("u;"+name+";"+x/SCALE+";"+y/SCALE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	private void updateVel(){
		try {
			out.writeUTF("v;"+name+";"+xV/SCALE+";"+yV/SCALE);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void tick(){
		x += xV;
		y += yV;
		currentAnimation.update();
		if(x<0){x=0;}
		if(y<0+73*SCALE){y=0+73*SCALE;}
		if(y>Game.WORLDHEIGHT){y = Game.WORLDHEIGHT;}
		if(x>Game.WORLDWIDTH){x = Game.WORLDWIDTH;}
	}
	public void render(Graphics g){
		g.drawImage(imageUpper,(int)(x - (imageUpper.getWidth()*SCALE*4)/2), (int)((y - imageUpper.getHeight()*SCALE*4)-4*SCALE),(int)((imageUpper.getWidth()*SCALE*4)),(int)( imageUpper.getHeight()*SCALE*4), null);
		g.drawImage(currentAnimation.getSprite(),(int)(x - (currentAnimation.getSprite().getWidth()*SCALE*4)/2),(int)((y - currentAnimation.getSprite().getHeight()*SCALE*4)+8*SCALE),(int)(currentAnimation.getSprite().getWidth()*SCALE*4 ),(int)( currentAnimation.getSprite().getHeight()*SCALE*4), null);
		/*Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.RED);
		g2d.draw(getBounds());*/
		g.setFont( new Font("", Font.PLAIN, 12));
		FontMetrics fm = g.getFontMetrics();
		
		int totalWidth = (fm.stringWidth(name))/2;
		
		int x = (int)(this.x - totalWidth + 2 * SCALE);
		int y = (int)(this.y - 76*SCALE - 4 * SCALE);
		
		g.setColor(Color.black);
		g.drawString(name,  x- 1,y-1);
		g.drawString(name,  x- 1, y+1);
		g.drawString(name, x+ 1, y-1);
		g.drawString(name, x+ 1, y+1);
		g.setColor(Color.white);
		g.drawString(name, x, y);
	}
	
	public Rectangle getBounds(){
		return new Rectangle((int)(x-(imageUpper.getWidth()*SCALE*4)/2+8*SCALE),(int) (y-4*SCALE),(int)(20*SCALE),(int)(4*SCALE));
	}
	public void Collide(Controller c){
		for(int i = 0; i < c.object.size(); i++)
		{
			GameObject tempObject = c.object.get(i);
			if(tempObject.getId() == ObjectId.Tree || tempObject.getId() == ObjectId.Flower){
				if(getBounds().intersects(tempObject.rect)){
					sendCollide();
					if(y >= tempObject.y){
						y = tempObject.y+4;
					}
					else{
						y = tempObject.y-4;
					}
				}
			}
		}
	}
	private void sendCollide() {
		if(collide != true){
			collide = true;
			xV = 0;
			yV = 0;
			updateVel();
		}
	}
	private void snap(){
		x = Math.round((x/4))*4;
    	y = Math.round((y/4))*4;
	}
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W){
			yV = -pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
			}
			if (xV == 0 ){
				imageUpper = playerUp;
				currentAnimation.stop();
				currentAnimation = animationDown;
				currentAnimation.start();
			}
		}
		if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S){
			yV = pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
			}
			if (xV == 0 ){
				imageUpper = playerDown;
				currentAnimation.stop();
				currentAnimation = animationDown;
				currentAnimation.start();
			}
		}        
		if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A){
			xV = -pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
			}
			currentAnimation.stop();
			currentAnimation = animationLeft;
			currentAnimation.start();
			imageUpper = playerLeft;
		}
		if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D){
			xV = pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
			}
			currentAnimation.stop();
			currentAnimation = animationRight;
			currentAnimation.start();
			imageUpper = playerRight;
		}
		lastKey = keyCode;
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W && yV == -pSpeed){
			yV = 0;
			updateVel();
			lastKey = 00;
			if(xV == pSpeed){
				imageUpper = playerRight;
			}
			else if(xV == -pSpeed){
				imageUpper = playerLeft;
			}else {currentAnimation.stop();
			currentAnimation.reset();}
		}
		if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S && yV == pSpeed){
			yV =  0;
			updateVel();
			lastKey = 00;
			if(xV == pSpeed){
				imageUpper = playerRight;
			}
			else if(xV == -pSpeed){
				imageUpper = playerLeft;
			}else {currentAnimation.stop();
			currentAnimation.reset();}
		}        
		if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A && xV == -pSpeed){
			xV = 0;
			updateVel();
			lastKey = 00;
			currentAnimation.reset();
			if(yV == pSpeed){
				imageUpper = playerDown;
			}
			else if(yV == -pSpeed){
				imageUpper = playerUp;
			}
			if(yV != 0){currentAnimation = animationDown;currentAnimation.start();}
		}
		if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D && xV == pSpeed){
			xV = 0;
			updateVel();
			lastKey = 00;
			currentAnimation.stop();
			currentAnimation.reset();
			if(yV == pSpeed){
				imageUpper = playerDown;
			}
			else if(yV == -pSpeed){
				imageUpper = playerUp;
			}
			if(yV != 0){currentAnimation = animationDown;currentAnimation.start();}
		}
		if(yV == 0 && xV == 0){
			snap();
			updatePos();
		}
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
}