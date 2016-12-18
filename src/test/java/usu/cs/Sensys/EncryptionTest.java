package usu.cs.Sensys;


import org.junit.Test;

import junit.framework.Assert;
import usu.cs.Sensys.util.PublicKeyManager;

public class EncryptionTest {
	@Test
	public void testSignature_CorrectMessage() throws Exception {
		PublicKeyManager pkeyMan = PublicKeyManager.getInstance();
		pkeyMan.makeKey();

		String message = "This is a test message sent from phongvm90@gmail.com";
		byte[] encryptedSignature = pkeyMan.generateSignature(message);

		Assert.assertEquals(true, pkeyMan.verirySignature(
				pkeyMan.getPublicKey(), encryptedSignature, message));
	}

	@Test
	public void testSignature_ModifiedMessage() throws Exception {
		PublicKeyManager pkeyMan = PublicKeyManager.getInstance();
		pkeyMan.makeKey();
		String message = "This is a test message sent from phongvm90@gmail.com";
		byte[] encryptedSignature = pkeyMan.generateSignature(message);
		String ModifiedMessage = "This is a test message sent from phongvm90@gmail.com but with a surprise";

		Assert.assertEquals(false, pkeyMan.verirySignature(
				pkeyMan.getPublicKey(), encryptedSignature, ModifiedMessage));
	}
}
