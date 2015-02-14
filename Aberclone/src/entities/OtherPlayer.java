package entities;

import gfx.Animation;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import util.ObjectId;
import util.Sprite;

public class OtherPlayer extends GameObject{
	private String name;
	private boolean stopped = true;
	private BufferedImage playerUp = Sprite.getSprite(4,0,8,16);
	private BufferedImage playerDown;
	private BufferedImage playerLeft = Sprite.getSprite(5,0,8,16);
	private BufferedImage playerRight;
	
	private BufferedImage[] walkingLeft = {Sprite.getSprite(12,1,8,8),Sprite.getSprite(12,2,8,8),Sprite.getSprite(12,3,8,8),Sprite.getSprite(12,4,8,8),Sprite.getSprite(13,1,9,8),Sprite.getSprite(13,2,9,8),Sprite.getSprite(13,3,9,8),Sprite.getSprite(13,4,9,8),Sprite.getSprite(13,5,8,8),Sprite.getSprite(13,6,8,8)};
	private Animation animationLeft = new Animation(walkingLeft, 2);
	private BufferedImage[] walkingRight = new BufferedImage[walkingLeft.length];
	private Animation animationRight;
	private BufferedImage[] walkingDown = new BufferedImage[17];
	private Animation animationDown;
	private Animation currentAnimation;
	
	public OtherPlayer(double x, double y, ObjectId id, double SCALE,
			BufferedImage image, String name) {
		super(x, y, id, SCALE, image);
		this.name = name;
		this.playerDown = image;
		
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

	public void tick(LinkedList<GameObject> object) {
		x += xV;
		y += yV;
		if(xV > 0 && yV == 0){
			image = playerRight;
			if(currentAnimation != animationRight || stopped){
				stopped = false;
				currentAnimation.stop();
				currentAnimation = animationRight;
				currentAnimation.start();
			}
		}
		if(xV < 0 && yV == 0){
			image = playerLeft;
			if(currentAnimation != animationLeft || stopped){
				stopped = false;
				currentAnimation.stop();
				currentAnimation = animationLeft;
				currentAnimation.start();
			}
		}
		if(yV > 0){
			image = playerDown;
			
			if(currentAnimation != animationDown || stopped){
				stopped = false;
				currentAnimation.stop();
				currentAnimation = animationDown;
				currentAnimation.start();
			}
		}
		if(yV < 0){
			image = playerUp;
			if(currentAnimation != animationDown || stopped){
				stopped = false;
				currentAnimation.stop();
				currentAnimation = animationDown;
				currentAnimation.start();
			}
		}
		if(xV == 0 && yV == 0){
			currentAnimation.reset();
			currentAnimation.stop();
			stopped = true;
		}
		currentAnimation.update();
	}

	public void render(Graphics g) {
		g.drawImage(image,(int)(x - (image.getWidth()*SCALE*4)/2), (int)((y - image.getHeight()*SCALE*4)-4*SCALE),(int)((image.getWidth()*SCALE*4)),(int)( image.getHeight()*SCALE*4), null);
		g.drawImage(currentAnimation.getSprite(),(int)(x - (currentAnimation.getSprite().getWidth()*SCALE*4)/2),(int)((y - currentAnimation.getSprite().getHeight()*SCALE*4)+8*SCALE),(int)(currentAnimation.getSprite().getWidth()*SCALE*4 ),(int)( currentAnimation.getSprite().getHeight()*SCALE*4), null);
		
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

	public double getY() {
		return y;
	}

	public ObjectId getId() {
		return id;
	}
	public String getName(){
		return name;
	}
	public void setxV(int xV){
		this.xV = xV;
	}
	public void setyV(int yV){
		this.yV = yV;
	}
}