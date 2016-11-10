package usu.cs.Sensys.Messages;

public class SensorHandshakeReply extends Reply{

	public SensorHandshakeReply(boolean success, String note) {
		super(success, note);
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}


}
