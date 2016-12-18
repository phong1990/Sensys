package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.Identity;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.util.PublicKeyManager;

public class ResponderLogin extends ResponderRRConversation {
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
			MessageNumber messageID = setupQueue();
			Message msg = new LoginReply(true, "",
					CommSubsystem.getMyEndpoint(),
					PublicKeyManager.getInstance().getPublicKey());
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
