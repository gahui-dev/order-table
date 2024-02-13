package com.java.POSSystem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.Border;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SignUpDialog extends JDialog{
	private JTextField userIdField;
	private JPasswordField userPwdField;
	private JTextField userBusinessField;
	private JTextField userNameField;
	private JButton submitButton;
	private JButton cancelButton;
	
	public SignUpDialog() {
		Init();
	}
	
	public void showDialog() {
		setModal(true);
		setVisible(true);
	}
	
	private void addUserData() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document;

			String inputUserId = userIdField.getText();
			String inputUserPwd = userPwdField.getText();
			String inputUserBusiness = userBusinessField.getText();
			String inputUserName = userNameField.getText();
			
			File xmlFile = new File(Common.getUserDatabaseConnectionString());
			if (xmlFile.exists()) {
				document = builder.parse(xmlFile);
			} else {
				document = builder.newDocument();
				Element root = document.createElement("users");
				document.appendChild(root);
			}
			// 하나라도 NULL 값이면 갱신이 안되게 해야한다.

			if (inputUserId.equals("") || (inputUserPwd.equals("")
					|| (inputUserBusiness.equals("") || (inputUserName.equals(""))))) {
				System.out.println("빈 값 확인");
				JOptionPane.showMessageDialog(this, "빈 칸이 있습니다.", "Message", JOptionPane.ERROR_MESSAGE);
			}
			
			// 아이디 중복 체크
			else if(isUserIdDuplication(inputUserId,document)) {
				JOptionPane.showMessageDialog(this, "이미 사용 중인 아이디입니다.");
			}else {
				Element newUser = document.createElement("user");

				Element newId = document.createElement("userId");
				newId.appendChild(document.createTextNode(inputUserId));
				newUser.appendChild(newId);

				Element newPwd = document.createElement("userPwd");
				newPwd.appendChild(document.createTextNode(inputUserPwd));
				newUser.appendChild(newPwd);

				Element newBusinessName = document.createElement("businessName");
				newBusinessName.appendChild(document.createTextNode(inputUserBusiness));
				newUser.appendChild(newBusinessName);

				Element newName = document.createElement("userName");
				newName.appendChild(document.createTextNode(inputUserName));
				newUser.appendChild(newName);

				document.getDocumentElement().appendChild(newUser);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(new File(Common.getUserDatabaseConnectionString()));
				transformer.transform(source, result);

				JOptionPane.showMessageDialog(this, "등록되었습니다.");
				
				dispose();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "등록에 실패하였습니다.", "Message", JOptionPane.ERROR_MESSAGE);
			//e.printStackTrace();
		}
	}
	private void createChildElement(Document document, Element menuItem, String tagName, String textContent) {
		Element element = document.createElement(tagName);
		element.setTextContent(textContent);
		menuItem.appendChild(element);
	}

	private boolean isUserIdDuplication(String inputUserId, Document document) {
		NodeList nList = document.getElementsByTagName("userId");
		for (int i = 0; i < nList.getLength(); i++) {
			Node node = nList.item(i);
			if (node.getTextContent().equals(inputUserId)) {
				return true; // 중복 아이디가 있음
			}
		}
		return false; // 중복 아이디가 없음
	}
	
	private void Init() {
		setTitle("회원가입");
		setPreferredSize(new Dimension(400, 400));
		setResizable(false);
		setLayout(new BorderLayout());
	
		JLabel logoLabel = new JLabel("회원가입");

		Font labelFont = logoLabel.getFont();
		Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		
		logoLabel.setHorizontalAlignment(JLabel.CENTER);
		logoLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20));
		logoLabel.setBorder(paddingBorder);

		JPanel inputPanel = new JPanel(new GridBagLayout());
		
		JLabel userIdLabel = new JLabel("아이디");
		userIdField = new JTextField();
		userIdField.setPreferredSize(new Dimension(200, 30));
	
		JLabel userPwdLabel = new JLabel("비밀번호");
		userPwdField = new JPasswordField();
		userPwdField.setPreferredSize(new Dimension(200, 30));
		
		JLabel userBusinessLabel = new JLabel("상호명");
		userBusinessField = new JTextField();
		userBusinessField.setPreferredSize(new Dimension(200, 30));
	
		JLabel userNameLabel = new JLabel("이름");
		userNameField = new JTextField();
		userNameField.setPreferredSize(new Dimension(200, 30));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		inputPanel.add(userIdLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		inputPanel.add(userIdField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		inputPanel.add(userPwdLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		inputPanel.add(userPwdField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		inputPanel.add(userBusinessLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		inputPanel.add(userBusinessField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		inputPanel.add(userNameLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		inputPanel.add(userNameField, gbc);
		
		JPanel buttonPanel = new JPanel();
		submitButton = new JButton("등록");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addUserData();
			}
		});
		
		cancelButton = new JButton("취소");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();

			}
		});
		
		paddingBorder = BorderFactory.createEmptyBorder(0, 0, 10, 0);
		buttonPanel.setBorder(paddingBorder);
		buttonPanel.add(submitButton);
		buttonPanel.add(cancelButton);
		
		add(logoLabel, BorderLayout.NORTH);
		add(inputPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		pack();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setLocationRelativeTo(null);
		
		setVisible(false);
	}
}
