package com.java.POSSystem;


import java.util.ArrayList;

import com.java.POSSystem.Model.*;

public class CustomInterface {
	public interface ConnectedClientCallback {
		void onClientConnected(int tableNumber, ClientSocketReceiver receiver);
	}
	public interface RequestReceivedCallback {
        void onRequestReceived(String header, String message);
    }

    public interface TableSubmitCallback {
        void onSubmit(int tableNumber, ArrayList<Order> orderInfo);
    }
    
    public interface MessageSendCallback {
    	void onMessageSend(int tableNumber, String message);
    }
    
    // 소켓과 관련된 이벤트들
    public interface SocketEventListener {
    	void onConnected();
    	void onDisCOnnected();
    	void onAccept();
    }
    
}
