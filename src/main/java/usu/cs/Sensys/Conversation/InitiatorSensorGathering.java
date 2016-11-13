package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.SensorGatheringReply;
import usu.cs.Sensys.Messages.SensorGatheringRequest;
import usu.cs.Sensys.Messages.SensorHandshakeReply;
import usu.cs.Sensys.Messages.SensorHandshakeRequest;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;

public class InitiatorSensorGathering extends InitiatorRRConversation{
	private PublicEndpoint _endpoint;
	private volatile SensorGatheringReply RespondedMessage = null;
	private SensorData dat;

	public SensorGatheringReply getResult() {
		return RespondedMessage;
	}

	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	public InitiatorSensorGathering(String host, int port, SensorData data) {
		// TODO Auto-generated constructor stub
		_endpoint = new PublicEndpoint(host, port);
		TimeOut = 10000; // 10s
		MaxRetries = 3;
	}

	@Override
	protected void ExecuteDetails() {
		State = PossibleState.Working;
		// TODO Auto-generated method stub
		// create login envelop
		Envelope envelop = null;
		TransactionLock.lock();
		{
			MessageNumber messageID = createQueue();
			Message msg = new SensorGatheringRequest(dat,1000);
			msg.setConversationId(ConversationId);
			msg.setMessageNr(messageID);
			envelop = new Envelope(msg, _endpoint);
		}
		TransactionLock.unlock();

		// send envelop
		sendMessage(envelop);
		if (ErrorMessage != null)
			return;
		// wait for reply message (in other thread so check state)
		boolean waiting = true;
		while (waiting) {
			// dequeue will wait until timeout is done
			Envelope reply = MyQueue.Dequeue(TimeOut);
			if (reply == null) {

				waiting = retriesRoutine(envelop, waiting);

			} else {
				if (IsEnvelopeValid(reply)) {
					// store the result in a volatile variable and exit, hope
					// for
					// the main process to read this result
					SensorGatheringReply replyMessage = (SensorGatheringReply) reply
							.getMsg();
					RespondedMessage = new SensorGatheringReply(
							replyMessage.isSuccess(), replyMessage.getNote());
					// add this sensor to the Sensor receiving List <unimplemented>
					
					ErrorMessage = null; // this mean successfully received the
											// data.
					waiting = false;
				}
			}
		}
	}

	@Override
	public boolean handleReply(Envelope env) {
		SensorGatheringReply message = (SensorGatheringReply) env.getMsg();
		if (!message.isSuccess()) {
			logger.info(message.getNote());
			return false;
		}
		return true;
	}

	@Override
	public boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return messageType.equals(SensorGatheringReply.class.getName());
	}
}
