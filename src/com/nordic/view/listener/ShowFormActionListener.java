package com.nordic.view.listener;

import java.awt.event.ActionEvent;

import com.nordic.view.NordicGui;


public class ShowFormActionListener extends MainActionListener{


	public ShowFormActionListener(String x){
		super(x);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		NordicGui.frame.dispose();
		runAction(e);		
	}
	
	// do nothing, already on correct screen
	public void notifyOfThreadComplete(Thread thread) {

	}
	
	
}
