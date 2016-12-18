package usu.cs.Sensys;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Conversation.ConversationQueue;
import usu.cs.Sensys.Conversation.Envelope;
import usu.cs.Sensys.Conversation.InitiatorHeartbeat;
import usu.cs.Sensys.Conversation.InitiatorLogin;
import usu.cs.Sensys.Conversation.InitiatorSensorGathering;
import usu.cs.Sensys.Conversation.QueueDictionary;
import usu.cs.Sensys.Conversation.UDPCommunicator;
import usu.cs.Sensys.Messages.HeartbeatReply;
import usu.cs.Sensys.Messages.HeartbeatRequest;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.SensorGatheringReply;
import usu.cs.Sensys.Messages.SensorGatheringRequest;
import usu.cs.Sensys.SharedObjects.GPSLocation;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;
import usu.cs.Sensys.util.ManualResetEvent;
import usu.cs.Sensys.util.PublicKeyManager;

public class ReliableCommunication {
	private int port = 10010;
	private int Timeout = 10000;
	private int validMessageLoginCount = 0;
	private int validMessageHBCount = 0;
	private int validMessageDataCount = 0;
	private boolean MIDDLECASE = false;

	@AfterClass
	public static void tearDown() {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		if (communicationSubsystem != null) {
			communicationSubsystem.Stop();
			communicationSubsystem = null;
		}
	}

	public void HandleReceivedMessage(Envelope env) {
		Message msg = env.getMsg();
		String type = msg.getMessageType();
		if (type.equals(LoginRequest.class.getName()))
			validMessageLoginCount++;
		if (type.equals(HeartbeatRequest.class.getName()))
			validMessageHBCount++;
		if (type.equals(SensorGatheringRequest.class.getName()))
			validMessageDataCount++;
		if (MIDDLECASE) {
			if (validMessageLoginCount == 2)
				sendLoginReply(env);
		}
	}

	public void sendLoginReply(Envelope env) {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		try {
			PublicKeyManager.getInstance().makeKey();
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Message msg = new LoginReply(true, "",
				communicationSubsystem.getMyEndpoint(),
				PublicKeyManager.getInstance().getPublicKey());
		msg.setConversationId(env.getMsg().getConversationId());
		msg.setMessageNr(MessageNumber.Create());
		Envelope envelop = new Envelope(msg, env.getEndPoint());
		try {
			communicationSubsystem.Send(envelop);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testSendingWithRetries() throws Exception {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		MockReceiver receiver = new MockReceiver();
		receiver.Start(communicationSubsystem.FindBestLocalIpAddress());
		PublicEndpoint serverEP = new PublicEndpoint();

		serverEP.setHost(communicationSubsystem.FindBestLocalIpAddress()
				.getHostAddress());
		serverEP.setPort(port);
		communicationSubsystem.Start();
		login(communicationSubsystem, serverEP);
		heartbeat(communicationSubsystem, serverEP);
		sendData(communicationSubsystem, serverEP);
		receiver.Stop();
	}

	@Test
	public void testSendingWithRetries_MIDDLECASE() throws Exception {
		MIDDLECASE = true;
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		MockReceiver receiver = new MockReceiver();
		receiver.Start(communicationSubsystem.FindBestLocalIpAddress());
		PublicEndpoint serverEP = new PublicEndpoint();

		serverEP.setHost(communicationSubsystem.FindBestLocalIpAddress()
				.getHostAddress());
		serverEP.setPort(port);
		communicationSubsystem.Start();

		try {
			String testID = "ID";
			String testPin = "PIN";
			communicationSubsystem.login(testID, testPin, serverEP.getHost(),
					serverEP.getPort());
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(Timeout * 5);// wait for a worst case
													// scenario with 3

			Assert.assertEquals(2, validMessageLoginCount);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

			Assert.assertEquals(4, validMessageLoginCount);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendData(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			communicationSubsystem.sendData(serverEP, new SensorData(0, ""));
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(10000 * 5);// wait for a worst case
													// scenario with 3

			Assert.assertEquals(4, validMessageDataCount);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void heartbeat(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			communicationSubsystem.sendHeartbeat(serverEP.getHost(),
					serverEP.getPort(), new GPSLocation(0, 0, 0));
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(10000 * 5);// wait for a worst case
													// scenario with 3

			Assert.assertEquals(4, validMessageHBCount);
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
					HandleReceivedMessage(env);
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
