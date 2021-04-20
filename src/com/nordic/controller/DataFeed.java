/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nordic.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nordic.model.RunStats;
import com.nordic.util.Base64Coder;
import com.nordic.util.DateUtil;
import com.nordic.util.FileUtil;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;

/**
 * This class is used to pull Insight data from XboxLive. Based on the user
 * input data is pulled onto the local machine as zipped XML files.
 */
public class DataFeed {

	
	/**
	 * This method will pull the feed from Insight.
	 * 
	 * 1. Create Nordic_Feeds_Authorization.xml in output/feed_metadata.
	 * 2. Create TITLE_ID_purchases_DatesAvailable.xml files in output/feed_metadata.
	 * 3. Create ZIP files from purchase feed in output/feed.
	 * 
	 * @param doDailyFeed = false on initial load
	 * @throws Exception
	 */
	public static void startFileFeed(boolean isDaily) throws Exception {
		cancel = false;
		doDailyFeed = isDaily;
		LogUtil.heading("START FILE FEED");
		offsetDate = DateUtil.getOffsetDate(PropUtil.feedDelay);
		fullFeedSkipDates = getFullFeedSkipDates();
		
		ArrayList<String> titleIds = getTitleIds();
		Iterator<String> titleIT = titleIds.iterator();
		LogUtil.report("# TITLES " + titleIds.size());
		while (titleIT.hasNext()) {
			String titleId = titleIT.next();
			ArrayList<String> dateUrls = getDateUrls(titleId);
			Iterator<String> it = dateUrls.iterator();
			LogUtil.report(titleId + " # DATES = " + dateUrls.size());
			int failedFileCreation = 0;
			while (it.hasNext()) {

				String dateUrl = it.next();
				String fileName = PropUtil.feedDir + dateUrl.substring(1)
						.replaceAll("/", "_") + FORMAT + ".zip";
				RunStats.addCountFilesFromFeed(1);
				try {
					createFile(PropUtil.endPointUrl + dateUrl + FORMAT,
							fileName, ZIP_CONTNET);
					RunStats.addCountFilesLoaded(1);
				} catch (Exception ex) {
					LogUtil.report(titleId + " FAILED FEED = " + dateUrl);
					LogUtil.error(ex);
					
				}
			}
		}
		
	}

	/**
	 * Create a file by populating data from the given URL into the given
	 * fileName.
	 * 
	 * @param url
	 * @param fileName
	 * @param contentType
	 */
	private static void createFile(String url, String fileName,
			String contentType) throws Exception {
		
		if(cancel){
			return;
		}
		
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		int i;
		
		
		
		// create the file & delete old copies (if they exist)
		File f = new File(fileName);
		if (f.exists()) {
			if (PropUtil.overwriteFiles.equals("Y")) {
				LogUtil.info("OVERWRITE " + fileName);
				f.delete();
			} else {
				LogUtil.info("SKIP " + fileName);
				return;
			}
		}

		HttpURLConnection con = null;
		try {
			con = getUrlConnection(url, contentType);
			bis = new BufferedInputStream(con.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(fileName));
			while ((i = bis.read()) != -1) {
				bos.write(i);
			}
			LogUtil.info("PULLED FEED = " + fileName);

		} catch (Exception ex) {
			LogUtil.report("FEED FAILED = " + fileName);
			LogUtil.error(ex);
			throw ex;
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
			if (con != null)
				con.disconnect();
		}

	}

	/**
	 * Create the authorization header for the given URL.
	 * 
	 * @param uc
	 * @param url
	 * @return AuthHeader - String
	 * @throws Exception
	 */
	private static String getAuthenticationHeader(URLConnection uc, String url)
			throws Exception {
		byte[] bEncodedKey = PropUtil.secretKey.getBytes("UTF8");
		String encodedKey = new String(Base64Coder.encode(bEncodedKey));

		byte[] sharedKey = Base64Coder.decodeLines(encodedKey);

		Mac hasher = Mac.getInstance("HmacSHA256");
		hasher.init(new SecretKeySpec(sharedKey, hasher.getAlgorithm()));
		hasher.update(getStringToSign(uc, url));
		byte[] bHashedSignature = hasher.doFinal();
		String base64Signature = new String(
				Base64Coder.encode(bHashedSignature));
		String authSignature = "SharedKeyLite " + PropUtil.accountName + ":"
				+ base64Signature;
		LogUtil.debug("url = " + url);
		LogUtil.debug("base64Signature = " + base64Signature);
		LogUtil.debug("Authentication Signiture = " + authSignature);
		return authSignature;

	}
	

