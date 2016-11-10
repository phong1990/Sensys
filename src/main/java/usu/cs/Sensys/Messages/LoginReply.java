package usu.cs.Sensys.Messages;

import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class LoginReply extends Reply{
	public LoginReply(boolean success, String note, PublicEndpoint endpoint) {
		super(success, note);
		// TODO Auto-generated constructor stub
		MyEndPoint = endpoint;
	}
	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}
	public LoginReply() {
		// TODO Auto-generated constructor stub
	}
}
