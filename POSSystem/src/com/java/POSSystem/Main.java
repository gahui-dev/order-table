package com.java.POSSystem;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.SwingUtilities;

import java.nio.charset.Charset;
import java.lang.reflect.Field;

public class Main {

	public static void main(String[] args) {
//		System.setProperty("file.encoding","UTF-8");
//		try{
//			Field charset = Charset.class.getDeclaredField("defaultCharset");
//			charset.setAccessible(true);
//			charset.set(null,null);
//		}
//		catch(Exception e){
//		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// SignInDialog 객체 생성 및 할당
				SignInDialog signInDialog = new SignInDialog();

				// WindowAdapter 내가 쓸 메서드만 재정의	
				// signInDialog 객체에 이벤트 연결
				signInDialog.addWindowListener(new WindowAdapter() {
					
					// windowClose 윈도우가 닫힐 때 발생
					// signInDialog 창이 닫히고 나면
					@Override
					public void windowClosed(WindowEvent e) {
						// signInDialog의 getIsSignIn이 true이면
						if(signInDialog.getIsSignIn()) {
							
							// MainFrame 객체 생성 및 할당
							MainFrame mainFrame = new MainFrame(signInDialog.getUserInfo());

							// mainFrame 객체에 이벤트 연결						
							mainFrame.addWindowListener(new WindowAdapter() {
								
								// mainFrame 창이 닫히면
								@Override
								public void windowClosed(WindowEvent e) {
									// signInDialog, mainFrame 창 닫기
									signInDialog.dispose();
									mainFrame.dispose();
								}
							});
							
							// mainFrame의 showDialog 호출
							mainFrame.showDialog();
							
						// signInDialog의 getIsSignIn이 false이면	
						} else {
							// signInDialog 창 닫기
							signInDialog.dispose();
						}
					}
				});
				
				// signInDialog의 showDialog 호출
				signInDialog.showDialog();
			}
		});
	}

}