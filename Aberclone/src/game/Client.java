package game;

import java.io.DataInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Client implements Runnable {
	DataInputStream in;
	private Game g;
	public Client(DataInputStream in, Game g){
		 this.in = in;
		 this.g = g;
	}

	public void run(){
		String message;
		while(true){
			try {
				Thread.sleep(4);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try{
				message = in.readUTF();
				g.recive(message);
			} catch(IOException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Connection between you and host has been terminated.","Error",JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
	}
}