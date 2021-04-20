package com.nordic.controller;


import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.csvreader.CsvWriter;
import com.nordic.model.PurchaseBo;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;
import com.nordic.view.listener.RangeChangeListener;

/**
 * This class is used to build the CSV report.

 * @author Mike Sioda
 *
 */
public class ReportBuilder {

	private static int x = 1;
	private static final String fileName = PropUtil.csvDir + "Report_"
			+ PropUtil.getLogNameDateString();
	
	/**
	 * Contents of report are built from the list of PurchaseBos.
	 * 
	 * @param bos
	 */
	public static void createFile(List<PurchaseBo> bos) {

		String outputFile = fileName + ".csv";
		LogUtil.info("outputFile = " + outputFile);
		// before we open the file check to see if it already exists
		boolean alreadyExists = new File(outputFile).exists();
		LogUtil.info("alreadyExists = " + alreadyExists);
		while(alreadyExists){
			outputFile = fileName + "_" + x++ +".csv";
			alreadyExists = new File(outputFile).exists();
			LogUtil.info("outputFile = " + outputFile);
			LogUtil.info("alreadyExists = " + alreadyExists);
		}
		
		LogUtil.info("CREATE CSV FILE = " + outputFile);

		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');

			Iterator<String> it = RangeChangeListener.labels.keySet().iterator();
			while(it.hasNext()){
				csvOutput.write(RangeChangeListener.labels.get(it.next()));
			}
		
			csvOutput.endRecord();
			LogUtil.info("PRINT RECORDS to CSV (total = " + bos.size() + ")");
			Iterator<PurchaseBo> results = bos.iterator();
			while(results.hasNext()){
				outputRecord(csvOutput, results.next());
			}
			LogUtil.info("CSV HAS BEEN CREATED");
			csvOutput.close();
		} catch (IOException ex) {
			LogUtil.error(ex);
		}

	}

	/**
	 * Called for each record output to the CSV file.
	 * 
	 * @param csvOutput
	 * @param bo
	 * @throws IOException
	 */
	private static void outputRecord(CsvWriter csvOutput, PurchaseBo bo) throws IOException{

		Iterator<String> it = RangeChangeListener.labels.keySet().iterator();
		while(it.hasNext()){
			String next = it.next();
			if (next.equals(PropUtil.Title)) csvOutput.write(bo.Title);
			else if (next.equals(PropUtil.TitleId)) csvOutput.write(bo.TitleId);
			else if (next.equals(PropUtil.DateStamp)) csvOutput.write(String.valueOf(bo.DateStamp));
			else if (next.equals(PropUtil.OfferName)) csvOutput.write(bo.OfferName);
			else if (next.equals(PropUtil.OfferGuid)) csvOutput.write(bo.OfferGuid);
			else if (next.equals(PropUtil.OfferRegionName)) csvOutput.write(bo.OfferRegionName);
			else if (next.equals(PropUtil.OfferCountryName)) csvOutput.write(bo.OfferCountryName);
			else if (next.equals(PropUtil.MediaType)) csvOutput.write(bo.MediaType);
			else if (next.equals(PropUtil.FreePurchases)) csvOutput.write(String.valueOf(bo.FreePurchases));
			else if (next.equals(PropUtil.MicrosoftBalancePurchases)) csvOutput.write(String.valueOf(bo.MicrosoftBalancePurchases));
			else if (next.equals(PropUtil.MonetaryPurchases)) csvOutput.write(String.valueOf(bo.MonetaryPurchases));
			else if (next.equals(PropUtil.TokenPurchases)) csvOutput.write(String.valueOf(bo.TokenPurchases));
			else if (next.equals(PropUtil.MSFTPointPurchases)) csvOutput.write(String.valueOf(bo.MSFTPointPurchases));
			else if (next.equals(PropUtil.TotalPurchases)) csvOutput.write(String.valueOf(bo.TotalPurchases));
			else if (next.equals(PropUtil.TotalPurchasesLTD)) csvOutput.write(String.valueOf(bo.TotalPurchasesLTD));
			else if (next.equals(PropUtil.NumberOfPurchasesMadeInGame)) csvOutput.write(String.valueOf(bo.NumberOfPurchasesMadeInGame));
			else if (next.equals(PropUtil.TotalInGamePurchasesSince20090617)) csvOutput.write(String.valueOf(bo.TotalInGamePurchasesSince20090617));
			else if (next.equals(PropUtil.PointsPrice)) csvOutput.write(String.valueOf(bo.PointsPrice));
			else if (next.equals(PropUtil.TotalPointsLTD)) csvOutput.write(String.valueOf(bo.TotalPointsLTD));
			else if (next.equals(PropUtil.MonetaryUnitPriceUSD)) csvOutput.write(String.valueOf(bo.MonetaryUnitPriceUSD));
			else if (next.equals(PropUtil.MonetarySalesAmountUSD)) csvOutput.write(String.valueOf(bo.MonetarySalesAmountUSD));
			else if (next.equals(PropUtil.MonetarySalesAmountLTDUSD)) csvOutput.write(String.valueOf(bo.MonetarySalesAmountLTDUSD));
			else if (next.equals(PropUtil.MSBalanceUnitPriceUSD)) csvOutput.write(String.valueOf(bo.MSBalanceUnitPriceUSD));
			else if (next.equals(PropUtil.MSBalancePurchSalesAmtUSD)) csvOutput.write(String.valueOf(bo.MSBalancePurchSalesAmtUSD));
			else if (next.equals(PropUtil.MSBalancePurchSalesAmtLTDUSD)) csvOutput.write(String.valueOf(bo.MSBalancePurchSalesAmtLTDUSD));
		}
		
		csvOutput.endRecord();
	}
	
	
	//========================================================================
	// FORMATTING METHODS THAT FOLLOW ARE NOT USED
	//========================================================================
	
	private static String getCurrency(double val){
		String x = new Double(val).toString();
		int i = x.indexOf(".");
		return "$" + x.substring(0, i+1) + getRoundedDecimal(x.substring(i+1));
	}

	private static String getRoundedDecimal(String x){
		LogUtil.debug("Calling getRoundedDecimal(" + x +")" );
		if(x==null || x.trim().length()<1) return "00"; 
		if(x.trim().length()==1) return x +"0" ;
		if(x.trim().length()==2) return x;
		
		Integer firstTwoDigits = new Integer(x.substring(0, 2));
		LogUtil.debug("First Two Digits = " + firstTwoDigits);
		Integer thirdDigit = new Integer(x.substring(2, 3));
		
		LogUtil.debug("third digit = " + thirdDigit);
		if (thirdDigit.intValue() > 4){
			LogUtil.debug("digit greater than 4, so rounding up " );
			firstTwoDigits = firstTwoDigits.intValue() + 1;
		}
		
		LogUtil.debug("Final Two Digits = " + firstTwoDigits);
		
		return firstTwoDigits.toString();
		
	}

}
