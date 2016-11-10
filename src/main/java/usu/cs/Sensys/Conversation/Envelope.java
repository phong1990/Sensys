package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class Envelope {
	private Message Msg;
	private PublicEndpoint EndPoint;

	public Envelope(Message msg, PublicEndpoint endpoint) {
		// TODO Auto-generated constructor stub
		Msg = msg;
		EndPoint = endpoint;
	}

	public boolean isValidToSend() {
		return (Msg != null && EndPoint != null && EndPoint.getHost() != null
				&& EndPoint.getPort() != 0);
	}

	public Message getMsg() {
		return Msg;
	}

	public void setMsg(Message msg) {
		Msg = msg;
	}

	public PublicEndpoint getEndPoint() {
		return EndPoint;
	}

	public void setEndPoint(PublicEndpoint endPoint) {
		EndPoint = endPoint;
	}
}
