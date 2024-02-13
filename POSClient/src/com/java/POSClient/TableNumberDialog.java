  package com.java.POSClient;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

public class TableNumberDialog extends JDialog{
	private JTextField tableNumberField;
	private JButton submitButton;

	public TableNumberDialog() {
		Init();
	}

	private void Init() {
		setTitle("Order Table");
		setPreferredSize(new Dimension(400, 300));
		setResizable(false);
		setLayout(new BorderLayout());
		
		JLabel logoLabel = new JLabel("Table Order");
		
		Font labelFont = logoLabel.getFont();
		Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		
		logoLabel.setHorizontalAlignment(JLabel.CENTER);
		logoLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20));
		logoLabel.setBorder(paddingBorder);

		JPanel inputPanel = new JPanel(new GridBagLayout());
		
		JLabel tableNumberLabel = new JLabel("테이블 번호");
		tableNumberField = new JTextField();
		tableNumberField.setPreferredSize(new Dimension(200, 30));
	
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(tableNumberLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		inputPanel.add(tableNumberField, gbc);
		
		JPanel buttonPanel = new JPanel();
		submitButton = new JButton("시작하기");
		
		paddingBorder = BorderFactory.createEmptyBorder(0, 0, 10, 0);
		buttonPanel.setBorder(paddingBorder);
		buttonPanel.add(submitButton);
		
		add(logoLabel, BorderLayout.NORTH);
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}
