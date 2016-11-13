package usu.cs.Sensys.Messages;

import usu.cs.Sensys.SharedObjects.GPSLocation;

public class HeartbeatRequest extends Request {
	private GPSLocation GPSLocation;

	public HeartbeatRequest(GPSLocation loc) {
		// TODO Auto-generated constructor stub
		GPSLocation = loc;
	}

	public GPSLocation getGPSLocation() {
		return GPSLocation;
	}

	public void setGPSLocation(GPSLocation gPSLocation) {
		GPSLocation = gPSLocation;
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	public HeartbeatRequest() {
		// TODO Auto-generated constructor stub
	}
}
