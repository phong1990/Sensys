package usu.cs.Sensys.SharedObjects;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sun.jna.platform.win32.Kernel32;

public class MessageNumber {
	private short ProcessID;
	private short SeqNumber;
	private static short _nextSeqNumber;
	private final static Lock MyLock = new ReentrantLock();

	public static MessageNumber Create() {
		MessageNumber result = new MessageNumber(
				(short)Kernel32.INSTANCE.GetCurrentProcessId(), GetNextSeqNumber());
		return result;
	}

//	public MessageNumber Clone()
//    {
//        return MemberwiseClone() as MessageId;
//    }

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return (ProcessID << 16) | (SeqNumber & 0xFFFF);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		// If the object is compared with itself then return true
		if (obj == this) {
			return true;
		}

		/* Check if o is an instance of Complex or not
		  "null instanceof [type]" also returns false */
		if (!(obj instanceof MessageNumber)) {
			return false;
		}

		// typecast o to Complex so that we can compare data members
		MessageNumber c = (MessageNumber) obj;

		// Compare the data members and return accordingly
		return compare(this, c) == 0;
	}

	public static int compare(MessageNumber a, MessageNumber b) {
		int result = 0;

		if (a != b) {
			if ((a == null) && (b != null))
				result = -1;
			else if ((a != null) && (b == null))
				result = 1;
			else {
				if (a.ProcessID < b.ProcessID)
					result = -1;
				else if (a.ProcessID > b.ProcessID)
					result = 1;
				else if (a.SeqNumber < b.SeqNumber)
					result = -1;
				else if (a.SeqNumber > b.SeqNumber)
					result = 1;
			}
		}
		return result;
	}

	private static short GetNextSeqNumber() {
		MyLock.lock();
		{
			if (_nextSeqNumber == Short.MAX_VALUE)
				_nextSeqNumber = 0;
			++_nextSeqNumber;
		}
		MyLock.unlock();
		return _nextSeqNumber;
	}

	public short getProcessID() {
		return ProcessID;
	}

	public void setProcessID(short processID) {
		ProcessID = processID;
	}

	public short getSeqNumber() {
		return SeqNumber;
	}

	public void setSeqNumber(short seqNumber) {
		SeqNumber = seqNumber;
	}

	public MessageNumber() {
		// TODO Auto-generated constructor stub
	}
	public MessageNumber(short processID, short sequenceNumber) {
		ProcessID = processID;
		SeqNumber = sequenceNumber;
	}

	public String toString() {
		return "<Process:" + ProcessID + "-Seq:" + SeqNumber + ">";
	}
}
