import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class GameUser {
	GameRoom room;
	static Socket sock = null;
	String nickName;
	int uid;
	
	public static void main(String[] args) {
        try {
        	
        	Scanner sc = new Scanner(System.in);
        	
        	System.out.println("서버의 IP를 입력하세요");
            String serverIP = sc.nextLine(); // 127.0.0.1 & localhost 본인
            System.out.println("connecting to server.... server IP : " + serverIP);
             
            // 소켓을 생성하여 연결을 요청한다.
            Socket socket = new Socket(serverIP, 5000);
            InputStream in = socket.getInputStream();
            DataInputStream dis = new DataInputStream(in);  // 기본형 단위로 처리하는 보조스트림
            
            OutputStream out = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            
            System.out.println("서버에 접속했습니다^.^");
            
            System.out.println("* * * * * 게임메뉴 * * * * *");
            System.out.println("* 1. 방 만들기              *");
            System.out.println("* 2. 방 목록 보기           *");
            System.out.println("* 3. 접속 종료              *");
            System.out.println("* * * * * * * * * * * * * * *");
           
            String selection = sc.nextLine();
            dos.writeUTF(selection);

            // 소켓으로 부터 받은 데이터를 출력한다. 서버에서 준 메시지 출
            while( true ) {
            	String serverSays = dis.readUTF();
            	if( serverSays.equals("Bye Bye") ) {
            		break;
            	}
            	System.out.println("message from server : " + serverSays);
            	String sendStr = sc.nextLine();
            	dos.writeUTF(sendStr);
            }
            System.out.println("connection close.");
             
            // 스트림과 소켓을 닫는다.
            dis.close();
            socket.close();
        	
        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } // try - catch
    } // main



	
	
	
	public GameUser(String _nickName){
		this.nickName = _nickName;
	}
	
	
	public GameUser(int _uid, String _nickName){
		this.uid = _uid;
		this.nickName = _nickName;
	}

	public void EnterRoom(GameRoom room){
		room.EnterRoom(this);
		this.room = room;
	}
}
