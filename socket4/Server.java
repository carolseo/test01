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
		//List<EchoThread> list2 = new Vector<>(); ��� �ٸ� �ɼ�
		try {
			server = new ServerSocket(9400);
			System.out.println("Ŭ���̾�Ʈ�� ������ �����");
			while(true){
			Socket socket = server.accept();
			list.add(socket); //socket ������ list�� ����
			new EchoThread(socket,list).start();  //list �߰� 
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
	List<Socket> list;  //����Ʈ �߰�
	Socket socket; 
	
	public EchoThread() {}
	public EchoThread(Socket socket,List<Socket> list) { //������method �߰���
		this.socket = socket;
		this.list = list; // list parameter �߰���
	}
	@Override
	public void run() {
		InetAddress address = socket.getInetAddress();
		System.out.println(address.getHostAddress()+" �κ��� ������ �����߽��ϴ�.");
		try{
		InputStream in = socket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	
		String message = null;
		while((message = br.readLine())!= null){ 
			broadcast(message); //broadcast method ȣ�� 
		}
		br.close();
		} catch (Exception e) {
		} finally {
			try {
				list.remove(socket); //list�� socket ���� 
				System.out.println(socket+" �� ������ �����Ǿ����ϴ�.");  //�������� print
				System.out.println(list);
				if(socket !=null) socket.close();  //socket �ڿ��ݳ� finally�� �̵���
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized void broadcast(String msg) throws IOException{  //��� ���Ͽ� �޽��� �Ѹ���(EchoThread�� ���� method)
		for(Socket socket:list){ 
			OutputStream out = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out)); 
			pw.println(msg); 
			pw.flush();
		}
	}
}

