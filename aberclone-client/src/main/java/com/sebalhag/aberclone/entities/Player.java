package com.sebalhag.aberclone.entities;

import com.sebalhag.aberclone.game.Game;
import com.sebalhag.aberclone.gfx.Animation;
import com.sebalhag.aberclone.gfx.Text;

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
import java.util.Timer;
import java.util.TimerTask;

import com.sebalhag.aberclone.util.ObjectId;
import com.sebalhag.aberclone.util.Replace;
import com.sebalhag.aberclone.util.Sprite;

public class Player{
	private int x;
	private int y;
	private int xV;
	private int yV;
	private double SCALE;
	private int pSpeed;
	private String name;
	private String say = "";
	private DataOutputStream out;
	private int lastKey;
	private boolean collide = false;
	private boolean rightArm = false;
	
	private BufferedImage[] punchingUpR = new BufferedImage[5];
	private Animation punchUpR;
	private BufferedImage[] punchingDownR = new BufferedImage[4];
	private Animation punchDownR;
	private BufferedImage[] punchingLeftR = new BufferedImage[5];
	private Animation punchLeftR;
	private BufferedImage[] punchingRightR = new BufferedImage[5];
	private Animation punchRightR;
	
	private BufferedImage[] punchingUpL = new BufferedImage[5];
	private Animation punchUpL;
	private BufferedImage[] punchingDownL = new BufferedImage[4];
	private Animation punchDownL;
	
	private Animation armAnimationR;
	private Animation armAnimationL;
	
	private BufferedImage playerUp = Sprite.getSprite(4,0,8,16);
	private BufferedImage playerDown = Sprite.getSprite(3,0,8,16);
	private BufferedImage playerLeft = Sprite.getSprite(5,0,8,16);
	private BufferedImage playerRight;
	
	private BufferedImage imageUpper;
	private BufferedImage[] walkingLeft = {Sprite.getSprite(12,1,8,8),Sprite.getSprite(12,2,8,8),Sprite.getSprite(12,3,8,8),Sprite.getSprite(12,4,8,8),Sprite.getSprite(13,1,9,8),Sprite.getSprite(13,2,9,8),Sprite.getSprite(13,3,9,8),Sprite.getSprite(13,4,9,8),Sprite.getSprite(13,5,8,8),Sprite.getSprite(13,6,8,8)};
	private Animation walkLeft;
	private BufferedImage[] walkingRight = new BufferedImage[walkingLeft.length];
	private Animation walkRight;
	private BufferedImage[] walkingDown = new BufferedImage[17];
	private Animation walkDown;
	private Animation legAnimation;
	
	private Replace rep = new Replace();
	
	public Player(int x, int y, double SCALE, String name, DataOutputStream out, String playerColor){
		this.x = x;
		this.y = y;
		this.SCALE = SCALE;
		this.name =  name;
		this.out = out;
		
		pSpeed = (int) (2*SCALE);
		
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
		
		walkRight = new Animation(walkingRight, 2, false);
		for(int i = 0; i < 17; i++){
			walkingDown[i] = Sprite.getSprite(7, i, 8, 8);
		}
		walkingDown = rep.replaceArray(-16237978, pants, walkingDown);
		
		walkDown = new Animation(walkingDown, 3, false);
		walkLeft = new Animation(walkingLeft, 2, false);
		
		
		for(int i = 0; i < 5; i++){
			punchingUpR[i] = Sprite.getSprite(4, i+2, 8, 8);
		}
		for(int i = 0; i < 4; i++){
			punchingDownR[i] = Sprite.getSprite(3, i+2, 8, 8);
		}
		for(int i = 0; i < 5; i++){
			punchingLeftR[i] = Sprite.getSprite(5.125, i+2, 8, 8);
		}
		
		punchDownR = new Animation(punchingDownR, 5, true);
		punchUpR = new Animation(punchingUpR, 3, true);
		punchLeftR = new Animation(punchingLeftR, 4, true);
		
		for(int i = 0; i<punchingLeftR.length; i++){
			BufferedImage temp = punchingLeftR[i];
			tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-temp.getWidth(null)-1, 0);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			punchingRightR[i] = op.filter(temp, null);
		}
		
