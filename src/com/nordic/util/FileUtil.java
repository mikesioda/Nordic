package com.nordic.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nordic.model.PurchaseBo;
import com.nordic.model.RunStats;

/**
 * This utility is used for file management.
 * 
 * @author Mike Sioda
 *
 */
public class FileUtil {

	public static DocumentBuilder builder;
	static{
		try{
			builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Zips up output/feed, output/feed_metadata, output/report, & output/log.
	 * Places zip into output/archive.
	 * @throws Exception
	 */
	public static void archive() throws Exception {

		LogUtil.heading("START ARCHIVE");

		// Initiate ZipFile object with the path/name of the zip file.
		ZipFile zipFile = new ZipFile(PropUtil.archiveDir + PropUtil.slash
				+ PropUtil.logFile + ".zip");

		// Initiate Zip Parameters which define various properties such
		// as compression method, etc.
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

		// Add folder to the zip file
		//zipFile.addFolder(PropUtil.logDir, parameters);
		zipFile.addFolder(PropUtil.feedDir, parameters);
		zipFile.addFolder(PropUtil.metadataDir, parameters);

		if (zipFile.isValidZipFile()) {
			LogUtil.info("ARCHIVE SUCCESSFUL");
		} else {
			LogUtil.warning("ARCHIVE CORRUPT");
		}

	}
	
	
	/**
	 * Expands the zips in output/feed.  Puts XML into same directory.
	 * 
	 * @throws Exception
	 */
	public static void expandZipsToXml(){

		try {
			LogUtil.info("START expandZipsToXml()");
			Iterator<String> fileNames = getFileNames(PropUtil.feedDir,
					PropUtil.ZIP).iterator();

			while (fileNames.hasNext()) {
				String source = PropUtil.feedDir + fileNames.next();
				unzipFile(source);
			}
		} finally {
			LogUtil.info("END expandZipsToXml()");
		}

	}
	
	
	
	/**
	 * Get all the file names for the given direcotry dir & fileType..
	 * 
	 * @param dir
	 * @param fileType
	 * @return List<String>
	 * @throws Exception
	 */
	public static List<String> getFileNames(String dir, String fileType) {
		File f = new File(dir);
		String[] names = f.list();
		List<String> list = new ArrayList<String>();
		//LogUtil.debug("LIST FILE NAME FOR = " + dir);
		for (int i = 0; i < names.length; i++) {
			if (!names[i].startsWith(".") && 
					(fileType==null || names[i].endsWith(fileType))) {
				//LogUtil.debug("NEXT FILE ADDED = " + names[i]);
				list.add(names[i]);
			} else {
				//LogUtil.debug("NEXT FILE SKIPPED = " + names[i]);
			}
		}
		
		LogUtil.info("# FILES in " + dir + " = " + list.size());

		return list;
	}

	/**
	 * Gets purchase data from the XML file.
	 * 
	 * @param fileName
	 * @return ArrayList<PurchaseBo>
	 * @throws Exception
	 */
	public static ArrayList<PurchaseBo> getPurchaseBosFromFile(String fileName)
			throws Exception {

		ArrayList<PurchaseBo> bos = new ArrayList<PurchaseBo>();

		String fullPathFileName = PropUtil.feedDir + fileName;

		Document document = builder.parse(fullPathFileName);
		NodeList dataNodes = document.getDocumentElement()
				.getElementsByTagName("Data");

		RunStats.addCountXmlDataNodes(dataNodes.getLength());
		for (int i = 0; i < dataNodes.getLength(); i++) {
			Element dataNode = (Element) dataNodes.item(i);
			bos.add(createPurchaseBo(dataNode));
		}

		return bos;
	}

	
	
	
	/**
	 * Deletes the feed & feed_metadata if property file deleteFiles=Y.
	 * 
	 */
	public static void deleteFiles() {

		try{
			
		
			if (!PropUtil.deleteFiles.equals("Y")) {
				LogUtil.heading("SKIP DELETE FEED + FEED_METADATA DIRECTORY");
				return;
			}
	
			LogUtil.heading("START DELETE FEED + FEED_METADATA DIRECTORIES");
	
			List<String> feedFileNames = getFileNames(PropUtil.feedDir);
			if (feedFileNames != null) {
				Iterator<String> feedFiles = feedFileNames.iterator();
				while (feedFiles.hasNext()) {
					String source = PropUtil.feedDir + feedFiles.next();
					File deleteFile = new File(source);
					if (deleteFile.delete()) {
						LogUtil.info(deleteFile.getName() + " is deleted.");
					} else {
						LogUtil.warning("Delete file failed for: "
								+ deleteFile.getName());
					}
				}
			}
	
			List<String> metadataFileNames = getFileNames(PropUtil.metadataDir);
			if (metadataFileNames != null) {
				Iterator<String> metaFiles = metadataFileNames.iterator();
				while (metaFiles.hasNext()) {
					String source = PropUtil.metadataDir + metaFiles.next();
					File deleteFile = new File(source);
					if (deleteFile.delete()) {
						LogUtil.info(deleteFile.getName() + " is deleted.");
					} else {
						LogUtil.warning("Delete file failed for: "
								+ deleteFile.getName());
					}
				}
			}
		

			LogUtil.heading("END DELETE FEED + FEED_METADATA DIRECTORIES");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		

	}
	
	
	/**
	 * Get all the file names for the given direcotry dir.
	 * 
	 * @param dir
	 * @return List<String>
	 * @throws Exception
	 */
	private static List<String> getFileNames(String dir)  {
		return getFileNames(dir, null);
	}


	
	/**
	 * Create the PurchaseBo from the XML data.
	 * 
	 * @param dataElement
	 * @return PurchaseBo
	 * @throws Exception
	 */
	private static PurchaseBo createPurchaseBo(Element dataElement)
			throws Exception {

		PurchaseBo bo = new PurchaseBo();

		bo.DateStamp = getRequiredIntegerTagData(dataElement, PropUtil.DateStamp);
		bo.Title = getRequiredStringTagData(dataElement, PropUtil.Title);
		bo.TitleId = getRequiredStringTagData(dataElement, PropUtil.TitleId);

		bo.MediaType = getStringTagData(dataElement, PropUtil.MediaType);
		bo.OfferCountryName = getStringTagData(dataElement, PropUtil.OfferCountryName);
		bo.OfferGuid = getStringTagData(dataElement, PropUtil.OfferGuid);
		bo.OfferName = getStringTagData(dataElement, PropUtil.OfferName);
		bo.OfferRegionName = getStringTagData(dataElement, PropUtil.OfferRegionName);

		bo.FreePurchases = getIntegerTagData(dataElement, PropUtil.FreePurchases);
		bo.MicrosoftBalancePurchases = getIntegerTagData(dataElement,
				PropUtil.MicrosoftBalancePurchases);
		bo.MonetaryPurchases = getIntegerTagData(dataElement,
				PropUtil.MonetaryPurchases);
		bo.MSFTPointPurchases = getIntegerTagData(dataElement,
				PropUtil.MSFTPointPurchases);
		bo.NumberOfPurchasesMadeInGame = getIntegerTagData(dataElement,
				PropUtil.NumberOfPurchasesMadeInGame);
		bo.PointsPrice = getIntegerTagData(dataElement, PropUtil.PointsPrice);
		bo.TokenPurchases = getIntegerTagData(dataElement, PropUtil.TokenPurchases);
		bo.TotalInGamePurchasesSince20090617 = getIntegerTagData(dataElement,
				PropUtil.TotalInGamePurchasesSince20090617);
		bo.TotalPointsLTD = getIntegerTagData(dataElement, PropUtil.TotalPointsLTD);
		bo.TotalPurchases = getIntegerTagData(dataElement, PropUtil.TotalPurchases);
		bo.TotalPurchasesLTD = getIntegerTagData(dataElement,
				PropUtil.TotalPurchasesLTD);
		
		bo.MonetarySalesAmountUSD = getDoubleTagData(dataElement,
				PropUtil.MonetarySalesAmountUSD);
		bo.MonetarySalesAmountUSD = getDoubleTagData(dataElement,
				PropUtil.MonetarySalesAmountLTDUSD);
		bo.MonetaryUnitPriceUSD = getDoubleTagData(dataElement,
				PropUtil.MonetaryUnitPriceUSD);
		bo.MSBalancePurchSalesAmtLTDUSD = getDoubleTagData(dataElement,
				PropUtil.MSBalancePurchSalesAmtLTDUSD);
		bo.MSBalancePurchSalesAmtUSD = getDoubleTagData(dataElement,
				PropUtil.MSBalancePurchSalesAmtUSD);
		bo.MSBalanceUnitPriceUSD = getDoubleTagData(dataElement,
				PropUtil.MSBalanceUnitPriceUSD);

		return bo;
	}

	private static void unzipFile(String source) {
		try {
			ZipFile zip = new ZipFile(source);
			zip.extractAll(PropUtil.feedDir);
			LogUtil.debug(source + " UNZIPPED");
		} catch (Exception ex) {
			LogUtil.report("ZIP FILE CORRUPT - CANNOT EXTRACT DATA = " + source);
			LogUtil.error(ex);
			copyFile(source, PropUtil.rejectDir+ source.substring(
					source.lastIndexOf(PropUtil.slash)+1));
		}
	}

	
	public static void copyFile(String source, String dest){
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try{
			
	    	   in = new BufferedInputStream(new FileInputStream(source));
	           out = new BufferedOutputStream(new FileOutputStream(dest));

	            int i=0;
				while ((i = in.read()) != -1) {
					out.write(i);
				}
				

	    	  LogUtil.info("File is moved successful!");
	    	}catch(Exception ex){
	    		LogUtil.info("File failed to move!  Bad File = " + source);
	    		LogUtil.error(ex);
	    	}finally{
	    		try{
	    			if(in!=null) in.close();
	    			if(out!=null) out.close();
	    		}catch(Exception ex){
	    			ex.printStackTrace();
	    		}
	    	}
	}

	private static Double getDoubleTagData(Element dataElement, String tagName) {
		Double tagValue = new Double(0);

		try {
			NodeList nodes = dataElement.getElementsByTagName(tagName);
			String content = ((Element) nodes.item(0)).getTextContent();
			tagValue = Double.parseDouble(content);
		} catch (Exception ex) {
			LogUtil.debug("MISSING TAG = " + tagName);
		}

		return tagValue;

	}

	private static Integer getIntegerTagData(Element dataElement, String tagName) {
		Integer tagValue = new Integer(0);
		;

		try {
			NodeList nodes = dataElement.getElementsByTagName(tagName);
			String content = ((Element) nodes.item(0)).getTextContent();
			tagValue = new Integer(content);
		} catch (Exception ex) {
			LogUtil.debug("MISSING TAG = " + tagName);
		}

		return tagValue;

	}

	private static Integer getRequiredIntegerTagData(Element dataElement,
			String tagName) throws Exception {
		Integer tagValue = null;
		try {
			NodeList nodes = dataElement.getElementsByTagName(tagName);
			tagValue = new Integer(((Element) nodes.item(0)).getTextContent());
		} catch (Exception ex) {
			LogUtil.report("MISSING REQUIRED TAG = " + tagName);
			throw ex;
		}

		return tagValue;

	}

	private static String getRequiredStringTagData(Element dataElement,
			String tagName) throws Exception {
		String tagValue = null;
		try {
			NodeList nodes = dataElement.getElementsByTagName(tagName);
			tagValue = ((Element) nodes.item(0)).getTextContent();
		} catch (Exception ex) {
			LogUtil.report("MISSING REQUIRED TAG = " + tagName);
			throw ex;
		}

		return tagValue;

	}

	private static String getStringTagData(Element dataElement, String tagName) {
		String tagValue = "";
		try {
			NodeList nodes = dataElement.getElementsByTagName(tagName);
			tagValue = ((Element) nodes.item(0)).getTextContent();
		} catch (Exception ex) {
			LogUtil.debug("MISSING TAG = " + tagName);
		}

		return tagValue;
	}

}
