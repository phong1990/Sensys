package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.LogoutReply;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.Identity;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

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
			ConversationId = MessageNumber.Create();
			System.out.println(ConversationId);
			MessageNumber messageID = MessageNumber.Create();
			MyQueue = CommSubsystem.SetupConversationQueue(ConversationId);
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

				// no reply yet, resend the message
				if (Retries < MaxRetries) {
					// send envelop
					sendMessage(envelop);
					if (ErrorMessage != null)
						waiting = false;
					increaseRetries();
				} else {
					waiting = false;
					logger.debug("Maxxed out the retries");
				}

			} else {
				if (IsEnvelopeValid(reply)) {
					// store the result in a volatile variable and exit, hope
					// for
					// the main process to read this result
					LoginReply replyMessage = (LoginReply) reply.getMsg();
					RespondedMessage = new LoginReply(replyMessage.isSuccess(),
							replyMessage.getNote(), replyMessage.getEndPoint());
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
	// @Override
	// public void Execute() {
	// // TODO Auto-generated method stub
	//
	// if (Initialize())
	// ExecuteDetails();
	//
	// if (ErrorMessage == null)
	// State = PossibleState.Successed;
	// else
	// {
	// State = PossibleState.Failed;
	// logger.warn(ErrorMessage);
	// }
	//
	//// PostExecuteAction?.Invoke(context);
	//
	// CommSubsystem.CloseConversationQueue(ConversationId);
	// }

	@Override
	public boolean isExpectedRespondType(String messageType) {
		// TODO Auto-generated method stub
		return messageType.equals(LoginReply.class.getName());
	}

	@Override
	protected boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return isExpectedRespondType(messageType);
	}

}
