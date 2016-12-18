package usu.cs.Sensys.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import usu.cs.Sensys.Conversation.CommProcessState;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Conversation.InitiatorLogin;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.util.ManualResetEvent;

public class SensorSimulator {

	public static void main(String[] args) throws IOException {
		System.out.println("STARTING SENSOR");
		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();
		communicationSubsystem.Start();
		ManualResetEvent _somethingEnqueued = new ManualResetEvent(false);
		try {
			_somethingEnqueued.waitOne(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // wait for a worst case
			// scenario with 3
		System.out.println(
				"SENSOR STARTED AND BROADCASTING TO THE ENTIRE NETWORK. HOHOHO");
		communicationSubsystem.StartSensorBroadcast();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(System.in));
		boolean loop = true;
		while (loop) {

			System.out.println("Type EXIT to exit ");
			String input = br.readLine();

			if ("exit".equalsIgnoreCase(input)) {
				System.out.println("Exit!");
				loop = false;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (SensorDataManager.getInstance().isOCCUPIED())
				loop = false;
		}
		communicationSubsystem.Stop();
	}

}