	/**
	 * Get the list of XML files for a specific purchase date. 
	 * Creates files in output/fee_metadata.
	 * Example file = 4A5707D2_purchases_DatesAvailable.xml
	 * 
	 * @return ArrayList<String> dateURLs
	 * * @throws Exception
	 */
	private static ArrayList<String> getDateUrls(String titleId)
			throws Exception {
		String fileName = PropUtil.metadataDir + titleId + "_"
				+ PropUtil.feedName + "_DatesAvailable.xml";

		createFile(PropUtil.dateFeedUrl + titleId, fileName, XML_CONTENT);
		return parseXml(fileName, "AvailableFeed", "blobContainerReference", true);
	}

	/**
	 * When executing the full feed, we still want to consider the feedDelay
	 * from the properties file.
	 * 
	 * @return ArrayList<String> dates
	 * @throws Exception
	 */
	private static ArrayList<String> getFullFeedSkipDates() {
		ArrayList<String> dates = new ArrayList<String>();
		for (int i = 1; i <= PropUtil.feedDelay; i++) {
			String next = DateUtil.getOffsetDate(i);
			dates.add(next);
		}

		return dates;
	}

	
	/**
	 * Get todays GMT date in the required format.
	 * @return Formatted GMT Date
	 */
	private static String getRequestDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(REQUEST_DATE_FORMAT);
		return sdf.format(DateUtil.getGmtDate());
	}

	/**
	 * @byte=64 <= replace them with a 10 value to match .NET byte conversion
	 * 
	 * @param uc
	 * @param url
	 * @return bStringToSign byte[]
	 * @throws Exception
	 */
	private static byte[] getStringToSign(URLConnection uc, String url)
			throws Exception {

		String stringToSign = "GET@" + uc.getRequestProperty("CONTENT-TYPE")
				+ "@" + uc.getRequestProperty("x-ms-date") + "@" + "/"
				+ PropUtil.accountName + "/" + url;

		byte[] bStringToSign = stringToSign.getBytes("UTF8");
		for (int i = 0; i < bStringToSign.length; i++) {
			byte b = bStringToSign[i];
			if (b == 64)
				bStringToSign[i] = 10;
		}

		return bStringToSign;
	}

	/**
	 * Get the list of titles that are authorized for the accountName. 
	 * Creates one file in output/feed_metadata = Nordic_Feeds_Authorizations.xml.
	 * 
	 * @return ArrayList<String> titles
	 */
	private static ArrayList<String> getTitleIds() throws Exception {
		String fileName = PropUtil.metadataDir + PropUtil.accountName
				+ "_Authorizations.xml";
		createFile(PropUtil.titleFeedUrl, fileName, XML_CONTENT);
		return parseXml(fileName, "Authorization", "ObjectTypeDiscriminator", false);
	}

	/**
	 * Create URL Connection and set request properties.
	 * 
	 * @param url
	 * @param contentType
	 * @return HttpURLConnection
	 * @throws Exception
	 */
	private static HttpURLConnection getUrlConnection(String url,
			String contentType) throws Exception {

		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setConnectTimeout(15 * 1000);
		con.setRequestProperty("CONTENT-TYPE", contentType);
		con.setRequestProperty("x-ms-date", getRequestDate());
		String authenticationHeader = getAuthenticationHeader(con, url);
		con.setRequestProperty("Authorization", authenticationHeader);
		return con;
	}
	
	

	/**
	 * Parse the XML file to & return list of urlValues.  If 
	 * 
	 * @param fileName
	 * @param tagName
	 * @param name
	 * @param isDateFeed - if true, extract node data for target date 
	 * @return urlValues as ArrayList<String>
	 */
	private static ArrayList<String> parseXml(String fileName, String tagName,
			String name, boolean isDateFeed) throws Exception {

		if(cancel) return new ArrayList<String>();
		
		ArrayList<String> urlList = new ArrayList<String>();

		
		Document document = FileUtil.builder.parse(fileName);
		NodeList nodeList = document.getDocumentElement().getElementsByTagName(
				tagName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element e = (Element) nodeList.item(i);
			String urlValue = e.getAttribute(name);
			String chkDate = e.getAttribute("dataAvailableFor");
			if (!isDateFeed){
				urlList.add(urlValue); // for authorizaion.xml
			}else if (doDailyFeed) { 
				Integer c = chkDate.compareTo(offsetDate);
				if (c == 0)
					urlList.add(urlValue);
			} else if(!fullFeedSkipDates.contains(chkDate)){
					urlList.add(urlValue);
			}
		}

		// sor the list for organized processing
		Collections.sort(urlList);

		return urlList;
	}
	
	public static void cancel(){
		cancel = true;
	}
	
	private static boolean cancel = false;
	private static final String FORMAT = "xml";
	private static String offsetDate;
	private static ArrayList<String> fullFeedSkipDates;
	private static final String REQUEST_DATE_FORMAT = "E, dd MMM yyyy HH':'mm':'ss 'GMT'";
	private static final String XML_CONTENT = "text/xml";
	private static final String ZIP_CONTNET = "application/x-zip-compressed";

	private static boolean doDailyFeed;

}
