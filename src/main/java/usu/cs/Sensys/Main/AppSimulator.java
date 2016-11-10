package usu.cs.Sensys.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import usu.cs.Sensys.Conversation.CommProcessState;
import usu.cs.Sensys.Conversation.CommSubsystem;
import usu.cs.Sensys.Conversation.Conversation;
import usu.cs.Sensys.Conversation.ConversationFactory;
import usu.cs.Sensys.Conversation.InitiatorLogin;
import usu.cs.Sensys.Conversation.ResponderLogin;
import usu.cs.Sensys.Messages.LoginReply;
import usu.cs.Sensys.util.ManualResetEvent;

public class AppSimulator {
	public static void main(String[] args) throws InterruptedException {
		BufferedReader br = null;

		CommProcessState processState = new CommProcessState();
		CommSubsystem communicationSubsystem = new CommSubsystem(processState);

		try {
			System.out.println("---- SENSYS mobile app simulator ----");
			br = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("UserID : ");
			String id = br.readLine();

			System.out.println("PIN : ");
			String pin = br.readLine();

			System.out.println("Server ip: ");
			String serverip = br.readLine();
			System.out.println("Server port: ");
			int serverport = Integer.parseInt(br.readLine());

			System.out.println("----- Logging in to server at " + serverip + ":"
					+ serverport + " -----");
			communicationSubsystem.Start();
			InitiatorLogin loginConvo = login(communicationSubsystem, id, pin,
					serverip, serverport);
			boolean loop = true;
			while (loop) {

				System.out.print("Type EXIT to exit ");
				String input = br.readLine();

				if ("exit".equalsIgnoreCase(input)) {
					System.out.println("Exit!");
					communicationSubsystem.Stop();
					communicationSubsystem = null;
					System.exit(0);
				}
				ManualResetEvent _somethingEnqueued = new ManualResetEvent(
						false);
				_somethingEnqueued.waitOne(1000);
				LoginReply result = loginConvo.getResult();
				if (result != null) {
					System.out.println("Login Successfull on server at: "
							+ result.getEndPoint().getHost() + ":"
							+ result.getEndPoint().getPort());
					loop = false;
				} else {
					System.out.println("seems like no reply yet!");
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

	private static InitiatorLogin login(CommSubsystem communicationSubsystem,
			String id, String pin, String serverip, int serverport) {
		// TODO Auto-generated method stub
		return communicationSubsystem.login(id, pin, serverip, serverport);
	}
}
