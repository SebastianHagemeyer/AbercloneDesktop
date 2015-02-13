package game;

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

import util.ObjectId;
import util.Sprite;
import entities.Controller;
import entities.Flower;
import entities.GameObject;
import entities.OtherPlayer;
import entities.Player;
import entities.Tree;

public class Game extends Canvas implements Runnable{
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	private static final long serialVersionUID = 1L;
	private static final double SCALE = 1;
	private static final int WIDTH = 630;
	private static final int HEIGHT = 470;
	private int WORLDWIDTH = (int) (2000 * SCALE);
	private int WORLDHEIGHT = (int) (2000 * SCALE);
	
	private static final String NAME = "Aber-clone Multiplayer";
	private JFrame frame;
	
	private boolean running = false;
	
	private BufferedImage f1;
	private BufferedImage f2;
	private BufferedImage f3;
	private BufferedImage f4;
	private BufferedImage fImage;
	private BufferedImage tImage;
	
	private Controller c;
	private Player p;
	private int maxHealth = 8;
	private int gridHeight = 12;
	private int health = 7;
	private Camera cam;
	
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
		double nsPerTick = 1000000000D / 60D;
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
		g.setColor(Color.white);
		g.drawRect(getWidth()-13,(int)(getHeight()-(gridHeight*maxHealth)),12,(int)(gridHeight*maxHealth)-1);
		
		for(int i = 0; i < gridHeight*maxHealth; i += gridHeight){
			g.drawLine(getWidth()-13, getHeight()-i, getWidth(), getHeight()-i);
		}
		
		
		g.setFont( new Font("", Font.PLAIN, 12));
		int x = 10;
		int y = getHeight()-8;
		String stats = "Attack: 1(1)       Defense: 1       Gold: 0";
		g.setColor(Color.black);
		g.drawString(stats,x- 1,y-1);
		g.drawString(stats,x- 1, y+1);
		g.drawString(stats,x+ 1, y-1);
		g.drawString(stats,x+ 1, y+1);
		g.setColor(Color.white);
		g.drawString(stats, x, y);
		
		g.dispose();
		bs.show();
	}
	
	public void init(){
		this.requestFocus();
		addKeyListener(new KeyInput(this));

		//String ip =  JOptionPane.showInputDialog ("IP address below.","120.149.28.54");
		String ip =  JOptionPane.showInputDialog ("IP address below.","localhost"); 
		//String ip =  JOptionPane.showInputDialog ("IP address below.","192.168.0.9");
		//String ip =  JOptionPane.showInputDialog ("IP address below.","10.128.211.25");
		try{
			socket = new Socket(ip, 7776);
		} catch (IOException e){
			JOptionPane.showMessageDialog(frame,ip+":7776 was not reachable!","Error",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Client input = new Client(in, this);
		Thread thread = new Thread(input);
		thread.start();
		
		String name = JOptionPane.showInputDialog ("Please enter your name below.","bob");

		f1 = Sprite.getSprite(0, 0, 8, 16);
		f2 = Sprite.getSprite(1, 0, 8, 8);
		f3 = Sprite.getSprite(1, 1, 8, 8);
		f4 = Sprite.getSprite(2, 0, 8, 8);
		tImage = Sprite.getSprite(0, 4, 25, 35);
		
		Random rand = new Random();
		p = new Player(WORLDWIDTH/2,WORLDHEIGHT/2,SCALE, name, out);
		cam = new Camera((int)(WIDTH * SCALE),(int)(HEIGHT * SCALE),WORLDWIDTH,WORLDHEIGHT);
		c = new Controller(p, cam, (int)(HEIGHT * SCALE));
		
		try {
			out.writeUTF(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	    int fNum = rand.nextInt(10) + 40;
	    for(int i = 0; i < fNum; i++){
	    	int randomX = rand.nextInt((WORLDWIDTH) + 1);
	    	int randomY = rand.nextInt((WORLDHEIGHT) + 1);
	    	randomX = Math.round((randomX/4))*4;
	    	randomY = Math.round((randomY/4))*4;

	    	int randomImg = rand.nextInt(4);
	    	
	    	switch(randomImg){
	    	case 0: fImage = f1; break;
	    	case 1: fImage = f2; break;
	    	case 2: fImage = f3; break;
	    	case 3: fImage = f4; break;
	    	}
	    	c.addObject(new Flower(randomX,randomY,ObjectId.Flower,SCALE,fImage));
	    	
	    }
	    int tNum = rand.nextInt(10) + 30;
	    for(int i = 0; i < tNum; i++){
	    	int randomX = rand.nextInt((WORLDWIDTH));
	    	int randomY = rand.nextInt((WORLDHEIGHT));
	    	randomX = Math.round((randomX/4))*4;
	    	randomY = Math.round((randomY/4))*4;
	   
	    	c.addObject(new Tree(randomX,randomY,ObjectId.Tree,SCALE,tImage));
	    }
	}
	
	public static void main(String[] args){
		Game game = new Game();
		game.init();
		game.start();
	}
	public void keyReleased(KeyEvent e) {
		p.keyReleased(e);
	}
	public void keyPressed(KeyEvent e) {
		p.keyPressed(e);
	}
	public void recive(String e){
		String[] parts = e.split(";");
		if(parts[0].equals("c")){
			c.addObject(new OtherPlayer(1000,1000,ObjectId.OtherPlayer,SCALE,Sprite.getSprite(3, 0, 8, 16), parts[1]));
		}
		if(parts[0].equals("u")){
			int x = (int) Float.parseFloat(parts[2]);
			int y = (int) Float.parseFloat(parts[3]);
			for(int i = 0; i < c.object.size(); i++)
			{
				GameObject tempObject = c.object.get(i);
				if(tempObject.getId() == ObjectId.OtherPlayer){
					if(parts[1].equals(tempObject.getName())){
						tempObject.setX(x);
						tempObject.setY(y);
					}
				}
			}
		}
		if(parts[0].equals("v")){
			int xV = (int) Float.parseFloat(parts[2]);
			int yV = (int) Float.parseFloat(parts[3]);
			for(int i = 0; i < c.object.size(); i++)
			{
				GameObject tempObject = c.object.get(i);
				if(tempObject.getId() == ObjectId.OtherPlayer){
					if(parts[1].equals(tempObject.getName())){
						tempObject.setxV(xV);
						tempObject.setyV(yV);
					}
				}
			}
		}
	}

}