package usu.cs.Sensys.Messages;

import org.apache.commons.codec.binary.Base64;

import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class LoginReply extends Reply {
	public String mKey = null;

	public LoginReply(boolean success, String note, PublicEndpoint endpoint,
			byte[] pkey) {
		super(success, note);
		// TODO Auto-generated constructor stub
		MyEndPoint = endpoint;
		mKey = Base64.encodeBase64String(pkey);
	}

	public byte[] getPublicKey() {
		return Base64.decodeBase64(mKey);
	}

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	public LoginReply() {
		// TODO Auto-generated constructor stub
	}
}
