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

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectMapper jsonMapper = new ObjectMapper();
				result = (Message) jsonMapper.readValue(bais, Message.class);
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
