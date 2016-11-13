package usu.cs.Sensys.Main;

import java.util.ArrayList;
import java.util.List;

import usu.cs.Sensys.Conversation.BroadcastListener;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Messages.AvailableSensorRequest;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.devices.*;

public class SensorManager {
	private List<Sensor> SensorList = new ArrayList<>();
	private CommSubsystem commsub;
	private static SensorManager instance = null;
	private BroadcastListener sensorListener = new BroadcastListener();

	public static SensorManager getInstance() {
		if (instance == null)
			instance = new SensorManager();
		return instance;
	}

	public SensorManager() {
		// TODO Auto-generated constructor stub
		commsub = CommSubsystem.getInstance();
	}

	public List<Sensor> getSensorList() {
		return SensorList;
	}

	public void addNewSensor(int type, PublicEndpoint endpoint) {
		SensorList.add(new Sensor(type, endpoint));
	}

	public void startSensorDiscoverer(){
		sensorListener.Start(commsub.FindBestLocalIpAddress());
	}
	public void stopSensorDiscoverer(){
		sensorListener.Stop();
	}
	public void shakeHandWithSensor(AvailableSensorRequest sensorMessage) {
		commsub.handshakeWithSensor(sensorMessage.getEndPoint().getHost(),
				sensorMessage.getEndPoint().getPort());
	}
}
