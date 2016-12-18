package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.LogoutReply;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.Identity;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.util.PublicKeyManager;

public class InitiatorLogin extends InitiatorRRConversation {
	private Identity _id;
	private PublicEndpoint _endpoint;
	private volatile LoginReply RespondedMessage = null;

	public LoginReply getResult() {
		return RespondedMessage;
	}

	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	public InitiatorLogin(String iden, String pin, String host, int port) {
		// TODO Auto-generated constructor stub
		_id = new Identity(iden, pin);
		_endpoint = new PublicEndpoint(host, port);
		TimeOut = 3000; // 10s
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
			Message msg = new LoginRequest(_id, CommSubsystem.getMyEndpoint());
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
					LoginReply replyMessage = (LoginReply) reply.getMsg();
					
					RespondedMessage = new LoginReply(replyMessage.isSuccess(),
							replyMessage.getNote(), replyMessage.getEndPoint(),
							replyMessage.getPublicKey());
					// set the key
					PublicKeyManager.getInstance().setKey(RespondedMessage.getPublicKey());
					ErrorMessage = null; // this mean successfully received the
											// data.

					waiting = false;
				}
			}
		}
	}

	@Override
	public boolean handleReply(Envelope env) {
		LogoutReply message = (LogoutReply) env.getMsg();
		if (!message.isSuccess()) {
			logger.info(message.getNote());
			return false;
		}
		return true;
	}

	@Override
	public boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return messageType.equals(LoginReply.class.getName());
	}

}
