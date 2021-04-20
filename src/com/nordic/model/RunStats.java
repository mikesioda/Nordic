package com.nordic.model;

import java.util.concurrent.TimeUnit;

import com.nordic.util.LogUtil;

/**
 * This class is used to pull some performance metrics.
 * 
 * @author Mike Sioda
 */
public class RunStats {

	
	
	private static long defaultTime;
	static{
		defaultTime = System.currentTimeMillis();
		startAppRunTime = System.currentTimeMillis();
	}

	public static int getCountXmlDataNodes() {
		return countXmlDataNodes;
	}

	public static void addCountXmlDataNodes(int x) {
		countXmlDataNodes += x;
	}

	public static int getCountDatabaseInserts() {
		return countDatabaseInserts;
	}

	public static void addCountDatabaseInserts(int x) {
		countDatabaseInserts += x;
	}

	public static int getCountFilesFromFeed() {
		return countFilesFromFeed;
	}

	public static void addCountFilesFromFeed(int x) {
		countFilesFromFeed += x;
	}

	public static int getCountFilesLoaded() {
		return countFilesLoaded;
	}

	public static void addCountFilesLoaded(int x) {
		countFilesLoaded += x;
	}

	public static long getStartDbLoadRunTime() {
		return startDbLoadRunTime;
	}

	public static void setStartDbLoadRunTime() {
		startDbLoadRunTime = System.currentTimeMillis();
	}

	public static long getEndDbLoadRunTime() {
		return endDbLoadRunTime;
	}

	public static void setEndDbLoadRunTime() {
		endDbLoadRunTime = System.currentTimeMillis();
	}

	public static long getEndFileFeedRunTime() {
		return endFileFeedRunTime;
	}

	public static void setEndFileFeedRunTime() {
		endFileFeedRunTime = System.currentTimeMillis();
	}

	public static long getStartFileFeedRunTime() {
		return startFileFeedRunTime;
	}

	public static void setStartFileFeedRunTime() {
		startFileFeedRunTime = System.currentTimeMillis();
	}

	public static long getStartAppRunTime() {
		return startAppRunTime;
	}

	public static void setStartAppRunTime() {
		startAppRunTime = System.currentTimeMillis();
	}
	
	
	public static void print(){
		
		LogUtil.heading("PROGRAM COMPLETE");
		LogUtil.report("# FILE DOWNLOADED = " + countFilesFromFeed);
		LogUtil.report("# FILES LOADED = " + countFilesLoaded);
		LogUtil.report("# DATA NODE COUNT = " + countXmlDataNodes);
		LogUtil.report("# DB RECORDS INSERTED = " + countDatabaseInserts);

		LogUtil.report("FILE FEED RUN TIME = "
				+ getRunTime(startFileFeedRunTime, endFileFeedRunTime));
		LogUtil.report("DB LOAD RUN TIME = "
				+ getRunTime(startDbLoadRunTime, endDbLoadRunTime));
		LogUtil.report("TOTAL RUN TIME = "
				+ getRunTime(startAppRunTime, System.currentTimeMillis()));
	}
	
	
	private static String getRunTime(long start, long end) {

		long diff = end - start;
		return String.format("%d min, %d sec",
				TimeUnit.MILLISECONDS.toSeconds(diff) / 60,
				TimeUnit.MILLISECONDS.toSeconds(diff) % 60);
	}
	
	
	private static int countXmlDataNodes;
	private static int countDatabaseInserts;
	private static int countFilesFromFeed;
	private static int countFilesLoaded;
	
	private static long startDbLoadRunTime = defaultTime; 
	private static long endDbLoadRunTime = defaultTime;
	
	private static long endFileFeedRunTime = defaultTime;
	private static long startFileFeedRunTime = defaultTime;
	
	private static long startAppRunTime = defaultTime;
	
}
