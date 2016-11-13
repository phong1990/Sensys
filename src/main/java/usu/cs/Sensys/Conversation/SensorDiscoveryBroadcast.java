package usu.cs.Sensys.Conversation;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Main.SensorDataManager;
import usu.cs.Sensys.Main.SensorManager;
import usu.cs.Sensys.Messages.AvailableSensorRequest;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.Messages.SensorHandshakeReply;
import usu.cs.Sensys.Messages.SensorHandshakeRequest;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.util.ManualResetEvent;

public class SensorDiscoveryBroadcast extends Conversation {
	private PublicEndpoint _endpoint;
	private final Object monitor = new Object();
	SensorDataManager senDataMan = SensorDataManager.getInstance();

	@Override
	protected boolean Initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	public SensorDiscoveryBroadcast() {
		// 255.255.255.255 is the default address for broadcasting in a local
		// network
		// the port used here is randomized
		_endpoint = new PublicEndpoint("255.255.255.255",
				BroadcastListener.BROADCAST_PORT);
		TimeOut = 60000; // 60s
	}

	@Override
	protected void ExecuteDetails() {
		State = PossibleState.Working;
		// TODO Auto-generated method stub
		// create login envelop
		Envelope envelop = null;
		TransactionLock.lock();
		{
			Message msg = new AvailableSensorRequest(senDataMan.getSensorType(),
					"");
			envelop = new Envelope(msg, _endpoint);
		}
		TransactionLock.unlock();

		// send envelop
		sendMessage(envelop);
		if (ErrorMessage != null)
			return;
		// wait for reply message (in other thread so check state)
		boolean waiting = true;
		while (waiting) {
			try {
				monitor.wait(TimeOut);
				if (!senDataMan.isOCCUPIED()) {
					sendMessage(envelop);
				} else
					waiting = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.warn("Warning Exception can't initiate object waiting"
						+ e.getMessage());
				waiting = false;
			}
		}
	}

	@Override
	public boolean isExpectedMessageType(String messageType) {
		// TODO Auto-generated method stub
		return true;
	}
}
