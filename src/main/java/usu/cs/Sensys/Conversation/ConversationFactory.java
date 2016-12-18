package usu.cs.Sensys.Conversation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import usu.cs.Sensys.Messages.AvailableSensorRequest;
import usu.cs.Sensys.Messages.HeartbeatRequest;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.MessageRequest;
import usu.cs.Sensys.Messages.SensorGatheringRequest;
import usu.cs.Sensys.Messages.SensorHandshakeRequest;

public class ConversationFactory {
	private static final Map<String, String> _typeMappings = new HashMap<>();
	private static int DefaultMaxRetries = 0;
	private static int DefaultTimeout = 0;
	private CommSubsystem ManagingSubsystem;
	private static ConversationFactory instance = null;
	public static ConversationFactory getInstance(){
		if(instance == null)
			instance = new ConversationFactory() ;
		return instance;
	}
	public CommSubsystem getManagingSubsystem() {
		return ManagingSubsystem;
	}

	public void setManagingSubsystem(CommSubsystem managingSubsystem) {
		ManagingSubsystem = managingSubsystem;
	}

	final static Logger logger = Logger.getLogger(ConversationFactory.class);

	private ConversationFactory() {
		_typeMappings.put(LoginRequest.class.getName(),
				ResponderLogin.class.getName());
		_typeMappings.put(HeartbeatRequest.class.getName(),
				ResponderHeartbeat.class.getName());
		_typeMappings.put(SensorHandshakeRequest.class.getName(),
				ResponderSensorHandshake.class.getName());
		_typeMappings.put(SensorGatheringRequest.class.getName(),
				ResponderSensorGathering.class.getName());
		_typeMappings.put(MessageRequest.class.getName(),
				ResponderMessage.class.getName());
		_typeMappings.put(LoginRequest.class.getName(),
				ResponderLogin.class.getName());
		_typeMappings.put(LoginRequest.class.getName(),
				ResponderLogin.class.getName());
		
		
		
//		_typeMappings.put(MessageType.EndSensorsRequest,
//				"ResponderEndSensorsConversation");
//		_typeMappings.put(MessageType.HeartBeatRequest,
//				"ResponderHeartBeatConversation");
//		_typeMappings.put(MessageType.LoginRequest,
//				"ResponderLoginConversation");
//		_typeMappings.put(MessageType.MessageRequest,
//				"ResponderMessageConversation");
//		_typeMappings.put(MessageType.SensorGatheringRequest,
//				"ResponderSensorGatheringConversation");
//		_typeMappings.put(MessageType.SensorHandshakeRequest,
//				"ResponderSensorHandshakeConversation");
//		_typeMappings.put(MessageType.LogoutRequest,
//				"ResponderLogoutConversation");
	}

	public Conversation CreateFromMessage(Envelope envelope) {
		// switch(envelope.getMsg().getMessageType()){
		// case AvailableSensorRequest:
		// break;
		// case EndSensorsRequest:
		// break;
		// case HeartBeatReply:
		// break;
		// case HeartBeatRequest:
		// break;
		// case LoginReply:
		// break;
		// case LoginRequest:
		// break;
		// case MessageReply:
		// break;
		// case MessageRequest:
		// break;
		// case SensorGatheringReply:
		// break;
		// case SensorGatheringRequest:
		// break;
		// case SensorHandshakeReply:
		// break;
		// case SensorHandshakeRequest:
		// break;
		// case LogoutRequest:
		// break;
		// case LogoutReply:
		// default:
		// return null;
		// }
		Conversation conversation = null;

		String messageType = envelope.getMsg().getMessageType();

		if (messageType != null && _typeMappings.containsKey(messageType))
			try {
				conversation = CreateResponderConversation(
						_typeMappings.get(messageType), envelope);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				// TODO Auto-generated catch block

				logger.error(
						"Error in creating a responder conversation: "+e.getClass().getName()+":"
								+ e.getMessage());
			}

		return conversation;
	}

	protected ResponderConversation CreateResponderConversation(
			String conversationClassName, Envelope envelope)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {
		ResponderConversation conversation = null;
		if (conversationClassName != null) {
			conversation = (ResponderConversation) Class
					.forName(conversationClassName).getConstructor()
					.newInstance();

			if (conversation != null) {
				conversation.setCommSubsystem(ManagingSubsystem);
				conversation.setMaxRetries(DefaultMaxRetries);
				conversation.setTimeOut(DefaultTimeout);
				conversation.setIncomingEnv(envelope);

				// conversation.CommSubsystem = ManagingSubsystem;
				// conversation.PreExecuteAction = PreExecuteAction;
				// conversation.PostExecuteAction = PostExecuteAction;
			}
		}
		return conversation;
	}

	// only for responders
	public <T extends Conversation> T CreateFromConversationType(
			String conversationClassName)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException {
		T conversation = (T) Class.forName(conversationClassName)
				.getConstructor().newInstance();

		if (conversation != null) {
			conversation.setCommSubsystem(ManagingSubsystem);
			conversation.setMaxRetries(DefaultMaxRetries);
			conversation.setTimeOut(DefaultTimeout);
		}

		// CommSubsystem = ManagingSubsystem,
		// PreExecuteAction = PreExecuteAction,
		// PostExecuteAction = PostExecuteAction

		return conversation;
	}
}
