package usu.cs.Sensys.Messages;

import usu.cs.Sensys.SharedObjects.Identity;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class LoginRequest extends Request{
	private Identity Identity;
	public Identity getIdentity() {
		return Identity;
	}
	public void setIdentity(Identity identity, PublicEndpoint endpoint) {
		Identity = identity;
		MyEndPoint = endpoint;
	}
	public LoginRequest(Identity id, PublicEndpoint myendpoint) {
		// TODO Auto-generated constructor stub
		Identity = id;
	}
	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

}
