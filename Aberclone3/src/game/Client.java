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
			try{
				message = in.readUTF();
				//JOptionPane.showMessageDialog(null,message,"Message",JOptionPane.INFORMATION_MESSAGE);
				g.recive(message);
			} catch(IOException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,"Connection between you and host has been terminated.","Error",JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
	}
}