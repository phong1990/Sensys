package usu.cs.Sensys.Conversation;

public class CommProcessState {
    public enum PossibleState { NotInitialized, Initialized, Registering, Running, Terminating }
    private PossibleState State  = PossibleState.NotInitialized;
    private static CommProcessState instance = null;
	public static CommProcessState getInstance(){
		if(instance == null)
			instance = new CommProcessState();
		return instance;
	}
	private CommProcessState() {
		// TODO Auto-generated constructor stub
	}
	public PossibleState getState() {
		return State;
	}
	public void setState(PossibleState state) {
		State = state;
	}

    // Rest of implementation not shown
}
