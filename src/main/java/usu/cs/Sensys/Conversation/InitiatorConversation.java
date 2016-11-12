package usu.cs.Sensys.Conversation;

public abstract class InitiatorConversation extends Conversation{
	
	protected boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return isExpectedMessageType(messageType);
	}
	public abstract boolean handleReply(Envelope env);
}
