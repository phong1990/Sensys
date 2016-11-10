package usu.cs.Sensys.Messages;

import usu.cs.Sensys.SharedObjects.SensorData;

public class SensorGatheringRequest extends Request{
	private int SensorId;
	public int getSensorId() {
		return SensorId;
	}
	public void setSensorId(int sensorId) {
		SensorId = sensorId;
	}
	public SensorData getSensorData() {
		return SensorData;
	}
	public void setSensorData(SensorData sensorData) {
		SensorData = sensorData;
	}
	public long getLastReadingTime() {
		return LastReadingTime;
	}
	public void setLastReadingTime(long lastReadingTime) {
		LastReadingTime = lastReadingTime;
	}
	private SensorData SensorData;
	private long LastReadingTime;
	public SensorGatheringRequest(int id, SensorData dat, long time) {
		// TODO Auto-generated constructor stub
		SensorId = id;
		SensorData = dat;
		LastReadingTime = time;
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

}
