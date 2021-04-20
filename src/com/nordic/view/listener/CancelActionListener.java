package com.nordic.view.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.nordic.controller.DataFeed;
import com.nordic.controller.NordicFeedApplication;
import com.nordic.controller.PurchaseDao;
import com.nordic.thread.NotifyingThread;
import com.nordic.util.AudioUtil;
import com.nordic.util.LogUtil;
import com.nordic.view.NordicGui;

/**
 * Handles Cancel action.
 * 
 * @author Mike Sioda
 *
 */
public class CancelActionListener implements ActionListener {


	public void actionPerformed(ActionEvent e) {
		LogUtil.info("CANCELING ACTION");
		cancel();
		LogUtil.info("CANCEL COMPLETE");
	}
	
	private void cancel(){
		
		DataFeed.cancel();
		MainActionListener.cancel();
		//NordicFeedApplication.setAction(
			//	NordicFeedApplication.ACTION_COMMAND_SCREEN);
		
		NotifyingThread thread = SearchActionListener.getQueryThread();
		if(thread!=null){
			thread.interrupt();
		}
		
		PurchaseDao.closeConnection();
		NordicGui.showReportPanel();
		AudioUtil.playStart();
	}

}
