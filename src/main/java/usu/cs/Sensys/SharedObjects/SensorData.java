package usu.cs.Sensys.SharedObjects;

public class SensorData {
	private int sensorType;
	private String Data;

	public String getData() {
		return Data;
	}
	
	public int getSensorType() {
		return sensorType;
	}

	public void setSensorType(int sensorType) {
		this.sensorType = sensorType;
	}

	public void setData(String data) {
		Data = data;
	}
	public SensorData(int type , String dat){
		Data = dat;
		sensorType = type;
	}
	public SensorData() {
		// TODO Auto-generated constructor stub
	}
}
