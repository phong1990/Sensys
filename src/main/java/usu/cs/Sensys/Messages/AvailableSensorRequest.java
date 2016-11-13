package usu.cs.Sensys.Messages;

public class AvailableSensorRequest extends Request {
	private int SensorID;
	private String SensorName;

	public AvailableSensorRequest(int id, String name) {
		// TODO Auto-generated constructor stub
		SensorID = id;
		SensorName = name;
	}

	public int getSensorID() {
		return SensorID;
	}

	public void setSensorID(int sensorID) {
		SensorID = sensorID;
	}

	public String getSensorName() {
		return SensorName;
	}

	public void setSensorName(String sensorName) {
		SensorName = sensorName;
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return AvailableSensorRequest.class.getName();
	}

	public AvailableSensorRequest() {
		// TODO Auto-generated constructor stub
	}
}
