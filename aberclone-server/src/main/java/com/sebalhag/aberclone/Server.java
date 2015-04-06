package com.sebalhag.aberclone;

import java.io.*;
import java.net.*;

public class Server{

	static ServerSocket serverSocket;
	static Socket socket;
	static DataOutputStream out;
	static DataInputStream in;
	static Users[] user = new Users[10];
	
	public static void main(String[] args) throws Exception{
		InetAddress ip;
		try {
		ip = InetAddress.getLocalHost();
		System.out.println("Current IP address : " + ip.getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		serverSocket = new ServerSocket(2042);
		System.out.println("Server started, on port 2021");
		while(true){
			socket = serverSocket.accept();
			System.out.println("Connection from: " + socket.getInetAddress());
			for(int i =0;i<10;i++){
				out = new DataOutputStream(socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
				if(user[i] == null){
					user[i] = new Users(out,in,user,i);
					Thread thread = new Thread(user[i]);
					thread.start();
					break;
				}
			}
		}
	}
	public static void destroyUser(Integer self){
		user[self] = null;
	}
}

class Users implements Runnable {
	DataOutputStream out;
	DataInputStream in;
	Users[] user = new Users[10];
	String name;
	long threadId;
	Integer self;
	String[] parts;
	
	public Users(DataOutputStream out, DataInputStream in, Users[] user,Integer i){
		this.out = out;
		this.in = in;
		this.user = user;
		this.self = i;
	}
	@SuppressWarnings("deprecation")
	public void run(){
		threadId = Thread.currentThread().getId();
		try{
			name = in.readUTF();//get all join data
			parts = name.split(";");//store string data in array
			name = parts[0];//name
			System.out.println("player name retrived: " + name + " and Color recived: "+parts[1]);
		} catch(IOException e1) {
			e1.printStackTrace();
		}
		
		try{
			for(int i=0;i<10;i++){
				if(user[i] != null){
					if(user[i].self != this.self){
						user[i].out.writeUTF("c;"+name+";"+parts[1]); // send create with player name and color(parts[1])
					}
				}
			}
			for(int i=0;i<10;i++){
				if(user[i] != null){
					if(user[i].self == this.self){
						for(int ii=0;ii<10;ii++){
							if(user[ii] != null){
								if(user[ii].self != this.self){
									user[i].out.writeUTF("c;" + user[ii].name+";"+user[ii].parts[1]);//tell user joining name and color of other players already joined
								}
							}
						}
					}
				}
			}
		} catch (IOException e2){
			e2.printStackTrace();		
		}
		
		while(true){
			try{
				String message = in.readUTF();
				System.out.println(message);
				for(int i=0;i<10;i++){
					if(user[i] != null){
						if(user[i].self != this.self){
							user[i].out.writeUTF(message);
						}
					}
				}
			} catch (IOException e2){
				System.out.println("l;"+name);
				Thread.currentThread().interrupt();
				if(Thread.currentThread().isInterrupted()){
					user[self] = null;
					try{
						for(int i=0;i<10;i++){
							if(user[i] != null){
								user[i].out.writeUTF("l;"+name);
							}
						}
					} catch (IOException e3){
						e2.printStackTrace();		
					}
					Server.destroyUser(self);
					out = null;
					in = null;
					user = null;
					name = null;
					self = null;
					Thread.currentThread().stop();
				}
			}
		}
	}
}