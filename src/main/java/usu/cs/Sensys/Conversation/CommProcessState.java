package usu.cs.Sensys.Conversation;

public class CommProcessState {
    public enum PossibleState { NotInitialized, Initialized, Registering, Running, Terminating }
    private PossibleState State  = PossibleState.NotInitialized;
	public PossibleState getState() {
		return State;
	}
	public void setState(PossibleState state) {
		State = state;
	}

    // Rest of implementation not shown
}
