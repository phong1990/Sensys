package usu.cs.Sensys.devices;

import usu.cs.Sensys.SharedObjects.PublicEndpoint;


// future implementation would include data related information
public class Sensor {
	private int SensorType;// future implementation
	private PublicEndpoint EndPoint;

	public Sensor(int type, PublicEndpoint ep) {
		// TODO Auto-generated constructor stub
		SensorType = type;
		EndPoint = ep;
	}

	public int getSensorType() {
		return SensorType;
	}

	public void setSensorType(int sensorType) {
		SensorType = sensorType;
	}

	public PublicEndpoint getEndPoint() {
		return EndPoint;
	}

	public void setEndPoint(PublicEndpoint endPoint) {
		EndPoint = endPoint;
	}
	
}
