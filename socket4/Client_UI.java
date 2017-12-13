package socket4; 

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;

public class Client_UI {
	JFrame f = new JFrame("채팅 클라이언트");
	Button b1, b2;
	TextField ip, port, id, msg;
	TextArea ta;
	Socket socket = null;
	BufferedReader br; 
	PrintWriter pw;
	
	Client_UI(){
		Panel p1 = new Panel();
		p1.add(ip = new TextField("70.12.109.59"));
		p1.add(port = new TextField("9400"));
		p1.add(b1 = new Button("연결"));
		p1.add(b2= new Button("종료"));
		f.add(p1,BorderLayout.NORTH);
		f.add(ta= new TextArea(25,30));
		Panel p2 = new Panel();
		p2.add(id= new TextField(10));
		p2.add(msg= new TextField(30));
		f.add(p2, BorderLayout.SOUTH);
		f.setSize(400, 350);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);	
		//연결 버튼 수행 처리
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//String name =id.getText(); //args 대신 id로 받음
				try {
					socket = new Socket(ip.getText(),9400);
					ta.append("***** 채팅 프로그램에 접속되셨습니다. *****");
					InputStream in = socket.getInputStream(); // 이동
					br = new BufferedReader(new InputStreamReader(in));
					OutputStream out = socket.getOutputStream();
					pw = new PrintWriter(new OutputStreamWriter(out));
					
					new ClientThread2(socket,br,ta).start();  //Thread 생성
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		//메시지창에 입력한 내용을 서버에 보내기
		msg.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				pw.println(id.getText()+":"+msg.getText());  //ID와 메시지 출력
				pw.flush();
				msg.setText(""); //메시지창 clear
			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket.close(); 	//자원반납
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String args[]){
		new Client_UI(){
		};
	}
}
		
	class ClientThread2 extends Thread{
		Socket socket;
		BufferedReader br; 
		TextArea ta; //TextArea추가 
		
		public ClientThread2() {}
		public ClientThread2(Socket socket, BufferedReader br, TextArea ta) {  //생성자 추가 
			this.socket = socket;
			this.br = br;
			this.ta = ta;
		}
		public void run(){
			try{
				String msg = null;
				while((msg = br.readLine()) != null ){
					ta.append(msg+"\n"); //텍스트입력창 연결 (setText는 하나만)
				}
			}catch (Exception e) {
			}finally{
			}
		}
	}


