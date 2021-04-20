package com.nordic.util;

import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Reads in the property values from Insight.properties.
 * 
 * @author Mike Sioda
 *
 */
public class PropUtil {
	
	public static final  String Title = "Title";
	public static final  String TitleId = "TitleId";
	public static final  String OfferCountryName = "OfferCountryName";
	public static final  String OfferGuid = "OfferGuid";
	public static final  String OfferName = "OfferName";
	public static final  String OfferRegionName = "OfferRegionName";
	public static final  String MediaType = "MediaType";
	public static final  String DateStamp = "DateStamp";
	public static final  String FreePurchases = "FreePurchases";
	public static final  String MSFTPointPurchases = "MSFTPointPurchases";
	public static final  String NumberOfPurchasesMadeInGame = "NumberOfPurchasesMadeInGame";
	public static final  String PointsPrice = "PointsPrice";
	public static final  String TokenPurchases = "TokenPurchases";
	public static final  String TotalInGamePurchasesSince20090617 = "TotalInGamePurchasesSince20090617";
	public static final  String TotalPointsLTD = "TotalPointsLTD";
	public static final  String TotalPurchases = "TotalPurchases";
	public static final  String TotalPurchasesLTD = "TotalPurchasesLTD";
	public static final  String MicrosoftBalancePurchases = "MicrosoftBalancePurchases";
	public static final  String MonetaryPurchases = "MonetaryPurchases";
	public static final  String MonetarySalesAmountLTDUSD = "MonetarySalesAmountLTDUSD";
	public static final  String MonetarySalesAmountUSD = "MonetarySalesAmountUSD";
	public static final  String MonetaryUnitPriceUSD = "MonetaryUnitPriceUSD";
	public static final  String MSBalancePurchSalesAmtLTDUSD = "MSBalancePurchSalesAmtLTDUSD";
	public static final  String MSBalancePurchSalesAmtUSD = "MSBalancePurchSalesAmtUSD";
	public static final  String MSBalanceUnitPriceUSD = "MSBalanceUnitPriceUSD";

	// XBOX PPROPERTIES
	public static String accountName;
	public static String archiveDir;
	public static String archiveFiles;
	public static String area;

	// DATABASE PROPERTIES
	public static String databaseJdbcDriver;
	public static String databasePassword;
	public static String databaseUrl;
	public static String databaseUserName;
	
	public static String csvDir;
	public static String dateFeedUrl;
	public static String deleteFiles;
	public static String feedDir;
	public static String feedName;
	public static String logDir;
	public static String logFile;
	public static String logLevel;
	public static String metadataDir;
	public static String outputDir;
	public static String overwriteFiles;
	public static String projectRootDir;
	public static String rejectDir;
	public static String reportDir;
	public static String secretKey;
	public static String slash;
	public static String titleFeedUrl;
	public static String endPointUrl;
	public static String enableSound;
	public static int feedDelay;
	public static int minSearchStringLength;
	
	
	// CONSTANTS
	private static final String LOG_FILE_NAME_DATE_FORMAT = "yyyy_MM_dd_kkmm";
	public static final String XML = "xml";
	public static final String ZIP = "zip";
	
	private static final String LOG = "logs";
	private static final String METADATA = "feed_metadata";
	private static final String REJECT_FOLDER = "feed_rejects";
	private static final String FEED = "feed";
	private static final String ARCHIVE_FOLDER = "archive";
	private static final String OUTPUT_FOLDER = "output";
	private static final String REPORT_FOLDER = "report";
	private static final String CSV_FOLDER = "csv";
	
	
	private static ResourceBundle bundle = null;
	private static Properties props = null;
	
	

