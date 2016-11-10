package usu.cs.Sensys.SharedObjects;

public class PublicEndpoint {
	private String Host = null;
	private int Port = 0;
	public PublicEndpoint(String host, int port) {
		// TODO Auto-generated constructor stub
		Host = host;
		Port = port;
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
}
