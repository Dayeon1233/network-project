import java.util.ArrayList;
import java.util.List;

public class RoomManager {
	static List<GameRoom> roomList;
	
	public RoomManager(){
		roomList = new ArrayList<GameRoom>();//방 리스트
	}
	

	public GameRoom CreateRoom(){//룸 새로 생성(빈 방)
		GameRoom room = new GameRoom();
		roomList.add(room);
		System.out.println("Room Created");
		return room;
	}
	
	public GameRoom CreateRoom(GameUser _owner){ // 유저가 방을 생성할 때 사용(유저가 방장으로 들어감) 
		GameRoom room = new GameRoom(_owner); 
		roomList.add(room); 
		System.out.println("Room Created!"); 
		return room;
		} 
	
	public GameRoom CreateRoom(List<GameUser> _userList){
		GameRoom room = new GameRoom(_userList);
		roomList.add(room); System.out.println("Room Created!"); 
		return room;
		} 
	
	public static void RemoveRoom(GameRoom _room){
		roomList.remove(_room); // 전달받은 룸을 제거한다. 
		System.out.println(_room+"Deleted!");
		} 
	
	public static int RoomCount(){
		return roomList.size();
		} // 룸의 크기를 리턴
	
	

}
