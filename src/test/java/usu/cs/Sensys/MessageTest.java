package usu.cs.Sensys;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.LoginRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class MessageTest extends TestCase {
	@Test
	public void testEncoderDecoder() throws Exception {
		String testMessage1 = "test1";
		PublicEndpoint testPublicEndpoint = new PublicEndpoint("255.255.255.255", 8888);
		LoginReply sentLoginMessage = new LoginReply(true,testMessage1,testPublicEndpoint);
		byte[] bytes = sentLoginMessage.Encode();
		LoginReply receivedLoginMessage = (LoginReply) Message.decode(bytes);
		Assert.assertEquals(sentLoginMessage.getNote(), receivedLoginMessage.getNote());
		Assert.assertEquals(sentLoginMessage.isSuccess(), receivedLoginMessage.isSuccess());
		Assert.assertEquals(sentLoginMessage.getEndPoint().getHost(), receivedLoginMessage.getEndPoint().getHost());
		Assert.assertEquals(sentLoginMessage.getEndPoint().getPort(), receivedLoginMessage.getEndPoint().getPort());
	}
}
