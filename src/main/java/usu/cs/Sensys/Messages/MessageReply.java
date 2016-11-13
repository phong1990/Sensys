package usu.cs.Sensys.Messages;

public class MessageReply extends Reply {

	public MessageReply(boolean success, String note) {
		super(success, note);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	public MessageReply() {
		// TODO Auto-generated constructor stub
	}
}
