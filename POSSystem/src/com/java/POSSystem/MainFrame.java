package com.java.POSSystem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.Border;
import com.java.POSSystem.UserComponent.OrderManagementPanel;
import com.java.POSSystem.UserComponent.MenuManagementPanel;
import com.java.POSSystem.CustomInterface.*;

import com.java.POSSystem.Model.*;

public class MainFrame extends JFrame {
	private JPanel headerPanel;
	private JLabel logoLabel;
	private JLabel userNameLabel;
	private JPanel bottomPanel;

	private OrderManagementPanel orderManagementPanel;
	private MenuManagementPanel menuManagementPanel;

	// Server Class의 Object(객체) "server"를 선언
	private Server server;

	public MainFrame(UserInfo userInfo) {
		Init();

		userNameLabel.setText("[" + userInfo.getUserName() + "] 님, 반갑습니다. ");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				// "server" 객체를 생성하고 메모리 할당
				server = new Server(new RequestReceivedCallback() {
					@Override
					public void onRequestReceived(String header, String message) {
						switch (header) {
						case "connected" :
							orderManagementPanel.ConnectionTable(Integer.parseInt(message));
							break;
						case "requestOrder" :
							System.out.println("여기서 주문내역 파싱 후 적용");
							System.out.println(message);
							String[] orders = message.split("<order>");
							orderManagementPanel.clearOldOrderData(Integer.parseInt(Common.XMLTagValue(orders[1], "tableNumber")));
							Order order;
							for(int i = 0; i < orders.length; i++) {
								if(!orders[i].equals("")) {
									order = new Order();

									order.setOrderId(Common.XMLTagValue(orders[i], "id"));
									order.setMenuId(Common.XMLTagValue(orders[i], "menuId"));
									order.setMenuName(Common.XMLTagValue(orders[i], "menuName"));
									order.setTableNumber(Common.XMLTagValue(orders[i], "tableNumber"));
									order.setMenuCount(Integer.parseInt(Common.XMLTagValue(orders[i], "count")));
									order.setTotalPrice(Integer.parseInt(Common.XMLTagValue(orders[i], "price")));
									order.setRequestMemo(Common.XMLTagValue(orders[i], "requestMemo"));
									
									orderManagementPanel.updateOrderDataTable(Integer.parseInt(order.getTableNumber()), order);
								}
							}
							break;
						case "requestMessage" :
							int reqTableNumber = Integer.parseInt(message.substring(0, message.indexOf("\n")));
							message = message.substring(message.indexOf("\n") + 1);
							MessageDialog dlg = new MessageDialog(reqTableNumber, message);
							dlg.showDialog();
							break;
						}
					}
				});
				
				// Thread를 상속받아 만든 SEerver 클래스의 Thread를 시작
				server.start();
				System.out.println("소켓통신을 위한 서버가 실행되었습니다.");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				if(server != null) {
					server.socketClose();
				}
			}
		});
	}

//	private boolean isVisible = false;
//	public void setVisible(boolean value) {
//		isVisible = value;
//		super.setVisible(value);
//	}

	
	public void showDialog() {
		setVisible(true);
	}
	
	public void hideDialog() {
		setVisible(false);
	}
	
	public void visibleDialog(boolean isVisible) {
		setVisible(isVisible);
	}
	
	private void Init() {
		setTitle("Order Table POS System");
		setSize(1280, 800);

		Color color = new Color(0, 102, 255);
		Border paddingBorder = BorderFactory.createEmptyBorder(20, 10, 20, 10);

		headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(color);

		logoLabel = new JLabel("아침부터 자정까지");
		Font labelFont = logoLabel.getFont();
		logoLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20));

		userNameLabel = new JLabel("");
		labelFont = userNameLabel.getFont();
		userNameLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20));

		logoLabel.setBorder(paddingBorder);

		headerPanel.add(logoLabel, BorderLayout.WEST);
		headerPanel.add(userNameLabel, BorderLayout.EAST);

		JTabbedPane tabbedPane = new JTabbedPane();
		orderManagementPanel = new OrderManagementPanel(new MessageSendCallback() {
			@Override
			public void onMessageSend(int tableNumber, String message) {
				if(server != null) {
					server.sendMessage(tableNumber, message);
				}
			}
		});
		menuManagementPanel = new MenuManagementPanel();

		tabbedPane.addTab("주문 관리", orderManagementPanel);
		tabbedPane.addTab("메뉴 관리", menuManagementPanel);

		bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton exitButton = new JButton("프로그램 종료");
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(
						MainFrame.this, 
						"종료하시겠습니까?", 
						"종료 확인",
						JOptionPane.OK_CANCEL_OPTION
						);

				if (response == JOptionPane.YES_OPTION) {
					dispose();
					System.exit(0);
				}
			}
		});

		paddingBorder = BorderFactory.createEmptyBorder(10, 20, 10, 10);
		bottomPanel.setBorder(paddingBorder);
		bottomPanel.add(exitButton);

		add(headerPanel, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(false);		
	}
}
