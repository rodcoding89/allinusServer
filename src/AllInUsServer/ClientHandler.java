package AllInUsServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import ALLINUSDB.DBConnection;

public class ClientHandler extends Thread{
	
	private static List<ClientHandler> listUser = new ArrayList();
	private static List<UserModele> activeUser = new ArrayList<>();;
	private static List<String> multiuse = new ArrayList();
	private static List<String> multiuse1 = new ArrayList();
	private static List<String> lfriendrequest = new ArrayList();
	private static List<String> lcountrequest = new ArrayList();
	
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket st;
	private String usernames = null;
	private String user;
	private String tel;
	private String inTel;
	private boolean islogged;
	private static DBConnection db;
	private String inemail;
	private String useremail;
	private String contactlist = "";
	private String data;
	private String phone;
	private int userID;
	private int checkUserID = 1;
	private String friendphone;
	private boolean verify;
	private int activeUserSize = 0;
	
	private static char [] coder = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
			'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'1','2','3','4','5','6','7','8','9','{','}','[',']','|','@','(',')'};
    
	
	public ClientHandler(DataInputStream dis, DataOutputStream dos, Socket st) {
		super();
		this.dis = dis;
		this.dos = dos;
		this.st = st;
		db = new DBConnection();
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//Thread clientThread = new Thread(() -> {
			String received = null;
	    while (true){ 
	    	try{ 
               // receive the string 
               //received = dis.readUTF();
	    		if(dis.available() > 0) {
	    			received = onReceivMessage(dis);
               
	               	if(received != null || !received.equals("")) {
	            	   if(received.contains("~")) {
		            	   String [] inMessage = received.split("~");
		                   
		                   
		                   System.out.println(inMessage[0]); 
		                     
		                   if(inMessage[1].equals("login")) {
		                	   if(checkLogin(inMessage[0])) {
		                		   Date date = new Date();
		                		   
		                		   if(updateInToSign(inemail,"on",date.toString())) {
		                			   //dos.writeUTF("broacast&datapart");
		                			   generedSession(usernames,st,dos,dis,tel,userID,user);
		                			   this.islogged = true;
		                		   }
		                		   
		                		   displayUser();
		                	   }else {
		                		   System.out.println("value returned is 'false'");
		                	   }
		                	   
		                	   new Timer().schedule(new TimerTask() {

	   								@Override
	   								public void run() {
	   									System.out.println("broadcast test");
										try {
											broacastMessage("broadcasttrue~"+returnList());
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
	   								}
	   		                		   
	   		                	   }, 15000);
		            	   
		                   } else if (inMessage[1].equals("sign")) {
		                	   if(checkSign(inMessage[0])) {
		                		   if(user.contains(" ")) {
		                			   createContactTable(user+inTel);
			                		   createInteractionTable(user+inTel);
		                		   }else {
		                			   createContactTable(user+inTel);
			                		   createInteractionTable(user+inTel);
		                		   }
		                		  
		                		   System.out.println("Saving is successfully");
		                		   
		                	   }
		                   } else if(inMessage[1].equals("chat")){
		                	   System.out.println(inMessage[0]);
		                	   String[] data = inMessage[0].split(",");
		                	   String tablenamesender = "message"+data[0]+data[4];
		                	   String tablenamereceiver = "message"+data[1]+data[5];
		                	   if(data[6].equals("off")){
		                		   if(insertMessageOnDB(data[0],data[2],"sender",tablenamesender,data[1],data[3],"on") && 
		                				   insertMessageOnDB(data[0],data[2],"receiver",tablenamereceiver,data[1],data[3],"off")) {
		                			   dos.writeUTF("sendingmsgtrue");
		                		   }else {
		                			   dos.writeUTF("sendingmsgfalse");
		                		   }
		                	   }else if(data[6].equals("on")) {
		                		   if(insertMessageOnDB(data[0],data[2],"sender",tablenamesender,data[1],data[3],"on") && 
		                				   insertMessageOnDB(data[0],data[2],"receiver",tablenamereceiver,data[1],data[3],"off")) {
		                			   onChatMessage(data[5],data[2],data[4],data[0],data[1],data[3]);
		                			   //dos.writeUTF("sendingmsgonlinetrue");
		                		   }
		                	   }
		                	   
		                	   
		                   }else if(inMessage[1].equals("getcontact")) {
		                	   
		                	   
		                	   int friendRequest = 0;
		                	   String[] data = inMessage[0].split(",");
		                	   String tablename = "contact"+data[0]+data[1];
		                	   String sql = "SELECT * FROM " +tablename+" INNER JOIN sign WHERE "+tablename+".signid = sign.userid";
		                	   Statement st = db.getDbConnection().createStatement();
		                	   ResultSet rs = st.executeQuery(sql);
		                	   multiuse.clear();
		                	   while(rs.next()) {
		                		   String status = rs.getString("status");
		                		   String contactStat = rs.getString("contactstat");
		             
		                		   if(!contactStat.equals(null)) {
		                			   System.out.println("status "+status);
			                		   System.out.println("contactstat "+contactStat);
		                			   if(contactStat.equals("on") && status == null) {
			                			   multiuse.add(rs.getInt("userid")+","+rs.getString("name")+","+rs.getString("phoneNumber")+",Off");
			                		   }else if(contactStat.equals("on") && status.equals("off")) {
			                			   multiuse.add(rs.getInt("userid")+","+rs.getString("name")+","+rs.getString("phoneNumber")+",Off");
			                		   }else if(contactStat.equals("on") && status.equals("on")) {
			                			   multiuse.add(rs.getInt("userid")+","+rs.getString("name")+","+rs.getString("phoneNumber")+",On");
			                		   }else if(contactStat.equals("off")){
			                			   friendRequest++;
			                		   }
		                		   }else {
		                			   System.out.println("no data in the Database");
		                		   } 
		                	   }
		                	   if(multiuse.size() > 0) {
		                		   dos.writeUTF("contactavaibletrue~"+extractList(multiuse)+","+friendRequest);
		                		   
		                	   }else {
		                		   dos.writeUTF("contactavaiblefalse~"+friendRequest);
		                		   
		                	   }
		                	   
		                   }else if(inMessage[1].equals("searchbyname")) {
		                	   String inName = inMessage[0];
		                	   System.out.println("byname "+inName);
		                	   String sql = "SELECT * FROM sign";
		            		   Statement st = db.getDbConnection().createStatement();
		            		   ResultSet rs = st.executeQuery(sql);
		            		   String rData = "";
		            		   //multiuse1.clear();
		            		   if(rs.next()) {
		            			   do {
		            				   String dbName =rs.getString("name");
		            				   if(inName.toLowerCase().equals(dbName.toLowerCase())) {
		            						String foundUser = dbName+","+rs.getString("phoneNumber")+","+rs.getString("email");
		            						 dos.writeUTF("bynametrue~"+foundUser);
		            						 break;
		            						//multiuse1.add(rname+","+rs.getString("phoneNumber")+","+rs.getString("email"));
		            					}else {
		            						dos.writeUTF("notfoundbysearching");
		            						System.out.println("no user found");
		            					}
		            			   	} while(rs.next());
		            							
		            			}else {
            						dos.writeUTF("notfoundbysearching");
            						System.out.println("no user found");
            					}
		            		   st.close();
		            		   rs.close();
		            		   db.getDbConnection().close();
		                   }else if(inMessage[1].equals("searchbyphone")) {
		                	   String inPhone = inMessage[0];
		            		   String sql = "SELECT * FROM sign";
		            		   System.out.println("receiv phone "+inPhone);
		            		   Statement st = db.getDbConnection().createStatement();
		            		   ResultSet rs = st.executeQuery(sql);
		            		   if(rs.next()) {
		            			   
		            			   do {
		            				   String dbPhone = outPhone(rs.getString("phoneNumber"));
		            				   if(checkPhoneNumber(inPhone,dbPhone)) {
		            					   System.out.println("test0 ");
		            					   data = rs.getString("name")+","+rs.getString("phoneNumber");
		            					   dos.writeUTF("phonetrue~"+data);
		            				   }else {
			            				   dos.writeUTF("notfoundbysearching"); 
			            			   }
		            			   }while(rs.next());}
		            			   
		            		   	else {
		            			   dos.writeUTF("notfoundbysearching");
		            			   System.out.println("no data found");
		            		   }
		            		   st.close();
		            		   rs.close();
		            		   db.getDbConnection().close();
		                   }else if(inMessage[1].equals("addcontact")){
		                	   String tablenameUserPart = "contact"+user+tel;
		          			   String[] data = inMessage[0].split(",");
		          			   String tablenameFriendPart = "contact"+data[0]+data[1];
		          			   System.out.println("donnees "+data[2]+" "+data[1]);
		          			   if(addContact(tablenameUserPart,data[1],"att") && addContact(tablenameFriendPart,tel,"off")) {
		          				 dos.writeUTF("true~contactadded");
		          			   }else if(verify){
		          				 dos.writeUTF("contactnotadded~Contact is already present");
		          			   }else {
		          				 dos.writeUTF("false~contactnotadded");
		          			   }
		          			   
			               }else if(inMessage[1].equals("logout")){
			            	   
								if(updateInToSign(inemail,"off",new Date().toString())) {
									   dos.writeUTF("trueandclose");
									   removeFromActiveUser(st);
									   broacastMessage("broadcasttrue~"+returnList());
									   this.islogged=false;
									       
								}else {
									System.out.println("update not possible please try later.");
								}
			            	  
			               }else if(inMessage[1].equals("checkonlineuser")){
			            	   int id = 0;
			            	   String[] data = inMessage[0].split(",");
			            	   String tablename = "contact"+data[0]+data[1];
			            	   String sql = "SELECT * FROM "+tablename+" WHERE signid = "+"'"+Integer.valueOf(data[2])+"'";
			            	   Statement st = db.getDbConnection().createStatement();
			            	   ResultSet rs = st.executeQuery(sql);
			            	   while(rs.next()) {
			            		   id = rs.getInt("signid");
			            	   }
			            	   if(id > 0) {
			            		   dos.writeUTF("isonlineandinmycontacttrue~data");
			            	   }else {
			            		   dos.writeUTF("isonlineandinmycontactfalse~data");
			            	   }
			               }else if(inMessage[1].equals("friendrequest")) {
			            	   lfriendrequest.clear();
			            	   String[] data = inMessage[0].split(",");
			            	   String tablename = "contact"+data[0]+data[1];
			            	   String sql = "SELECT * FROM sign INNER JOIN "+tablename+" WHERE "+tablename+".signid = userid";
			            	   Statement st = db.getDbConnection().createStatement();
			            	   ResultSet rs = st.executeQuery(sql);
			            	   while(rs.next()) {
			            		   if(rs.getString("contactstat").equals("off")) {
			            			   lfriendrequest.add(rs.getInt("userid")+","+rs.getString("name")+","+rs.getString("phoneNumber"));
			            		   }
			            	   }
			            	   if(lfriendrequest.size() > 0) {
			            		   dos.writeUTF("friendrequesttrue~"+extractList(lfriendrequest));
			            	   }else {
			            		   dos.writeUTF("friendrequestfalse~data");
			            	   }
			            	   System.out.println("tablename "+tablename);
			               }else if(inMessage[1].equals("validfriendrequest")) {
			            	   String[] data = inMessage[0].split(",");
			            	   String tablenamefriend ="contact"+data[0]+data[1];
			            	   String tablenameuser ="contact"+user+tel;
			            	   int userid = Integer.valueOf(data[2]);
			            	   System.out.println(" tes "+Integer.valueOf(data[3])+" "+userid);
			            	   if(confirmFriendRequest(tablenamefriend,userid) &&  confirmFriendRequest(tablenameuser,Integer.valueOf(data[3]))) {
			            		   dos.writeUTF("confirmrequestupdatetrue");
			            	   }else {
			            		   dos.writeUTF("confirmrequestupdatefalse");
			            	   }
			            	   System.out.println("tablename1 "+tablenamefriend);
			            	   System.out.println("tablename "+tablenameuser);
			               }else if(inMessage[1].equals("listofdoingrequest")) {
			            	   lcountrequest.clear();
			            	   String data = "att";
			            	   String tablenameuser ="contact"+user+tel;
			            	   String sql = "SELECT * FROM " +"'"+tablenameuser+"'" +" INNER JOIN sign WHERE "+"'"+tablenameuser+
			            			   "'"+".contactstat = 'att'" +"AND sign.userid = "+"'"+tablenameuser+"'"+".signid";
		                	   Statement st = db.getDbConnection().createStatement();
		                	   ResultSet rs = st.executeQuery(sql);
		                	   while(rs.next()) {
		                		   if(rs.getString("contactstat").equals("att")) {
		                			   lcountrequest.add( rs.getInt("userid")+","+rs.getString("name")+","+rs.getString("phoneNumber"));
		                		   }
		                		   
		                	   }
		                	   if(lcountrequest.size() > 0) {
			                	   dos.writeUTF("doingrequesttrue~"+extractList(lcountrequest));
			                   }else {
			                	   dos.writeUTF("doingrequestfalse~data");
			                   }
		                	   st.close();
		                	   rs.close();
		                	   db.getDbConnection().close();
			               }else if(inMessage[1].equals("getdoingrequestcount")) {
			            	   getCount("att","doingrequestcount");
			            	   
			               }else if(inMessage[1].equals("getcontactfriendcount")) {
			            	   getCount("off","contactcounttrue");
			            	   
			               }else if(inMessage[1].equals("checkpresentusername")) {
			            	   String sql = "SELECT name FROM sign";
			            	   Statement st = db.getDbConnection().createStatement();
			            	   ResultSet rs = st.executeQuery(sql);
			            	   int count = 0;
			            	   while(rs.next()) {
			            		   if(rs.getString("name").toLowerCase().equals(inMessage[0].toLowerCase()) && rs.getString("name") != null) {
			            			   count ++;
			            		   }else if(rs.getString("name") == null) {
			            			   dos.writeUTF("unameisfree~data");
			            		   }
			            		   
			            	   }
			            	   if(count > 0) {
			            		   dos.writeUTF("unamealreadypresent~data");
			            	   }else {
			            		   dos.writeUTF("unameisfree~data");
			            	   }
			               }
		                   
	               		}
	               	}
	               	
	    		}
	    	
               
           } catch (IOException | SQLException | ClassNotFoundException e) { 
                 
               e.printStackTrace(); 
           } 
	    	
	   }
	  
	//});
	//clientThread.start();
	}
	public boolean insertMessageOnDB(String sender,String msg,String status, String tablename,String receiver,String date,String mstatus) throws ClassNotFoundException, SQLException {
		String sql = "INSERT INTO "+tablename+" (sender,sendedMessage,receivedMessage,receiver,date,mstatus)"
				+ "VALUES(?,?,?,?,?,?)";
		PreparedStatement ps = db.getDbConnection().prepareStatement(sql);
		if(status.equals("sender")) {
			ps.setString(1, sender);
			ps.setString(2, msg);
			ps.setString(3, "");
			ps.setString(4, receiver);
			ps.setString(5, date);
			ps.setString(6, mstatus);
		}else if(status.equals("receiver")) {
			ps.setString(1, sender);
			ps.setString(2, "");
			ps.setString(3, msg);
			ps.setString(4, receiver);
			ps.setString(5, date);
			ps.setString(6, mstatus);
		}
		
		if(ps.executeUpdate() > 0) {
			return true;
		}else {
			return false;
		}
		
	}
	public void getCount(String data,String tosend) throws ClassNotFoundException, SQLException, IOException {
		int count = 0;
 	   String tablenameuser ="contact"+user+tel;
 	   System.out.println("table name "+tablenameuser);
 	   String sql = "SELECT * FROM "+"'"+tablenameuser+"'";
 	   Statement st = db.getDbConnection().createStatement();
 	   ResultSet rs = st.executeQuery(sql);
 	   while(rs.next()) {
 		   if(rs.getString("contactstat").equals(data)) {
 			   count++;
 		   }
 	   }
 	   dos.writeUTF(tosend+"~"+String.valueOf(count));
 	   st.close();
 	   rs.close();
 	   db.getDbConnection().close();
	}
	//update the user contact and friend contact table. the gold is to confirm the friend request
	//friend request sign
	public boolean confirmFriendRequest(String tablename,int id) throws ClassNotFoundException, SQLException, IOException {
		boolean checkrequest;
		String sql = "UPDATE " +tablename+ " SET contactstat = ? WHERE signid = ?";
		PreparedStatement ps = db.getDbConnection().prepareStatement(sql);
		ps.setString(1, "on");
		ps.setInt(2, id);
		
		int check = ps.executeUpdate();
		if(check > 0) {
			checkrequest = true;
			
			ps.close();
			db.getDbConnection().close();
			return checkrequest;
		}else {
			checkrequest = false;
			
			ps.close();
			db.getDbConnection().close();
			return checkrequest;
		}
		
	}
	// ajoute des utilisateurs a la table contact. celui ci se fait de chaque coté. coté utilisateur et du coté friend 
	public boolean addContact(String tablename,String friendphone,String status) throws ClassNotFoundException, SQLException, IOException {
		   int count = 0;
		   boolean check = false;
		   Statement st = db.getDbConnection().createStatement();
		   ResultSet rs = st.executeQuery("SELECT * FROM sign WHERE phoneNumber ="+"'"+friendphone+"'");
		   int dbuserid = 0;
		   while(rs.next()) {
			   dbuserid = rs.getInt("userid"); // recupere le id de l'autre utilisateur ou celui a ajouter comme contact
			   															//pour verifier si il est deja dans les contacts
			   System.out.println("etape1 "+dbuserid);
		   }
		   st.close();
		   rs.close();
		   db.getDbConnection().close();
		   
		   if(dbuserid > 0) {// prouve qu'il est inscrit dans la table sign
			   
			   Statement st1 = db.getDbConnection().createStatement();
			   ResultSet rs1 = st1.executeQuery("SELECT * FROM "+tablename+" WHERE signid ="+"'"+dbuserid+"'");
			   while(rs1.next()) {
				   count ++;
				   System.out.println("counter "+count);
			   }
			   st.close();
			   rs.close();
			   db.getDbConnection().close();
		   }
		   
		   if(count > 0) {
			   check = false;
			   verify = true;
			   return check;
		   }else {
			   String sql = "INSERT INTO "+tablename+" (signid,contactstat)"
						+ "VALUES(?,?)";
				PreparedStatement ps = db.getDbConnection().prepareStatement(sql);
				ps.setInt(1, dbuserid); //name,phone,email
				ps.setString(2, status);
				
				if(ps.executeUpdate() > 0) {
					check = true;
					ps.close();
					db.getDbConnection().close();
					return check;
				}else {
					check = false;
					ps.close();
					db.getDbConnection().close();
					return check;
					
				}
				
		   }
		   
	}
	
	public Boolean checkLogin(String value) throws IOException, SQLException, ClassNotFoundException {
		
		Boolean isLogged = null;
		
		String[] data = extractLoginData(value);
		String pass = data[1];
		inemail = data[0];
		String name;
		Statement st;
		Boolean check;
		db.getDbConnection().setAutoCommit(false);
		st = db.getDbConnection().createStatement();
		System.out.println("email "+inemail);
		ResultSet rs = st.executeQuery("SELECT * FROM sign WHERE email ='"+inemail+"'");
		if(rs.next()) {
			try {
				do {
					String dbpass = rs.getString("pass");
					if(passDecoder(dbpass).equals(pass)) {
						String dbname = rs.getString("name");
						String dbphone = rs.getString("phoneNumber");
						String dbemail = rs.getString("email");
						userID = rs.getInt("userid");
						isLogged = true;
						user = dbname;
						useremail = dbemail;
						tel = dbphone;
						activeUserSize = activeUser.size();
						dos.writeUTF("truelogin,"+dbname+","+dbphone+","+dbemail+","+String.valueOf(userID)+","+activeUserSize);
						usernames = data[0];
						
						if(data != null) {
							data[0] = null;
							data[1] = null;
						}
						
						System.out.println(usernames+" You are now connected");
						return isLogged;
					}else {
						dos.writeUTF("falselogin~pass");
						isLogged = false;
						return isLogged;
					}
				} while(rs.next());
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			dos.writeUTF("falselogin~email");
			isLogged = false;
			return isLogged;
		}
		st.close();
		db.getDbConnection().close();
		return isLogged;
	}
	
	public void createContactTable(String user) throws ClassNotFoundException, SQLException {
		String sql= "CREATE TABLE IF NOT EXISTS contact"+user+"(contactid INTEGER PRIMARY KEY,signid INTEGER NOT NULL UNIQUE,"
				+ "contactstat VARCHAR(5) NOT NULL,"+ "FOREIGN KEY(signid) REFERENCES sign(userid));";
		Statement st = db.getDbConnection().createStatement();
		st.executeUpdate(sql);
		st.close();
		db.getDbConnection().close();
	}
	
	public void createInteractionTable(String user) throws ClassNotFoundException, SQLException {
		String sql= "CREATE TABLE IF NOT EXISTS message"+user+"(messageid INTEGER PRIMARY KEY,sender VARCHAR(255) NOT NULL,"
				+ " sendedMessage TEXT, receivedMessage TEXT, receiver VARCHAR(255) NOT NULL, date STRING NOT NULL, mstatus VARCHAR(5) NOT NULL);";
		Statement st = db.getDbConnection().createStatement();
		st.executeUpdate(sql);
		st.close();
		db.getDbConnection().close();
	}
	
	
	public Boolean checkSign(String token) throws IOException, SQLException, ClassNotFoundException {
		Boolean isSave = null;
		String[] data = extractSignData(token);
		Statement st;
		
		//db.getDbConnection().setAutoCommit(false);
		//st = db.getDbConnection().createStatement();
		
		String sql = "INSERT INTO sign (name,bornDate,phoneNumber,email,pass) VALUES"
				+ "(?,?,?,?,?)";
		PreparedStatement ps = db.getDbConnection().prepareStatement(sql);
		ps.setString(1, data[0]);
		ps.setString(2, data[1]);
		if(data[2].contains("+")) {
			inTel = data[2].replace("+", "00");
			ps.setString(3, inTel);
		}else {
			inTel = data[2];
			ps.setString(3, inTel);
		}
		ps.setString(4, data[3]);
		ps.setString(5, passEnCoder(data[4]));
		int rs = ps.executeUpdate();
		if(rs > 0) {
			isSave = true;
			user = data[0];
			
			dos.writeUTF("signtrue~returnsign");
		}else {
			isSave = false;
			dos.writeUTF("signfalse~returnsign");
		}
		ps.close();
		db.getDbConnection().close();
		return isSave;
	}
	
	public Boolean updateInToSign(String email,String status, String date) throws SQLException, ClassNotFoundException {
		Statement st = db.getDbConnection().createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM sign WHERE email = "+"'"+email+"'");
		int dbuserid = 0;
		while(rs.next()) {
			dbuserid = rs.getInt("userid");
		}
		
		st.close();
		rs.close();
		db.getDbConnection().close();
		
		Boolean verify = null;
		
		String sql = "UPDATE sign SET status = ?, date = ? WHERE userid = ?";
		
		PreparedStatement ps = db.getDbConnection().prepareStatement(sql);
		ps.setString(1, status);
		ps.setString(2, date);
		ps.setInt(3, dbuserid);
		int check = ps.executeUpdate();
		if(check > 0) {
			verify = true;
			ps.close();
			db.getDbConnection().close();
			return verify;
		}else {
			verify = false;
			ps.close();
			db.getDbConnection().close();
			return verify;
		}
	}
	
	public static String[] extractLoginData(String text) {
		String[] data = new String[2];
		String[] textBlock = text.split(",");
		String[] lastBlock = textBlock[0].split("\\[");
		data[0] = lastBlock[1];
		String[] lastBlock1 = textBlock[1].split("\\]");
		data[1] = lastBlock1[0];
		return data;
	}
	//email,first_last-name,phone,pass,borndate
	public static String[] extractSignData(String text) {
		String[] data = new String[5];
		String[] textBlock = text.split(",");
		String[] lastBlock = textBlock[0].split("\\[");
		data[0] = lastBlock[1];
		String[] lastBlock1 = textBlock[4].split("\\]");
		data[4] = lastBlock1[0];
		data[1] = textBlock[1];
		data[2] = textBlock[2];
		data[3] = textBlock[3];
		return data;
	}
	
	public void displayUser() {
		/*for(Entry<String, UserModele> atu : activeUser.entrySet()) {
			System.out.println("Active user "+ atu.getValue().getUser());
		}*/
		for(int i = 0; i< activeUser.size(); i++) {
			System.out.println(activeUser.get(i).getUser());
		}
	}
	
	public static String onReceivMessage(DataInputStream dis) throws IOException {
		String message = null;
		message = dis.readUTF();
		return message;
	}
	
	public static void onChatMessage(String telto,String msg,String telfrom, String from, String to, String date) throws IOException {
		
		for(int i = 0; i<activeUser.size();i++) {
			if(telto.equals(activeUser.get(i).getPhone())) {
				activeUser.get(i).getDos().writeUTF(new ServerMessageHandler(from,msg,to,telfrom,telto,date).sendDate()+"~fromchatonlineuser");
				break;
			}
		}
   	}
	
	public String[] extractChatData(String text) {
		String[] data = new String[3] ;
		String[] textBlock = text.split(",");
		String[] from = textBlock[0].split("\\[");
		String[] date = textBlock[2].split("\\]");
		data[0] = from[1];
		data[1] = textBlock[1];
		data[2] = date[0];
		return data;
	}
	
	public static void generedSession(String usernames, Socket st, DataOutputStream dos, 
			DataInputStream dis,String phone,int userID, String user) throws NumberFormatException, UnknownHostException, IOException {
		activeUser.add(new UserModele(usernames,st,dos,dis,phone,userID,user,"on"));
		//broacastMessage(returnList());
	}
	
	public void removeFromActiveUser(Socket st) throws IOException{
		for(int i = 0; i<activeUser.size(); i++) {
			if(activeUser.get(i).getS() == st) {
				activeUser.remove(i);
			}
		}
	}
	
	public static void broacastMessage(String message) throws IOException {
		for(int i = 0; i<activeUser.size(); i++) {
			activeUser.get(i).getDos().writeUTF(message);
		}
	}
	
	
	public static String returnList() {
		String userList = "";
		
		for(int i = 0; i < activeUser.size(); i++) {
			if(i == activeUser.size() - 1) {
				userList += activeUser.get(i).getUsername()+","+activeUser.get(i).getPhone()+","
						+activeUser.get(i).getUserID()+","+activeUser.size();
			}else {
				userList += activeUser.get(i).getUsername()+","+activeUser.get(i).getPhone()+","
			+activeUser.get(i).getUserID()+","+activeUser.size()+"-";
			}
		}
		return userList;
	}
	
	public static String passEnCoder(String text) {
		String indexOfPass = "";
		char[] data = text.toCharArray();
		for(int j = 0; j<data.length;j++) {
			for(int i = 0; i<coder.length;i++) {
				if(data[j] == coder[i]) {
					indexOfPass += String.valueOf(i);
					if(j != text.length() - 1) {
						indexOfPass += "-";
					}
				}
			}
		}
		return indexOfPass;
	}
	
	public static String passDecoder(String text) {
		String pass = "";
		String[] data = text.split("-");
		for(int j = 0; j<data.length;j++) {
			for(int i = 0; i<coder.length;i++) {
				if(Integer.valueOf(data[j]) == i) {
					pass += coder[i];
				}
			}
		}
		return pass;
		
	}
	
	public String outPhone(String phone) {
	
		if(phone.contains(" ")) {
			String newinput = phone.replace(" ", "");
			if(newinput.contains("-")) {
				String tmpData = newinput.replace("-", "");
				return tmpData;
			}else {
				return newinput;
			}
			
		}else if(phone.contains("-")){
			String newinput = phone.replace("-", "");
			if(newinput.contains(" ")) {
				String tmpData = newinput.replace(" ", "");
				return tmpData;
			}else {
				return newinput;
			}
		}else {
			return phone;
		}
	}
	
	public static Boolean checkPhoneNumber(String inPhone,String dbPhone) {
		Boolean check = null;
		if(inPhone.length() > dbPhone.length()) {
			int counter = 0;
			char[] inData = inPhone.toCharArray();
			String tmpData = "";
			int size = inPhone.length() - dbPhone.length();
			for(int i = 0; i<size ; i++) {
				tmpData += "0";
			}
			String phoneData = tmpData+dbPhone;
			char[] dbData = phoneData.toCharArray();
			for(int i = 0; i < dbData.length; i++) {
				if(inData[i] == dbData[i]) {
					counter++;
				}
			}
			if(counter >= dbData.length - 3) {
				check = true;
			}else {
				check = false;
			}
		}else if(dbPhone.length() > inPhone.length()) {
			int counter = 0;
			char[] inData = dbPhone.toCharArray();
			String tmpData = "";
			int size =  dbPhone.length() - inPhone.length();
			for(int i = 0; i<size ; i++) {
				tmpData += "0";
			}
			String phoneData = tmpData+inPhone;
			char[] phData = phoneData.toCharArray();
			System.out.println(phData.length);
			System.out.println(inData.length);
			for(int i = 0; i < phData.length; i++) {
				if(phData[i] == inData[i]) {
					counter++;
				}
			}
			if(counter >= phData.length - 3) {
				check = true;
			}else {
				check = false;
			}
		}else if(dbPhone.length() == inPhone.length()) {
			int counter = 0;
			char[] inData = inPhone.toCharArray();
			char[] phData = dbPhone.toCharArray();
			for(int i = 0; i < phData.length; i++) {
				if(phData[i] == inData[i]) {
					counter++;
				}
			}
			if(counter == phData.length) {
				check = true;
			}else {
				check = false;
			}
			System.out.println(counter);
		}
		
		return check;
	}
	
	public String extractList(List list) {
		String listToSend = "";
		
		for(int i = 0; i<list.size(); i++) {
			if(i == list.size() - 1) {
				listToSend += list.get(i);
			}else {
				listToSend += list.get(i)+"-";
			}
		}
		return listToSend;
	}
	
	public String extractList1(List list) {
		String listToSend = "";
		for(int i = 0; i<list.size(); i++) {
			if(i == list.size() - 1) {
				listToSend += list.get(i);
			}else {
				listToSend += list.get(i)+"-";
			}
		}
		return listToSend;
	}
	
	public DataInputStream getDis() {
		return dis;
	}


	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}


	public DataOutputStream getDos() {
		return dos;
	}


	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}


	public Socket getSt() {
		return st;
	}


	public void setSt(Socket st) {
		this.st = st;
	}


	public String getUsernames() {
		return usernames;
	}


	public void setUsernames(String usernames) {
		this.usernames = usernames;
	}


	public static List<ClientHandler> getListUser() {
		return listUser;
	}


	public static void setListUser(List<ClientHandler> listUser) {
		ClientHandler.listUser = listUser;
	}


}
