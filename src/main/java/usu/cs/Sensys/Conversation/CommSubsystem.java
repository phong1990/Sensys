package usu.cs.Sensys.Conversation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Main.SensorDataManager;
import usu.cs.Sensys.Main.SensorManager;
import usu.cs.Sensys.Main.ServerLoginManager;
import usu.cs.Sensys.Messages.AvailableSensorRequest;
import usu.cs.Sensys.Messages.HeartbeatRequest;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.MessageRequest;
import usu.cs.Sensys.Messages.SensorGatheringRequest;
import usu.cs.Sensys.Messages.SensorHandshakeRequest;
import usu.cs.Sensys.SharedObjects.GPSLocation;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.SharedObjects.SensorData;
import usu.cs.Sensys.util.PublicKeyManager;

public class CommSubsystem {
	final static Logger logger = Logger.getLogger(CommSubsystem.class);
	private static CommSubsystem instance = null;
	public static final int GLOBAL_TIMEOUT = 2000;

	public static CommSubsystem getInstance() {
		if (instance == null)
			instance = new CommSubsystem();
		return instance;
	}

	private static final ExecutorService _threadPool = Executors
			.newCachedThreadPool();
	private UDPCommunicator _myUdpCommunicator;
	private InetAddress _bestAddress;
	private static final QueueDictionary _queueDictionary = QueueDictionary
			.getInstance();
	private static final ConversationFactory _conversationFactory = ConversationFactory
			.getInstance();
	private final CommProcessState _processState;
	private PublicEndpoint MyEndPoint = null;

	public PublicEndpoint getMyEndpoint() {
		return MyEndPoint;
	}

	public CommProcessState get_processState() {
		return _processState;
	}

	public CommSubsystem() {
		_processState = CommProcessState.getInstance();
		Initialize();
	}

	public static void handleReceivedMessage(Envelope env) {
		ConversationQueue queue = _queueDictionary
				.Lookup(env.getMsg().getConversationId());
		if (queue == null) {
			// new conversation, a responder conversation to be exact
			Conversation convo = _conversationFactory.CreateFromMessage(env);
			if (convo != null) {
				String typeOfMessage = env.getMsg().getMessageType();
				if (typeOfMessage.equals(LoginRequest.class.getName())) {
					handleLoginRequest(env, convo);
				}
				if (typeOfMessage.equals(MessageRequest.class.getName())) {
					handleMessageRequest(env, convo);

				}
				if (typeOfMessage
						.equals(SensorHandshakeRequest.class.getName())) {
					handleSensorHandshakeRequest(env, convo);

				}
				if (typeOfMessage
						.equals(AvailableSensorRequest.class.getName())) {
					handleAvailableSensorRequest(env);
				}
				if (typeOfMessage
						.equals(SensorGatheringRequest.class.getName())) {
					handleSensorGatheringRequest(env, convo);
				}

				if (typeOfMessage.equals(HeartbeatRequest.class.getName())) {

					handleHeartBeatRequest(env, convo);
				}
			}
		} else {
			// a reply
			queue.Enqueue(env);
		}

	}

	private static void handleAvailableSensorRequest(Envelope env) {
		if (SensorManager.getInstance().isAcceptingNewSensors()) {
			instance.handshakeWithSensor(env.getEndPoint().getHost(),
					env.getEndPoint().getPort());
		}
	}

	private static void handleHeartBeatRequest(Envelope env,
			Conversation convo) {
		ResponderHeartbeat specificConvo = (ResponderHeartbeat) convo;
		specificConvo.setIncomingEnv(env);
		ServerLoginManager.getInstance().resetTimer(env.getEndPoint());
		// put it in the threadpool and execute it
		_threadPool.execute(specificConvo);
	}

	private static void handleSensorHandshakeRequest(Envelope env,
			Conversation convo) {
		ResponderSensorHandshake specificConvo = (ResponderSensorHandshake) convo;
		specificConvo.setIncomingEnv(env);
		if (!SensorDataManager.getInstance().isOCCUPIED())
			SensorDataManager.getInstance().setOCCUPIED(true);
		// put it in the threadpool and execute it
		_threadPool.execute(specificConvo);
	}

	private static void handleSensorGatheringRequest(Envelope env,
			Conversation convo) {
		ResponderSensorGathering specificConvo = (ResponderSensorGathering) convo;
		specificConvo.setIncomingEnv(env);
		// put it in the threadpool and execute it
		_threadPool.execute(specificConvo);
	}

	private static void handleLoginRequest(Envelope env, Conversation convo) {
		// first add the user to our business list so we know he
		// will be sending data from now on
		ServerLoginManager.getInstance().addNewUser(env.getEndPoint());

		// now reply to him, saying we accept his login session
		ResponderLogin specificConvo = (ResponderLogin) convo;
		specificConvo.setIncomingEnv(env);
		// put it in the threadpool and execute it
		_threadPool.execute(specificConvo);

	}

