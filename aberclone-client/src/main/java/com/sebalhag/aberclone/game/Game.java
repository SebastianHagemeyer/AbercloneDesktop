package com.sebalhag.aberclone.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sebalhag.aberclone.util.ObjectId;
import com.sebalhag.aberclone.util.Sprite;
import com.sebalhag.aberclone.entities.Controller;
import com.sebalhag.aberclone.entities.Flower;
import com.sebalhag.aberclone.entities.GameObject;
import com.sebalhag.aberclone.entities.OtherPlayer;
import com.sebalhag.aberclone.entities.Player;
import com.sebalhag.aberclone.entities.Tree;
import com.sebalhag.aberclone.gfx.Text;

public class Game extends Canvas implements Runnable{
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	private static final long serialVersionUID = 1L;
	private static final double SCALE = 1;
	private static final int WIDTH = 630;
	private static final int HEIGHT = 470;
	public static final int WORLDWIDTH = (int) (2000 * SCALE);
	public static final int WORLDHEIGHT = (int) (2000 * SCALE);
	
	private static final String NAME = "Aber-clone 4";
	private JFrame frame;
	
	private boolean running = false;
	private int seed = 2634786;
	
	private BufferedImage f1;
	private BufferedImage f2;
	private BufferedImage f3;
	private BufferedImage f4;
	private BufferedImage fImage;
	private BufferedImage tImage;
	
	private Controller c;
	private Player p;
	private String pName;
	private int maxHealth = 8;
	private int gridHeight = 12;
	private int health = 8;
	private Color barColor = Color.white;
	//private Color barColor = new Color(255, 255, 255, 111);
	private Camera cam;
	
	private boolean chat = false;
	private String say = "";
	
	public Game(){
		Dimension dimension = new Dimension((int)(WIDTH * SCALE),(int)(HEIGHT * SCALE));
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		
		frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		new Thread(this).start();
		running = true;
	}
	
	public synchronized void stop() {
		running = false;
	}
	
	
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000 / 60;
		int ticks = 0;
		int frames = 0;
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true;
			
			while(delta >= 1){
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(shouldRender){
				frames ++;
				render();
			}
			if(System.currentTimeMillis() - lastTimer > 1000){
				lastTimer += 1000;
				System.out.println("Ticks "+ticks + ", Frames "+ frames);
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick(){
		c.tick();
		cam.tick(p);
		p.Collide(c);
	}
		
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}
	
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		g.clearRect(0, 0, getWidth(), getHeight());	
		
		g.setColor(new Color(0x1D4D13));
		g.fillRect(0,0,getWidth(),getHeight());
		
		g2d.translate(cam.getX(),cam.getY());
		c.render(g);
		g2d.translate(-cam.getX(),-cam.getY());
		
		g.setColor(new Color(0x404040));
		g.fillRect(getWidth()-13,(int)(getHeight()-(gridHeight*maxHealth)),12,(int)(gridHeight*maxHealth));
		g.setColor(new Color(0xFF4040));
		g.fillRect(getWidth()-13,(int)(getHeight()-(gridHeight*health)),12,(int)(gridHeight*health));
		g.setColor(barColor);
		for(int i = 0; i < gridHeight*maxHealth; i += gridHeight){
			g.drawLine(getWidth()-13, getHeight()-i, getWidth(), getHeight()-i);
		}
		g.setColor(Color.white);
		g.drawRect(getWidth()-13,(int)(getHeight()-(gridHeight*maxHealth)),12,(int)(gridHeight*maxHealth)-1);
		
		g.setFont( new Font("", Font.PLAIN, 12));
		int x = 10;
		int y = getHeight()-8;
		
		String stats = "Attack: 1(1)       Defense: 1       Gold: 0";
		Text.renderText(g, stats, x, y, Color.white);

		if(chat){
			y = 20;
			Text.renderText(g, "Say: "+say, x, y, Color.white);
		}
		g.dispose();
		bs.show();
	}
	
	public void init(){
		//String s = JOptionPane.showInputDialog ("Enter world seed.","aberclone"); 
		
		this.requestFocus();
		addKeyListener(new KeyInput(this));//add key adapter to the games canvas
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	try {
					out.writeUTF("l;"+pName);
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});

		String ip =  JOptionPane.showInputDialog ("IP address below.","localhost"); 
		//String ip =  JOptionPane.showInputDialog ("IP address below.","120.149.28.54"); 
	
		//connects
		try{
			socket = new Socket(ip, 2021);
		} catch (IOException e){
			try{
				socket = new Socket(ip, 2042);
			} catch (IOException e1){
				JOptionPane.showMessageDialog(frame,ip+" was not reachable!","Error",JOptionPane.ERROR_MESSAGE);
			}
		}
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		pName = JOptionPane.showInputDialog ("Please enter your name below.","Player");
		String color =  JOptionPane.showInputDialog ("Enter player color below.","33CC33"); 

		//loads in sprites
		f1 = Sprite.getSprite(0, 0, 8, 16);
		f2 = Sprite.getSprite(1, 0, 8, 8);
		f3 = Sprite.getSprite(1, 1, 8, 8);
		f4 = Sprite.getSprite(2, 0, 8, 8);
		tImage = Sprite.getSprite(0, 15, 25, 35);
		
