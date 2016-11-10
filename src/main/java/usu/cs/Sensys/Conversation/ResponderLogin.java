package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.Identity;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class ResponderLogin extends ResponderRRConversation{
	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}
	public ResponderLogin() {
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void ExecuteDetails() {
		// TODO Auto-generated method stub
		State = PossibleState.Working;
		// TODO Auto-generated method stub
		// create login envelop
		Envelope envelop = null;
		TransactionLock.lock();
		{
			ConversationId = IncomingEnv.getMsg().getConversationId();
			MessageNumber messageID = MessageNumber.Create();
			MyQueue = CommSubsystem.SetupConversationQueue(ConversationId);
			Message msg = new LoginReply(true, "", CommSubsystem.getMyEndpoint());
			msg.setConversationId(ConversationId);
			msg.setMessageNr(messageID);
			envelop = new Envelope(msg,IncomingEnv.getEndPoint());
		}
		TransactionLock.unlock();

		// send envelop
		sendMessage(envelop);
		if (ErrorMessage != null)
			return;
	}

	@Override
	protected boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return false;
	}

}