	private static void handleMessageRequest(Envelope env, Conversation convo) {
		MessageRequest reqMsg = (MessageRequest) env.getMsg();
		try {
			if (PublicKeyManager.getInstance().verirySignature(
					PublicKeyManager.getInstance().getForeginPublicKey(),
					reqMsg.getSignature(), reqMsg.getMessage())) {
				System.out.println("Received a message from server: "
						+ reqMsg.getMessage());
				ResponderMessage specificConvo = (ResponderMessage) convo;
				specificConvo.setIncomingEnv(env);
				// put it in the threadpool and execute it
				_threadPool.execute(specificConvo);
			}
		} catch (InvalidKeyException | InvalidKeySpecException
				| NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void handleBroadcastedMessage(Envelope env) {
		SensorManager senMan = SensorManager.getInstance();

		String typeOfMessage = env.getMsg().getMessageType();
		if (typeOfMessage.equals(AvailableSensorRequest.class.getName())) {
			senMan.shakeHandWithSensor( env);
		}
	}

	/// <summary>
	/// This methods setup up all of the components in a CommSubsystem. Call
	/// this method
	/// sometime after setting the MinPort, MaxPort, and ConversationFactory
	/// </summary>
	public void Initialize() {
		// _conversationFactory.Initialize();

		_conversationFactory.setManagingSubsystem(this);
		_myUdpCommunicator = new UDPCommunicator();
		_myUdpCommunicator.setTimeout(GLOBAL_TIMEOUT);

		_processState.setState(CommProcessState.PossibleState.Initialized);
	}

	/// <summary>
	/// This method starts up all active components in the CommSubsystem. Call
	/// this method
	/// sometime after calling Initalize.
	/// </summary>
	public void Start() {
		logger.debug("Entering Start");
		MyEndPoint = _myUdpCommunicator.Start(FindBestLocalIpAddress());
		logger.debug("Leaving Start");
	}

	/// <summary>
	/// This method stops all of the active components of a CommSubsystem and
	/// release the
	/// releases (or at least allows them to be garabage collected. Once stop is
	/// called,
	/// a CommSubsystem cannot be restarted with setting it up from scratch.
	/// </summary>
	public void Stop() {
		logger.debug("Entering Stop");

		if (_myUdpCommunicator != null) {
			_myUdpCommunicator.Stop();
			_myUdpCommunicator = null;
		}
		_threadPool.shutdown();
		while (!_threadPool.isTerminated()) {
		}
		logger.debug("Leaving Stop");
		instance = null;
	}

	public <T extends Conversation> T CreateFromConversationType(
			String conversationClassName)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {
		return _conversationFactory
				.CreateFromConversationType(conversationClassName);
	}

	public ConversationQueue SetupConversationQueue(MessageNumber convId) {
		return _queueDictionary.CreateQueue(convId);
	}

	public void CloseConversationQueue(MessageNumber convId) {
		_queueDictionary.CloseQueue(convId);
	}

	public Error Send(Envelope env) throws UnsupportedEncodingException {
		return _myUdpCommunicator.Send(env);
	}

	public void ProcessIncomingEnvelope(Envelope env) {
		// Implementation not shown
	}

	public static String getAWSIp() throws Exception {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = null;
		try {
			in = new BufferedReader(
					new InputStreamReader(whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public InetAddress FindBestLocalIpAddress() {
		if (_bestAddress != null)
			return _bestAddress;

		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();

			while (networkInterfaces.hasMoreElements()) {

				////////
				NetworkInterface networkInterface = networkInterfaces
						.nextElement();

				byte[] hardwareAddress = networkInterface.getHardwareAddress();
				if (null == hardwareAddress || 0 == hardwareAddress.length
						|| (0 == hardwareAddress[0] && 0 == hardwareAddress[1]
								&& 0 == hardwareAddress[2]))
					continue;

				Enumeration<InetAddress> inetAddresses = networkInterface
						.getInetAddresses();

				if (inetAddresses.hasMoreElements())
					_bestAddress = inetAddresses.nextElement();

				break;
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.println("The best local IP address is: "
				+ _bestAddress.getHostAddress());
		return _bestAddress;
	}

	public InitiatorLogin login(String iden, String pin, String host,
			int port) {
		// TODO Auto-generated method stub
		InitiatorLogin loginConvo = new InitiatorLogin(iden, pin, host, port);
		return (InitiatorLogin) startConversation(loginConvo);
	}

	public InitiatorMessage sendMessage(String message, String host, int port) {
		// TODO Auto-generated method stub
		InitiatorMessage convo = new InitiatorMessage(host, port, false,
				message);
		return (InitiatorMessage) startConversation(convo);
	}

	public InitiatorSensorHandshake handshakeWithSensor(String host, int port) {
		// TODO Auto-generated method stub
		InitiatorSensorHandshake handshakeConvo = new InitiatorSensorHandshake(
				host, port);
		return (InitiatorSensorHandshake) startConversation(handshakeConvo);
	}

	public SensorDiscoveryBroadcast StartSensorBroadcast() {
		// TODO Auto-generated method stub
		SensorDiscoveryBroadcast broadcastConvo = new SensorDiscoveryBroadcast();
		return (SensorDiscoveryBroadcast) startConversation(broadcastConvo);

	}

	private Conversation startConversation(Conversation convo) {
		convo.setCommSubsystem(this);
		_threadPool.execute(convo);
		return convo;
	}

	public InitiatorSensorGathering sendData(PublicEndpoint reciepientEndpoint,
			SensorData sensorData) {
		// TODO Auto-generated method stub
		InitiatorSensorGathering dataConvo = new InitiatorSensorGathering(
				reciepientEndpoint.getHost(), reciepientEndpoint.getPort(),
				sensorData);
		return (InitiatorSensorGathering) startConversation(dataConvo);

	}

	public InitiatorHeartbeat sendHeartbeat(String host, int port,
			GPSLocation loc) {
		// TODO Auto-generated method stub
		InitiatorHeartbeat heartbeatConvo = new InitiatorHeartbeat(loc, host,
				port);
		return (InitiatorHeartbeat) startConversation(heartbeatConvo);
	}
}
