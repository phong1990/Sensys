package usu.cs.Sensys.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import usu.cs.Sensys.SharedObjects.Identity;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public abstract class Message {
	final static Logger logger = Logger.getLogger(Message.class);
	protected MessageNumber ConversationId;
	protected MessageNumber MessageNr;
	protected PublicEndpoint MyEndPoint;

	public PublicEndpoint getEndPoint() {
		return MyEndPoint;
	}

	public void setEndPoint(PublicEndpoint endPoint) {
		MyEndPoint = endPoint;
	}

	private static final String[] MESSAGE_TYPES = {
			AvailableSensorRequest.class.getName(),
			EndSensorsRequest.class.getName(), HeartbeatReply.class.getName(),
			HeartbeatRequest.class.getName(), LoginReply.class.getName(),
			LoginRequest.class.getName(), MessageReply.class.getName(),
			MessageRequest.class.getName(),
			SensorGatheringReply.class.getName(),
			SensorGatheringRequest.class.getName(),
			SensorHandshakeReply.class.getName(),
			SensorHandshakeRequest.class.getName(),
			LogoutRequest.class.getName(), LogoutReply.class.getName() };
	public static final Set<String> AllowedMessageType = new HashSet<>(
			Arrays.asList(MESSAGE_TYPES));

	public abstract String getMessageType();

	public MessageNumber getConversationId() {
		return ConversationId;
	}

	public void setConversationId(MessageNumber conversationId) {
		ConversationId = conversationId;
	}

	public MessageNumber getMessageNr() {
		return MessageNr;
	}

	public void setMessageNr(MessageNumber messageNr) {
		MessageNr = messageNr;
	}

	public static Message decode(byte[] bytes) {
		Message result = null;
		if (bytes != null) {
			try {
				// dirty fix for getting class name
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				JsonReader reader = Json.createReader(bais);
				JsonObject jsonobj = reader.readObject();
				String messageType = jsonobj.getString("messageType");
//				if (messageType.equals(LoginRequest.class.getName())) {
//					
//				}
//				if (messageType.equals(LoginReply.class.getName())) {
//
//				}
				ObjectMapper jsonMapper = new ObjectMapper();
				jsonMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
	                     false);
				bais.reset();
				result = (Message) jsonMapper.readValue(bais,
						Class.forName(messageType));
			} catch (Exception ex) {
				logger.warn("Except warning in decoding a message: {0} - "
						+ ex.getMessage());
			}
		}
		return result;
	}

	public byte[] Encode() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		try {
			mapper.writeValue(baos, this);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			logger.warn("Except warning in encoding a message: {0} - "
					+ e.getMessage());
			return null;
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			logger.warn("Except warning in encoding a message: {0} - "
					+ e.getMessage());
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn("Except warning in encoding a message: {0} - "
					+ e.getMessage());
			return null;
		}

		return baos.toByteArray();
	}
}
