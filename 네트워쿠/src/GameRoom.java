import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameRoom {
	List<GameUser> userList;
	GameUser roomOwner; //방장
	String roomName;// 방 이름
	
	public GameRoom(){//아무도 없는 방을 생성할 때
		userList = new ArrayList<GameUser>();
	}
	
	public GameRoom(GameUser user){ //유저가 방을 만들 때
		userList = new ArrayList<GameUser>();
		userList.add(user);
		this.roomOwner = user;//방장을 유저로
	}
	
	public GameRoom(List<GameUser> _userList){//유저 리스트가 방을 생성할 때
		this.userList = _userList;//유저리스트 복사
		this.roomOwner = userList.get(0);//첫번째 유저를 방장으로 설정
	}
	
	
	public void EnterRoom(GameUser user){
		userList.add(user);
		
	}
	
	public void ExitRoom(GameUser user){
		userList.remove(user);
		
		if(userList.size() < 1){
			RoomManager.RemoveRoom(this);
			return;
		}
		else if(userList.size() <2){
			this.roomOwner = userList.get(0);
			return;
		}
	}
	
	
	public void Broadcast(byte[] data) {
		for (GameUser user : userList) {
			// 방에 속한 유저의 수만큼 반복 // 각 유저에게 데이터를 전송하는 메서드 호출~ // ex) 
			//user.SendData(data); //
			try 
			{ // 
				user.sock.getOutputStream().write(data); // 이런식으로 바이트배열을 보낸다. // 
				} catch (IOException e) { 
					// // TODO Auto-generated catch block // e.printStackTrace(); //
					} 
			}
		}
	
	
	public void SetOwner(GameUser _user) { 
		this.roomOwner = _user; // 특정 사용자를 방장으로 변경한다. 
		}
	
	public void SetRoomName(String _name) { // 방 이름을 설정 
		this.roomName = _name; 
		} 
	
	public GameUser GetUserByNickName(String _nickName){
		// 닉네임을 통해서 방에 속한 유저를 리턴함
		for(GameUser user : userList){
			if(user.nickName.equals(_nickName)){ 
				return user; // 유저를 찾았다면 } 
				} 
			}
		return null; // 찾는 유저가 없다면
	} 
		
	public String GetRoomName() { // 방 이름을 가져옴
		return roomName; 
	} 
	public int GetUserSize() { // 유저의 수를 리턴
		return userList.size(); 
	}
	public GameUser GetOwner() { // 방장을 리턴
		return roomOwner; 
	}
			
		
	

		
	
	
}
