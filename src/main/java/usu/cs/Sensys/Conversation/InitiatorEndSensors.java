package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Main.SensorManager;
import usu.cs.Sensys.Messages.EndSensorsRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.SensorHandshakeReply;
import usu.cs.Sensys.Messages.SensorHandshakeRequest;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.devices.Sensor;

public class InitiatorEndSensors extends InitiatorConversation {
	private volatile SensorHandshakeReply RespondedMessage = null;

	public SensorHandshakeReply getResult() {
		return RespondedMessage;
	}

	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	public InitiatorEndSensors() {
	}

	@Override
	protected void ExecuteDetails() {
		State = PossibleState.Working;

		SensorManager senMan = SensorManager.getInstance();
		for (Sensor sensor : senMan.getSensorList()) {
			// create login envelop
			Envelope envelop = null;
			TransactionLock.lock();
			{
				// one way multicast, no need to keep track of conversation id,
				// just send the words out
				Message msg = new EndSensorsRequest();
				envelop = new Envelope(msg, sensor.getEndPoint());
			}
			TransactionLock.unlock();

			// send envelop
			sendMessage(envelop);
			if (ErrorMessage != null)
				return;
		}

	}

	@Override
	public boolean handleReply(Envelope env) {
		// expecting no reply
		return true;
	}

	@Override
	public boolean isExpectedMessageType(String messageType) {
		// expecting no reply
		return true;
	}
}
