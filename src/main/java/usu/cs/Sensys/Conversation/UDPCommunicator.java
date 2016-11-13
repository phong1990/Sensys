package usu.cs.Sensys.Conversation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public class UDPCommunicator implements Runnable {
	final static Logger logger = Logger.getLogger(UDPCommunicator.class);
	private int MinPort = 10000;
	private int MaxPort = 12000;
	private boolean _started;
	private int Timeout;
	private final static Lock StartStopLock = new ReentrantLock();
	private DatagramSocket _myUdpClient = null;
	private Thread _receiveThread = null;
	protected static final Lock senderLock = new ReentrantLock();

	public int getPort() {
		if (_myUdpClient == null)
			return 0;
		return _myUdpClient.getPort();
	}

	public int getMinPort() {
		return MinPort;
	}

	public void setMinPort(int minPort) {
		MinPort = minPort;
	}

	public int getMaxPort() {
		return MaxPort;
	}

	public void setMaxPort(int maxPort) {
		MaxPort = maxPort;
	}

	public int getTimeout() {
		return Timeout;
	}

	public void setTimeout(int timeout) {
		Timeout = timeout;
	}

	public PublicEndpoint Start(InetAddress IPAddress) {
		PublicEndpoint endpoint = null;
		logger.info("Start communicator");

		StartStopLock.lock();
		{
			if (_started)
				Stop();

			int portToTry = FindAvailablePort(MinPort, MaxPort);
			if (portToTry > 0) {
				try {
					_myUdpClient = new DatagramSocket(portToTry, IPAddress);
					_started = true;
				} catch (SocketException e) {
					logger.warn("Except warning in starting UDP communicator:  "
							+ e.getMessage());
				}
			}

			if (!_started)
				logger.error(
						"Except Error in starting UDP communicator: Cannot bind port "
								+ portToTry);
			else {

				_receiveThread = new Thread(new Runnable() {
					@Override
					public void run() {
						Receive();
					}
				});
				_receiveThread.start();

				endpoint = new PublicEndpoint(IPAddress.getHostAddress(),
						portToTry);
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

	public Error Send(Envelope outgoingEnvelope)
			throws UnsupportedEncodingException {
		Error error = null;
		senderLock.lock();
		{
			if (outgoingEnvelope == null || !outgoingEnvelope.isValidToSend())
				logger.warn("Invalid Envelope or Message");
			else {
				byte[] bytesToSend = outgoingEnvelope.getMsg().Encode();

				logger.debug("Send out: " + new String(bytesToSend, "US-ASCII")
						+ " to " + outgoingEnvelope.getEndPoint());

				try {

					DatagramPacket sendPacket = new DatagramPacket(bytesToSend,
							bytesToSend.length,
							InetAddress.getByName(
									outgoingEnvelope.getEndPoint().getHost()),
							outgoingEnvelope.getEndPoint().getPort());
					_myUdpClient.send(sendPacket);

					logger.debug("Send complete");
				} catch (Exception err) {
					error = new Error("Cannnot send a "
							+ outgoingEnvelope.getMsg().getMessageType()
							+ " to " + outgoingEnvelope.getEndPoint() + ": "
							+ err.getMessage());
					logger.warn(error.getMessage());
				}
			}
		}
		senderLock.unlock();
		return error;
	}

	private void Receive() {
		while (_started) {
			Envelope env = ReceiveOne();
			// process the incoming envelope
			if (env != null) {
				CommSubsystem.handleReceivedMessage(env);
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
				try {
					logger.error(
							"Message=" + new String(receivedBytes, "US-ASCII"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					logger.error("Except Error cant print out message: "
							+ e.getMessage());
				}
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
				logger.debug(err.getMessage());
				receivePacket = null;
			} catch (Exception err) {
				logger.warn(err.getMessage());
				receivePacket = null;
			}
		}
		return receivePacket;
	}

	private int FindAvailablePort(int minPort, int maxPort) {
		int availablePort = -1;

		logger.debug("Find a free port between " + minPort + " and " + maxPort);
		for (int possiblePort = minPort; possiblePort <= maxPort; possiblePort++) {
			if (IsAvailable(possiblePort)) {
				availablePort = possiblePort;
				break;
			}
		}
		logger.debug("Available Port = " + availablePort);
		return availablePort;
	}

	private boolean IsAvailable(int port) {
		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