	static {

		try{
			props = new Properties();
			FileInputStream file = null;
			try{
				//when called from bat file
				file = new FileInputStream("../nordic.properties");
			}catch(Exception ex2){
				//when called from runnable jar file
				file = new FileInputStream("./nordic.properties");
			}
			
			if(file!=null){
				props.load(file);
				file.close();
				System.out.println("READ IN PROPERTIES FROM PROJECT DIRECTORY");
			}
			
			
		} catch (Exception ex) {
			LogUtil.error(ex);
			props = null;
		}
		
		if (props==null){
			System.out.println("READ IN PROPERTIES FROM JAR FILE");
			bundle = ResourceBundle.getBundle("nordic");
		}
			
		
		try {
			// DIRECT PROPERTIES
			accountName = getProperty("accountName");
			area = getProperty("area");
			endPointUrl = getProperty("endPointUrl");
			feedName = getProperty("feedName");
			secretKey = getProperty("secretKey");
			databaseJdbcDriver = getProperty("databaseJdbcDriver");
			databaseUrl = getProperty("databaseUrl");
			databaseUserName = getProperty("databaseUserName");
			databasePassword = getProperty("databasePassword");
			projectRootDir = getProperty("projectRootDir");
			slash = getProperty("slash");
			logLevel = getProperty("logLevel");
			archiveFiles = getProperty("archiveFiles");
			deleteFiles = getProperty("deleteFiles");
			overwriteFiles = getProperty("overwriteFiles");
			
			enableSound = getProperty("enableSound");
			feedDelay = new Integer(getProperty("feedDelay"));
			minSearchStringLength = new Integer(getProperty("minSearchStringLength"));

			titleFeedUrl = endPointUrl + "/myFeeds/" + accountName;
			dateFeedUrl = endPointUrl + "/" + area + "/" + feedName + "/daily/";
			outputDir = projectRootDir + OUTPUT_FOLDER + slash;
			logDir = outputDir + LOG + slash;
			archiveDir = outputDir + ARCHIVE_FOLDER + slash;
			feedDir = outputDir + FEED + slash;
			rejectDir = outputDir + REJECT_FOLDER + slash;
			reportDir = outputDir + REPORT_FOLDER + slash;
			csvDir = outputDir + CSV_FOLDER + slash;
			metadataDir = outputDir + METADATA + slash;
			logFile = accountName + "_" + getLogNameDateString();
			
			bundle = null;
		} catch (Exception ex) {
			LogUtil.error(ex);
		}
	}

	
	/**
	 * Print out the values read in at runtime.
	 */
	public static void printProperties() {

		LogUtil.heading("INSIGHT.PROPERTIES");
		LogUtil.info("accountName = " + accountName);
		LogUtil.info("area = " + area);
		LogUtil.info("endPointUrl = " + endPointUrl);
		LogUtil.info("feedName = " + feedName);
		LogUtil.info("secretKey = " + secretKey);
		LogUtil.info("databaseJdbcDriver = " + databaseJdbcDriver);
		LogUtil.info("databaseUrl = " + databaseUrl);
		LogUtil.info("databaseUserName = " + databaseUserName);
		LogUtil.info("databasePassword = " + databasePassword);
		LogUtil.info("projectRootDir = " + projectRootDir);
		LogUtil.info("slash = " + slash);
		LogUtil.info("logLevel = " + logLevel);
		LogUtil.info("archiveFiles = " + archiveFiles);
		LogUtil.info("deleteFiles = " + deleteFiles);
		LogUtil.info("overwriteFiles = " + overwriteFiles);
		LogUtil.info("feedDelay = " + feedDelay);
		LogUtil.info("minSearchStringLength = " + minSearchStringLength);
		LogUtil.info("titleFeedUrl = " + titleFeedUrl);
		LogUtil.info("dateFeedUrl = " + dateFeedUrl);
		LogUtil.info("archiveDir = " + archiveDir);
		LogUtil.info("logDir = " + logDir);
		LogUtil.info("outputDir = " + outputDir);
		LogUtil.info("metadataDir = " + metadataDir);
		LogUtil.info("feedDir = " + feedDir);
		LogUtil.info("rejectDir = " + rejectDir);
	}

	/**
	 * Used for creating new file names.
	 * @return
	 */
	public static String getLogNameDateString() {
		return DateUtil.getDateString(new Date(), LOG_FILE_NAME_DATE_FORMAT);
	}
	
	private static String getProperty(String key){
		if(props!=null) return props.getProperty(key).trim();
		return bundle.getString(key).trim();
	}

}
