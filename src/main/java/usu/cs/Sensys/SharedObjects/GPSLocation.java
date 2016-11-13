package usu.cs.Sensys.SharedObjects;

public class GPSLocation {
	private double Latitude;
	private double Longtitude;
	private long TimeStamp;
	public GPSLocation(double lat, double lon, long time) {
		// TODO Auto-generated constructor stub
		Latitude = lat;
		Longtitude = lon;
		TimeStamp = time;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public double getLongtitude() {
		return Longtitude;
	}
	public void setLongtitude(double longtitude) {
		Longtitude = longtitude;
	}
	public long getTimeStamp() {
		return TimeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		TimeStamp = timeStamp;
	}
	public GPSLocation() {
		// TODO Auto-generated constructor stub
	}
	
}
