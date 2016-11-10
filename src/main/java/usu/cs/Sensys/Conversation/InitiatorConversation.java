package usu.cs.Sensys.Conversation;

public abstract class InitiatorConversation extends Conversation{
	public abstract boolean isExpectedRespondType(String messageType);
	public abstract boolean handleReply(Envelope env);
}
