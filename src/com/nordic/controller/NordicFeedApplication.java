package com.nordic.controller;

import java.io.File;

import com.nordic.model.RunStats;
import com.nordic.util.AudioUtil;
import com.nordic.util.FileUtil;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;
import com.nordic.view.NordicGui;

/**
 * MAIN APPLICATION CLASS
 * 
 * Main method can receive arguments directly from java command line
 * or actions will be set by GUI.
 * 
 * @author Mike Sioda
 *
 */
public class NordicFeedApplication {


	/**
	 * Initialize the program & call runProgram().
	 * 
	 * Calls cleanUp() when complete.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		try{
			RunStats.setStartAppRunTime();
			init(args);
			AudioUtil.playWelcome();
			runProgram(action);
		}catch (Exception ex) {
			LogUtil.error(ex);
		}finally{
			cleanUp();
		}	
	}
	
	
	
	/**
	 * Main work method.  Analyze action value & execute
	 * 1. Data Feed.
	 * 2. Archive.
	 * 3. Database Load.
	 * 4. Show Screen (search or command).
	 * 
	 * @param userAction
	 */
	public static void runProgram(String userAction) {
		try{
			action = userAction;
			LogUtil.heading("RUNNING PROGRAM FOR ACTION = " + action);
	
			if(doFeed()){
				RunStats.setEndFileFeedRunTime();;
				DataFeed.startFileFeed(isDaily());
				FileUtil.expandZipsToXml();
				RunStats.setEndFileFeedRunTime();
			}
			
			
			if (doLoad()){
				RunStats.setStartDbLoadRunTime();
				DatabaseLoad.load();
				RunStats.setEndDbLoadRunTime();	
			}
			
			if (doArchive()){
				FileUtil.archive();
			}
			
			if (doLoad() || doArchive()){
				FileUtil.deleteFiles();
			}
			
			
			if (doShowScreen()){
				NordicGui.showScreen();
			}
			
			if(isAuto()){
				quitProgram();
			}
			
			
		} catch (Exception ex) {
			LogUtil.error(ex);
			systemErrorDetected = true;
		} 
	}
	
	
	/**
	 * Quit the applications.
	 * 	 
	 * 1. Call cleanUp()
	 * 2. Play program complete wav.
	 * 3. Print stats to log & report files.
	 * 
	 */
	public static void quitProgram(){
		cleanUp();
		try{
			
			if(systemErrorDetected){
				AudioUtil.playFailed();
			}else{
				AudioUtil.playOk();
			}

			RunStats.print();
		}catch(Exception ex){
			LogUtil.error(ex);
		}
		
		System.exit(0);
	}
	
	
	/**
	 * Get the program action.
	 * @return action
	 */
	public static String getAction() {
		return action;
	}
	
	/**
	 * Set the program action.
	 */
	public static String setAction(String x) {
		return action = x;
	}
	
	
	/**
	 * Set the program action.
	 */
	public static void errorDetected() {
		systemErrorDetected = true;
	}
	
	

	/**
	 * Initilize the follwing:
	 * 1. Load property file class into memory.
	 * 2. Set action if input in main method.
	 * 3. Create output directories if needed.
	 * 4. Print properties to log.
	 * 
	 * @param args - from main method
	 */
	private static void init(String[] args) {

		try{
			Class.forName("com.nordic.util.PropUtil");
			
			if (args != null && args.length > 0) {
				action = args[0];
			}else{
				action = ACTION_COMMAND_SCREEN;
			}
			
						
			File outputDir = new File(PropUtil.outputDir);
			if (!outputDir.exists()){
				outputDir.mkdir();
			}
			
			File archiveDir = new File(PropUtil.archiveDir);
			if (!archiveDir.exists()){
				archiveDir.mkdir();
			}
			
			File csvDir = new File(PropUtil.csvDir);
			if (!csvDir.exists()){
				csvDir.mkdir();
			}
			
			File feedDir = new File(PropUtil.feedDir);
			if (!feedDir.exists()){
				feedDir.mkdir();
			}
			
			File metadataDir = new File(PropUtil.metadataDir);
			if (!metadataDir.exists()){
				metadataDir.mkdir();
			}
			
			File rejectDir = new File(PropUtil.rejectDir);
			if (!rejectDir.exists()){
				rejectDir.mkdir();
			}
			
			File logDir = new File(PropUtil.logDir);
			if (!logDir.exists()){
				logDir.mkdir();
			}
			
			
		}catch(Exception ex){
			LogUtil.error(ex);
		}
		
		PropUtil.printProperties();
	}
	
