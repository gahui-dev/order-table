package com.java.POSSystem;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.java.POSSystem.CustomInterface.*;

public class Server extends Thread {
	// ServerSocket이라는 클래스의 전역 객체를 private로 선언했다.
	private ServerSocket serverSocket;
	//	private static final String HOST_IP = "127.0.0.1";
	//	private static final int PORT = 5005;

	// 다이얼로그에서 받아와야 함.
	public static String HOST_IP;
	public static int PORT;



	private RequestReceivedCallback callback;

	ArrayList<ClientSocketReceiver> clientList = new ArrayList<>();

	private volatile boolean isRunning = true;  

	public Server(RequestReceivedCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			System.out.println("메인에서 IP > " + HOST_IP);
			System.out.println("메인에서 Port > " + PORT);

			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(HOST_IP, PORT));
			System.out.println("서버가 " + PORT + " 포트에서 실행 중입니다.");

			// 몇개의 클라이언트가 연결된지 아무도 모르기 때문에
			// 클라이언트 소켓을 선언만 해두기.
			// 아래쪽에 while 반복문 안에서
			// client 가 연결될때 (accept) 
			// 클라이언트 소켓을 생성하기위해서 (여러 클라이언트의 연결을 받아서 처리하기 위해)
			
			Socket clientSocket;
			
			while (isRunning) {
				// System.out.println("클라이언트의 접속을 대기합니다.");
				clientSocket = serverSocket.accept();
				// System.out.println("클라이언트가 연결되었습니다." + clientSocket.toString());
				
				ClientSocketReceiver receiver = new ClientSocketReceiver(clientSocket, callback);
				clientList.add(receiver);
				receiver.start();
			}
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	public void sendMessage(int tableNumber, String message) {
		for(ClientSocketReceiver clientSocket : clientList) {
			if(clientSocket.getTableNumber() == tableNumber) {
				clientSocket.sendMessage(message);
				break;
			}
		}
	}
	
	
	// 전체 테이블 메세지 전송
//	public void sendMessage(String message) {
//		for(ClientSocketReceiver clientSocket : clientList) {
//			if(clientSocket.getTableNumber() == tableNumber) {
//				clientSocket.sendMessage(message);
//				break;
//			}
//		}
//	}

	public void socketClose() {
		isRunning = false;
		if (serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}

		if(clientList.size() > 0) {
			for(ClientSocketReceiver clientSocketReceiver : clientList) {
				clientSocketReceiver.close();
			}
		}
		try
		{
			this.join();
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}

	}
}
