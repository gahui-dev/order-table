package com.java.POSSystem.UserComponent;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import com.java.POSSystem.CustomInterface.*;

import com.java.POSSystem.Model.Order;

public class TableOrderItem extends JPanel {
	private JLabel tableNumberLabel;
	private JLabel orderAmountLabel;
	private JTable orderDetailsTable;
	private DefaultTableModel orderListTableModel;
	public DefaultTableModel getOrderListTableModel() {
		return orderListTableModel;
	}
	private JButton submitButton;
	
	ArrayList<Order> orderInfo = new ArrayList<>();
	
	public void setOrderInfo(Order order) {
		orderInfo.add(order);
	}
	
	public void clearOrderInfo() {
		orderInfo.clear();
	}

	private int tableNumber;
	public int getTableNumber() {
		return tableNumber;
	}
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
		tableNumberLabel.setText(Integer.toString(tableNumber) + " 번 테이블");
	}

	private TableSubmitCallback callback;
	
	public TableOrderItem(TableSubmitCallback callback) {
		this.callback = callback;
		Init();
	}
	
	private void Init() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(300, 300));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		tableNumberLabel = new JLabel("테이블 번호");
		tableNumberLabel.setPreferredSize(new Dimension(tableNumberLabel.getPreferredSize().width, 40));
		tableNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);

		orderAmountLabel = new JLabel("총 주문 금액 : - 원");
		orderAmountLabel.setPreferredSize(new Dimension(orderAmountLabel.getPreferredSize().width, 20));
		orderAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel orderInfoBodyPanel = new JPanel(new BorderLayout());

		orderListTableModel = new DefaultTableModel(new String[]{"메뉴", "수량", "금액"}, 0);
		orderListTableModel.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				DefaultTableModel orderlist = (DefaultTableModel)e.getSource();

                // 모든 행의 데이터를 가져옵니다.
                int i_TotalAmount = 0;
                for (int i = 0; i < orderlist.getRowCount(); i++) {
                    i_TotalAmount += Integer.parseInt(orderlist.getValueAt(i, 2).toString());
                    orderAmountLabel.setText("총 주문금액 : " + i_TotalAmount + " 원");
                }
			}
		});
		
		orderDetailsTable = new JTable(orderListTableModel);
		orderDetailsTable.setRowHeight(30);
		JScrollPane scrollPane = new JScrollPane(orderDetailsTable);

		orderInfoBodyPanel.add(scrollPane, BorderLayout.CENTER);
		orderInfoBodyPanel.add(orderAmountLabel, BorderLayout.SOUTH);

		submitButton = new JButton("계산하기");
		submitButton.setPreferredSize(new Dimension(orderAmountLabel.getPreferredSize().width, 40));
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(orderListTableModel.getRowCount() > 0) {
					if(callback != null) {
						callback.onSubmit(tableNumber, orderInfo);
						orderInfo.clear();
					}
					orderAmountLabel.setText("총 주문 금액 : - 원");
					orderListTableModel.setNumRows(0);
				}
			}
		});
		add(tableNumberLabel, BorderLayout.NORTH);
		add(orderInfoBodyPanel, BorderLayout.CENTER);
		add(submitButton, BorderLayout.SOUTH);

	}
}
