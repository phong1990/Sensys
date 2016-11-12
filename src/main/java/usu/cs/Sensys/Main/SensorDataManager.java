package usu.cs.Sensys.Main;

import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class SensorDataManager {
	private boolean OCCUPIED = false;
	private PublicEndpoint ReciepientEndpoint = null;
	private static SensorDataManager instance = null;

	public static SensorDataManager getInstance() {
		if (instance == null)
			instance = new SensorDataManager();
		return instance;
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
}
