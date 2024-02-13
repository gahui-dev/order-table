package com.java.POSSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.java.POSSystem.CustomInterface.*;

public class ClientSocketReceiver extends Thread{
	private Socket client = null;
	private BufferedReader receiveMessage = null;
	
	private RequestReceivedCallback callback;
	
	private int tableNumber;
	public int getTableNumber() {
		return tableNumber;
	}

	public ClientSocketReceiver(Socket socket, RequestReceivedCallback callback) {
        this.client = socket;
        this.callback = callback;
    }
	
	@Override
    public void run() {
        try (BufferedReader receiveMessage = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            //BufferedWriter sendMessage = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            while (true) {
                String message = receiveMessage.readLine();
                if (message == null) {
                    System.out.println("클라이언트와의 연결이 끊겼습니다.");
                    break;
                }
                
                if(!message.substring(0, 10).equals("connectID:")) {
                    switch (message) {
                        case "getMenuList":
                            System.out.println("클라이언트로부터 메뉴 목록 조회 요청을 받았습니다.");
                            sendMenuXmlToClient();
                            break;
                        case "requestOrder" :
                            System.out.println("클라이언트로부터 음식 주문이 들어왔습니다.");
                            message = receiveMessage.readLine();
                            if (callback != null) {
                                callback.onRequestReceived("requestOrder", message);
                            }
                            message = receiveMessage.readLine();
                            if(message.equals("EndPacket")) {
                                message = "";
                            }
                            break;
                        case "requestMessage" :
                        	StringBuilder sb = new StringBuilder();
                        	message = receiveMessage.readLine();
                        	sb.append(message).append("\n");
//                        	System.out.println(message + " 번 테이블에서 메시지가 도착했습니다.");
//                        	message = receiveMessage.readLine();
                        	while(true) {
                        		message = receiveMessage.readLine();
                        		if(!message.equals("EndPacket")) {
                        			sb.append(message).append("\n");	
                        		} else {
                        			break;
                        		}
                        	}
                        	if (callback != null) {
                                callback.onRequestReceived("requestMessage", sb.toString());
                            }
                        	message = "";
                        	break;
                        default:
                            System.out.println("정의되지 않은 명령어 수신 : " + message);
                            break;
                    }
                }
                else {
                    System.out.println(message.split(":")[1] + " 테이블이 연결되었습니다.");
                    if (callback != null) {
                        //clientList.put(Integer.parseInt(message.split(":")[1].toString()), this);
                    	this.tableNumber = Integer.parseInt(message.split(":")[1]);
                        callback.onRequestReceived("connected", message.split(":")[1]);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("클라이언트와의 연결이 끊겼습니다.");
        }
    }

    public void sendMessage(String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            bw.write(message);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void sendMenuXmlToClient() {
        try {
        	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            File menuXmlData = new File(Common.getMenuDatabaseConnectionString());
            BufferedReader br = new BufferedReader(new FileReader(menuXmlData));

            String line;
            StringBuilder sb = new StringBuilder();
            sb.append("ResponseMenuList" + "\n");
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            sb.append("EndPacket" + "\n");
            bw.write(sb.toString());
            br.close();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void close() {
    	if(client != null) {
    		try {
    			client.close();	
    		}catch (Exception e) {
				// TODO: handle exception
			}
    	}
    }
}
