package usu.cs.Sensys.SharedObjects;

public class SensorData {
	private String Data;

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}
	public SensorData(String dat){
		Data = dat;
	}
}
