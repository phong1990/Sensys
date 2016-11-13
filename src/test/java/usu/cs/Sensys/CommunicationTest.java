package usu.cs.Sensys;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import junit.framework.Assert;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Conversation.InitiatorHeartbeat;
import usu.cs.Sensys.Conversation.InitiatorLogin;
import usu.cs.Sensys.Conversation.InitiatorSensorGathering;
import usu.cs.Sensys.Messages.HeartbeatReply;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.SensorGatheringReply;
import usu.cs.Sensys.SharedObjects.GPSLocation;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;
import usu.cs.Sensys.util.ManualResetEvent;

public class CommunicationTest {

	@Test
	public void testCommunication() {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		PublicEndpoint serverEP = new PublicEndpoint();
		getServerParams(serverEP);
		communicationSubsystem.Start();
		login(communicationSubsystem, serverEP);
		heartbeat(communicationSubsystem, serverEP);
		sendData(communicationSubsystem, serverEP);
		if (communicationSubsystem != null) {
			communicationSubsystem.Stop();
			communicationSubsystem = null;
		}

	}

	private void sendData(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			SensorGatheringReply result = null;
			InitiatorSensorGathering msgConvo = communicationSubsystem
					.sendData(serverEP, new SensorData(0, ""));
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(10000 * 5);// wait for a worst case
													// scenario with 3
			// retries
			result = msgConvo.getResult();
			if (result != null) {
				System.out.println("Sending data Successfull to server ");
			} else {
				System.out.println("seems like no reply yet!");
			}
			Assert.assertEquals(true, result.isSuccess());
			Assert.assertEquals("", result.getNote());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void login(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			String testID = "ID";
			String testPin = "PIN";
			LoginReply result = null;
			InitiatorLogin loginConvo = communicationSubsystem.login(testID,
					testPin, serverEP.getHost(), serverEP.getPort());
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(10000 * 5);// wait for a worst case
													// scenario with 3
			// retries
			result = loginConvo.getResult();
			if (result != null) {
				System.out.println("Login Successfull on server at: "
						+ result.getEndPoint().getHost() + ":"
						+ result.getEndPoint().getPort());
			} else {
				System.out.println("seems like no reply yet!");
			}
			Assert.assertEquals(true, result.isSuccess());
			Assert.assertEquals("", result.getNote());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void heartbeat(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			HeartbeatReply result = null;
			InitiatorHeartbeat heartbeatConvo = communicationSubsystem
					.sendHeartbeat(serverEP.getHost(), serverEP.getPort(),
							new GPSLocation(0, 0, 0));
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			_somethingEnqueued.waitOne(10000 * 5);// wait for a worst case
													// scenario with 3
			// retries
			result = heartbeatConvo.getResult();
			if (result != null) {
				System.out.println("sending heartbeat Successfull on server ");
			} else {
				System.out.println("seems like no reply yet!");
			}
			Assert.assertEquals(true, result.isSuccess());
			Assert.assertEquals("", result.getNote());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getServerParams(PublicEndpoint serverEP) {
		BufferedReader br = null;
		try {
			System.out.println("---- SENSYS mobile app simulator (COMMUNICATION TESTER)----");
			br = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Server ip: ");
			String ip = br.readLine();
			System.out.println("Server port: ");
			int port = Integer.parseInt(br.readLine());
			serverEP.setHost(ip);
			serverEP.setPort(port);
			System.out.println("----- Logging in to server at ");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
