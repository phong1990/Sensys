package usu.cs.Sensys;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

import org.junit.AfterClass;
import org.junit.Test;

import junit.framework.Assert;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Conversation.Envelope;
import usu.cs.Sensys.Messages.HeartbeatRequest;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.MessageRequest;
import usu.cs.Sensys.Messages.SensorGatheringRequest;
import usu.cs.Sensys.SharedObjects.GPSLocation;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;
import usu.cs.Sensys.util.ManualResetEvent;
import usu.cs.Sensys.util.PublicKeyManager;

public class SecuredCommunicationTest {
	private int port = 10010;
	private int Timeout = 10000;
	private boolean isOriginalMessage = false;
	private String message = "This is a test message sent from phongvm90@gmail.com";
	private byte[] returnedSig = null;

	@AfterClass
	public static void tearDown() {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		if (communicationSubsystem != null) {
			communicationSubsystem.Stop();
			communicationSubsystem = null;
		}
	}

	public void HandleReceivedMessage(Envelope env, byte[] key) {
		Message msg = env.getMsg();
		String type = msg.getMessageType();
		if (type.equals(LoginRequest.class.getName())) {
			Envelope envelop = null;
			MessageNumber messageID = MessageNumber.Create();
			Message msgReply = new LoginReply(true, "note",
					CommSubsystem.getInstance().getMyEndpoint(), key);
			msgReply.setConversationId(env.getMsg().getConversationId());
			msgReply.setMessageNr(messageID);
			envelop = new Envelope(msgReply, env.getEndPoint());
			// send envelop

			try {
				System.out.println("SENDING REPLY");
				CommSubsystem.getInstance().Send(envelop);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (type.equals(MessageRequest.class.getName())) {
			MessageRequest typedMsg = (MessageRequest) msg;
			returnedSig = typedMsg.getSignature();

		}

	}

	@Test
	public void testSendingSecuredMessage_original() throws Exception {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		MockReceiver receiver = new MockReceiver();
		receiver.Start(communicationSubsystem.FindBestLocalIpAddress());
		PublicEndpoint serverEP = new PublicEndpoint();

		serverEP.setHost(communicationSubsystem.FindBestLocalIpAddress()
				.getHostAddress());
		serverEP.setPort(port);
		communicationSubsystem.Start();
		login(communicationSubsystem, serverEP);
		sendMessage(communicationSubsystem, serverEP);
		PublicKeyManager man = PublicKeyManager.getInstance();
		Assert.assertEquals(true, man.verirySignature(man.getForeginPublicKey(),
				returnedSig, message));
		receiver.Stop();
	}

	@Test
	public void testSendingSecuredMessage_modified() throws Exception {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		MockReceiver receiver = new MockReceiver();
		receiver.Start(communicationSubsystem.FindBestLocalIpAddress());
		PublicEndpoint serverEP = new PublicEndpoint();

		serverEP.setHost(communicationSubsystem.FindBestLocalIpAddress()
				.getHostAddress());
		serverEP.setPort(port);
		communicationSubsystem.Start();
		login(communicationSubsystem, serverEP);
		sendMessage(communicationSubsystem, serverEP);
		PublicKeyManager man = PublicKeyManager.getInstance();
		Assert.assertEquals(false, man.verirySignature(man.getForeginPublicKey(),
				returnedSig, message+"injected hell loose"));
		receiver.Stop();
	}

	private void login(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			String testID = "ID";
			String testPin = "PIN";
			communicationSubsystem.login(testID, testPin, serverEP.getHost(),
					serverEP.getPort());
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(Timeout * 5);// wait for a worst case
													// scenario with 3
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMessage(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			communicationSubsystem.sendMessage(message, serverEP.getHost(),
					serverEP.getPort());
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(10000 * 5);// wait for a worst case
													// scenario with 3
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class MockReceiver {
		private boolean _started;
		private DatagramSocket _myUdpClient = null;
		private Thread _receiveThread = null;

		public PublicEndpoint Start(InetAddress IPAddress) {
			PublicEndpoint endpoint = null;

			try {
				_myUdpClient = new DatagramSocket(port, IPAddress);
				_started = true;
			} catch (SocketException e) {
			}

			_receiveThread = new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						PublicKeyManager.getInstance().makeKey();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchProviderException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Receive();
				}
			});
			_receiveThread.start();

			endpoint = new PublicEndpoint(IPAddress.getHostAddress(), port);

			return endpoint;
		}

		public void Stop() {
			_started = false;

			try {
				_receiveThread.join(Timeout * 2);
			} catch (InterruptedException e) {
			}
			_receiveThread = null;

			if (_myUdpClient != null) {
				_myUdpClient.close();
				_myUdpClient = null;
			}
		}

		private void Receive() {
			while (_started) {
				Envelope env = ReceiveOne();
				// process the incoming envelope
				if (env != null) {
					HandleReceivedMessage(env,
							PublicKeyManager.getInstance().getPublicKey());
				}
				// if (env != null)
				// EnvelopeHandler?.Invoke(env);
			}
		}

		private Envelope ReceiveOne() {
			Envelope result = null;
			DatagramPacket receivePacket = receivePacket(Timeout);

			if (receivePacket != null && receivePacket.getData().length > 0) {
				byte[] receivedBytes = receivePacket.getData();
				InetAddress ep = receivePacket.getAddress();
				PublicEndpoint sendersEndPoint = new PublicEndpoint(
						ep.getHostAddress(), receivePacket.getPort());
				Message message = Message.decode(receivedBytes);
				if (message != null) {
					result = new Envelope(message, sendersEndPoint);
					System.out.println(("Just received message, Nr="
							+ result.getMsg().getMessageNr() + ", Conv="
							+ result.getMsg().getConversationId() + ", Type="
							+ result.getMsg().getMessageType() + ", From="
							+ result.getEndPoint().toString()));
				} else {
				}
			}

			return result;
		}

		private DatagramPacket receivePacket(int timeout) {
			DatagramPacket receivePacket = null;
			if (_myUdpClient != null) {
				try {
					_myUdpClient.setSoTimeout(timeout);
				} catch (SocketException e) {
				}

				try {

					byte[] buffer = new byte[2048];
					receivePacket = new DatagramPacket(buffer, buffer.length);
					_myUdpClient.receive(receivePacket);
				} catch (SocketTimeoutException err) {
					receivePacket = null;
				} catch (Exception err) {
					receivePacket = null;
				}
			}
			return receivePacket;
		}

	}
}
