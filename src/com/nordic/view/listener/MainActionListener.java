package com.nordic.view.listener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.nordic.controller.NordicFeedApplication;
import com.nordic.thread.NotifyingThread;
import com.nordic.thread.ThreadCompleteInterface;
import com.nordic.util.AudioUtil;
import com.nordic.util.LogUtil;
import com.nordic.view.NordicGui;



public class MainActionListener implements ActionListener, ThreadCompleteInterface {

	private String action;
	
	private static boolean cancel;
	
	public static void cancel(){
		cancel = true;
	}
	
	public MainActionListener(String action){
		this.action = action;
	}
	
	protected String getAction(){
		return action;
	}

	public void actionPerformed(ActionEvent e) {
		cancel = false;
		NordicGui.showProgramWorkingPanel();
		runAction(e);
	}
	
	protected void runAction(ActionEvent e) {

		try {

			AudioUtil.playStart();
			
			NotifyingThread thread = new NotifyingThread() {
			    public void doRun()
			    {
			    	try{
			    		NordicFeedApplication.runProgram(action);
			    	}catch(Exception ex){
			    		LogUtil.error(ex);
			    	}
			    }
			 };
			 
			 thread.addListener(this); 
			 thread.start();
			
		} catch (Exception ex) {
			LogUtil.error(ex);
			JOptionPane.showMessageDialog(NordicGui.frame, ex.getMessage(),
					"Dialog", JOptionPane.ERROR_MESSAGE);
		}
		
		System.gc();
	}
	



	public void notifyOfThreadComplete(Thread thread) {
		
		EventQueue.invokeLater(new Runnable() {
		    public void run()
		    {
		    	if(!cancel){
		    		try{
			    		LogUtil.info(action + " COMPLETE");
	
			    		AudioUtil.playOk();
			    		NordicGui.frame.dispose();
			    		NordicFeedApplication.setAction(
			    				NordicFeedApplication.ACTION_COMMAND_SCREEN);
						NordicGui.showScreen();
						JOptionPane.showMessageDialog(NordicGui.frame,
								 "ACTION COMPLETE", "STATUS",
								 JOptionPane.OK_OPTION);
			    	}catch(Exception ex){
			    		LogUtil.error(ex);
			    	}
			    }
		    }
		    	
		  });
	
	}


}