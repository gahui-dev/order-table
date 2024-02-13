package com.java.POSSystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.JLabel;

public class MessageDialog extends JDialog{
   private JPanel messageDialogPanel;
   private JLabel tableNumber;
   private JLabel requestMemoLabel;
   
   private String message;
   public MessageDialog(int tableNumber, String message) {
      Init();
      this.tableNumber.setText(tableNumber + "번 테이블 요청사항");
      requestMemoLabel.setText(message);
   }
   
   public void showDialog() {
      setVisible(true);
   }
   
   private void Init() {
      setTitle("요청사항");
      setPreferredSize(new Dimension(400, 300));
      setResizable(false);
      setLayout(new BorderLayout());
      
      JPanel headerPanel = new JPanel();
      Color color = new Color(0, 102, 255);
      Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
      
      messageDialogPanel = new JPanel();
      
      tableNumber = new JLabel("테이블 번호");
      tableNumber.setHorizontalAlignment(JLabel.CENTER);
      tableNumber.setFont(new Font(tableNumber.getName(), Font.BOLD, 24));
      headerPanel.setBackground(color);
      headerPanel.add(tableNumber);
      
      requestMemoLabel = new JLabel("요청 사항");
      requestMemoLabel.setHorizontalAlignment(JLabel.CENTER);
      requestMemoLabel.setFont(new Font(requestMemoLabel.getName(), Font.BOLD, 20));
      
      messageDialogPanel.add(requestMemoLabel);
      messageDialogPanel.setBorder(paddingBorder);
      
      add(headerPanel, BorderLayout.NORTH);
      add(messageDialogPanel, BorderLayout.CENTER);
      
      pack();
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      setLocationRelativeTo(null);
      setModal(true);
      setVisible(false);
   }
}