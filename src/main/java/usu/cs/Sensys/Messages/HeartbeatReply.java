package usu.cs.Sensys.Messages;

public class HeartbeatReply extends Reply{

	public HeartbeatReply(boolean success, String note) {
		super(success, note);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}


}
