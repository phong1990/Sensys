package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.MessageReply;
import usu.cs.Sensys.Messages.MessageRequest;
import usu.cs.Sensys.Messages.SensorGatheringReply;
import usu.cs.Sensys.SharedObjects.MessageNumber;

public class ResponderMessage extends ResponderRRConversation{
	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void ExecuteDetails() {
		MessageRequest incomingMsg= (MessageRequest)IncomingEnv.getMsg();
		if(incomingMsg.isEmmergency())
			return;
		// TODO Auto-generated method stub
		State = PossibleState.Working;
		// TODO Auto-generated method stub
		// create login envelop
		Envelope envelop = null;
		TransactionLock.lock();
		{
			MessageNumber messageID = setupQueue();
			Message msg = new MessageReply(true, "");
			msg.setConversationId(ConversationId);
			msg.setMessageNr(messageID);
			envelop = new Envelope(msg,IncomingEnv.getEndPoint());
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
