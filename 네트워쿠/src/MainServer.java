import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MainServer {
		

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  Scanner input = new Scanner(System.in);
	        ServerSocket serversocket = null;
	        int client_cnt = 0;
	 
	        try {
	            serversocket = new ServerSocket(5000);
	            System.out.println(" server is ready");
	        } catch (IOException e) {
	            e.printStackTrace();
	        } // try - catch
	        
	        while(true){
	        	try{
	           // System.out.println("--SERVER Close : input num");
	            System.out.println("--SERVER Waiting...");
	            
	            Socket socket = serversocket.accept();
                System.out.println( "connection requires came from  "+socket.getInetAddress());
                client_cnt ++;
                 
                System.out.println("접속된 클라이언트 수" + client_cnt);
                
                InputStream in = socket.getInputStream();
                DataInputStream dis = new DataInputStream(in);  // 기본형 단위로 처리하는 보조스트림
                
                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                
               switch( dis.readUTF() ) {
	               case "1":
	            	   dos.writeUTF("You select Create Room.");
	            	   break;
	               case "2":
	            	   dos.writeUTF("You select Search Room.");
	            	   break;
	               case "3":
	            	   dos.writeUTF("Bye Bye.");
	            	   break;
	               
	               default:
	            	   dos.writeUTF("Wrong selection. Please retry.");
	            	   break;
               }
               
               while ( true ) {
            	   String clientSays = dis.readUTF();
            	   if( clientSays.equals("quit") ) {
            		   System.out.println("Bye Bye");
            		   dos.writeUTF("Bye Bye");
            		   break;
            	   }
            	   System.out.println(clientSays);
            	   dos.writeUTF("this way.");
               }
                 
                // 원격 소켓(remote socket)에 데이터를 보낸다.
                
               // System.out.println(  "melong.");


                // 스트림과 소켓을 달아준다.
               // dos.close();
               // socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } // try - catch
        } // while
    } // main

}


