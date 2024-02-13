package com.java.POSClient;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.lang.reflect.Field;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.java.POSClient.Model.MenuItemModel;


public class Main {
	// IP와 Port는 StartDialog에서 받아와야 함
	public static int SERVER_PORT;
	public static String SERVER_IP;

	public static Socket serverSocket;
	public static StartDialog startDialog; 
	public static void main(String[] args) {
		System.setProperty("file.encoding","UTF-8");
		try{
			Field charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null,null);
		}
		catch(Exception e){
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				// MainFrame 객체 생성 및 할당
				MainFrame mainFrame = new MainFrame();
				
				//  MainFrame 객체에 이벤트 연결
				mainFrame.addWindowListener(new WindowAdapter() {
					
					// MainFrame 창이 닫히고 나면
					@Override
					public void windowClosed(WindowEvent e) {
						try {
							serverSocket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						serverSocket = null;
						if(startDialog != null) {
							startDialog.dispose();
						}
						mainFrame.dispose();
					}
				});

				
				// startDialog 객체 생성 및 할당
				startDialog = new StartDialog(new CustomInterface.ReceiveCallback() {
					public void onClear() {

					}

					public void onReceiveMenuList(String[] menuList) {

					}

					@Override
					public void onConnection() {
						try {
							System.out.println("MAIN에서의 SERVER_IP > " + Main.SERVER_IP);
							System.out.println("MAIN에서의 SERVER_PORT > " + Main.SERVER_PORT);

							serverSocket = new Socket(SERVER_IP, SERVER_PORT);
							System.out.println("서버와 연결되었습니다.");

							String tableNumber = startDialog.getTableNumber();

							SocketSender socketSender = new SocketSender(serverSocket, tableNumber, new CustomInterface.ReceiveCallback() {
								@Override
								public void onClear() {
									mainFrame.clearAll();
								}

								public void onConnection() {

								};

								public void onReceiveMenuList(String[] menuList) {
									MenuItemModel menuModel;

									for(int i = 0; i < menuList.length; i++) {
										if(!menuList[i].equals("")) {
											menuModel = new MenuItemModel();

											menuModel.setMenuId(XMLTagValue(menuList[i], "menuId"));
											menuModel.setMenuName(XMLTagValue(menuList[i], "menuName"));
											menuModel.setCategory(XMLTagValue(menuList[i], "category"));
											menuModel.setPrice(Integer.parseInt(XMLTagValue(menuList[i], "price")));
											menuModel.setCookTime(XMLTagValue(menuList[i], "cookTime"));
											menuModel.setProductImage(XMLTagValue(menuList[i], "productImage"));
											menuModel.setDescription(XMLTagValue(menuList[i], "description"));

											mainFrame.addMenuItemModel(menuModel);
										}
									}

									mainFrame.setCategoryTab();
								}
							});

							startDialog.dispose();
							mainFrame.setSocketSender(socketSender);
							mainFrame.setTableNumber(tableNumber);
							mainFrame.showDialog();


							socketSender.start();
						}catch(ConnectException ce) {
							JOptionPane.showMessageDialog(null, "서버에 연결 할 수 없습니다.", "Message", JOptionPane.ERROR_MESSAGE);
						}catch(Exception ex){
							ex.printStackTrace();
							startDialog.dispose();
							mainFrame.dispose();
						}
					}
				});

				startDialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {

					}
				});
				startDialog.showDialog();
			}
		});
	}

	public static String XMLTagValue(String original, String tagName) {
		int startTagIndex = original.indexOf("<" + tagName + ">") + ("<" + tagName + ">").length();
		int endTagIndex = original.indexOf("</" + tagName + ">");
		return  original.substring(startTagIndex, endTagIndex);
	}
}