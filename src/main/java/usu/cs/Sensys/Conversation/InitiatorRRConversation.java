package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.SharedObjects.MessageNumber;

public abstract class InitiatorRRConversation extends InitiatorConversation{

	protected MessageNumber createQueue() {
		ConversationId = MessageNumber.Create();
		MessageNumber messageID = MessageNumber.Create();
		MyQueue = CommSubsystem.SetupConversationQueue(ConversationId);
		return messageID;
	}
	protected boolean retriesRoutine(Envelope envelop, boolean waiting) {
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
		return waiting;
	}

}
