package com.java.POSClient;

public class CustomInterface {
	public interface ReceiveCallback {
        void onClear();
        void onConnection();
        void onReceiveMenuList(String[] menuList);
    }
}
