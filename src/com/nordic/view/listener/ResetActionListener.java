package com.nordic.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.nordic.controller.NordicFeedApplication;
import com.nordic.util.AudioUtil;
import com.nordic.view.NordicGui;

public class ResetActionListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {
		AudioUtil.playStart();
		NordicGui.frame.dispose();
		NordicFeedApplication.setAction(
				NordicFeedApplication.ACTION_SEARCH);;
		NordicGui.showScreen();
		NordicGui.showSearchPanel();
	}

}
