package usu.cs.Sensys;

import org.junit.Test;

import junit.framework.Assert;
import usu.cs.Sensys.Conversation.ConversationQueue;
import usu.cs.Sensys.Conversation.QueueDictionary;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class DictionaryTest {
	@Test
	public void testDictionary() throws Exception {
		QueueDictionary dict = QueueDictionary.getInstance();
		MessageNumber id = MessageNumber.Create();
		ConversationQueue queue = dict.CreateQueue(id);
		MessageNumber otherid = MessageNumber.Create();
		Assert.assertEquals(null,dict.Lookup(otherid));
		Assert.assertEquals(queue,dict.Lookup(id));
		dict.CloseQueue(id);
		Assert.assertEquals(null,dict.Lookup(id));
	}
}
