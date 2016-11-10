package usu.cs.Sensys.Messages;

public class SensorGatheringReply extends Reply{

	public SensorGatheringReply(boolean success, String note) {
		super(success, note);
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}


}
