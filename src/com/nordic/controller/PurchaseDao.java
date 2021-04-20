package com.nordic.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.nordic.model.PurchaseBo;
import com.nordic.model.RunStats;
import com.nordic.model.SearchRange;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;

/**
 * This class manages database interactions.
 * 
 * @author Mike Sioda
 *
 */
public class PurchaseDao {

	public static ArrayList<PurchaseBo> bosToInsert = new ArrayList<PurchaseBo>();
	private static Connection connect;
	
	private static final int COMMIT_THRESHOLD = 100000;
	private static final int BATCH_MAXIMUM = 1000;
	private static final String INSERT_STATEMENT = "INSERT into xbox.PURCHASE values (default, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, default)";
	private static final String UPDATE_STATEMENT = "UPDATE xbox.PURCHASE set DateStamp=?, TitleId=?, Title=?, MediaType=?, OfferName=?, OfferGuid=?, " + //6
			"OfferRegionName=?, OfferCountryName=?, FreePurchases=?, MSFTPointPurchases=?, MonetaryPurchases=?, TokenPurchases=?, TotalPurchases=?, " + //7
			"PointsPrice=?, TotalPurchasesLTD=?, TotalPointsLTD=?, NumberOfPurchasesMadeInGame=?, TotalInGamePurchasesSince20090617=?, " + //5
			"MicrosoftBalancePurchases=?, MonetaryUnitPriceUSD=?, MonetarySalesAmountUSD=?, MonetarySalesAmountLTDUSD=?, " + //4
			"MSBalanceUnitPriceUSD=?, MSBalancePurchSalesAmtUSD=?, MSBalancePurchSalesAmtLTDUSD=?, UpdatedDate=default where id=?"; //3 + ID
	private static final String SELECT_STATEMENT = "SELECT * FROM PURCHASE WHERE ";
	private static PreparedStatement insertStatement = null;
	private static PreparedStatement updateStatement = null;
	private static PreparedStatement selectStatement = null;
	private static ResultSet resultSet = null;
	
	/**
	 * Get the open connection.  Call refreshConnection() if none exist.
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception{
		if(connect==null || connect.isClosed()){
			refreshConnection();
		}
		
		return connect;
	}
	
	/**
	 * Get the prepared insert statement, create it if null.
	 * @return PreparedStatement
	 * @throws Exception
	 */
	private static PreparedStatement getInsertStatement() throws Exception{
		if(insertStatement==null || insertStatement.isClosed()){
			insertStatement = connect.prepareStatement(INSERT_STATEMENT);
		}
		
		return insertStatement;
	}
	
	/**
	 * Get the prepared updated statement, create it if null.
	 * @return PreparedStatement
	 * @throws Exception
	 */
	private static PreparedStatement getUpdateStatement() throws Exception{
		if(updateStatement==null || updateStatement.isClosed()){
			updateStatement = connect.prepareStatement(UPDATE_STATEMENT);
		}
		
		return updateStatement;
	}
	

	static {
		try {
			Class.forName(PropUtil.databaseJdbcDriver);
			// setup the connection with the DB.
			connect = DriverManager.getConnection(PropUtil.databaseUrl
					+ "?user=" + PropUtil.databaseUserName + "&password="
					+ PropUtil.databasePassword);
			connect.setAutoCommit(false);
			insertStatement = connect.prepareStatement(INSERT_STATEMENT);
			updateStatement = connect.prepareStatement(UPDATE_STATEMENT);
		} catch (Exception ex) {
			LogUtil.error(ex);
		}
	}
	
	
	
	/**
	 * Add bos the the batch array in preparation of batch insert.
	 * 
	 * @param bos
	 */
	public static void addToBatch(ArrayList<PurchaseBo> bos){
		bosToInsert.addAll(bos);
	}
	
