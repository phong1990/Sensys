package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.SharedObjects.MessageNumber;

public abstract class ResponderRRConversation extends ResponderConversation{
	protected MessageNumber setupQueue() {
		ConversationId = IncomingEnv.getMsg().getConversationId();
		MessageNumber messageID = MessageNumber.Create();
		MyQueue = CommSubsystem.SetupConversationQueue(ConversationId);
		return messageID;
	}
}
