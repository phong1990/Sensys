package usu.cs.Sensys;

import org.junit.Test;

import junit.framework.Assert;
import usu.cs.Sensys.Conversation.Conversation;
import usu.cs.Sensys.Conversation.ConversationFactory;
import usu.cs.Sensys.Conversation.Envelope;
import usu.cs.Sensys.Conversation.InitiatorLogin;
import usu.cs.Sensys.Conversation.ResponderHeartbeat;
import usu.cs.Sensys.Conversation.ResponderLogin;
import usu.cs.Sensys.Conversation.ResponderMessage;
import usu.cs.Sensys.Conversation.ResponderSensorGathering;
import usu.cs.Sensys.Conversation.ResponderSensorHandshake;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class FactoryTest {
	@Test
	public void testConversationCreationFromType() throws Exception {
		ConversationFactory factory = ConversationFactory.getInstance();
		Conversation loginConvo = factory
				.CreateFromConversationType(ResponderLogin.class.getName());
		Assert.assertEquals(loginConvo.getClass().getName(),
				ResponderLogin.class.getName());
		Conversation hbConvo = factory
				.CreateFromConversationType(ResponderHeartbeat.class.getName());
		Assert.assertEquals(hbConvo.getClass().getName(),
				ResponderHeartbeat.class.getName());
		Conversation msgConvo = factory
				.CreateFromConversationType(ResponderMessage.class.getName());
		Assert.assertEquals(msgConvo.getClass().getName(),
				ResponderMessage.class.getName());
		Conversation sensorConvo = factory.CreateFromConversationType(
				ResponderSensorGathering.class.getName());
		Assert.assertEquals(sensorConvo.getClass().getName(),
				ResponderSensorGathering.class.getName());
		Conversation hsConvo = factory.CreateFromConversationType(
				ResponderSensorHandshake.class.getName());
		Assert.assertEquals(hsConvo.getClass().getName(),
				ResponderSensorHandshake.class.getName());
	}

	@Test
	public void testConversationCreationFromMessage() throws Exception {
		ConversationFactory factory = ConversationFactory.getInstance();
		LoginRequest sentLoginMessage = new LoginRequest();
		Envelope env = new Envelope(sentLoginMessage, null);

		Conversation conv = factory.CreateFromMessage(env);

		Assert.assertEquals(conv.getClass().getName(),
				ResponderLogin.class.getName());
	}
}
