package usu.cs.Sensys.SharedObjects;

public class Identity {
	private String ID;
	private String Pin;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getPin() {
		return Pin;
	}
	public void setPin(String pin) {
		Pin = pin;
	}
	public Identity(String iden, String pin){
		ID = iden;
		Pin = pin;
	}
}
