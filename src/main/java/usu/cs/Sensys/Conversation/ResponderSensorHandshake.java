package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Main.SensorDataManager;
import usu.cs.Sensys.Messages.HeartbeatReply;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.SensorHandshakeReply;
import usu.cs.Sensys.SharedObjects.MessageNumber;

public class ResponderSensorHandshake extends ResponderRRConversation {
	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void ExecuteDetails() {
		// TODO Auto-generated method stub
		State = PossibleState.Working;
		SensorDataManager dataMan = SensorDataManager.getInstance();
		// check to see if it is already occupied by a process
		boolean occupied = dataMan.isOCCUPIED();
		if (!occupied) {
			// Save the Endpoint of the recipient for data, also set sensor
			// status
			// to Occupied
			dataMan.setOCCUPIED(true);
			dataMan.setReciepientEndpoint(IncomingEnv.getEndPoint());
		}
		// TODO Auto-generated method stub
		// create envelop
		Envelope envelop = null;
		TransactionLock.lock();
		{
			MessageNumber messageID = setupQueue();

			Message msg = null;
			if (occupied) {
				msg = new SensorHandshakeReply(false,
						"Other process has already occupied this sensor",SensorDataManager.getInstance().getSensorType());
			} else {
				msg = new SensorHandshakeReply(true, "", SensorDataManager.getInstance().getSensorType());
			}
			msg.setConversationId(ConversationId);
			msg.setMessageNr(messageID);
			envelop = new Envelope(msg, IncomingEnv.getEndPoint());
		}
		TransactionLock.unlock();

		// send envelop
		sendMessage(envelop);

	}

	@Override
	protected boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return false;
	}
}
