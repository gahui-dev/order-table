package com.java.POSClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

public class StartDialog extends JDialog {
	private JPanel headerPanel;
	private JPanel tableNumberPanel;
	private JPanel buttonPanel;

	private JLabel logoLabel;
	private JButton submitButton;

	private JTextField tableNumberField;

	// ip 받는 필드
	private JTextField IpField;
	// port 받는 필드
	private JTextField PortField;

	private String tableNumber;

	public String getTableNumber() {
		return tableNumber;
	}

	private CustomInterface.ReceiveCallback callback;

	public StartDialog(CustomInterface.ReceiveCallback callback) {
		this.callback = callback;
		Init();
	}

	public void showDialog() {
		setVisible(true);
	}

	private void Init() {
		setTitle("Order Table");

		setPreferredSize(new Dimension(400, 300));
		setLayout(new BorderLayout());
		setResizable(false);

		headerPanel = new JPanel(new BorderLayout());
		Color color = new Color(0, 102, 255);
		headerPanel.setBackground(color);

		logoLabel = new JLabel("Order Table");
		Font labelFont = logoLabel.getFont();
		Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		logoLabel.setHorizontalAlignment(JLabel.CENTER);
		logoLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20));
		logoLabel.setBorder(paddingBorder);
		headerPanel.add(logoLabel, BorderLayout.CENTER);

		// 레이블과 필드 설정
		tableNumberPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel tableNumberLabel = new JLabel("테이블 번호");
		tableNumberField = new JTextField();
		tableNumberField.setText("1");
		tableNumberField.setPreferredSize(new Dimension(200, 30));

		gbc.gridx = 0;
		gbc.gridy = 2;
		tableNumberPanel.add(tableNumberLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		tableNumberPanel.add(tableNumberField, gbc);

		// IP 레이블과 필드 설정
		// 여기서 받아온 값을 Main 측으로 넘겨줘야 함
		// "사용 시작" 버튼을 눌렸을 때, 이벤트로 넘겨주게 해야할 듯
		JLabel IpLabel = new JLabel("IP");
		IpField = new JTextField();
		IpField.setPreferredSize(new Dimension(200, 30));

		gbc.gridx = 0;
		gbc.gridy = 0;
		tableNumberPanel.add(IpLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		tableNumberPanel.add(IpField, gbc);

		JLabel PortLabel = new JLabel("PORT");
		PortField = new JTextField();
		PortField.setPreferredSize(new Dimension(200, 30));

		gbc.gridx = 0;
		gbc.gridy = 1;
		tableNumberPanel.add(PortLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		tableNumberPanel.add(PortField, gbc);

		buttonPanel = new JPanel();
		submitButton = new JButton("사용 시작");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String inputTableNumber = tableNumberField.getText();
				String inputPort = PortField.getText();

				if (inputTableNumber.isBlank() || inputTableNumber.isEmpty() || inputPort.isBlank()
						|| inputPort.isEmpty() || IpField.getText().isBlank() || IpField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "모든 항목은 필수값입니다.\n모든 항목을 입력해주세요.", "Message",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				int inputPortInt = Integer.parseInt(inputPort);

				Main.SERVER_IP = IpField.getText();
				Main.SERVER_PORT = inputPortInt;

				// System.out.println("StartDialog에서의 SERVER_IP > " + Main.SERVER_IP);
				// System.out.println("StartDialog에서의 SERVER_PORT > " + Main.SERVER_PORT);

				if (!inputTableNumber.isEmpty()) {
					tableNumber = inputTableNumber;

					if (callback != null) {
						callback.onConnection();
					}
				} else {
					JOptionPane.showMessageDialog(StartDialog.this, "테이블 번호를 입력해주세요. ", "경고",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		paddingBorder = BorderFactory.createEmptyBorder(0, 0, 10, 0);
		buttonPanel.setBorder(paddingBorder);
		buttonPanel.add(submitButton);

		add(headerPanel, BorderLayout.NORTH);
		add(tableNumberPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);
		setModal(true);
		setVisible(false);
	}
}