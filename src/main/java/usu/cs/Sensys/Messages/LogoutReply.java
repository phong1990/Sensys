package usu.cs.Sensys.Messages;

public class LogoutReply extends Reply {

	public LogoutReply(boolean success, String note) {
		super(success, note);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	public LogoutReply() {
		// TODO Auto-generated constructor stub
	}

}
