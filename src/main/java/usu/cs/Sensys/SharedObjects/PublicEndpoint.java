package usu.cs.Sensys.SharedObjects;


public class PublicEndpoint {
	private String Host = null;
	private int Port = 0;
	public PublicEndpoint(String host, int port) {
		// TODO Auto-generated constructor stub
		Host = host;
		Port = port;
	}
	public PublicEndpoint() {
		// TODO Auto-generated constructor stub
	}
	public String getHost() {
		return Host;
	}
	public void setHost(String host) {
		Host = host;
	}
	public int getPort() {
		return Port;
	}
	public void setPort(int port) {
		Port = port;
	}
	public String toString(){
		return Host+":"+Port;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof PublicEndpoint) {
			PublicEndpoint w = (PublicEndpoint) obj;
			return Host.equals(w.Host) && Port == w.Port;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Host.hashCode();
	}
}
