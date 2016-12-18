package usu.cs.Sensys.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.util.ManualResetEvent;

public class ServerLoginManager {
	private final Map<PublicEndpoint, UserInstance> UserList = new ConcurrentHashMap<>();
	private static ServerLoginManager instance = null;
	private boolean started = false;
	private int Timeout = 10000;
	private int LivingTime = 60000; // 60s

	public static ServerLoginManager getInstance() {
		if (instance == null)
			instance = new ServerLoginManager();
		return instance;
	}

	private Thread _receiveThread = null;

	public void start() {
		if (started == false) {
			_receiveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					regularCheck();
				}
			});
			_receiveThread.start();
			started = true;
		}
	}

	private void regularCheck() {
		// TODO Auto-generated method stub
		while (started) {
			long now = System.currentTimeMillis();
			for (UserInstance user : UserList.values()) {
				if (!user.isDead)
					if (now - user.lastCommunicatedTime > LivingTime) {
						user.isDead = true;
						System.out.println("User from "
								+ user.endpoint.getHost() + ":"
								+ user.endpoint.getPort() + " is disconnected");
					} else {
						if (!user.sendFirstMsg) {
							// also send a message back to the user just to
							// demonstrate the ability
							// to send secured message
							CommSubsystem.getInstance().sendMessage(
									"This is a secured message. The Server sent you this "
											+ "to demonstrate the system ability to send such messages around",
									user.endpoint.getHost(),
									user.endpoint.getPort());
							user.sendFirstMsg = false;
						}
					}
			}
			try {
				ManualResetEvent _somethingEnqueued = new ManualResetEvent(
						false);
				_somethingEnqueued.waitOne(Timeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // wait for a worst case
				// scenario with 3
		}
	}

	public void addNewUser(PublicEndpoint ep) {
		UserList.put(ep, new UserInstance(ep));
		System.out.println("User from " + ep.getHost() + ":" + ep.getPort()
				+ " is connecteds");
	}

	public void resetTimer(PublicEndpoint ep) {
		UserInstance user = UserList.get(ep);
		user.lastCommunicatedTime = System.currentTimeMillis();
	}

	public void stop() {
		started = false;

		try {
			_receiveThread.join(Timeout * 2);
		} catch (InterruptedException e) {
		}
		_receiveThread = null;

	}

	private static class UserInstance {
		PublicEndpoint endpoint;
		boolean isDead = false;
		boolean sendFirstMsg = false;

		public UserInstance(PublicEndpoint ep) {
			endpoint = ep;
		}

		long lastCommunicatedTime = 0;
	}
}
