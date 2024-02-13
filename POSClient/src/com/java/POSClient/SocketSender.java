package com.java.POSClient;

import java.io.*;
import java.net.Socket;

public class SocketSender extends Thread {
	private Socket socket;
    private String tableNumber;

    CustomInterface.ReceiveCallback receiveCallback;
    
	public SocketSender(Socket socket, String tableNumber, CustomInterface.ReceiveCallback callback) {
        this.socket = socket;
        this.tableNumber = tableNumber;
        this.receiveCallback = callback;
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write("connectID:" + tableNumber + "\n");
            bw.flush();
            bw.write("getMenuList" + "\n");
            bw.flush();
        } catch (IOException e) {

        }
    }
	
	@Override
    public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            while (true) {
                String message = br.readLine();
                switch (message) {
                    case "ClearAll" :
                        if(receiveCallback != null) {
                            receiveCallback.onClear();
                        }
                        break;
                    case "ResponseMenuList" :
                    	System.out.println("메뉴리스트 도착");
                    	message = br.readLine();
                    	StringBuilder menuXml = new StringBuilder();
                    	while (!message.equals("EndPacket")) {
                            menuXml.append(message);
                            message = br.readLine();
                        }
                    	
                    	message = menuXml.substring(menuXml.indexOf("<menuItems>") + "<menuItems>".length(), menuXml.indexOf("</menuItems>")).replaceAll("\\s", "");
                    	String[] menuList = message.split("<menuItem>");
                    	if(receiveCallback != null) {
                            receiveCallback.onReceiveMenuList(menuList);
                        }
                    	break;
                    default:
                        break;
                }
            }
        }
        catch (IOException e) {

        }
	}
	
	public void sendMessage(String message) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(message + "\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("클라이언트 IO 예외가 발생하였습니다.");
        }
    }
}
