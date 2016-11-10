package usu.cs.Sensys.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import usu.cs.Sensys.Conversation.CommProcessState;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;

/**
 * Hello world!
 *
 */
public class ServerSimulator {
	public static void main(String[] args) {
		BufferedReader br = null;

		CommProcessState processState = new CommProcessState();
		CommSubsystem communicationSubsystem = new CommSubsystem(processState);

		try {
			System.out.println("---- SENSYS server simulator ----");
			br = new BufferedReader(new InputStreamReader(System.in));
			communicationSubsystem.Start();
			PublicEndpoint myEndpoint = communicationSubsystem.getMyEndpoint();
			System.out.println("Openned server at " + myEndpoint.getHost() + ":"
					+ myEndpoint.getPort());

			boolean loop = true;
			while (loop) {

				System.out.print("Type EXIT to exit ");
				String input = br.readLine();

				if ("exit".equalsIgnoreCase(input)) {
					System.out.println("Exit!");
					System.exit(0);
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (communicationSubsystem != null) {
				communicationSubsystem.Stop();
				communicationSubsystem = null;
			}
		}
	}
}
