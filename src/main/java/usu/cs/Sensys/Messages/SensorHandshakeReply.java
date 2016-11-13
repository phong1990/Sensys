package usu.cs.Sensys.Messages;

public class SensorHandshakeReply extends Reply {
	private int SensorType;

	public SensorHandshakeReply(boolean success, String note, int type) {
		super(success, note);
		SensorType = type;
		// TODO Auto-generated constructor stub
	}

	public int getSensorType() {
		return SensorType;
	}

	public SensorHandshakeReply() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

}
