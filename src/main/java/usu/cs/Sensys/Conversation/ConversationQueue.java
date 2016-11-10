package usu.cs.Sensys.Conversation;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.util.ManualResetEvent;

public class ConversationQueue {

	final static Logger logger = Logger.getLogger(ConversationQueue.class);

	private final ConcurrentLinkedQueue<Envelope> _myQueue = new ConcurrentLinkedQueue<Envelope>();
	private final ManualResetEvent _somethingEnqueued = new ManualResetEvent(
			false);

	private MessageNumber QueueID;

	public int getQueueCount() {
		return _myQueue.size();
	}

	public ConversationQueue(MessageNumber id) {
		QueueID = id;
		// TODO Auto-generated constructor stub
	}
	public void Enqueue(Envelope envelope) {
		if (envelope != null) {
			_myQueue.add(envelope);
			logger.debug("Enqueued an envelope into queue " + QueueID);
			_somethingEnqueued.set();
		}
	}

	public Envelope Dequeue(int timeout) {
		Envelope result = null;
		int remainingTime = timeout;
		while (result == null && remainingTime > 0) {
			long tStart = System.currentTimeMillis();
			if (_myQueue.size() == 0)
				try {
					_somethingEnqueued.waitOne(timeout);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					logger.warn(
							"warning timeout not effective " + e1.getMessage());
				}
			try {
				result = _myQueue.remove();
				_somethingEnqueued.set();
				logger.debug("Dequeued an envelope from queue " + QueueID);
			} catch (NoSuchElementException e) {
				// queue is empty
				long tEnd = System.currentTimeMillis();
				long tDelta = tEnd - tStart;
				remainingTime -= tDelta;
			}
		}

		return result;
	}
}
