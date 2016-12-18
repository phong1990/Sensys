package usu.cs.Sensys.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import usu.cs.Sensys.Conversation.CommProcessState;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.SharedObjects.PublicEndpoint;
import usu.cs.Sensys.util.PublicKeyManager;

/**
 * Hello world!
 *
 */
public class ServerSimulator {
	public static void main(String[] args) throws Exception {
		BufferedReader br = null;

		CommSubsystem communicationSubsystem = CommSubsystem.getInstance();

		ServerLoginManager loginMan = ServerLoginManager.getInstance();
		try {
			System.out.println("---- SENSYS server simulator ----");
			PublicKeyManager.getInstance().makeKey();
			loginMan.start();
			br = new BufferedReader(new InputStreamReader(System.in));
			communicationSubsystem.Start();
			PublicEndpoint myEndpoint = communicationSubsystem.getMyEndpoint();
			System.out.println(
					"Openned server at " + communicationSubsystem.getAWSIp()
							+ ":" + myEndpoint.getPort());

			boolean loop = true;
			while (loop) {

				System.out.print("Type EXIT to exit ");
				String input = br.readLine();

				if ("exit".equalsIgnoreCase(input)) {
					System.out.println("Exit!");
					communicationSubsystem.Stop();
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
			loginMan.stop();
		}
	}
}
