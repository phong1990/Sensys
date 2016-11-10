package usu.cs.Sensys.Conversation;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class CommSubsystem {
	final static Logger logger = Logger.getLogger(CommSubsystem.class);

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

	public CommSubsystem(CommProcessState processState) {
		_processState = processState;
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
					// first add the user to our business list so we know he
					// will be sending data from now on
					// <unimplemented>

					// now reply to him, saying we accept his login session
					ResponderLogin loginConvo = (ResponderLogin) convo;
					loginConvo.setIncomingEnv(env);
					// put it in the threadpool and execute it
					_threadPool.execute(loginConvo);
				}
			}
		} else {
			// a reply
			queue.Enqueue(env);
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

	private InetAddress FindBestLocalIpAddress() {
		if (_bestAddress != null)
			return _bestAddress;

		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();

			while (networkInterfaces.hasMoreElements()) {

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
		loginConvo.setCommSubsystem(this);
		_threadPool.execute(loginConvo);
		return loginConvo;
	}
}
