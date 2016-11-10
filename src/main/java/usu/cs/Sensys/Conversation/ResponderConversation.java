package usu.cs.Sensys.Conversation;

public abstract class ResponderConversation extends Conversation{
	protected Envelope IncomingEnv;

	public Envelope getIncomingEnv() {
		return IncomingEnv;
	}

	public void setIncomingEnv(Envelope incomingEnv) {
		IncomingEnv = incomingEnv;
	}
	
	
}