	/**
	 * Execute batch insert in chunks of 1000, commit every 100,000.
	 * If record exists, update with new values.
	 * If all string values & date match, record exists.
	 * Read from bosToInsert Array (prepared previously).
	 * 
	 * @throws Exception
	 */
	public static void executeBatchInsert() throws Exception {

		try {
			//refreshConnection();
			insertStatement = getInsertStatement();
			updateStatement = getUpdateStatement();
			Iterator<PurchaseBo> it = bosToInsert.iterator();
			LogUtil.report("PREPARED BATCH OF " + bosToInsert.size() + " 	RECORDS");
			int count = 0;
			while (it.hasNext()) {
				count++;
				PurchaseBo bo = it.next();
				if(recordExists(bo)){
					addBatch(bo, updateStatement, true);
				}else{
					addBatch(bo, insertStatement, false);	
				}
				
				if (count % BATCH_MAXIMUM == 0) {
					int[] i = insertStatement.executeBatch(); 
					int[] u = updateStatement.executeBatch(); 
					LogUtil.info("EXECUTED BATCH OF  " + BATCH_MAXIMUM);
					if(i!=null && i.length>0){
						LogUtil.info("Inserted = " + i.length);
					}else if(u!=null && u.length>0){
						LogUtil.info("Updated = " + u.length);
					}
					
				}

				if (count % COMMIT_THRESHOLD == 0) {
					connect.commit();
					LogUtil.info("COMMITTED BATCH SIZE = " + COMMIT_THRESHOLD);
					RunStats.addCountDatabaseInserts(COMMIT_THRESHOLD);
					//refreshConnection();
				}
			}
				
		
			int[] i = insertStatement.executeBatch(); 
			int[] u = updateStatement.executeBatch(); 
			LogUtil.info("EXECUTED BATCH OF  " + (count % BATCH_MAXIMUM));
			if(i!=null && i.length>0){
				LogUtil.info("Inserted = " + i.length);
			}else if(u!=null && u.length>0){
				LogUtil.info("Updated = " + u.length);
			}

			connect.commit();
			LogUtil.info("COMMITTED FINAL BATCH SIZE = " + (count % COMMIT_THRESHOLD));
			RunStats.addCountDatabaseInserts(count % COMMIT_THRESHOLD);

		} catch (Exception ex) {
			LogUtil.report("BATCH INSERT FAILED");
			LogUtil.error(ex);
			throw ex;
		} finally {
			try{
				LogUtil.report("BATCH INSERT COMPLETE");
				bosToInsert.clear();
				refreshConnection();
			}catch(Exception ex){
				LogUtil.info("Tried to optimize DB, if not supported, no harm.  Exception below is handled.");
				LogUtil.error(ex);
			}
			
		}
	}
	
	
	

	/**
	 * Close resultSet, insertStatement, selectStatement, & connect.  
	 */
	public static void closeConnection() {
		try {

			if (resultSet != null)
				resultSet.close();
			if (insertStatement != null)
				insertStatement.close();
			if (updateStatement != null)
				updateStatement.close();
			if (selectStatement != null)
				selectStatement.close();
			if (connect != null)
				connect.close();
			LogUtil.info("DATABASE CONNECTION CLOSED");
		} catch (Exception ex) {
			LogUtil.info("PROBLEM CLOSING DATABASE CONNECTION");
			LogUtil.error(ex);
		}
	}
	
	

	/**
	 * Search based on the map of search criteria.
	 * 
	 * @param map - search criteria
	 * @return List<PurchaseBo>
	 * @throws Exception
	 */
	public static List<PurchaseBo> search(HashMap<String, Object> map)
			throws Exception {
		
		LogUtil.info("Executing Query");
		List<PurchaseBo> bos = new ArrayList<PurchaseBo>();
		try{
			
			resultSet = executeQuery(map);
			LogUtil.info("Query Complete - Building Result Set (size = " +getResultSetSize() + ")");
			while (resultSet.next()) {
	
				PurchaseBo bo = new PurchaseBo();
	
				bo.DateStamp = resultSet.getInt(PropUtil.DateStamp);
				bo.TitleId = resultSet.getString(PropUtil.TitleId);
				bo.Title = resultSet.getString(PropUtil.Title);
				bo.MediaType = resultSet.getString(PropUtil.MediaType);
				bo.OfferName = resultSet.getString(PropUtil.OfferName);
				bo.OfferGuid = resultSet.getString(PropUtil.OfferGuid);
				bo.OfferRegionName = resultSet.getString(PropUtil.OfferRegionName);
				bo.OfferCountryName = resultSet.getString(PropUtil.OfferCountryName);
	
				bo.FreePurchases = resultSet.getInt(PropUtil.FreePurchases);
				bo.MSFTPointPurchases = resultSet.getInt(PropUtil.MSFTPointPurchases);
				bo.MonetaryPurchases = resultSet.getInt(PropUtil.MonetaryPurchases);
				bo.TokenPurchases = resultSet.getInt(PropUtil.TokenPurchases);
				bo.TotalPurchases = resultSet.getInt(PropUtil.TotalPurchases);
				bo.PointsPrice = resultSet.getInt(PropUtil.PointsPrice);
				bo.TotalPurchasesLTD = resultSet.getInt(PropUtil.TotalPurchasesLTD);
				bo.TotalPointsLTD = resultSet.getInt(PropUtil.TotalPointsLTD);
				bo.NumberOfPurchasesMadeInGame = resultSet
						.getInt(PropUtil.NumberOfPurchasesMadeInGame);
				bo.TotalInGamePurchasesSince20090617 = resultSet
						.getInt(PropUtil.TotalInGamePurchasesSince20090617);
				bo.MicrosoftBalancePurchases = resultSet
						.getInt(PropUtil.MicrosoftBalancePurchases);
	
				bo.MonetaryUnitPriceUSD = resultSet
						.getDouble(PropUtil.MonetaryUnitPriceUSD);
				bo.MonetarySalesAmountUSD = resultSet
						.getDouble(PropUtil.MonetarySalesAmountUSD);
				bo.MonetarySalesAmountLTDUSD = resultSet
						.getDouble(PropUtil.MonetarySalesAmountLTDUSD);
				bo.MSBalanceUnitPriceUSD = resultSet
						.getDouble(PropUtil.MSBalanceUnitPriceUSD);
				bo.MSBalancePurchSalesAmtUSD = resultSet
						.getDouble(PropUtil.MSBalancePurchSalesAmtUSD);
				bo.MSBalancePurchSalesAmtLTDUSD = resultSet
						.getDouble(PropUtil.MSBalancePurchSalesAmtLTDUSD);
		
				 LogUtil.info("Query Returned MSBalancePurchSalesAmtLTDUSD: " + String.valueOf(bo.MSBalancePurchSalesAmtLTDUSD));
					
				bos.add(bo);
	
			}
		}finally{
			resultSet.close();
		}
		
		LogUtil.info("Result Set Complete (size = " +bos.size() + ")");

		return bos;
	}
	
	
	/**
	 * Get the size of the result set.
	 * 
	 * @return int size
	 * @throws Exception
	 */
	private static int getResultSetSize() throws Exception{
		int count = 0;
		if (resultSet!=null && resultSet.last()) {
		  count = resultSet.getRow();
		  resultSet.beforeFirst();
		}
		
		return count;
	}
	
