package usu.cs.Sensys.Conversation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class BroadcastListener{
	final static Logger logger = Logger.getLogger(UDPCommunicator.class);
	public static final int BROADCAST_PORT = 18888; // just a random port
	private boolean _started;
	private int Timeout;
	private final static Lock StartStopLock = new ReentrantLock();
	private DatagramSocket _myUdpClient = null;
	private Thread _receiveThread = null;

	public int getTimeout() {
		return Timeout;
	}

	public void setTimeout(int timeout) {
		Timeout = timeout;
	}

	public PublicEndpoint Start(InetAddress IPAddress) {
		PublicEndpoint endpoint = null;
		logger.info("Start broadcast listener, listen to everything coming to port" + BROADCAST_PORT);

		StartStopLock.lock();
		{
			if (_started)
				Stop();

				try {
					_myUdpClient = new DatagramSocket(BROADCAST_PORT, IPAddress);
					_started = true;
				} catch (SocketException e) {
					logger.warn("Except warning in starting Broadcast listener:  "
							+ e.getMessage());
				}
			

			if (!_started)
				logger.error(
						"Except Error in starting Broadcast listener: Cannot bind port "
								+ BROADCAST_PORT);
			else {

				_receiveThread = new Thread(new Runnable() {
					@Override
					public void run() {
						Receive();
					}
				});
				_receiveThread.start();
			}
		}
		StartStopLock.unlock();
		return endpoint;
	}

	public void Stop() {
		logger.debug("Entering Stop");

		StartStopLock.lock();
		{
			_started = false;

			try {
				_receiveThread.join(Timeout * 2);
			} catch (InterruptedException e) {
				logger.warn("Except warning cant join receiving thread: "
						+ e.getMessage());
			}
			_receiveThread = null;

			if (_myUdpClient != null) {
				_myUdpClient.close();
				_myUdpClient = null;
			}
		}

		StartStopLock.unlock();
		logger.info("Communicator Stopped");
	}



	private void Receive() {
		while (_started) {
			Envelope env = ReceiveOne();
			// process the incoming envelope
			if (env != null) {
				CommSubsystem.handleBroadcastedMessage(env);
			}
			// if (env != null)
			// EnvelopeHandler?.Invoke(env);
		}
	}

	private Envelope ReceiveOne() {
		Envelope result = null;

		DatagramPacket receivePacket = receivePacket(Timeout);

		if (receivePacket != null && receivePacket.getData().length > 0) {
			byte[] receivedBytes = receivePacket.getData();
			InetAddress ep = receivePacket.getAddress();
			PublicEndpoint sendersEndPoint = new PublicEndpoint(
					ep.getHostAddress(), receivePacket.getPort());
			Message message = Message.decode(receivedBytes);
			if (message != null) {
				result = new Envelope(message, sendersEndPoint);
				logger.debug("Just received message, Nr="
						+ result.getMsg().getMessageNr() + ", Conv="
						+ result.getMsg().getConversationId() + ", Type="
						+ result.getMsg().getMessageType() + ", From="
						+ result.getEndPoint().toString());
			} else {
				logger.error("Cannot decode message received from "
						+ sendersEndPoint);
			}
		}

		return result;
	}

	private DatagramPacket receivePacket(int timeout) {
		DatagramPacket receivePacket = null;
		if (_myUdpClient != null) {
			try {
				_myUdpClient.setSoTimeout(timeout);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				logger.warn(
						"Except warning can't set timeout: " + e.getMessage());
			}

			try {

				logger.debug("Try receive bytes from anywhere");

				byte[] buffer = new byte[2048];
				receivePacket = new DatagramPacket(buffer, buffer.length);
				_myUdpClient.receive(receivePacket);
				logger.debug("Back from receive");
				if (logger.isDebugEnabled()) {
					if (receivePacket.getData() != null) {
						String tmp = new String(receivePacket.getData(),
								"US-ASCII");
						logger.debug("Incoming message=" + tmp);
					}
				}
			} catch (SocketTimeoutException err) {
				logger.warn(err.getMessage());
				receivePacket = null;
			} catch (Exception err) {
				logger.warn(err.getMessage());
				receivePacket = null;
			}
		}
		return receivePacket;
	}


}
