package usu.cs.Sensys.Main;

import java.util.ArrayList;
import java.util.List;

import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.devices.*;

public class SensorManager {
	private List<Sensor> SensorList = new ArrayList<>();
	private static SensorManager instance = null;

	public static SensorManager getInstance() {
		if (instance == null)
			instance = new SensorManager();
		return instance;
	}

	public SensorManager() {
		// TODO Auto-generated constructor stub
	}

	public List<Sensor> getSensorList() {
		return SensorList;
	}

	public void addNewSensor(int type, PublicEndpoint endpoint) {

	}
}