	/** 
	 * Determine if the record already exists.
	 * @return
	 * @throws Exception
	 */
	private static boolean recordExists(PurchaseBo bo) throws Exception {
		
		try{
			String sql = SELECT_STATEMENT + "DateStamp = ? and TitleId = ? and MediaType = ? and OfferGuid = ? and OfferRegionName = ? and OfferCountryName = ?";
			selectStatement = connect.prepareStatement(sql);
			selectStatement.setInt(1, bo.DateStamp);
			selectStatement.setString(2, bo.TitleId);
			selectStatement.setString(3, bo.MediaType);
			selectStatement.setString(4, bo.OfferGuid);
			selectStatement.setString(5, bo.OfferRegionName);
			selectStatement.setString(6, bo.OfferCountryName);
			
			resultSet = selectStatement.executeQuery();
			if(getResultSetSize() >0){
				return true;
			}
		}finally{
			if(resultSet!=null){
				resultSet.close();
			}
		}

		return false;
	}
	
	
	/**
	 * Set the prepared statement params of insertStatement for the 
	 * given PurchaseBo.
	 * 
	 * @param bo - PurchaseBo
	 * @throws Exception
	 */
	private static void addBatch(PurchaseBo bo, PreparedStatement ps, boolean isUpdate) throws Exception {

		// parameters start with 1
		ps.setInt(1, bo.DateStamp);
		ps.setString(2, bo.TitleId);
		ps.setString(3, bo.Title);
		ps.setString(4, bo.MediaType);
		ps.setString(5, bo.OfferName);
		ps.setString(6, bo.OfferGuid);
		ps.setString(7, bo.OfferRegionName);
		ps.setString(8, bo.OfferCountryName);

		ps.setInt(9, bo.FreePurchases);
		ps.setInt(10, bo.MSFTPointPurchases);
		ps.setInt(11, bo.MonetaryPurchases);
		ps.setInt(12, bo.TokenPurchases);
		ps.setInt(13, bo.TotalPurchases);
		ps.setInt(14, bo.PointsPrice);
		ps.setInt(15, bo.TotalPurchasesLTD);
		ps.setInt(16, bo.TotalPointsLTD);
		ps.setInt(17, bo.NumberOfPurchasesMadeInGame);
		ps.setInt(18, bo.TotalInGamePurchasesSince20090617);
		ps.setInt(19, bo.MicrosoftBalancePurchases);

		ps.setDouble(20, bo.MonetaryUnitPriceUSD);
		ps.setDouble(21, bo.MonetarySalesAmountUSD);
		ps.setDouble(22, bo.MonetarySalesAmountLTDUSD);
		ps.setDouble(23, bo.MSBalanceUnitPriceUSD);
		ps.setDouble(24, bo.MSBalancePurchSalesAmtUSD);
		ps.setDouble(25, bo.MSBalancePurchSalesAmtLTDUSD);

		if(isUpdate){
			ps.setInt(26, bo.id);
		}
		
		LogUtil.debug("insertStatement toString() = "
				+ ps.toString());
		ps.addBatch();
	}
	