		Random rand = new Random(seed);
		
		//creates instances
		p = new Player(WORLDWIDTH/2,WORLDHEIGHT/2,SCALE, pName, out, color);
		cam = new Camera((int)(WIDTH * SCALE),(int)(HEIGHT * SCALE),WORLDWIDTH,WORLDHEIGHT);
		c = new Controller(p, cam, (int)(HEIGHT * SCALE),(int)(WIDTH * SCALE));
		
		try {
			out.writeUTF(pName+";"+color);// on init tell server what name is
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    int flowerNum = rand.nextInt(10) + 30;
	    for(int i = 0; i < flowerNum; i++){
	    	//random coordinates
	    	int randomX = rand.nextInt((WORLDWIDTH) + 1);
	    	int randomY = rand.nextInt((WORLDHEIGHT) + 1);
	    	randomX = Math.round((randomX/4))*4;
	    	randomY = Math.round((randomY/4))*4;

	    	//gets random flower image
	    	int randomImg = rand.nextInt(4);
	    	switch(randomImg){
	    	case 0: fImage = f1; break;
	    	case 1: fImage = f2; break;
	    	case 2: fImage = f3; break;
	    	case 3: fImage = f4; break;
	    	}
	    	c.addObject(new Flower(randomX,randomY,ObjectId.Flower,SCALE,fImage));
	    }
	    int treeNum = rand.nextInt(10) + 40;
	    for(int i = 0; i < treeNum; i++){
	    	//random coordinates
	    	int randomX = rand.nextInt((WORLDWIDTH));
	    	int randomY = rand.nextInt((WORLDHEIGHT));
	    	randomX = Math.round((randomX/4))*4;
	    	randomY = Math.round((randomY/4))*4;
	   
	    	c.addObject(new Tree(randomX,randomY,ObjectId.Tree,SCALE,tImage));
	    }
	    c.addObject(new Tree(0,100,ObjectId.Tree,SCALE,tImage));
	    
	    Client input = new Client(in, this);
		Thread thread = new Thread(input);
		thread.start();
	}
	
	public static void main(String[] args){
		//First line that get called on launch
		Game game = new Game();//makes new instance of game
		game.init();
		game.start();
	}
	public void keyReleased(KeyEvent e){
		//when game class recives call from key adapter
		
		if(e.getKeyCode() == 10){
			if(chat){
				chat = false;
				if(say.length()>0){
					p.say(say);
					try {
						out.writeUTF("s;"+pName+";"+say);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					say = "";
				}
			}else{
				chat = true;
			}
		}
		if(!chat){
			p.keyReleased(e);
		}else{
			if(e.getKeyCode() == 8 && say.length() > 0){
				say = say.substring(0, say.length() - 1);
			}
			say += e.getKeyChar();
			say = say.replaceAll("[^\\w!?@#$%^&*()=+-.,:;' ]","");
			
		}
	}
	public void keyPressed(KeyEvent e) {
		if(!chat){
			p.keyPressed(e);
		}
	}
	public void recive(String e){
		//when game class recives input from client listener
		String[] parts = e.split(";");//store string data in array
		
		if(parts[0].equals("c")){ //create player
			c.addObject(new OtherPlayer(1000,1000,ObjectId.OtherPlayer,SCALE,Sprite.getSprite(3, 0, 8, 16), parts[1],parts[2]));

			try {
				out.writeUTF("u;"+pName+";"+p.getX()/SCALE+";"+p.getY()/SCALE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(parts[0].equals("u")){ //update pos
			int x = (int) (Float.parseFloat(parts[2])*SCALE);
			int y = (int) (Float.parseFloat(parts[3])*SCALE);
			for(int i = 0; i < c.object.size(); i++){
				GameObject tempObject = c.object.get(i);
				if(tempObject.getId() == ObjectId.OtherPlayer){
					if(parts[1].equals(tempObject.getName())){
						tempObject.setX(x);
						tempObject.setY(y);
					}
				}
			}
		}
		if(parts[0].equals("v")){ // update velocity
			int xV = (int) (Float.parseFloat(parts[2])*SCALE);
			int yV = (int) (Float.parseFloat(parts[3])*SCALE);
			for(int i = 0; i < c.object.size(); i++){
				GameObject tempObject = c.object.get(i);
				if(tempObject.getId() == ObjectId.OtherPlayer){
					if(parts[1].equals(tempObject.getName())){
						tempObject.setxV(xV);
						tempObject.setyV(yV);
					}
				}
			}
		}
		if(parts[0].equals("l")){ // player leave
			for(int i = 0; i < c.object.size(); i++){
				GameObject tempObject = c.object.get(i);
				if(tempObject.getId() == ObjectId.OtherPlayer){
					if(parts[1].equals(tempObject.getName())){
						c.removeObject(tempObject);
					}
				}
			}
		}
		if(parts[0].equals("s")){ // player leave
			for(int i = 0; i < c.object.size(); i++){
				GameObject tempObject = c.object.get(i);
				if(tempObject.getId() == ObjectId.OtherPlayer){
					if(parts[1].equals(tempObject.getName())){
						tempObject.say(parts[2]);
					}
				}
			}
		}
	}
}