package AllInUsServer;

import java.util.Date;

public class ServerMessageHandler {
	private String from;
	private String msg;
	private String to;
	private String telfrom;
	private String telto;
	private String date;
	private String messageType;
	//private Date date = new Date();
	
	public ServerMessageHandler(String from,String msg, String to,String telfrom,String telto,String date) {
		super();
		this.from = from;
		this.msg = msg;
		this.to = to;
		this.telfrom = telfrom;
		this.date = date;
		this.telto = telto;
	}
	
	public String sendDate() {
		return this.from+","+this.msg+","+this.to+","+telfrom+","+telto+","+date;
	}
}
