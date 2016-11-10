package usu.cs.Sensys.Conversation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import usu.cs.Sensys.SharedObjects.MessageNumber;

public class QueueDictionary {
	final static Logger logger = Logger.getLogger(QueueDictionary.class);

	// Create a dictionary of queues for conversations in progress, plus a lock
	// object for the dictionary
	private final ConcurrentMap<MessageNumber, ConversationQueue> _activeQueues = new ConcurrentHashMap<>();
	private static QueueDictionary instance = null;

	public static QueueDictionary getInstance() {
		if (instance == null)
			instance = new QueueDictionary();
		return instance;
	}

	private QueueDictionary() {
		// TODO Auto-generated constructor stub
	}

	public ConversationQueue CreateQueue(MessageNumber convId) {
		ConversationQueue queue = new ConversationQueue(MessageNumber.Create());
		_activeQueues.putIfAbsent(convId, queue);
		return queue;
	}

	public ConversationQueue Lookup(MessageNumber convId) {
		logger.debug("Lookup for name=" + convId);

		
		return _activeQueues.get(convId);
	}

	public void CloseQueue(MessageNumber queueId) {
		logger.debug("Remove Queue " + queueId);
		_activeQueues.remove(queueId);
	}

	public void ClearAllQueues() {
		_activeQueues.clear();
	}

	public int ConversationQueueCount() {
		return _activeQueues.size();
	}
}
