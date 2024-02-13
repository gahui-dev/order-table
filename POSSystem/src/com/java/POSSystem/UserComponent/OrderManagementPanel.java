package com.java.POSSystem.UserComponent;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.java.POSSystem.Common;
import com.java.POSSystem.CustomInterface.*;

import com.java.POSSystem.Model.*;

public class OrderManagementPanel extends JPanel{
	private JPanel tableOrderItemsPanel;
	private JPanel salesListPanel;
	private DefaultTableModel salesListTableModel; 
	private JTable salesListTable;

	private JLabel todayTotalAmount;
	private JLabel monthTotalAmount;

	private TableOrderItem tableOrderItem;

	private MessageSendCallback messageSendCallback;

	public OrderManagementPanel(MessageSendCallback messageSendCallback) {
		this.messageSendCallback = messageSendCallback;
		Init();
	}

	public void ConnectionTable(int tableNumber) {
		for (Component table : tableOrderItemsPanel.getComponents()) {
			if(((TableOrderItem)table).getTableNumber() == 0) {
				((TableOrderItem)table).setTableNumber(tableNumber);
				break;
			}
		}
	}

	public void clearOldOrderData(int tableNumber) {
		for (Component table : tableOrderItemsPanel.getComponents()) {
			if(((TableOrderItem)table).getTableNumber() == tableNumber) {
				((TableOrderItem)table).clearOrderInfo();
				DefaultTableModel model = ((TableOrderItem)table).getOrderListTableModel();
				model.setNumRows(0);
			}
		}
	}

	public void updateOrderDataTable(int tableNumber, Order order) {
		for (Component table : tableOrderItemsPanel.getComponents()) {
			if(((TableOrderItem)table).getTableNumber() == tableNumber) {
				DefaultTableModel model = ((TableOrderItem)table).getOrderListTableModel();
				model.addRow(new String[]{order.getMenuName(), Integer.toString(order.getMenuCount()), Integer.toString(order.getTotalPrice())});
				((TableOrderItem)table).setOrderInfo(order);
				break;
			}
		}
	}

	private void saveSalesData(String orderId, String tableNumber, String menuName, String Count, String totalAmount) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document;

			File xmlFile = new File(Common.getOrderDatabaseConnectionString());
			document = builder.parse(xmlFile);

			// 아이디 중복 체크

			Element newSale = document.createElement("order");

			Element newOrderId = document.createElement("orderId");
			newOrderId.appendChild(document.createTextNode(orderId));
			newSale.appendChild(newOrderId);

			Element newTableNumber = document.createElement("tableNumber");
			newTableNumber.appendChild(document.createTextNode(tableNumber));
			newSale.appendChild(newTableNumber);

			Element newMenuName = document.createElement("menuName");
			newMenuName.appendChild(document.createTextNode(menuName));
			newSale.appendChild(newMenuName);

			Element newCount = document.createElement("count");
			newCount.appendChild(document.createTextNode(Count));
			newSale.appendChild(newCount);

			Element newSalesAmount = document.createElement("salesAmount");
			newSalesAmount.appendChild(document.createTextNode(totalAmount));
			newSale.appendChild(newSalesAmount);


			document.getDocumentElement().appendChild(newSale);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(Common.getOrderDatabaseConnectionString()));
			transformer.transform(source, result);

			//JOptionPane.showMessageDialog(this, "등록되었습니다.");

		} catch (Exception e) {
			//JOptionPane.showMessageDialog(this, "등록에 실패하였습니다.", "Message", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void Init() {
		setLayout(new BorderLayout());

		// 테이블별 주문 목록 패널(좌측)
		tableOrderItemsPanel = new JPanel();
		tableOrderItemsPanel.setLayout(new GridLayout(2, 2, 20, 20));
		tableOrderItemsPanel.setMinimumSize(new Dimension(600, 600));

		for(int i = 0; i < 4; i++) {
			//tableOrderItem = new TableOrderItem(messageSendCallback);
			tableOrderItem = new TableOrderItem(new TableSubmitCallback() {
				@Override
				public void onSubmit(int tableNumber, ArrayList<Order> orderInfo) {
					// "메뉴", "수량", "금액"
					//dataModel
					for(Order order : orderInfo) {
						//new String[]{"주문 시간", "테이블", "메뉴", "수량", "총 주문 금액"

						salesListTableModel.addRow(new String[] {order.getOrderId(), order.getTableNumber(), order.getMenuName(),
								Integer.toString(order.getMenuCount()), Integer.toString(order.getTotalPrice())});

						// 여기서 매출 내역기록
						saveSalesData(order.getOrderId(),
								order.getTableNumber(), order.getMenuName(),
								Integer.toString(order.getMenuCount()), Integer.toString(order.getTotalPrice()));
					}
					messageSendCallback.onMessageSend(tableNumber, "ClearAll\n");
				}

			});
			tableOrderItemsPanel.add(tableOrderItem);
		}

		//tableOrderItemsPanel.add(tableOrderItem);

		// 판매 목록 패널(우측)
		salesListPanel = new JPanel(new BorderLayout());
		salesListPanel.setMinimumSize(new Dimension(600, 600));
		salesListPanel.setPreferredSize(new Dimension(600, 600));
		//salesListPanel.setLayout(new BoxLayout(salesListPanel, BoxLayout.Y_AXIS));

		// 매출 목록 라벨 패널
		JPanel salesListLabelPanel = new JPanel();
		JLabel salesListLabel = new JLabel("매출 목록");
		salesListLabel.setFont(new Font(salesListLabel.getName(), Font.BOLD, 20));
		salesListLabelPanel.add(salesListLabel);

		// 매출 목록 데이터 테이블
		salesListTableModel = new DefaultTableModel(new String[]{"주문 시간", "테이블", "메뉴", "수량", "총 주문 금액" }, 0);
		salesListTableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				DefaultTableModel orderList = (DefaultTableModel)e.getSource();

				// 모든 행의 데이터를 가져옵니다.
				int i_TotalAmount = 0;
				for (int i = 0; i < orderList.getRowCount(); i++) {
					i_TotalAmount += Integer.parseInt(orderList.getValueAt(i, 4).toString());
					todayTotalAmount.setText(i_TotalAmount + " 원");
				}
			}
		});
		salesListTable = new JTable(salesListTableModel);
		salesListTable.setRowHeight(30);
		JScrollPane scrollPane = new JScrollPane(salesListTable);

		// 매출 목록 
		JPanel totalAmountLabelPanel = new JPanel(new GridBagLayout());
		JLabel todayTotalAmountLabel = new JLabel("금일 총 매출 금액");
		todayTotalAmount = new JLabel();
		//      JLabel monthTotalAmountLabel = new JLabel("금월 총 매출 금액");
		//      monthTotalAmount = new JLabel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.gridy = 0;
		totalAmountLabelPanel.add(todayTotalAmountLabel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		totalAmountLabelPanel.add(todayTotalAmount, gbc);

		//      gbc.gridx = 0;
		//      gbc.gridy = 1;
		//      totalAmountLabelPanel.add(monthTotalAmountLabel, gbc);
		//
		//      gbc.gridx = 1;
		//      gbc.gridy = 1;
		//      totalAmountLabelPanel.add(monthTotalAmount, gbc);

		salesListPanel.add(salesListLabelPanel, BorderLayout.NORTH);
		salesListPanel.add(scrollPane, BorderLayout.CENTER);
		salesListPanel.add(totalAmountLabelPanel, BorderLayout.SOUTH);

		add(tableOrderItemsPanel, BorderLayout.WEST);
		add(salesListPanel, BorderLayout.EAST);
	}
}