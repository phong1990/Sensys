package usu.cs.Sensys.Messages;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import usu.cs.Sensys.util.PublicKeyManager;

public class MessageRequest extends Request {
	private boolean isEmmergency = false;
	private String message;
	private byte[] signature;

	@Override
	public String getMessageType() {
		// TODO Auto-generated method stub
		return this.getClass().getName();
	}

	public MessageRequest(boolean emmergency, String msg) {
		// TODO Auto-generated constructor stub
		isEmmergency = emmergency;
		message = msg;
		try {
			signature = PublicKeyManager.getInstance().generateSignature(msg);
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchProviderException | SignatureException
				| IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] getSignature() {
		return signature;
	}

	public String getMessage() {
		return message;
	}

	public boolean isEmmergency() {
		return isEmmergency;
	}

	public MessageRequest() {
		// TODO Auto-generated constructor stub
	}
}
