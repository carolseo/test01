package socket4; // BroadCast

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;


public class Server {
	public static void main(String args[]){
		ServerSocket server = null;
		List<Socket> list = new Vector<>();
		//List<EchoThread> list2 = new Vector<>(); 상단 다른 옵션
		try {
			server = new ServerSocket(9400);
			System.out.println("클라이언트의 접속을 대기중");
			while(true){
			Socket socket = server.accept();
			list.add(socket); //socket 생성후 list에 담음
			new EchoThread(socket,list).start();  //list 추가 
			}
		} catch (Exception e){
			System.out.println(e.getMessage());
		} finally {
			try{
			if(server !=null) server.close();
			}catch (IOException e){
				e.printStackTrace();
			}
	}
 }	
}

class EchoThread extends Thread{
	List<Socket> list;  //리스트 추가
	Socket socket; 
	
	public EchoThread() {}
	public EchoThread(Socket socket,List<Socket> list) { //생성자method 추가됨
		this.socket = socket;
		this.list = list; // list parameter 추가됨
	}
	@Override
	public void run() {
		InetAddress address = socket.getInetAddress();
		System.out.println(address.getHostAddress()+" 로부터 서버에 접속했습니다.");
		try{
		InputStream in = socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
		String message = null;
		while((message = br.readLine())!= null){ 
			broadcast(message); //broadcast method 호출 
		}
		br.close();
		} catch (Exception e) {
		} finally {
			try {
				list.remove(socket); //list내 socket 삭제 
				System.out.println(socket+" 의 접속이 해제되었습니다.");  //접속해제 print
				System.out.println(list);
				if(socket !=null) socket.close();  //socket 자원반납 finally로 이동됨
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized void broadcast(String msg) throws IOException{  //모든 소켓에 메시지 뿌리기(EchoThread가 가진 method)
		for(Socket socket:list){ 
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out)); 
			pw.println(msg); 
			pw.flush();
		}
	}
}

