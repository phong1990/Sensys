package usu.cs.Sensys.Conversation;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import usu.cs.Sensys.Conversation.Conversation.PossibleState;
import usu.cs.Sensys.Messages.Message;
import usu.cs.Sensys.SharedObjects.MessageNumber;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

public abstract class Conversation implements Runnable {
	final static Logger logger = Logger.getLogger(Conversation.class);
	protected MessageNumber ConversationId;
	protected PublicEndpoint RemoteEndPoint;
	protected int MaxRetries;
	protected static final Lock TransactionLock = new ReentrantLock();
	protected int TimeOut;
	protected CommSubsystem CommSubsystem;
	protected boolean Done;
	protected ConversationQueue MyQueue;
	protected String ErrorMessage;
	protected PossibleState State = PossibleState.NotInitialized;
	protected int Retries = 0;
	protected boolean Wakeup = false;

	public void wakeup() {
		Wakeup = true;
	}

	public int getRetries() {
		return Retries;
	}

	public void increaseRetries() {
		this.Retries = Retries + 1;
	}

	protected void sendMessage(Envelope env) {
		try {
			CommSubsystem.Send(env);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			ErrorMessage = "UnsupportedEncodingException";
		}
	}

	public CommSubsystem getCommSubsystem() {
		return CommSubsystem;
	}

	public void setCommSubsystem(CommSubsystem commSubsystem) {
		CommSubsystem = commSubsystem;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}

	public PossibleState getState() {
		return State;
	}

	public void setState(PossibleState state) {
		State = state;
	}

	public enum PossibleState {
		NotInitialized, Working, Failed, Successed
	};

	public MessageNumber getConversationId() {
		return ConversationId;
	}

	public void setConversationId(MessageNumber conversationId) {
		ConversationId = conversationId;
	}

	public PublicEndpoint getRemoteEndPoint() {
		return RemoteEndPoint;
	}

	public void setRemoteEndPoint(PublicEndpoint remoteEndPoint) {
		RemoteEndPoint = remoteEndPoint;
	}

	public int getMaxRetries() {
		return MaxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		MaxRetries = maxRetries;
	}

	public long getTimeOut() {
		return TimeOut;
	}

	public void setTimeOut(int timeOut) {
		TimeOut = timeOut;
	}

	@Override
	public void run() {
		logger.debug("Launch of " + getClass().getName() + " thread name = "
				+ Thread.currentThread().getName());
		Execute();
	}

	public void Execute() {
		//
		// if (Initialize())
		ExecuteDetails();

		if (ErrorMessage == null)
			State = PossibleState.Successed;
		else {
			State = PossibleState.Failed;
			logger.warn(ErrorMessage);
		}

		// PostExecuteAction?.Invoke(context);
		CommSubsystem.CloseConversationQueue(ConversationId);
	}

	protected abstract boolean Initialize();

	protected abstract void ExecuteDetails();

	protected boolean IsEnvelopeValid(Envelope env) {
		ErrorMessage = null;
		logger.debug(
				"Checking to see if envelope is valid and message of appropriate type");
		if (env.getMsg() == null)
			ErrorMessage = "Null or empty message";
		else if (env.getMsg().getMessageNr() == null)
			ErrorMessage = "Null Message Number";
		else if (env.getMsg().getConversationId() == null)
			ErrorMessage = "Null Conversation Id";
		else {
			String messageType = env.getMsg().getMessageType();

			logger.debug("See if " + messageType + " is valid for a "
					+ getClass().getName() + " conversation");
			if (!isExpectedMessageType(messageType)) {
				ErrorMessage = "Invalid Type of Message : " + messageType;
			}
		}

		if (ErrorMessage != null)
			logger.error(ErrorMessage);

		return (ErrorMessage == null);
	}

	protected abstract boolean isExpectedMessageType(String messageType);
}
