package usu.cs.Sensys;

import org.junit.Test;

import junit.framework.Assert;
import usu.cs.Sensys.Conversation.ConversationQueue;
import usu.cs.Sensys.Conversation.Envelope;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.util.PublicKeyManager;

public class QueueTest {
	@Test
	public void testQueue() throws Exception {
		String testMessage1 = "test1";
		PublicEndpoint testPublicEndpoint = new PublicEndpoint(
				"255.255.255.255", 8888);
		PublicKeyManager.getInstance().makeKey();
		LoginReply sentLoginMessage = new LoginReply(true, testMessage1,
				testPublicEndpoint,PublicKeyManager.getInstance().getPublicKey());
		Envelope env = new Envelope(sentLoginMessage, testPublicEndpoint);
		ConversationQueue queue = new ConversationQueue(new MessageNumber());
		Envelope result = null;
		Enqueuer enqueuerThread = new Enqueuer(queue, env);
		(new Thread(enqueuerThread)).start();
		result = queue.Dequeue(200);
		Assert.assertEquals(null, result);
		result = queue.Dequeue(1500);
		Assert.assertEquals(env, result);
	}

	public static class Enqueuer implements Runnable {
		ConversationQueue queueInstance = null;
		Envelope env = null;

		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			queueInstance.Enqueue(env);
		}

		public Enqueuer(ConversationQueue que, Envelope e) {
			// TODO Auto-generated constructor stub
			queueInstance = que;
			env = e;
		}

	}
}
