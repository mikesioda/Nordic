package com.nordic.controller;

import java.util.ArrayList;
import java.util.Iterator;

import com.nordic.model.PurchaseBo;
import com.nordic.util.FileUtil;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;

/**
 * This class manages the database load.  
 * Prevent memory problems occur when loading  large datasets.
 * 
 * @author Mike Sioda
 *
 */
public class DatabaseLoad {

	private static final int MAX_SET_FOR_BATCH_INSERT = 50000;
	
	/**
	 * Read input files & load them to database in batch.
	 * 
	 * 1. Get Purchase data from FileUtil.getPurchaseBosFromFile(fileName)
	 * 2. Update PurchaseDao batch array up to size MAX_SET_FOR_BATCH_INSERT 
	 * 3. Execute PurchaseDao batch insert for each chunk of MAX_SET_FOR_BATCH_INSERT 
	 *
	 **/
	public static void load() throws Exception {

		LogUtil.heading("LOADING DATABASE");

		Iterator<String> fileNames = FileUtil.getFileNames(
				PropUtil.feedDir, PropUtil.XML).iterator();

		int count = 0;
		while (fileNames.hasNext()) {
			String fileName = fileNames.next();
			try {
				ArrayList<PurchaseBo> bos = FileUtil.getPurchaseBosFromFile(fileName);
				PurchaseDao.addToBatch(bos);
				count += bos.size();
				if(count>0 && (!fileNames.hasNext() || (count>MAX_SET_FOR_BATCH_INSERT))){
					PurchaseDao.executeBatchInsert();
					count = 0;
				}
				
			} catch (Exception ex) {
				LogUtil.report("FILE NOT LOADED TO DATABASE = " + fileName);
				LogUtil.error(ex);
				throw ex;
			}
		}
		
		LogUtil.heading("DATABASE LOAD COMPLETE");
		
	}
	
	
}
