package com.java.POSClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.java.POSClient.Model.MenuItemModel;

public class AddOrderDialog extends JDialog {
	// 메뉴 상세 화면 패널
	private JPanel detailMenuPanel;

	// 메뉴 이미지 패널
	private JPanel menuImagePanel;
	private ImageIcon menuIcon;
	private ImageIcon resizedIcon;
	private Image scaledImage;

	// 메뉴 정보 패널
	private JPanel menuInfoPanel;
	private JLabel menuNameLabel;
	private JLabel descriptionLabel;

	// 수량, 메뉴 담기 버튼
	private JPanel quantityPanel;
	private JComboBox quantityComboBox;
	private JButton addButton;

	private JSplitPane splitPane;

	MenuItemModel menuItem;

	private DefaultTableModel orderListTableModel;

	public AddOrderDialog(DefaultTableModel orderListTableModel, MenuItemModel menuItem) {
		this.orderListTableModel = orderListTableModel;
		this.menuItem = menuItem; 

		Init();
	}

	private int parseCookTime(String ctime) {
		try {
			String[] parts = ctime.split(":"); // "분:초"를 ":"로 분리
			if (parts.length == 2) {
				int minutes = Integer.parseInt(parts[0]);
				int seconds = Integer.parseInt(parts[1]);
				return minutes * 60 + seconds; // 초로 변환
			} else {

				return 0;
			}
		} catch (NumberFormatException e) {
			return 0;
		}
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

	private void Init() {
		setTitle("메뉴 상세 화면");
		setPreferredSize(new Dimension(800, 400));
		setResizable(false);

		// 메뉴 이미지
		menuIcon = new ImageIcon(getMenuImagePath(menuItem.getProductImage()));
		scaledImage = menuIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
		resizedIcon = new ImageIcon(scaledImage);
		JLabel imageLabel = new JLabel(resizedIcon);
		detailMenuPanel = new JPanel(new BorderLayout());

		menuImagePanel = new JPanel();
		menuImagePanel.setMinimumSize(new Dimension(400, 400));
		menuImagePanel.setPreferredSize(new Dimension(400, 400));

		// 메뉴 설명
		menuInfoPanel = new JPanel(new BorderLayout());
		menuInfoPanel.setMinimumSize(new Dimension(400, 400));
		menuInfoPanel.setPreferredSize(new Dimension(400, 400));

		menuNameLabel = new JLabel("<" + menuItem.getMenuName() + ">");
		descriptionLabel = new JLabel(menuItem.getDescription());

		Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);

		menuNameLabel.setHorizontalAlignment(JLabel.CENTER);
		menuNameLabel.setFont(new Font(menuNameLabel.getName(), Font.BOLD, 20));
		menuNameLabel.setBorder(paddingBorder);
		descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
		descriptionLabel.setFont(new Font(descriptionLabel.getName(), Font.BOLD, 18));
		descriptionLabel.setBorder(paddingBorder);

		// 수량, 메뉴 담기 버튼
		quantityPanel = new JPanel();
		quantityPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel quantityLabel = new JLabel("수량 : ");
		quantityComboBox = new JComboBox();
		for (int j = 1; j <= 10; j++) {
			quantityComboBox.addItem(String.valueOf(j));
		}

		addButton = new JButton("메뉴 담기");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int quantity = Integer.parseInt((String) quantityComboBox.getSelectedItem());

					int rowCount = orderListTableModel.getRowCount();
					int cookTimeSeconds = parseCookTime(menuItem.getCookTime()) * quantity;

					orderListTableModel.addRow(new Object[] { menuItem.getMenuId(), 
							menuItem.getMenuName(), 
							quantity, 
							menuItem.getPrice(), 
							cookTimeSeconds, 
							menuItem.getPrice() * quantity});

					JOptionPane.showMessageDialog(null, "선택된 수량: " + quantity + "개 를 장바구니에 담았습니다.");

					dispose();

				} catch (NullPointerException npe) {
					System.out.println("null값입니다.");
				}

			}
		});

		quantityPanel.add(quantityLabel);
		quantityPanel.add(quantityComboBox);
		quantityPanel.add(addButton);

		menuInfoPanel.add(menuNameLabel, BorderLayout.NORTH);
		menuInfoPanel.add(descriptionLabel, BorderLayout.CENTER);
		menuInfoPanel.add(quantityPanel, BorderLayout.SOUTH);

		detailMenuPanel.add(menuInfoPanel, BorderLayout.CENTER);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(imageLabel);
		splitPane.setRightComponent(detailMenuPanel);

		add(splitPane);

		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}