package com.java.POSSystem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.*;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.java.POSSystem.Model.*;

public class SignInDialog extends JDialog {
   // user.xml
   private static final String XML_USER_PATH = Common.getUserDatabaseConnectionString();
   private JTextField userIdField;
   private JPasswordField userPwdField;
   private JTextField IpField;
   private JTextField PortField;
   private JButton SignInButton;
   private JButton SignUpButton;
   
   private UserInfo userInfo;

   public UserInfo getUserInfo() {
      return userInfo;
   }

   // 로그인 여부를 확인하는 변수 isSignIn
   private boolean isSignIn = false;

   // isSignIn getter
   public boolean getIsSignIn() {
      return isSignIn;
   }

   // 생성자
   public SignInDialog() {
      Init();
   }

   // 
   public void showDialog() {
      setVisible(true);
   }

   private void Init() {
      setTitle("로그인");
      setPreferredSize(new Dimension(400, 400));
      setResizable(false);
      setLayout(new BorderLayout());

      JLabel logoLabel = new JLabel("<html>Order Table<br>POS System</html>");
      
      Font labelFont = logoLabel.getFont();
      Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);

      logoLabel.setHorizontalAlignment(JLabel.CENTER);
      logoLabel.setFont(new Font(labelFont.getName(), Font.BOLD, 20));
      logoLabel.setBorder(paddingBorder);

      JPanel inputPanel = new JPanel(new GridBagLayout());

      // IP와 PORT 레이블 및 필드
      JLabel IpLabel = new JLabel("IP");
      IpField = new JTextField();
      IpField.setPreferredSize(new Dimension(200, 30));

      JLabel PortLabel = new JLabel("PORT");
      PortField = new JTextField();
      PortField.setPreferredSize(new Dimension(200, 30));

      JLabel userIdLabel = new JLabel("아이디");
      userIdField = new JTextField(20);
      userIdField.setPreferredSize(new Dimension(200, 30));

      JLabel userPwdLabel = new JLabel("비밀번호");
      userPwdField = new JPasswordField();
      userPwdField.setPreferredSize(new Dimension(200, 30));

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(5, 5, 5, 5);

      gbc.gridx = 0;
      gbc.gridy = 0;
      inputPanel.add(IpLabel, gbc);

      gbc.gridx = 1;
      gbc.gridy = 0;
      inputPanel.add(IpField, gbc);

      gbc.gridx = 0;
      gbc.gridy = 1;
      inputPanel.add(PortLabel, gbc);

      gbc.gridx = 1;
      gbc.gridy = 1;
      inputPanel.add(PortField, gbc);
      
      gbc.gridx = 0;
      gbc.gridy = 2;
      inputPanel.add(userIdLabel, gbc);

      gbc.gridx = 1;
      gbc.gridy = 2;
      inputPanel.add(userIdField, gbc);

      gbc.gridx = 0;
      gbc.gridy = 3;
      inputPanel.add(userPwdLabel, gbc);

      gbc.gridx = 1;
      gbc.gridy = 3;
      inputPanel.add(userPwdField, gbc);

      JPanel buttonPanel = new JPanel();
      SignInButton = new JButton("로그인");
      SignUpButton = new JButton("회원가입");
      
      addListener();

      paddingBorder = BorderFactory.createEmptyBorder(0, 0, 10, 0);
      buttonPanel.setBorder(paddingBorder);
      buttonPanel.add(SignInButton);
      buttonPanel.add(SignUpButton);

      add(logoLabel, BorderLayout.NORTH);
      add(inputPanel, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);

      pack();
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      setLocationRelativeTo(null);
      setModal(true);
      setVisible(false);
   }
   
   // 162~166 줄여보기
//   private boolean isBlackOrEmpty(Component comp) {
//	   comp.
//	   return false;
//   }

   private void addListener() {
      ActionListener listener = new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent ae) { 
        	 // 로그인 버튼
            if(ae.getSource().equals(SignInButton)){
               String inputUserId = userIdField.getText();
               String inputUserPwd = new String(userPwdField.getPassword());

               // IP, PORT 넘기는 부분
               String inputPort = PortField.getText();
               
               if(IpField.getText().isBlank() || IpField.getText().isEmpty() || 
            		   inputPort.isBlank() || inputPort.isEmpty() ||
            		   inputUserId.isBlank() || inputUserId.isEmpty() ||
            		   inputUserPwd.isBlank() || inputUserPwd.isEmpty()) {
            	   JOptionPane.showMessageDialog(null, "모든 항목은 필수값입니다.\n모든 항목을 입력해주세요.", "Message", JOptionPane.ERROR_MESSAGE);
            	   return;
               }
               
               // 연결 가능한 정보인가 ? (서버 아이피와 포트번호)
               // 더미 서버 클
               
               int inputPortInt = Integer.parseInt(inputPort);
               Server.HOST_IP = IpField.getText();
               Server.PORT = inputPortInt;
               
               try {
                  Document doc = Common.getXMLDocument(XML_USER_PATH);
                  NodeList nList = doc.getElementsByTagName("user");
                  Element user;
                  for(int i = 0; i < nList.getLength(); i++) {
                     user = (Element)nList.item(i);
                     String userId = user.getElementsByTagName("userId").item(0).getTextContent();
                     String userPwd = user.getElementsByTagName("userPwd").item(0).getTextContent();

                     if(userId.equals(inputUserId) && userPwd.equals(inputUserPwd)) {
                        // 로그인 성공
                        isSignIn = true;
                        userInfo = new UserInfo(userId,
                              user.getElementsByTagName("businessName").item(0).getTextContent(),
                              user.getElementsByTagName("userName").item(0).getTextContent());
                        dispose();
                        break;
                     } 
                  }

                  if(!isSignIn) {
                     JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 올바르지 않습니다.", "Message", JOptionPane.ERROR_MESSAGE);
                     userIdField.setText("");
                     userPwdField.setText("");
                  }

               } catch (ParserConfigurationException | SAXException | IOException e1) {

                  e1.printStackTrace();
               }
               
               // 회원가입 버튼
            } else if (ae.getSource().equals(SignUpButton)) {
               SignUpDialog signUpdialog = new SignUpDialog();
               signUpdialog.addWindowListener(new WindowAdapter() {
                  @Override
                  public void windowClosed(WindowEvent e) {
                     signUpdialog.dispose();
                  }

               });

               signUpdialog.showDialog();
            }

         }
      }; 

      // 버튼에 이벤트 연결
      SignInButton.addActionListener(listener);
      SignUpButton.addActionListener(listener);
   }
}