	/**
	 * Cleanup closes the database connections.
	 */
	private static void cleanUp(){
		try{
			PurchaseDao.closeConnection();
		}catch (Exception ex) {
			LogUtil.error(ex);
		}
	}
	
	/**
	 * Should program download feed?
	 * @return boolean
	 */
	private static boolean doFeed(){
		if(action.equals(ACTION_DAILY_FEED_AND_DB_LOAD) ||
				action.equals(ACTION_DAILY_FEED_ONLY) ||
				action.equals(ACTION_AUTOMATIC_DAILY_FEED) ||
				action.equals(ACTION_INITIAL_FEED_AND_DB_LOAD) ){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Should program upload to database?
	 * @return boolean
	 */
	private static boolean doLoad(){
		if(action.equals(ACTION_DAILY_FEED_AND_DB_LOAD) ||
				action.equals(ACTION_DB_LOAD) ||
				action.equals(ACTION_AUTOMATIC_DAILY_FEED) ||
				action.equals(ACTION_INITIAL_FEED_AND_DB_LOAD)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Should program show GUI?
	 * @return boolean
	 */
	private static boolean doShowScreen(){
		if(action.equals(ACTION_COMMAND_SCREEN) ||
				action.equals(ACTION_SEARCH)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Should program archive files?
	 * @return boolean
	 */
	private static boolean doArchive(){
		
		
		if(action.equals(ACTION_ARCHIVE)){
			return true;
		}
		
		if(!PropUtil.archiveFiles.equals("Y")){
			return false;
		}
		
		if(action.equals(ACTION_DB_LOAD) ||
				action.equals(ACTION_DAILY_FEED_AND_DB_LOAD)  ||
				action.equals(ACTION_AUTOMATIC_DAILY_FEED)  ){
			return true;
		}
		
		return false;
	}

	/**
	 * Is the the daily feed called by task (not GUI)?
	 * @return boolean
	 */
	private static boolean isAuto(){
		if(action.equals(ACTION_AUTOMATIC_DAILY_FEED) ){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Is this a daily feed (auto, feed-only, or feed & load)?
	 * @return boolean
	 */
	private static boolean isDaily(){
		if(action.equals(ACTION_DAILY_FEED_AND_DB_LOAD) ||
				action.equals(ACTION_DAILY_FEED_ONLY) ||
				action.equals(ACTION_AUTOMATIC_DAILY_FEED)){
			return true;
		}
		
		return false;
	}
	
	
	
	

	public static final String ACTION_COMMAND_SCREEN = "ACTION_COMMAND_SCREEN";
	public static final String ACTION_ARCHIVE = "ACTION_ARCHIVE";
	public static final String ACTION_DB_LOAD = "ACTION_DB_LOAD";
	public static final String ACTION_DAILY_FEED_ONLY = "ACTION_DAILY_FEED_ONLY";
	public static final String ACTION_SEARCH = "ACTION_SEARCH";
	public static final String ACTION_DAILY_FEED_AND_DB_LOAD = "ACTION_DAILY_FEED_AND_DB_LOAD";
	public static final String ACTION_AUTOMATIC_DAILY_FEED = "DAILY";
	public static final String ACTION_INITIAL_FEED_AND_DB_LOAD = "ACTION_INITIAL_FEED_AND_DB_LOAD";
	public static final String ACTION_PROGRAM_COMPLETE = "ACTION_PROGRAM_COMPLETE";

	private static String action;
	private static boolean systemErrorDetected = false;

}
