
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPoolServerDo {
	
	public static void main(String args[]) {
		
		ThreadPoolServer server = null;
		if (args.length != 1) {
			System.out.println("Usage: Classname ServerPort");
			System.exit(1);
		}

		int serverPort = Integer.parseInt(args[0]);
	
		server = new ThreadPoolServer(serverPort);
		new Thread(server).start(); // call ThreadPoolServer's run()
		
		try {
			Thread.sleep(300 * 1000);  // 1000 (1sec)
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		server.shutdownAndAwaitTermination();
	}
}

class ThreadPoolServer implements Runnable {

	protected int serverPort			= 0000;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped			= false;
	private final int poolCount			= 10;
	
	// private ThreadPoolExecutor threadPoolExecutor;
	
	ExecutorService threadPool = Executors.newFixedThreadPool(poolCount);;

	public ThreadPoolServer(int port) {
		this.serverPort = port;
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
			System.out.println("Opened ServerSocket,");
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port "+serverPort, e);
		}
	}

	public void run(){
		/* // check thread status
        if (threadPool instanceof ThreadPoolExecutor) {
            threadPoolExecutor = (ThreadPoolExecutor) threadPool;
        } else {
            threadPoolExecutor = null;
            System.out.println("This executor doesn't support ThreadPoolExecutor ");
        } // check thread status */
        
		try {
			for (;;) {
				threadPool.execute(new PoolRunnable(serverSocket.accept()));
				
				/*// check thread status
		        if (threadPoolExecutor != null) {
		            do {
		                System.out.println("- Active:" + threadPoolExecutor.getActiveCount()
		                	+ " MaxPoolValue: " + threadPoolExecutor.getMaximumPoolSize());
		                try {
		                    Thread.sleep(timeDiff);
		                } catch (Exception e) {
		                }
		            } while(threadPoolExecutor.getActiveCount() > 1);
		        } // check thread status */
			}
	     } catch (IOException ex) {
	    	 ex.printStackTrace();
	    	 shutdownAndAwaitTermination();
	     }
	}
	
	void shutdownAndAwaitTermination() {
		this.threadPool.shutdown();
		try {
			// Wait a while for existing tasks to terminate
			if (!this.threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				this.threadPool.shutdownNow();
				if (!this.threadPool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("threadPool did not terminate");
			}
		} catch (InterruptedException ie) {
			this.threadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}

class PoolRunnable implements Runnable {

	private Socket acceptedSocket = null;
	private String threadName = null;
	
	public PoolRunnable(Socket acceptedSocket){
		this.acceptedSocket = acceptedSocket;
	}
	
	public void run() {
		try{
			threadName = Thread.currentThread().getName();
			InputStream input = acceptedSocket.getInputStream();
			OutputStream output = acceptedSocket.getOutputStream();
			LocalDateTime timePoint = LocalDateTime.now();
			for(int i = 0;i<50;i++){
				output.write(("\n\rHTTP/1.1 response sequence "+ i + " OK"+"\n\rPoolProcess: '"+threadName+"' : "+timePoint+"").getBytes());
				
				System.out.println("Request processed, work with '"+threadName+"' at, "+timePoint);
				System.out.println("");
				
				try{
					Thread.sleep(2000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			
			output.close();
			input.close();
			acceptedSocket.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
