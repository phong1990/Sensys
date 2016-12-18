package usu.cs.Sensys.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;

public class PublicKeyManager {
	private static PublicKeyManager instance = null;
	private PrivateKey mPrivateKey = null;
	private PublicKey mPublicKey = null;
	private byte[] foreignPublicKey = null;

	public static PublicKeyManager getInstance() {
		if (instance == null)
			instance = new PublicKeyManager();
		return instance;
	}

	public void setKey(byte[] key) {
		foreignPublicKey = key;
	}

	public byte[] getPublicKey() {
		return mPublicKey.getEncoded();
	}

	public byte[] getForeginPublicKey() {
		return foreignPublicKey;
	}

	private PublicKeyManager() {

	}

	public void makeKey()
			throws NoSuchProviderException, NoSuchAlgorithmException {
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA", "SUN");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		keygen.initialize(1024, random);
		KeyPair pair = keygen.generateKeyPair();
		mPrivateKey = pair.getPrivate();
		mPublicKey = pair.getPublic();
	}

	public byte[] generateSignature(String message)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidKeyException, SignatureException, IOException {
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
		dsa.initSign(mPrivateKey);
		InputStream stream = new ByteArrayInputStream(
				message.getBytes(StandardCharsets.UTF_8));
		byte[] buffer = new byte[1024];
		int len;
		while ((len = stream.read(buffer)) >= 0) {
			dsa.update(buffer, 0, len);
		}

		stream.close();
		return dsa.sign();
	}

	public boolean verirySignature(byte[] publickey, byte[] signature,
			String message) throws IOException, InvalidKeyException,
			InvalidKeySpecException, NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException {
		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publickey);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
		PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
		Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
		sig.initVerify(pubKey);

		InputStream stream = new ByteArrayInputStream(
				message.getBytes(StandardCharsets.UTF_8));

		byte[] buffer = new byte[1024];
		int len;
		while (stream.available() != 0) {
			len = stream.read(buffer);
			sig.update(buffer, 0, len);
		}
		stream.close();
		return sig.verify(signature);

	}
}
