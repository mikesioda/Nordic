package com.nordic.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import com.nordic.controller.NordicFeedApplication;

public class QuitActionListener extends WindowAdapter implements ActionListener {


	@Override
	public void windowClosing(WindowEvent windowEvent) {
		quitApplication();
	}
	
	public void actionPerformed(ActionEvent e) {
		quitApplication();
	}
	
	
	private void quitApplication(){
		 NordicFeedApplication.quitProgram();
		 
	}
	
}
