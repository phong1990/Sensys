package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.MessageReply;
import usu.cs.Sensys.Messages.MessageRequest;
import usu.cs.Sensys.Messages.SensorGatheringReply;
import usu.cs.Sensys.Messages.SensorGatheringRequest;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;

public class InitiatorMessage extends InitiatorRRConversation {
	private boolean isEmmergency = false;
	private PublicEndpoint _endpoint;
	private volatile MessageReply RespondedMessage = null;
	private String message;
	public MessageReply getResult() {
		return RespondedMessage;
	}

	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	public InitiatorMessage(String host, int port, boolean emmergency, String msg) {
		// TODO Auto-generated constructor stub
		isEmmergency = emmergency;
		_endpoint = new PublicEndpoint(host, port);
		if (isEmmergency){
			TimeOut = 5000; // 5s for emmergency messages
		}else
			TimeOut = 10000; // 10s
		MaxRetries = 3;
		message = msg;
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
			Message msg = new MessageRequest(isEmmergency, message);
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
				// emmergency protocol doesn't require dealling with replies
				if (!isEmmergency) {
					if (IsEnvelopeValid(reply)) {
						// store the result in a volatile variable and exit,
						// hope
						// for
						// the main process to read this result
						MessageReply replyMessage = (MessageReply) reply
								.getMsg();
						RespondedMessage = new MessageReply(
								replyMessage.isSuccess(),
								replyMessage.getNote());
						// add this sensor to the Sensor receiving List
						// <unimplemented>

						ErrorMessage = null; // this mean successfully received
												// the
												// data.
						waiting = false;
					}
				}
			}
		}
	}

	@Override
	public boolean handleReply(Envelope env) {
		MessageReply message = (MessageReply) env.getMsg();
		if (!message.isSuccess()) {
			logger.info(message.getNote());
			return false;
		}
		return true;
	}

	@Override
	public boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return messageType.equals(MessageReply.class.getName());
	}
}
