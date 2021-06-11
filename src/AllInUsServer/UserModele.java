package AllInUsServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class UserModele {
	private String username;
	private Socket s;
	private DataOutputStream dos;
	private DataInputStream dis;
	private String phone;
	private int userID;
	private String user;
	private String status;
	public UserModele(String username, Socket s, DataOutputStream dos, DataInputStream dis,String phone,int userID,String user,String status) {
		super();
		this.username = username;
		this.s = s;
		this.dos = dos;
		this.dis = dis;
		this.phone = phone;
		this.userID = userID;
		this.user = user;
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Socket getS() {
		return s;
	}

	public void setS(Socket s) {
		this.s = s;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public String getPhone() {
		return phone;
	}

	public int getUserID() {
		return userID;
	}

	public String getUser() {
		return user;
	}
	
	
	
}
