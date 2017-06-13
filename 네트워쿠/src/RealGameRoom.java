
public class RealGameRoom {
	private int roomId;
	private int host;//방장 
	private int client;
	//private int client2;
	private String title;//방 제 
	private int status;//방 상태 
	private int players;//플레이어 수 
	boolean start = false;
	//private int roomSeq;
	

	private String waiting = "waiting..";
	private String playing = "playing game";
	
	public RealGameRoom() {
		super();
		this.roomId = 0;
		this.host = 0;
		this.client = 0;
		this.title = "";
		this.status = 0;
		this.players = 0;
	}
	/*
	public void enterRoom(String title, int players, String host_name){
		
	}
	*/
	
	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}
	
	
	public String str_roomStatu(int status){
		if(status == 1){
			return waiting;
		}
		else if(status == 2){
			return playing;
		}
		else return null;
		
	}
	
	public void increaseUSer(){
		this.players ++;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	

	public int getHost() {
		return host;
	}
	public void setHost(int host) {
		this.host = host;
	}
	public int getClient() {
		return client;
	}
	
	public void setClient(int client) {
		this.client = client;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
		//0개설 안된 상태 1대기중 2게임
	}
	public int getPlayers() {
		return players;
	}
	public void setPlayers(int players) {
		this.players = players;
	}
	
	
}
