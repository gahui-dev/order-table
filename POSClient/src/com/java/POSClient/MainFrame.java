package com.java.POSClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.java.POSClient.Model.MenuItemModel;

public class MainFrame extends JFrame {

	// 헤더 패널(테이블 번호, + 상호명)
	private JPanel headerPanel;
	private JLabel tableNumberLabel;

	// 카테고리 탭 (좌측) 
	private JPanel categoryTabPanel;
	private JTabbedPane categoryTab;

	// 주문 내역, 요청 사항 패널 (우측)
	private JPanel orderPanel;

	// 주문하기 패널(주문 내역 데이터 테이블, 총 주문 금액 라벨, 주문하기 버튼)
	private JPanel orderListTablePanel;
	private DefaultTableModel orderListTableModel;
	private JTable orderListTable;
	private JLabel totalAmountLabel;
	private JButton submitButton;

	// 요청사항 패널(요청사항 라벨, 요청사항 입력필드, 전송하기 버튼)
	private JPanel requestMemoPanel;
	private JTextArea requestMemo;
	private JButton requestMemoSubmitButton;

	private MenuItem menuItem;

	// 사용 종료 패널(사용 종료 버튼)
	private JPanel bottomPanel;

	private String tableNumber = "";
	public String getTableNumber() {
		return tableNumber;
	}
	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
		tableNumberLabel.setText("[" + this.tableNumber + "] 번 테이블");
	}

	private SocketSender socketSender;
	public void setSocketSender(SocketSender socketSender) {
		this.socketSender = socketSender;
	}
	private BufferedReader bufferedReader;
	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}
	ArrayList<String> menuCategories = new ArrayList<String>();
	private ArrayList<MenuItemModel> menuItemModel = new ArrayList<>();
	public void addMenuItemModel(MenuItemModel model) {
		menuItemModel.add(model);
		if(!menuCategories.contains(model.getCategory())) {
			menuCategories.add(model.getCategory());
		}
	}

	public void setCategoryTab() {
		categoryTab.removeAll();
		String title = tableNumberLabel.getText();
		tableNumberLabel.setText(title + " - 서버로부터 모든 메뉴를 불러오고있습니다...");
		for (String category : menuCategories) {
			JPanel panel = new JPanel(new GridLayout(2, 2));
			JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			categoryTab.addTab(category, scrollPane);
			//categoryTab.addTab(category, panel);
			setMenuList(panel, category);
		}
		tableNumberLabel.setText(title);

		JOptionPane.showMessageDialog(null, "모든 메뉴를 불러왔습니다.", "메세지", JOptionPane.INFORMATION_MESSAGE);
	}

	// 전송 후 시간 갱신
	Timer timer;

	public MainFrame() {
		Init();
	}

	private URL getMenuImagePath(String imageSrc) {
		URL resourceURL = this.getClass().getResource("/image/" + imageSrc + ".jpg");
		if (resourceURL != null) {
			return resourceURL;
		} else {
			JOptionPane.showMessageDialog(null, "이미지안뜸.", "메세지", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
	}

	public void setMenuList(JPanel panel, String category) {
		for(MenuItemModel item : menuItemModel){
			if(category.equals(item.getCategory())) {
				//ImageIcon imageIcon = new ImageIcon("src/image/" + item.getProductImage() + ".jpg");
				ImageIcon imageIcon = new ImageIcon(getMenuImagePath(item.getProductImage()));
				Image scaledImage = imageIcon.getImage().getScaledInstance(260, 200, Image.SCALE_SMOOTH);
				ImageIcon resizedIcon = new ImageIcon(scaledImage);
				JButton button = new JButton(resizedIcon);

				Dimension dimension = new Dimension(260, 240);

				button.setPreferredSize(dimension);
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new AddOrderDialog(orderListTableModel, item);
					}
				});
				button.setPreferredSize(dimension);

				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
				buttonPanel.setPreferredSize(dimension);
				buttonPanel.add(button);

				JLabel menuNameLabel = new JLabel("메뉴 이름 : " + item.getMenuName());
				menuNameLabel.setFont(new Font(menuNameLabel.getName(), Font.BOLD, 18));

				//JLabel menuPriceLabel = new JLabel("음식 가격 : " + new DecimalFormat("###,###").format(item.getPrice()) + " 원");       
				JLabel menuPriceLabel = new JLabel("음식 가격 : " + String.format("%,d", item.getPrice()) + " 원");
				menuPriceLabel.setFont(new Font(menuPriceLabel.getName(), Font.BOLD, 16));

				buttonPanel.add(menuNameLabel);
				buttonPanel.add(menuPriceLabel);

				buttonPanel.setBackground(Color.white);
				buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
				panel.add(buttonPanel);
			}

		}
	}

	public void clearAll() {
		try {
			timer.stop();
			// timer 가 완전히 정지 하도록 대기
			Thread.sleep(1000);
			timer = null;
			totalAmountLabel.setText("총 주문금액 : 0 원");
			orderListTableModel.setNumRows(0);
		}
		catch (Exception e) {

		}
		orderListTableModel.setNumRows(0);
	}

	public String creatOrderPacket(DefaultTableModel orderModel, String requestMemo) {
		//"헤더\n데이터" 구조
		StringBuilder packet = new StringBuilder("requestOrder").append("\n");
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
		String orderId = currentDate.format(formatter);
		for(int i = 0; i < orderModel.getRowCount(); i++) {
			packet.append("<order>");
			packet.append("<id>").append(orderId).append("</id>");
			packet.append("<menuId>").append(orderModel.getValueAt(i, 0).toString()).append("</menuId>");
			packet.append("<menuName>").append(orderModel.getValueAt(i, 1).toString()).append("</menuName>");
			packet.append("<tableNumber>").append(this.tableNumber).append("</tableNumber>");
			packet.append("<count>").append(orderModel.getValueAt(i, 2).toString()).append("</count>");
			packet.append("<price>").append(orderModel.getValueAt(i, 5).toString()).append("</price>");
			packet.append("<requestMemo>").append(requestMemo).append("</requestMemo>");
			packet.append("</order>");
		}
		packet.append("\n").append("EndPacket");

		return packet.toString();
	}

	public void showDialog() {
		setVisible(true);
	}

	private void Init() {
		setTitle("Taeble Order");
		setSize(1280, 800);

		Color color = new Color(0, 102, 255);
		headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(color);

		// 테이블 번호 -> tableNumber

		tableNumberLabel = new JLabel("테이블");
		Font labelFont = tableNumberLabel.getFont();
		Border paddingBorder = BorderFactory.createEmptyBorder(20, 10, 20, 10);
		tableNumberLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20));
		tableNumberLabel.setBorder(paddingBorder);
		headerPanel.add(tableNumberLabel, BorderLayout.WEST);

		// 메뉴별 카테고리 탭 (좌측 패널)
		categoryTab = new JTabbedPane();
		categoryTab.setMinimumSize(new Dimension(600, 600));
		categoryTab.setPreferredSize(new Dimension(600, 600));


		// 주문하기 패널
		// 주문하기 라벨 패널
		JPanel orderListLabelPanel = new JPanel();
		JLabel orderListLabel = new JLabel("주문내역");
		orderListLabel.setFont(new Font(orderListLabel.getName(), Font.BOLD, 20));
		orderListLabelPanel.setBorder(paddingBorder);
		orderListLabelPanel.add(orderListLabel);

		// 주문 내역 데이터 테이블
		JPanel TablePanel = new JPanel(new BorderLayout());

		orderListTableModel = new DefaultTableModel(new String[] {"메뉴 ID", "메뉴 이름", "수량", "상품 가격(원)", "예상 대기 시간(초)", "주문 금액(원)"}, 0);
		orderListTable = new JTable(orderListTableModel);
		orderListTable.setRowHeight(30);

		orderListTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				DefaultTableModel orderMenuItem = (DefaultTableModel)e.getSource();

				// 모든 행의 데이터를 가져옵니다.
				int i_TotalAmount = 0;
				for (int i = 0; i < orderMenuItem.getRowCount(); i++) {
					i_TotalAmount += Integer.parseInt(orderMenuItem.getValueAt(i, 5).toString());
					totalAmountLabel.setText("총 주문금액 : " + String.format("%,d", i_TotalAmount) + " 원");

				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(orderListTable);

		// 총 주문 금액
		totalAmountLabel = new JLabel("총 주문 금액 : 0 원");
		paddingBorder = BorderFactory.createEmptyBorder(20, 0, 20, 0);
		totalAmountLabel.setFont(new Font(totalAmountLabel.getName(), Font.BOLD, 16));
		totalAmountLabel.setBorder(paddingBorder);

		TablePanel.add(scrollPane, BorderLayout.CENTER);
		TablePanel.add(totalAmountLabel, BorderLayout.SOUTH);

		// 주문하기 버튼
		submitButton = new JButton("주문하기");
		submitButton.setPreferredSize(new Dimension(submitButton.getPreferredSize().width, 40));
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(orderListTableModel.getRowCount() > 0) {
					String packet = creatOrderPacket(orderListTableModel, "");
					System.out.println(packet);
					socketSender.sendMessage(packet);

					for (int i = 0; i < orderListTableModel.getRowCount(); i++) {

						int cookTime = (int)orderListTableModel.getValueAt(i, 4);
						int rowIndex = i;

						// 테스트 할때는 delay 를 10 정도로 수정하면 빠르게 진행가능
						timer = new Timer(10, new ActionListener() {
							int remainingTime = cookTime;
							@Override
							public void actionPerformed(ActionEvent e) {
								remainingTime--;
								if(orderListTableModel.getRowCount() > 0) {
									orderListTableModel.setValueAt(remainingTime, rowIndex, 4);

									if (remainingTime <= 0) {
										((Timer) e.getSource()).stop();
										orderListTableModel.setValueAt("요리완료", rowIndex, 4);
									}
								}
							}
						});

						timer.start();
					}
				} else {
					JOptionPane.showMessageDialog(null, "주문할 음식을 담아주세요.", "메세지", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		orderListTablePanel = new JPanel(new BorderLayout());
		orderListTablePanel.add(orderListLabelPanel, BorderLayout.NORTH);
		orderListTablePanel.add(TablePanel, BorderLayout.CENTER);
		orderListTablePanel.add(submitButton, BorderLayout.SOUTH);


		// 요청사항 패널
		// 요청사항 라벨 패널
		JPanel requestMemoLabelPanel = new JPanel();
		JLabel requestMemoLabel = new JLabel("요청사항");
		requestMemoLabel.setFont(new Font(requestMemoLabel.getName(), Font.BOLD, 20));
		requestMemoLabelPanel.setBorder(paddingBorder);
		requestMemoLabelPanel.add(requestMemoLabel);

		// 요청사항 입력창
		requestMemo = new JTextArea(4, 20);
		JScrollPane requestMemoScrollPane = new JScrollPane(requestMemo);

		// 요청사항 버튼
		requestMemoSubmitButton = new JButton("요청사항 보내기");
		requestMemoSubmitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				sb.append("requestMessage").append("\n").append(tableNumber).append("\n").append(requestMemo.getText()).append("\n").append("EndPacket");
				socketSender.sendMessage(sb.toString());

				requestMemo.setText("");
			}
		});
		requestMemoPanel = new JPanel(new BorderLayout());
		requestMemoPanel.add(requestMemoLabelPanel, BorderLayout.NORTH);
		requestMemoPanel.add(requestMemoScrollPane, BorderLayout.CENTER);
		requestMemoPanel.add(requestMemoSubmitButton, BorderLayout.EAST);

		// 우측 패널
		orderPanel = new JPanel(new BorderLayout());
		orderPanel.setMinimumSize(new Dimension(600, 600));
		orderPanel.setPreferredSize(new Dimension(600, 600));
		orderPanel.add(requestMemoPanel);
		orderPanel.add(orderListTablePanel, BorderLayout.CENTER);
		orderPanel.add(requestMemoPanel, BorderLayout.SOUTH);

		// 프로그램 종료 패널
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
		add(categoryTab, BorderLayout.WEST);
		add(orderPanel, BorderLayout.EAST);
		add(bottomPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(false);      
	}
}