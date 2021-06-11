package AllInUsServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ALLINUSDB.DBConnection;

public class ChatServer{
	static boolean check = false;
	private static String ip = "127.0.0.1";
    private static int port = 5252;
   
    private static ServerSocket server;
    private static Socket client = null;
    
    
    
    public ChatServer() throws IOException {
    	//this.server = new ServerSocket(port,0,InetAddress.getByName(ip)); // on definit le server
    	
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		//server = new DatagramSocket(port,InetAddress.getByName(ip));
    	server = new ServerSocket(port,0,InetAddress.getByName(ip));
		System.out.println("Server is running ...");
		
    	while(true) {
    		System.out.println("waiting for client connection");
			client = server.accept();
			System.out.println("New Client present IP "+client.getInetAddress()+" Port "+client.getPort());
			DataInputStream dis = new DataInputStream(client.getInputStream()); 
	        DataOutputStream dos = new DataOutputStream(client.getOutputStream()); 
			
			ClientHandler clienth = new ClientHandler(dis,dos,client);
		
			clienth.getListUser().add(clienth);
			
			//System.out.println("new client");
			new Thread(clienth).start();}
		}
	
    //[Duval Bul,2002-03-12,00458545253,test@email.com,my@pass]~sign

}
