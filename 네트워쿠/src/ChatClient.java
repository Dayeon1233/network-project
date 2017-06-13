import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
	
	static String eServer = "";
	static int ePort = 0000;
	// static int clientID = -1;
	static Socket chatSocket = null;
	
	
	public static void main(String[] args) {
		
		System.out.println("ChatClient main()");

		if (args.length != 2) {
			System.out.println("Usage: Classname ServerName ServerPort");
			System.exit(1);
		}
		
		eServer = args[0];
		ePort = Integer.parseInt(args[1]);
		
		try {
			chatSocket = new Socket(eServer, ePort);
			// clientID = chatSocket.getLocalPort();
		} catch (BindException b) {
			System.out.println("Can't bind on: "+ePort);
			System.exit(1);
		} catch (IOException i) {
			System.out.println(i);
			System.exit(1);
		}
		new Thread(new ClientReceiver(chatSocket)).start();
		new Thread(new ClientSender(chatSocket)).start();
	}
}

class ClientSender implements Runnable {
	
	private Socket chatSocket = null;
	
	ClientSender(Socket socket){
		System.out.println("ClientSender Constructor");
		this.chatSocket = socket;
	}

	public void run(){
		System.out.println("ClientSender run()");
		Scanner keyIn = null;
		PrintWriter out = null;
		try{
			keyIn = new Scanner(System.in);
			out = new PrintWriter(chatSocket.getOutputStream(),true);
			String userInput = "";
			System.out.println("Your're localPort: "+chatSocket.getLocalPort());
			System.out.println("");
			System.out.println("What's your name?");
			//out.println(userInput);
			
			//System.out.println("Hello "+ + "!! choose one please");
			
			
//			+", Type Message (\"Bye.\" to leave\n");
			
			
			while((userInput = keyIn.nextLine()) != null){//////////계속 클라의 입력을 받는 라
				if(userInput.equals("exit"))
					break;
				else 
					out.println(userInput);
			}
			
			out.println(userInput);
			
			out.close();
			keyIn.close();
		}catch(IOException i){
			try{
				if(out != null) out.close();
				if(keyIn != null) keyIn.close();
				if(chatSocket != null) chatSocket.close();
			}catch(IOException e){
			}
			System.exit(1);
		}
	}	
}




class ClientReceiver implements Runnable{
	
	private Socket chatSocket = null;
	
	ClientReceiver(Socket socket){
		System.out.println("ClientReceiver Constructor");
		this.chatSocket = socket;
	}
	
	public void run(){
		System.out.println("ClientReceiver run()");
		while(chatSocket.isConnected()){
			BufferedReader in = null;
			
			try{
				in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
				String readSome = null;
				while((readSome = in.readLine()) != null){
					System.out.println(readSome);
					if( readSome.equals(".bye.") ) {
						break;
					}
				}
				in.close();
				chatSocket.close();
			}catch(IOException i){
				try{
					if(in != null) in.close();
					if(chatSocket != null) chatSocket.close();
				}catch(IOException e){
					System.out.println("Leave.");
					System.exit(1);
				}
			}
			
		}
	}
}