	/**
	 * Return true if key = picklist.
	 * 
	 * @param key
	 * @return boolean
	 */
	private static boolean isPicklist(String key){
		
		if (key.equals(PropUtil.MediaType) || 
				key.equals(PropUtil.OfferRegionName) || 
				key.equals(PropUtil.OfferCountryName) ||
				key.equals(PropUtil.Title) ||
				key.equals(PropUtil.TitleId)){
			return true;
		}
		
		return false;
		
	}

	/**
	 * Builds the query SQL based on the searchParams (map) & executes
	 * the query.
	 * 
	 * @param map - search parameters
	 * @return ResultSet
	 * @throws Exception
	 */
	private static ResultSet executeQuery(HashMap<String, Object> map)
			throws Exception {
		//refreshConnection();
		String sql = SELECT_STATEMENT;
		Iterator<String> it = map.keySet().iterator();
		boolean firstParam = true;
		while (it.hasNext()) {

			if (!firstParam) {
				sql = sql + " and ";
			} else {
				firstParam = false;
			}

			String key = it.next();
			Object val = map.get(key);
			if (val instanceof SearchRange) {
				sql = sql + key + " >= ? and " + key + " <= ?";
			} else if (key.startsWith("start")) {
				sql = sql + key.substring(5) + " >= ?";
			} else if (key.startsWith("end")) {
				sql = sql + key.substring(3) + " <= ?";
			}else if (val instanceof Double) {
				sql = sql + key + " = ?";
			} else if (val instanceof Integer) {
				sql = sql + key + " = ?";
			} else if (isPicklist(key)) {
				sql = sql + key + " = ?";
			} else if (val instanceof String) {
				sql = sql + key + " like ?";
			}
			
		}

		selectStatement = connect.prepareStatement(sql);

		Iterator<String> it2 = map.keySet().iterator();
		int i = 1;
		while (it2.hasNext()) {

			String key = it2.next();
			Object val = map.get(key);
			if (val instanceof SearchRange) {
				SearchRange sr = (SearchRange) val;
				if (sr.rangeStart instanceof Double) {
					LogUtil.info("SQL Search Param: (" + key + " = " + 
							sr.rangeStart.doubleValue() + "/" + 
							sr.rangeEnd.doubleValue() + ")");
					selectStatement.setDouble(i++, (Double) sr.rangeStart);
					selectStatement.setDouble(i++, (Double) sr.rangeEnd);
				} else { // must be Integer
					LogUtil.info("SQL Search Param: (" + key + " = " + 
							sr.rangeStart.intValue() + "/" + 
							sr.rangeEnd.intValue() + ")");
					selectStatement.setInt(i++, (Integer) sr.rangeStart);
					selectStatement.setInt(i++, (Integer) sr.rangeEnd);
				}
			} else if (val instanceof Double) {
				LogUtil.info("SQL Search Param: (" + key + " = " + 
						((Double)val).doubleValue() + ")");
				selectStatement.setDouble(i++, (Double) val);
			} else if (val instanceof Integer) {
				LogUtil.info("SQL Search Param: (" + key + " = " + 
						((Integer)val).intValue() + ")");
				selectStatement.setInt(i++, (Integer) val);
			} else if (isPicklist(key)) {
				LogUtil.info("SQL Search Param: (" + key + " = " + 
						((String)val).toString() + ")");
				selectStatement.setString(i++, (String) val);
			}else if (val instanceof String) {
				LogUtil.info("SQL Search Param: (" + key + " like " + 
						((String)val).toString() + "%)");
				selectStatement.setString(i++, (String) val + "%");
			}
		}

		LogUtil.info(sql.toString());
		return selectStatement.executeQuery();

	}
	

	/**
	 * Resets connect & insertStatement
	 * .
	 * @throws Exception
	 */
	private static void refreshConnection() throws Exception {

//		if (insertStatement != null) {
//			insertStatement.close();
//		}
		
		if (connect != null) {
			connect.close();
		}
		
		connect = DriverManager.getConnection(PropUtil.databaseUrl
					+ "?user=" + PropUtil.databaseUserName + "&password="
					+ PropUtil.databasePassword);
		connect.setAutoCommit(false);
		
	}
	
	
	

}