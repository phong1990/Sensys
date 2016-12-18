package usu.cs.Sensys.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;

import junit.framework.Assert;
import usu.cs.Sensys.Conversation.CommProcessState;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Conversation.Conversation;
import usu.cs.Sensys.Conversation.ConversationFactory;
import usu.cs.Sensys.Conversation.InitiatorHeartbeat;
import usu.cs.Sensys.Conversation.InitiatorLogin;
import usu.cs.Sensys.Conversation.InitiatorSensorGathering;
import usu.cs.Sensys.Conversation.ResponderLogin;
import usu.cs.Sensys.Messages.HeartbeatReply;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.SensorGatheringReply;
import usu.cs.Sensys.SharedObjects.GPSLocation;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;
import usu.cs.Sensys.util.ManualResetEvent;

public class AppSimulator {
	static BufferedReader br = new BufferedReader(
			new InputStreamReader(System.in));

	public static void main(String[] args)
			throws UnknownHostException, Exception {
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		PublicEndpoint serverEP = new PublicEndpoint();
		getServerParams(serverEP);
		communicationSubsystem.Start();
		login(communicationSubsystem, serverEP);
		boolean loop = true;
		while (loop) {

			System.out.println("Type EXIT to exit ");
			String input = br.readLine();

			if ("exit".equalsIgnoreCase(input)) {
				System.out.println("Exit!");
				stopHeartbeat();
				communicationSubsystem.Stop();
				loop = false;
			}
			if ("pairing".equalsIgnoreCase(input)) {
				System.err.println("Start discovering sensors");
				SensorManager.getInstance().startSensorDiscoverer();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		sendData(communicationSubsystem, serverEP);
		if (communicationSubsystem != null) {
			communicationSubsystem.Stop();
			communicationSubsystem = null;
		}
		br.readLine();
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendData(CommSubsystem communicationSubsystem,
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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void login(CommSubsystem communicationSubsystem,
			PublicEndpoint serverEP) {
		try {
			String testID = "ID";
			String testPin = "PIN";
			LoginReply result = null;
			InitiatorLogin loginConvo = communicationSubsystem.login(testID,
					testPin, serverEP.getHost(), serverEP.getPort());
			ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
			int count = 0;
			while (result == null) {
				_somethingEnqueued.waitOne(100);// wait for a worst case
												// scenario with 3
				// retries
				result = loginConvo.getResult();
				count++;
				if (count == 500)
					break;
			}
			if (result != null) {
				System.out.println("Login Successfull on server at: "
						+ result.getEndPoint().getHost() + ":"
						+ result.getEndPoint().getPort());
				mServerEP = serverEP;
				startHeartbeat(communicationSubsystem);
			} else {
				System.out.println("seems like no reply yet!");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static PublicEndpoint mServerEP = null;
	private static Thread _receiveThread = null;
	private static boolean started = false;

	private static void stopHeartbeat() {
		started = false;

		try {
			_receiveThread.join(10000 * 2);
		} catch (InterruptedException e) {
		}
		_receiveThread = null;
	}

	private static void startHeartbeat(CommSubsystem communicationSubsystem) {
		if (started == false) {
			_receiveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (started) {
						HeartbeatReply result = null;
						InitiatorHeartbeat heartbeatConvo = CommSubsystem
								.getInstance()
								.sendHeartbeat(mServerEP.getHost(),
										mServerEP.getPort(),
										new GPSLocation(0, 0, 0));

						ManualResetEvent _somethingEnqueued = new ManualResetEvent(
								false);
						try {
							_somethingEnqueued.waitOne(9000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// retries
						result = heartbeatConvo.getResult();
						if (result != null) {
						} else {
							System.out.println(
									"seems like server is dead, disconnecting");
							started = false;
						}

					}
				}
			});
			_receiveThread.start();
			started = true;
		}

	}

	private static void getServerParams(PublicEndpoint serverEP) {
		try {
			System.out.println("---- SENSYS mobile app simulator ----");

			System.out.println("Server ip: ");
			String ip = br.readLine();
			System.out.println("Server port: ");
			int port = Integer.parseInt(br.readLine());
			serverEP.setHost(ip);
			serverEP.setPort(port);
			System.out.println("----- Logging in to server at "
					+ serverEP.getHost() + ":" + serverEP.getPort() + " -----");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
}
