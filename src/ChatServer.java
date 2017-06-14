import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class ChatServer implements Runnable {

   private ChatServerRunnable clients[] = new ChatServerRunnable[10];
   private RealGameRoom rooms[] = new RealGameRoom[5];

   public int clientCount = 0;
   public int roomCount = 0;

   private int roomSeq = 0;
   private int roomShowSeq = 1;
   private int ePort = -1;
   
   
   private CardList stackCards;
   private CardList player1Cards;
   private CardList player2Cards;
   
   
   public CardList getCardList(int listNum)
   {
      if(listNum == 0)
         return stackCards;
      else if(listNum == 1)
         return player1Cards;
      else if(listNum == 2)
         return player2Cards;
      else 
         return null;
   }
   
   public int getCardsListSize(int listNum)
   {
      if(listNum == 0)
         return stackCards.cardListSize();
      else if(listNum == 1)
         return player1Cards.cardListSize();
      else if(listNum == 2)
         return player2Cards.cardListSize();
      else
         return -1;
   }
   
   public int getClientID(int i)
   {
      return clients[i].getClientID();
   }
   
   
   

   public ChatServer(int port) {

      stackCards = new CardList();
      
      initStackCardList();
      System.out.println("ChatServer Constructor");
      clientCount = 0;

      this.ePort = port;
   }
   
public void initStackCardList() {
      
      char []color = { 'G','Y','B','R' };

      for (int i = 0; i<4; i++)
         for (int j = 0; j<5; j++)
         {
            Card card = new Card(color[i], 1);
            stackCards.add(card);
         }

      for (int i = 0; i<4; i++)
         for (int j = 0; j<3; j++)
         {
            Card card = new Card(color[i], 2);
            stackCards.add(card);
         }

      for (int i = 0; i<4; i++)
         for (int j = 0; j<3; j++)
         {
            Card card = new Card(color[i], 3);
            stackCards.add(card);
         }

      for (int i = 0; i<4; i++)
         for (int j = 0; j<2; j++)
         {
            Card card = new Card(color[i], 4);
            stackCards.add(card);
         }

      for (int i = 0; i<4; i++)
         for (int j = 0; j<1; j++)
         {
            Card card = new Card(color[i], 5);
            stackCards.add(card);
         }

      stackCards.shuffle(0, 55);
      
   }

   public void run() {

      System.out.println("ChatServer run()");

      SSLServerSocketFactory serverSocketFactory = null;
      try {
    	  serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault(); //serverSocketFactory 인스턴스를 초기화
    	  SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(ePort);
         System.out.println("Server started: socket created on " + ePort);

         while (true) {
            addClient(serverSocket);
         }
      } catch (BindException b) {
         System.out.println("Can't bind on: " + ePort);
      } catch (IOException i) {
         System.out.println(i);
      }

      /*
       * finally { try { if (serverSocket != null) serverSocket.close(); }
       * catch (IOException i) { System.out.println(i); }
       */
      // }
   }

   public int whoClient(int clientID) {

      System.out.println("ChatServer whoClient()");

      for (int i = 0; i < clientCount; i++)
         if (clients[i].getClientID() == clientID)
            return i;
      return -1;
   }

   public void putClient(int clientID, String inputLine) {

      System.out.println("ChatServer putClient()");

      for (int i = 0; i < clientCount; i++)
         if (clients[i].getClientID() == clientID) {
            System.out.println("writer: " + clientID);
         } else {
            System.out.println("write: " + clients[i].getClientID());
            clients[i].out.println(inputLine);
         }
   }

   public void addClient(SSLServerSocket serverSocket) {

      System.out.println("ChatServer addClient()");

      SSLSocket clientSocket = null;

      if (clientCount < clients.length) {
         try {
            clientSocket = (SSLSocket) serverSocket.accept();
            clientSocket.setSoTimeout(400000000); // 1000/sec

         } catch (IOException i) {
            System.out.println("Accept() fail: " + i);
         }
         clients[clientCount] = new ChatServerRunnable(this, clientSocket);
         new Thread(clients[clientCount]).start();
         clientCount++;
         System.out.println("Client connected: " + clientSocket.getPort() + ", CurrentClient: " + clientCount);
         
         if(clientCount == 1){
            player1Cards = new CardList();
            player1Cards.setUserID(clientSocket.getPort());
         }
         else if(clientCount == 2){
            player2Cards = new CardList();
            player2Cards.setUserID(clientSocket.getPort());
         }
      
         
      } else {
         try {
            SSLSocket dummySocket = (SSLSocket) serverSocket.accept();
            ChatServerRunnable dummyRunnable = new ChatServerRunnable(this, dummySocket);
            new Thread(dummyRunnable);
            dummyRunnable.out.println(dummySocket.getPort() + " < Sorry maximum user connected now");
            System.out.println("Client refused: maximum connection " + clients.length + " reached.");
            dummyRunnable.close();
         } catch (IOException i) {
            System.out.println(i);
         }
      }
      
      if(clientCount == 2)   //if the number of client is two 
      {
         for (int i = 0; i < 28; i++)
         {
            //give player 28cards
            player1Cards.add(stackCards.get(0));
            stackCards.removeFirst();
            player2Cards.add(stackCards.get(0));
            stackCards.removeFirst();   
         }
         
      }
      
   }

   public synchronized void delClient(int clientID) {

      System.out.println("ChatServer delClient()");

      int pos = whoClient(clientID);
      ChatServerRunnable endClient = null;
      if (pos >= 0) {
         endClient = clients[pos];
         if (pos < clientCount - 1)
            for (int i = pos + 1; i < clientCount; i++)
               clients[i - 1] = clients[i];

         clientCount--;
         System.out
               .println("Client removed: " + clientID + " at clients[" + pos + "], CurrentClient: " + clientCount);
         endClient.close();
      }
   }

   public static void main(String[] args) throws IOException {

      System.out.println("ChatServer main()");

      if (args.length != 1) {
         System.out.println("Usage: Classname ServerPort");
         System.exit(1);
      }
      int ePort = Integer.parseInt(args[0]);

      new Thread(new ChatServer(ePort)).start();
   }

   // 0개설 안 1대기중 2겜
   public void createRoom(String inputLine, int clientID, String userName, PrintWriter out) {
      // TODO Auto-generated method stub

      if (roomCount < rooms.length) {// 최대 다섯개 게임방까지 가
         for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] == null) {
               rooms[i] = new RealGameRoom();

               if (rooms[i].getStatus() == 0) {
                  rooms[i].setHost(clientID);
                  rooms[i].setTitle(inputLine);
                  rooms[i].setStatus(1);
                  rooms[i].setRoomId(roomSeq);
                  rooms[i].increaseUSer();

                  roomSeq++;
                  roomCount++;

                  System.out.println(clientID + "클라이언트가 " + inputLine + " 제목의 방을 만들음 ");
                  String notice = "--------------------< " + inputLine + " >---------------------------------\n@@@ 접속유저 : " + userName ;
              
                  clients[whoClient(rooms[i].getHost())].out.println(notice);
                  clients[whoClient(rooms[i].getHost())].out.println("@@@ please wait..... .. .\n");
               }
               break;
            }

         }

      } else {
         clients[whoClient(clientID)].out.println("@@@ 방이 꽉 찼습니다. 더 이 방을 더 만들 수 없습니다.");
      }
   }// createRoom

   private void printf(String string) {
      // TODO Auto-generated method stub

   }

   public void destroyRoom(int RoomID) {
      for (int i = 0; i < rooms.length; i++) {
         if (rooms[i].getRoomId() == RoomID) {
            rooms[i] = new RealGameRoom();
            roomCount--;

         }
      }
   }

   public void joinRoom(int roomSeq, int clientID, String username) {

      for (int i = 0; i < rooms.length; i++) {
         if (rooms[i].getRoomId() == roomSeq) {
            if (rooms[i] == null) {
               System.out.println("@@@ 개설되지 않은 방입니다. ");
               break;
            } else

            {

               System.out.println("클라가선택한 방번호  " + roomSeq);

               rooms[i].setStatus(1);// 대기중
               rooms[i].setClient(clientID);
               rooms[i].increaseUSer();
               clients[whoClient(rooms[i].getHost())].out.println("@@@ "+username + "님이 접속했습니다.");
               clients[whoClient(rooms[i].getHost())].out.println("@@@ 접속유저 : "+clients[whoClient(rooms[i].getHost())].userName + ", " + username );
               clients[whoClient(clientID)].out.println("\n\n-------------------<" + rooms[i].getTitle() + ">-------------------------\n@@@ 접속유저 : "
                     + clients[whoClient(rooms[i].getHost())].userName + ", " + username );
               clients[whoClient(clientID)].out.println("\n@@@ Are you ready? (to start press y)");

               // clients[whoClient(rooms[i].getHost)]
               break;
            }
         }
      }
   }

   public void broadcast2(int i, String says) {
      clients[whoClient(rooms[i].getHost())].out.println(says);
      clients[whoClient(rooms[i].getClient())].out.println(says);

   }

   public void broadcast(int roomSeq, String says) {
      for (int i = 0; i < rooms.length; i++) {
         if (rooms[i].getRoomId() == roomSeq) {
            clients[whoClient(rooms[i].getHost())].out.println(says);
            clients[whoClient(rooms[i].getClient())].out.println(says);
         }
      }
   }

   public void startGame(int RoomID, PrintWriter out, BufferedReader in, int clientID) throws IOException {
      for (int i = 0; i < rooms.length; i++) {
         if (rooms[i].getRoomId() == RoomID) {
            // rooms[i].setStatus(5);// 방장이 게임시작
            out.println("게임로직슝");// out이 방장
            String inputLine;

            while (true) {

               inputLine = in.readLine();
               if (clientID == rooms[i].getHost()) {
                  clients[whoClient(rooms[i].getClient())].out.println(inputLine);
               } else
                  clients[whoClient(rooms[i].getHost())].out.println(inputLine);
            }

         }
      }

      // TODO DESTROY ROOM 만들고
      // TODO JOIN ROOM 만들고
      // TODO START GAME 만들고
      // 상대방에게 뭔갈 보낸다 뭐 그런거는 위에 PUT어쩔시구를 참조하시길.
      // 혹시 모를까 설명을 덧붙이자면 putClient 메써드는 INPUTLINE을 접속한 모든 사용자에게 BROADCASTING
      // 하는것임.
      // 보면 알거임.

   }

   class ChatServerRunnable implements Runnable {

      protected ChatServer chatServer = null;
      protected SSLSocket clientSocket = null;
      protected PrintWriter out = null;
      protected BufferedReader in = null;
      public int clientID = -1;
      public int clientStatus = 0;
      public String userName = "";
     // private boolean gameStartStatus = false;
      private int j =-1;
      

      public ChatServerRunnable(ChatServer server, SSLSocket socket) {

         System.out.println("ChatServerRunnable Constructor");

         this.chatServer = server;
         this.clientSocket = socket;
         clientID = clientSocket.getPort();

         try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         } catch (IOException i) {
         }
      }

      public void run() {
         boolean turn = true;
         System.out.println("ChatServerRunnable run()");

         // int gameReady = 0;
         String inputLine;

         try {// client status 0: 방금서버에 입장/ 1:방장 /2 :방찾는중/
            inputLine = in.readLine();
            userName = inputLine;
            System.out.println("user name :" + userName);
            out.println("\n@@@ Hello " + userName + "!! please choose one");
            out.println("");
            // System.out.println("First msg: "+inputLine);
            out.println("1. Make GameRoom ");
            out.println("2. Find Other's GameRoom ");
            out.println("3. Exit ");
            out.println();
            while ((inputLine = in.readLine()) != null) {
           //    if (clientStatus == 0) { // First message.
                  switch (inputLine) {
                  case "1":
                     clientStatus = 1; // Room host.
                     out.println("\n@@@ Enter the room name :");
                     inputLine = in.readLine();
                     this.chatServer.createRoom(inputLine, clientID, userName, out);

                     inputLine = in.readLine();
                     if (inputLine.equals("y")) {
                    	 
                        out.println("\n\nstart game!!\n\n");
                       // gameStartStatus = true;

                        for ( j = 0; j < rooms.length; j++) {
                            if (rooms[j].getHost() == clientID) {
                            
                              // rooms[j].setStatus(5);// 방장이 게임시작
                              clients[whoClient(rooms[j].getClient())].out.println("\n\ngame start!!\n\n");
                              rooms[j].setStatus(2);
                              rooms[j].setStart(true);
                              break;
                            }
                        }//// = 0; j < rooms.length; j++) 
                        
                       
                        
                     }
                     break;
                  case "2":
                     clientStatus = 2; // Finding room.
                     // TODO: 현재 개설된 방의 목록을 뿌려준다.
                     out.println("-----------------현재 개설된 방 목록--------------------");
                     out.println("방번호   방이름   접속유저 수     방상태 ");
                     int i = 0;
                     for (i = 0; i < roomCount; i++) {
                        out.println(
                              " " + (i + 1) + ".     " + rooms[i].getTitle() + "      [" + rooms[i].getPlayers()
                                    + "]      " +rooms[i].str_roomStatu(rooms[i].getStatus()));
                     }
                     out.println("----------------------------------------------------");
                     out.println("@@@ Choose room number you're going to enter");
                     inputLine = in.readLine();
                     int j = Integer.parseInt(inputLine);
                     while (true) {
                        if (rooms[j - 1].getPlayers() == 2) {

                           out.println("@@@ 접속 유저가 1명인 다른방을 선택해주세요 ");
                           inputLine = in.readLine();
                           j = Integer.parseInt(inputLine);

                        } else
                           break;
                     }

                     System.out.println("유저가 선택한 방 : " + inputLine);
                     int num = Integer.parseInt(inputLine);
                      clientStatus = 5;
                      this.j = num-1;
                     chatServer.joinRoom(num - 1, clientID, userName);
                     inputLine = in.readLine();
                     if (inputLine.equals("y")) {
                        out.println("@@@ please wait for the host to press the game start!!");
                        clients[whoClient(rooms[this.j].getHost())].out.println("@@@ to start game press y");
                     }

                     break;

                  case "3":
                     clientStatus = 3; // Disconnect.
                     out.println(".bye.");
                     break;
                  default:
                     clientStatus = 0; // Error;
                     out.println("@@@ ERROR!! Wrong command. please retry.");
                     break;
                  }//스위치문 종료    
                  
                  if( clientStatus == 5 ) {
                	  synchronized(this) {
                		  try {
							this.wait(100000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	  }
                  } else if (clientStatus == 1){
                	  synchronized(clients[whoClient(rooms[this.j].getClient())]) {
                		  clients[whoClient(rooms[this.j].getClient())].notify();
                	  }
                  }

                  if (rooms[j].isStart() == true) {
                  ////////여기부터 게임시작 코드 ////////////////////////
                	  if (clientStatus == 1){
                  broadcast2(j,"------------------------------------------------------------ ");
                  broadcast2(j,"< Enter 1 ,   If you want to present your card for your turn > ");
                  broadcast2(j,"< Enter 2 , If you want to ring the bell > ");
                  broadcast2(j,"< Enter 3 , If you want to send a message to another player >");
                  broadcast2(j,"   Ex) 3");
                  broadcast2(j,"   host : first turn,    guest : next ");
                  broadcast2(j,"------------------------------------------------------------\n\n ");
                 
                	  }
                  
                  while ((inputLine = in.readLine()) != null) {
                     switch(inputLine){
                     case "1":
                        
                        if(turn ==true){
                           
                           if(getClientID() == rooms[j].getHost()){
                              
                           clients[whoClient(rooms[j].getHost())].out.println("Player 1 Cards : "+"|"+chatServer.getCardList(1).get(0).get_color()
                                 +""+chatServer.getCardList(1).get(0).get_number()+"|");
                           if(chatServer.getCardList(0).cardListSize() != 0)
                              clients[whoClient(rooms[j].getHost())].out.println("Player 2 Cards : "+"|"+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_color()
                                 +""+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_number()+"|");
                           else
                              clients[whoClient(rooms[j].getHost())].out.println("Player 2 Cards : "+"|  |");
                           
                           
                           clients[whoClient(rooms[j].getClient())].out.println("Player 1 Cards : "+"|"+chatServer.getCardList(1).get(0).get_color()
                                 +""+chatServer.getCardList(1).get(0).get_number()+"|");
                           if(chatServer.getCardList(0).cardListSize() != 0)
                              clients[whoClient(rooms[j].getClient())].out.println("Player 2 Cards : "+"|"+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_color()
                                 +""+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_number()+"|");
                           else
                              clients[whoClient(rooms[j].getClient())].out.println("Player 2 Cards : "+"|  |");
                              
                           
                           chatServer.getCardList(0).add(chatServer.getCardList(1).get(0));
                           chatServer.getCardList(1).removeFirst();
                           
                           broadcast2(j,"The number of Stack Cards : "+chatServer.getCardList(0).cardListSize());
                           broadcast2(j,"The number of Player 1's Cards : "+chatServer.getCardList(1).cardListSize());
                           broadcast2(j,"The number of Player 2's Cards : "+chatServer.getCardList(2).cardListSize());
                           broadcast2(j,"------------------------------------------------------------");
                           
                           }
                        else if(getClientID() == rooms[j].getClient()){
                           clients[whoClient(rooms[j].getClient())].out.println("This time is not your turn");
                        }
                        turn = !turn;
                     }
                        
                        else if(turn == false){
                           if(getClientID() == rooms[j].getClient()){//
                              
                              clients[whoClient(rooms[j].getClient())].out.println("Player 1 Cards : "+"|"+chatServer.getCardList(1).get(0).get_color()
                                    +""+chatServer.getCardList(1).get(0).get_number()+"|");
                              if(chatServer.getCardList(0).cardListSize() != 0)
                                 clients[whoClient(rooms[j].getClient())].out.println("Player 2 Cards : "+"|"+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_color()
                                    +""+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_number()+"|");
                              else
                                 clients[whoClient(rooms[j].getClient())].out.println("Player 2 Cards : "+"|  |");
                              
                              
                              clients[whoClient(rooms[j].getHost())].out.println("Player 1 Cards : "+"|"+chatServer.getCardList(1).get(0).get_color()
                                    +""+chatServer.getCardList(1).get(0).get_number()+"|");
                              if(chatServer.getCardList(0).cardListSize() != 0)
                                 clients[whoClient(rooms[j].getHost())].out.println("Player 2 Cards : "+"|"+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_color()
                                    +""+chatServer.getCardList(0).get(chatServer.getCardList(0).cardListSize()-1).get_number()+"|");
                              else
                                 clients[whoClient(rooms[j].getHost())].out.println("Player 2 Cards : "+"|  |");
                                 
                              
                              chatServer.getCardList(0).add(chatServer.getCardList(1).get(0));
                              chatServer.getCardList(1).removeFirst();
                              
                              broadcast2(j,"The number of Stack Cards : "+chatServer.getCardList(0).cardListSize());
                              broadcast2(j,"The number of Player 1's Cards : "+chatServer.getCardList(1).cardListSize());
                              broadcast2(j,"The number of Player 2's Cards : "+chatServer.getCardList(2).cardListSize());
                              broadcast2(j,"------------------------------------------------------------");
                              
                              }
                           else if(getClientID() == rooms[j].getHost()){
                              clients[whoClient(rooms[j].getHost())].out.println("This time is not your turn");
                           }
                           turn = !turn;
                        }
                        
                        break;
                        
                     case "2":
                        
                        broadcast2(j,"벨!");
                        break;
                        
                     case "3" :
                        if(getClientID() == rooms[j].getHost()){
                           inputLine = in.readLine();
                           clients[whoClient(rooms[j].getClient())].out.println("Player 2 :"+inputLine);
                        }
                        else if(getClientID() == rooms[j].getClient()){
                           inputLine = in.readLine();
                           clients[whoClient(rooms[j].getHost())].out.println("Player 1 :"+inputLine);
                        }
                        break;
                     }//스위치종료
                  
                  
                  }//와일종료
         }//겜코드 
                  
               if (inputLine.equals("exit")) {
                  out.println(".bye.");
                  break;
               }
            }//스위치문 종

            // }

            
            
            
            
            chatServer.delClient(getClientID());
         } catch (

         SocketTimeoutException ste)

         {
            System.out.println("Socket timeout Occured, force close() : " + getClientID());
            chatServer.delClient(getClientID());
         } catch (

         IOException e)

         {
        	 System.err.println(e.getLocalizedMessage());
        	 e.printStackTrace();
            chatServer.delClient(getClientID());
         }
      }

      /*
       * private void println(String string) { // TODO Auto-generated method
       * stub
       * 
       * }
       */
      public int getClientID() {

         System.out.println("ChatServerRunnable getClientID()");

         return clientID;
      }

      public void close() {

         System.out.println("ChatServerRunnable close()");

         try {
            if (in != null)
               in.close();
            if (out != null)
               out.close();
            if (clientSocket != null)
               clientSocket.close();
         } catch (IOException i) {

         }
      }
   }

public void startGame(int j) {
	// TODO Auto-generated method stub
	
}
}