		punchRightR = new Animation(punchingRightR, 4, true);
		
		
		for(int i = 0; i<punchingDownR.length; i++){
			BufferedImage temp = punchingDownR[i];
			tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-temp.getWidth(null)-1, 0);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			punchingDownL[i] = op.filter(temp, null);
		}
		
		for(int i = 0; i<punchingUpR.length; i++){
			BufferedImage temp = punchingUpR[i];
			tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-temp.getWidth(null)-1, 0);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			punchingUpL[i] = op.filter(temp, null);
		}
		
		punchDownL = new Animation(punchingDownL, 5, true);
		punchUpL = new Animation(punchingUpL, 3, true);
		
		legAnimation = walkDown;
		imageUpper = playerDown;
		armAnimationR = punchDownR;
		armAnimationL = punchDownL;
		
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
	
	private void updatePos(){
		try {
			out.writeUTF("u;"+name+";"+(int)(x/SCALE)+";"+(int)(y/SCALE));
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
		legAnimation.update();
		armAnimationR.update();
		armAnimationL.update();
		if(x<0){x=0;}
		if(y<0+73*SCALE){y=(int)(0+73*SCALE);}
		if(y>Game.WORLDHEIGHT){y = Game.WORLDHEIGHT;}
		if(x>Game.WORLDWIDTH){x = Game.WORLDWIDTH;}
	}
	public void render(Graphics g){
		g.drawImage(imageUpper,(int)(x - (imageUpper.getWidth()*SCALE*4)/2), (int)((y - imageUpper.getHeight()*SCALE*4)-4*SCALE),(int)((imageUpper.getWidth()*SCALE*4)),(int)( imageUpper.getHeight()*SCALE*4), null);
		g.drawImage(legAnimation.getSprite(),(int)(x - (legAnimation.getSprite().getWidth()*SCALE*4)/2),(int)((y - legAnimation.getSprite().getHeight()*SCALE*4)+8*SCALE),(int)(legAnimation.getSprite().getWidth()*SCALE*4 ),(int)( legAnimation.getSprite().getHeight()*SCALE*4), null);
		g.drawImage(armAnimationR.getSprite(),(int)(x - (armAnimationR.getSprite().getWidth()*SCALE*4)/2),(int)((y - imageUpper.getHeight()*SCALE*4)+(armAnimationR.getSprite().getHeight()*SCALE*4 - 12*SCALE)),(int)(armAnimationR.getSprite().getWidth()*SCALE*4 ),(int)( armAnimationR.getSprite().getHeight()*SCALE*4), null);
		//g.drawImage(armAnimationL.getSprite(),(int)(x - (armAnimationL.getSprite().getWidth()*SCALE*4)/2),(int)((y - imageUpper.getHeight()*SCALE*4)+(armAnimationL.getSprite().getHeight()*SCALE*4 - 12*SCALE)),(int)(armAnimationL.getSprite().getWidth()*SCALE*4 ),(int)( armAnimationL.getSprite().getHeight()*SCALE*4), null);
		/*Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.RED);
		g2d.draw(getBounds());*/
		g.setFont( new Font("", Font.PLAIN, 12));
		
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
	
	public Rectangle getBounds(){
		return new Rectangle((int)(x-(imageUpper.getWidth()*SCALE*4)/2+8*SCALE),(int) (y-4*SCALE),(int)(20*SCALE),(int)(4*SCALE));
	}
	public void Collide(Controller c){
		for(int i = 0; i < c.object.size(); i++)
		{
			GameObject tempObject = c.object.get(i);
			if(tempObject.getId() == ObjectId.Tree || tempObject.getId() == ObjectId.Flower || tempObject.getId() == ObjectId.OtherPlayer){
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
		
		if(keyCode == KeyEvent.VK_F){
			if(!rightArm){
				armAnimationR.start();
				rightArm = true;
			}
		}
		
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W){
			yV = -pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
				if (xV == 0){
					imageUpper = playerUp;
					legAnimation.stop();
					legAnimation = walkDown;
					legAnimation.start();
					
					armAnimationR = punchUpR;
					if(rightArm){armAnimationR.start();}else{armAnimationR.reset();}
				}
			}
		}
		if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S){
			yV = pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
				if (xV == 0 ){
					imageUpper = playerDown;
					legAnimation.stop();
					legAnimation = walkDown;
					legAnimation.start();
					
					armAnimationR = punchDownR;
					if(rightArm){armAnimationR.start();}else{armAnimationR.reset();}
				}
			}
		}        
		if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A){
			xV = -pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
			
				legAnimation.stop();
				legAnimation = walkLeft;
				legAnimation.start();
				imageUpper = playerLeft;
				
				armAnimationR = punchLeftR;
				if(rightArm){armAnimationR.start();}else{armAnimationR.reset();}
			}
		}
		if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D){
			xV = pSpeed;
			if(lastKey != keyCode){
				updateVel();
				collide = false;
			
				legAnimation.stop();
				legAnimation = walkRight;
				legAnimation.start();
				imageUpper = playerRight;
				
				armAnimationR = punchRightR;
				if(rightArm){armAnimationR.start();}else{armAnimationR.reset();}
			}
		}
		lastKey = keyCode;
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		if(keyCode == KeyEvent.VK_F){
			rightArm = false;
			armAnimationR.revert();
		}
		
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W && yV == -pSpeed){
			yV = 0;
			updateVel();
			lastKey = 00;
			if(xV == pSpeed){
				imageUpper = playerRight;
				armAnimationR = punchRightR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}
			else if(xV == -pSpeed){
				imageUpper = playerLeft;
				armAnimationR = punchLeftR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}else {legAnimation.reset();}
		}
		if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S && yV == pSpeed){
			yV =  0;
			updateVel();
			lastKey = 00;
			if(xV == pSpeed){
				imageUpper = playerRight;
				armAnimationR = punchRightR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}
			else if(xV == -pSpeed){
				imageUpper = playerLeft;
				armAnimationR = punchLeftR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}else {legAnimation.reset();}
		}        
		if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A && xV == -pSpeed){
			xV = 0;
			updateVel();
			lastKey = 00;
			legAnimation.reset();
			if(yV == pSpeed){
				imageUpper = playerDown;
				armAnimationR = punchDownR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}
			else if(yV == -pSpeed){
				imageUpper = playerUp;
				armAnimationR = punchUpR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}
			if(yV != 0){legAnimation = walkDown;legAnimation.start();}
		}
		if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D && xV == pSpeed){
			xV = 0;
			updateVel();
			lastKey = 00;
			legAnimation.reset();
			if(yV == pSpeed){
				imageUpper = playerDown;
				armAnimationR = punchDownR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}
			else if(yV == -pSpeed){
				imageUpper = playerUp;
				armAnimationR = punchUpR;
				if(!rightArm){armAnimationR.reset();}else{armAnimationR.start();}
			}
			if(yV != 0){legAnimation = walkDown;legAnimation.start();}
		}
		if(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S){
			if(yV == 0 && xV == 0){
				snap();
				updatePos();
			}
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	public void say(String say){
		this.say += say+"\n";
	}
}