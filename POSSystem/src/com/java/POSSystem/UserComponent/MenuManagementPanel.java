package com.java.POSSystem.UserComponent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.java.POSSystem.Common;


public class MenuManagementPanel extends JPanel{
	private JPanel menuInputPanel;
	private JPanel menuInputFieldPanel;
	private JTextField menuIdField;
	private JTextField menuNameField;
	private JTextField categoryField;
	private JTextField priceField;
	private JTextField cookTimeField;
	private JTextField productImageField;
	private JTextArea descriptionField;
	private JButton submitButton;
	private JButton deleteButton;
	
	private DefaultTableModel menuListTableModel;
	private JTable menuListTable;

	private File xmlMenuDatabase = new File(Common.getMenuDatabaseConnectionString());
	// 생성자
	public MenuManagementPanel() {
		// 컴포넌트 생성
		Init();
		
		loadMenuData();
	}
	
	// 컴포넌트 생성
	private void Init() {
		setLayout(new BorderLayout());
		
		JPanel menuInputPanel = new JPanel(new BorderLayout());
		
		menuInputPanel.setMinimumSize(new Dimension(600, 600));
		menuInputPanel.setPreferredSize(new Dimension(600, 600));
		
		menuInputFieldPanel = new JPanel(new GridBagLayout());
		
		JLabel menuIdLabel = new JLabel("메뉴 아이디");
		menuIdField = new JTextField();
		menuIdField.setEditable(false);
		menuIdField.setPreferredSize(new Dimension(300, 30));
	
		JLabel menuNameLabel = new JLabel("카테고리");
		menuNameField = new JTextField();
		menuNameField.setPreferredSize(new Dimension(300, 30));
		
		JLabel categoryLabel = new JLabel("메뉴 이름");
		categoryField = new JTextField();
		categoryField.setPreferredSize(new Dimension(300, 30));
	
		JLabel priceLabel = new JLabel("가격");
		priceField = new JTextField();
		priceField.setPreferredSize(new Dimension(300, 30));

		JLabel cookTimeLabel = new JLabel("조리 시간");
		cookTimeField = new JTextField();
		cookTimeField.setPreferredSize(new Dimension(300, 30));
		
		JLabel productImageLabel = new JLabel("상품 이미지");
		productImageField = new JTextField();
		productImageField.setPreferredSize(new Dimension(300, 30));
		
		JLabel descriptionLabel = new JLabel("상세설명");
		descriptionField = new JTextArea();
		descriptionField.setPreferredSize(new Dimension(300, 30));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 20, 10, 20);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		menuInputFieldPanel.add(menuIdLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		menuInputFieldPanel.add(menuIdField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		menuInputFieldPanel.add(menuNameLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		menuInputFieldPanel.add(menuNameField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		menuInputFieldPanel.add(categoryLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		menuInputFieldPanel.add(categoryField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		menuInputFieldPanel.add(priceLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		menuInputFieldPanel.add(priceField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		menuInputFieldPanel.add(cookTimeLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		menuInputFieldPanel.add(cookTimeField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		menuInputFieldPanel.add(productImageLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 5;
		menuInputFieldPanel.add(productImageField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		menuInputFieldPanel.add(priceLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 6;
		menuInputFieldPanel.add(priceField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 7;
		menuInputFieldPanel.add(descriptionLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 7;
		menuInputFieldPanel.add(descriptionField, gbc);

		JPanel buttonPanel = new JPanel();
		submitButton = new JButton("메뉴 등록");
		submitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((JButton)e.getSource()).getText().equals("메뉴 등록")) {
					int maxId = 0;
					for (int i = 0; i < menuListTableModel.getRowCount(); i++) {
						int id = Integer.parseInt(menuListTableModel.getValueAt(i, 0).toString());
						
						if (id > maxId) {
							maxId = id;
						}
					}
					    
					addMenuData(new String[] {Integer.toString(maxId + 1), 
							menuNameField.getText(),
							categoryField.getText(),
							priceField.getText(),
							cookTimeField.getText(),
							productImageField.getText(),
							descriptionField.getText()
					});
					
				} else {
					clearField();
				}
			}
		});
		deleteButton = new JButton("메뉴 삭제");
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = menuListTable.getSelectedRow();
				menuListTable.clearSelection();
				if (selectedRow != -1) { // 선택된 행이 있는지 확인
			        try {
			        	// XML 파일에서도 해당 항목 제거
			        	//File xmlFile = new File(AdminFrame.XML_MENU_PATH);
			        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			        	DocumentBuilder builder = factory.newDocumentBuilder();
			        	Document document;

			        	//document = builder.parse(Common.getMenuDatabaseStream());
			        	document = builder.parse(Common.getMenuDatabaseConnectionString());
			        	NodeList itemList = document.getElementsByTagName("menuItem");
			        	// 선택된 행의 데이터와 일치하는 항목 찾아서 제거
			        	Element itemToRemove = null;
			        	for (int i = 0; i < itemList.getLength(); i++) {
			        		Element item = (Element) itemList.item(i);
			        		String menuId = item.getElementsByTagName("menuId").item(0).getTextContent();
			        		if (menuId.equals(menuListTable.getValueAt(selectedRow, 0))) {
			        			itemToRemove = item;
			        			break;
			        		}
			        	}
			        	// 선택된 행 제거
			        	menuListTableModel.removeRow(selectedRow);

			        	if (itemToRemove != null) {
			        		itemToRemove.getParentNode().removeChild(itemToRemove);
			        		// XML 파일 업데이트
			        		try (FileOutputStream outputStream = new FileOutputStream(Common.getMenuDatabaseConnectionString())) {
			        			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			        			Transformer transformer = transformerFactory.newTransformer();
			        			transformer.transform(new DOMSource(document), new StreamResult(outputStream));
			        		}
			        	}

			        	clearField();
			        } catch (Exception ex) {
			        	ex.printStackTrace();
			        }
			    }
			}
		});
		Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
		paddingBorder = BorderFactory.createEmptyBorder(0, 0, 10, 0);
		buttonPanel.setBorder(paddingBorder);
		buttonPanel.add(submitButton);
		buttonPanel.add(deleteButton);
		
		menuInputPanel.add(menuInputFieldPanel, BorderLayout.CENTER);
		menuInputPanel.add(buttonPanel, BorderLayout.SOUTH);	
		
		// 메뉴 목록 데이터 테이블
		JPanel menuListPanel = new JPanel(new BorderLayout());
		menuListPanel.setMinimumSize(new Dimension(600, 600));
		menuListPanel.setPreferredSize(new Dimension(600, 600));
		
		menuListTableModel = new DefaultTableModel(new String[] {"메뉴 아이디", "카테고리", "메뉴 이름", "가격", "조리 시간", "이미지 경로", "상세 설명"}, 0);
		menuListTable = new JTable(menuListTableModel);
		menuListTable.setRowHeight(30);
		menuListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = menuListTable.getSelectedRow();
				
				if (selectedRow != -1) { // 선택된 행이 있는지 확인
					menuIdField.setText(menuListTable.getValueAt(menuListTable.getSelectedRow(), 0).toString());
					menuNameField.setText(menuListTable.getValueAt(menuListTable.getSelectedRow(), 1).toString());
					categoryField.setText(menuListTable.getValueAt(menuListTable.getSelectedRow(), 2).toString());
					priceField.setText(menuListTable.getValueAt(menuListTable.getSelectedRow(), 3).toString());
					cookTimeField.setText(menuListTable.getValueAt(menuListTable.getSelectedRow(), 4).toString());
					productImageField.setText(menuListTable.getValueAt(menuListTable.getSelectedRow(), 5).toString());
					descriptionField.setText(menuListTable.getValueAt(menuListTable.getSelectedRow(), 6).toString());

					submitButton.setText("신규등록");
				}
			}
		});
		JScrollPane scrollPane = new JScrollPane(menuListTable);
		
		menuListPanel.add(scrollPane);
		
		add(menuInputPanel, BorderLayout.WEST);
		add(menuListPanel, BorderLayout.EAST);
	}
	
	private void clearField() {
		menuIdField.setText("");
		menuNameField.setText("");
		categoryField.setText("");
		priceField.setText("");
		cookTimeField.setText("");
		productImageField.setText("");
		descriptionField.setText("");
		
		submitButton.setText("메뉴 등록");
	}

	// 메뉴 테이블 불러오기
	private void loadMenuData() {
		try {
			menuListTableModel.setNumRows(0);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(Common.getMenuDatabaseConnectionString());
			//Document document = builder.parse(Common.getMenuDatabaseStream());
			NodeList itemList = document.getElementsByTagName("menuItem");

			for (int i = 0; i < itemList.getLength(); i++) {
				Element item = (Element) itemList.item(i);
				String menuId = item.getElementsByTagName("menuId").item(0).getTextContent();
				String menuName = item.getElementsByTagName("menuName").item(0).getTextContent();
				String category = item.getElementsByTagName("category").item(0).getTextContent();
				String price = item.getElementsByTagName("price").item(0).getTextContent();
				String cookTime = item.getElementsByTagName("cookTime").item(0).getTextContent();
				String productImage = item.getElementsByTagName("productImage").item(0).getTextContent();
				String description = item.getElementsByTagName("description").item(0).getTextContent();

				menuListTableModel.addRow(
						new Object[] { menuId, category, menuName, price, cookTime, productImage, description });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 메뉴 테이블 추가하기
	private void addMenuData(String[] menuData) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			//Document document = builder.parse(Common.getMenuDatabaseStream());
			Document document = builder.parse(Common.getMenuDatabaseConnectionString());

			Element menuItem = document.createElement("menuItem");
			createChildElement(document, menuItem, "menuId", menuData[0]);
			createChildElement(document, menuItem, "menuName", menuData[1]);
			createChildElement(document, menuItem, "category", menuData[2]);
			createChildElement(document, menuItem, "price", menuData[3]);
			createChildElement(document, menuItem, "cookTime", menuData[4]);
			createChildElement(document, menuItem, "productImage", menuData[5]);
			createChildElement(document, menuItem, "description", menuData[6]);

			document.getDocumentElement().appendChild(menuItem);
			
			try (FileOutputStream outputStream = new FileOutputStream(Common.getMenuDatabaseConnectionString())) {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.transform(new DOMSource(document), new StreamResult(outputStream));
				
				clearField();
				
				loadMenuData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createChildElement(Document document, Element menuItem, String tagName, String textContent) {
		Element element = document.createElement(tagName);
		element.setTextContent(textContent);
		menuItem.appendChild(element);
	}
}
