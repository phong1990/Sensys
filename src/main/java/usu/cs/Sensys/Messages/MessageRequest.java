package usu.cs.Sensys.Messages;

public class MessageRequest extends Request {
	private boolean isEmmergency = false;
	private String Message;

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	public MessageRequest(boolean emmergency, String msg) {
		// TODO Auto-generated constructor stub
		isEmmergency = emmergency;
		Message = msg;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public boolean isEmmergency() {
		return isEmmergency;
	}

	public MessageRequest() {
		// TODO Auto-generated constructor stub
	}
}
