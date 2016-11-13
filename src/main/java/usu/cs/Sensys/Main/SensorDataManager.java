package usu.cs.Sensys.Main;

import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;

public class SensorDataManager {
	private boolean OCCUPIED = false;
	private int SensorType = 0;
	private PublicEndpoint ReciepientEndpoint = null;
	private static SensorDataManager instance = null;
	private CommSubsystem commSub = CommSubsystem.getInstance();
	public static SensorDataManager getInstance() {
		if (instance == null)
			instance = new SensorDataManager();
		return instance;
	}
	
	public int getSensorType() {
		return SensorType;
	}

	public void setSensorType(int sensorType) {
		SensorType = sensorType;
	}

	private SensorDataManager() {
		// TODO Auto-generated constructor stub
	}

	public boolean isOCCUPIED() {
		return OCCUPIED;
	}

	public void setOCCUPIED(boolean oCCUPIED) {
		OCCUPIED = oCCUPIED;
	}

	public PublicEndpoint getReceipientEndpoint() {
		return ReciepientEndpoint;
	}

	public void setReciepientEndpoint(PublicEndpoint reciepientEndpoint) {
		// deep copy to make sure
		if (reciepientEndpoint != null)
			ReciepientEndpoint = new PublicEndpoint(
					reciepientEndpoint.getHost(), reciepientEndpoint.getPort());
		else
			ReciepientEndpoint = null;
	}
	public void startSensorBroadcast(){
		commSub.StartSensorBroadcast();
	}
	public void sendData(){
		// send dummy data
		commSub.sendData(ReciepientEndpoint,new SensorData(SensorType, "1234"));
	}
}
