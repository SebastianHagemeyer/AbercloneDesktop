package com.sebalhag.aberclone.entities;

import com.sebalhag.aberclone.gfx.Animation;
import com.sebalhag.aberclone.gfx.Text;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import com.sebalhag.aberclone.util.ObjectId;
import com.sebalhag.aberclone.util.Replace;
import com.sebalhag.aberclone.util.Sprite;

public class OtherPlayer extends GameObject{
	private String name;
	private boolean stopped = true;
	
	private BufferedImage playerUp = Sprite.getSprite(4,0,8,16);
	private BufferedImage playerDown = Sprite.getSprite(3,0,8,16);
	private BufferedImage playerLeft = Sprite.getSprite(5,0,8,16);
	private BufferedImage playerRight;
	
	private BufferedImage[] walkingLeft = {Sprite.getSprite(12,1,8,8),Sprite.getSprite(12,2,8,8),Sprite.getSprite(12,3,8,8),Sprite.getSprite(12,4,8,8),Sprite.getSprite(13,1,9,8),Sprite.getSprite(13,2,9,8),Sprite.getSprite(13,3,9,8),Sprite.getSprite(13,4,9,8),Sprite.getSprite(13,5,8,8),Sprite.getSprite(13,6,8,8)};
	private Animation animationLeft;
	private BufferedImage[] walkingRight = new BufferedImage[walkingLeft.length];
	private Animation animationRight;
	private BufferedImage[] walkingDown = new BufferedImage[17];
	private Animation animationDown;
	private Animation currentAnimation;
	
	private String say = "";
	
	private Replace rep = new Replace();
	
	public OtherPlayer(int x, int y, ObjectId id, double SCALE,
			BufferedImage image, String name, String playerColor) {
		super(x, y, id, SCALE, image);
		this.name = name;
		this.playerDown = image;
		
		rect = new Rectangle((int)(x-8 *SCALE),(int)(y-4*SCALE),(int)(16*SCALE),(int)(4*SCALE));
		
		int shirt = new Color(Integer.parseInt(playerColor, 16)).getRGB();
		int pants = (rep.brighten(new Color(Integer.parseInt(playerColor, 16)), -0.10f)).getRGB();
		
		walkingLeft = rep.replaceArray(-16237978, pants, walkingLeft);
		
		playerLeft = rep.replaceImage(-15969134, shirt, playerLeft);
		playerUp = rep.replaceImage(-15969134, shirt, playerUp);
		playerDown = rep.replaceImage(-15969134, shirt, playerDown);
		playerLeft = rep.replaceImage(-16237978, pants, playerLeft);		
		playerUp = rep.replaceImage(-16237978, pants, playerUp);
		playerDown = rep.replaceImage(-16237978, pants, playerDown);
		
		
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
		
		animationRight = new Animation(walkingRight, 2, false);
		for(int i = 0; i < 17; i++){
			walkingDown[i] = Sprite.getSprite(7, i, 8, 8);
		}
		walkingDown = rep.replaceArray(-16237978, pants, walkingDown);
		
		animationDown = new Animation(walkingDown, 3, false);
		animationLeft = new Animation(walkingLeft, 2, false);
		currentAnimation = animationDown;
		this.image = playerDown;
		
		
		Timer timer = new Timer();

		timer.schedule(myTask, 6000, 6000);
	}
	
	TimerTask myTask = new TimerTask() {
	    @Override
	    public void run() {
	    	if(say.length()>0){
	    		String lines[] = say.split("\n");
	    		say = say.substring((lines[0].length()+1), say.length());
	    	}
	    }
	};

	public void tick(LinkedList<GameObject> object) {
		x += xV;
		y += yV;
		if(xV > 0){
			image = playerRight;
			if(currentAnimation != animationRight || stopped){
				stopped = false;
				currentAnimation.stop();
				currentAnimation = animationRight;
				currentAnimation.start();
			}
		}
		if(xV < 0 ){
			image = playerLeft;
			if(currentAnimation != animationLeft || stopped){
				stopped = false;
				currentAnimation.stop();
				currentAnimation = animationLeft;
				currentAnimation.start();
			}
		}
		if(yV > 0 && xV == 0){
			image = playerDown;
			if(currentAnimation != animationDown || stopped){
				stopped = false;
				currentAnimation.stop();
				currentAnimation = animationDown;
				currentAnimation.start();
			}
		}
		if(yV < 0 && xV == 0){
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
		}else{
			rect = new Rectangle((int)(x-8 *SCALE),(int)(y-4*SCALE),(int)(16*SCALE),(int)(4*SCALE));
		}
		currentAnimation.update();
	}

	public void render(Graphics g) {
		g.drawImage(image,(int)(x - (image.getWidth()*SCALE*4)/2), (int)((y - image.getHeight()*SCALE*4)-4*SCALE),(int)((image.getWidth()*SCALE*4)),(int)( image.getHeight()*SCALE*4), null);
		g.drawImage(currentAnimation.getSprite(),(int)(x - (currentAnimation.getSprite().getWidth()*SCALE*4)/2),(int)((y - currentAnimation.getSprite().getHeight()*SCALE*4)+8*SCALE),(int)(currentAnimation.getSprite().getWidth()*SCALE*4 ),(int)( currentAnimation.getSprite().getHeight()*SCALE*4), null);
		/*Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.RED);
		g2d.draw(rect);*/
		
		FontMetrics fm = g.getFontMetrics();
		int totalWidth;
		int x;
		int y = (int)(this.y - 76*SCALE + 8 * SCALE);
		
		String lines[] = say.split("\n");
		
		if(say.length()>0){
			fm = g.getFontMetrics();
			y = (int)(this.y - 76*SCALE + 4 * SCALE);
			
			y -= fm.getHeight()*lines.length;
			for (String line : lines){
				
				totalWidth = (fm.stringWidth(line))/2;
				x = (int)(this.x - totalWidth + 2 * SCALE);
				y += fm.getHeight();
				
				Text.renderText(g, line, x, y, new Color(0xC5C3C5));
			}
		}
		
		totalWidth = (fm.stringWidth(name))/2;
		y = (int)(this.y - 76*SCALE + 4 * SCALE) - fm.getHeight()*lines.length;;
		x = (int)(this.x - totalWidth + 2 * SCALE);
		
		Text.renderText(g, name, x, y, Color.white);
	}

	public int getY() {
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
	public void say(String say){
		this.say += say+"\n";
	}
}