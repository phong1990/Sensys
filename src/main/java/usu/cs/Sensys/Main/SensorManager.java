package usu.cs.Sensys.Main;

import java.util.ArrayList;
import java.util.List;

import usu.cs.Sensys.Conversation.BroadcastListener;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Conversation.Envelope;
import usu.cs.Sensys.Messages.AvailableSensorRequest;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.devices.*;

public class SensorManager {
	private List<Sensor> SensorList = new ArrayList<>();
	private CommSubsystem commsub;
	private static SensorManager instance = null;
	private BroadcastListener sensorListener = new BroadcastListener();
	private boolean isAcceptingNewSensors = false;

	private void startAcceptingNewSensors() {
		isAcceptingNewSensors = true;
	}

	public boolean isAcceptingNewSensors() {
		return isAcceptingNewSensors;
	}

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
		System.err.println("Added a new sensor from port" + endpoint.getPort());
		SensorList.add(new Sensor(type, endpoint));
	}

	public void startSensorDiscoverer() {
		startAcceptingNewSensors();
		sensorListener.Start(commsub.FindBestLocalIpAddress());
	}

	public void stopSensorDiscoverer() {
		sensorListener.Stop();
	}

	public void shakeHandWithSensor(Envelope env) {
		commsub.handshakeWithSensor(env.getEndPoint().getHost(),
				env.getEndPoint().getPort());
	}
}
