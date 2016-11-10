package usu.cs.Sensys.Messages;

public abstract class Reply extends Message{
	private boolean Success;
	private String Note;
	public Reply(boolean success, String note) {
		// TODO Auto-generated constructor stub
		Success = success;
		Note = note;
	}
	public boolean isSuccess() {
		return Success;
	}
	public void setSuccess(boolean success) {
		Success = success;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
}
