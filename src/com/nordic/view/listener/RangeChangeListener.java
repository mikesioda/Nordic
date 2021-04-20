package com.nordic.view.listener;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nordic.controller.PurchaseDao;
import com.nordic.model.SearchRange;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;
import com.nordic.view.NordicGui;

import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class RangeChangeListener implements ChangeListener, DocumentListener {

	public static LinkedHashMap<String, String> labels = new LinkedHashMap<String, String>(); 
	private static ArrayList<String> strongSearchParams = new ArrayList<String>();
	private String startAtt;
	private String endAtt;

	static {
		setStrongSearchParams();
		initLabels();
	}
	
	
	public RangeChangeListener(String startAtt, String endAtt) {
		this.startAtt = startAtt;
		this.endAtt = endAtt;
	}

	public void stateChanged(ChangeEvent e) {

		UtilDateModel model = (UtilDateModel) e.getSource();
		Date formValue = (Date)NordicGui.startDateStamp.getModel().getValue();
		if (formValue != null){
			Iterator<JComponent> it = NordicGui.fieldList.iterator();
			while (it.hasNext()) {
				JComponent c = it.next();
				if (isEndField(c, e)) {
					JDatePickerImpl jdpEnd = (JDatePickerImpl) c;
					Object endDateValue = jdpEnd.getModel().getValue();
					if (endDateValue == null) {
						jdpEnd.getModel().setDate(model.getYear(), model.getMonth(), model.getDay());
						jdpEnd.getModel().setSelected(true);
					}
				}
			}
		}
	}
	
	
	public void changedUpdate(DocumentEvent e) {
		textFieldChanged();
	}

	public void insertUpdate(DocumentEvent e) {
		textFieldChanged();
		
	}

	public void removeUpdate(DocumentEvent e) {
		// nothing to do
	}


	public static String[] getPicklist(String field) {
		String[] picklist = null;
		ArrayList<String> list = new ArrayList<String>();
		PreparedStatement picklistStatement = null;
		ResultSet resultSet = null;
		try {
			String picklistSQL = "SELECT DISTINCT " + field
					+ " FROM PURCHASE order by " + field + " ASC";

			picklistStatement = PurchaseDao.getConnection()
					.prepareStatement(picklistSQL);
			resultSet = picklistStatement.executeQuery();
			while (resultSet.next()) {
				list.add(resultSet.getString(1));
			}

			int i = 0;
			picklist = new String[list.size() + 1];
			picklist[i++] = null;
			if (list.contains("United States")) {
				picklist[i++] = "United States";
				list.remove("United States");
			}
			Iterator<String> it = list.iterator();

			while (it.hasNext()) {
				picklist[i++] = it.next();
			}

		} catch (Exception ex) {
			LogUtil.report("COULD NOT GET PICKLIST FOR " + field);
			LogUtil.error(ex);
		} finally {
			try {
				if (picklistStatement != null)
					picklistStatement.close();
				if (resultSet != null)
					resultSet.close();
			} catch (Exception ex2) {
				LogUtil.error(ex2);
			}

		}

		return picklist;
	}
	
	private static void initLabels(){
		labels.put(PropUtil.Title, PropUtil.Title);
		labels.put(PropUtil.TitleId, "Title ID");
		labels.put(PropUtil.DateStamp, "Date Range");
		labels.put(PropUtil.OfferName, "Offer Name");
		labels.put(PropUtil.OfferGuid, "Offer GUID");
		labels.put(PropUtil.OfferRegionName, "Offer Region Name");
		labels.put(PropUtil.OfferCountryName, "Offer Country Name");
		labels.put(PropUtil.MediaType, "Media Type");
		labels.put(PropUtil.FreePurchases, "Free Purchases");
		labels.put(PropUtil.MonetaryPurchases, "Monetary Purchases");
		labels.put(PropUtil.MicrosoftBalancePurchases, "MS Balance Purchases");
		labels.put(PropUtil.TokenPurchases, "Token Purchases");
		labels.put(PropUtil.MSFTPointPurchases, "MSFT Point Purchases");
		labels.put(PropUtil.TotalPurchases, "Total Purchases");
		labels.put(PropUtil.TotalPurchasesLTD, "Total Purchases LTD");
		labels.put(PropUtil.NumberOfPurchasesMadeInGame, "Number Of Purchases Made In Game");
		labels.put(PropUtil.TotalInGamePurchasesSince20090617, "Total In Game Purchases Since 06/17/2009");
		labels.put(PropUtil.PointsPrice, "Points Price");
		labels.put(PropUtil.TotalPointsLTD, "Total Points LTD");
		labels.put(PropUtil.MonetaryUnitPriceUSD, "Monetary Unit Price USD");
		labels.put(PropUtil.MonetarySalesAmountUSD, "Monetary Sales Amount USD");
		labels.put(PropUtil.MonetarySalesAmountLTDUSD, "Monetary Sales Amount LTD USD");
		labels.put(PropUtil.MSBalanceUnitPriceUSD, "MS Balance Unit Price USD");
		labels.put(PropUtil.MSBalancePurchSalesAmtUSD, "MS Balance Purch Sales Amt USD");
		labels.put(PropUtil.MSBalancePurchSalesAmtLTDUSD, "MS Balance Purch Sales Amt LTD USD");

	}
	
	
	private void textFieldChanged(){
		Component[] components = NordicGui.searchPanel.getComponents();
		
		JTextField jStartField = null;
		JTextField jEndField = null;
		
		
		for (int i = 0; i < components.length; i++) {
			Component c = components[i];
			if(isStartField(c)){
				jStartField = (JTextField) c;
			}else if(isEndField(c)){
				jEndField = (JTextField) c;
			}
			
			if (jStartField!=null && jEndField!=null){
				break;
			}
		}

		String endValue = jEndField.getText();
		LogUtil.info("Starting endValue= " + endValue);
		if (endValue == null || endValue.trim().isEmpty()) {
			String startValue = jStartField.getText();
			LogUtil.info("Set End Value = " + startValue);
			jEndField.setText(startValue);
		}

	}
	
	
	

	public static boolean validSearchParams(HashMap<String, Object> map)
			throws Exception {

		if (map.size() < 1) {
			throw new Exception("INVALID ACTION_SEARCH - NO ACTION_SEARCH VALUES");
		}

		if (!hasStrongParams(map)) {
			throw new Exception("INVALID ACTION_SEARCH - NO STRONG ACTION_SEARCH VALUES");
		}

		validateRange(map);
		validateSize(map);
		return true;
	}

	
	
	
	private static boolean hasStrongParams(HashMap<String, Object> map) {

		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			if (strongSearchParams.contains(it.next())) {
				return true;
			}
		}

		return false;
	}

	private static boolean negativeRangeValue(SearchRange searchRange) {

		if (searchRange.rangeStart.doubleValue() < 0
				|| searchRange.rangeEnd.doubleValue() < 0) {
			return true;
		}

		return false;
	}

	private static boolean rangeStartLargerThanRangeEnd(SearchRange searchRange) {

		LogUtil.info("Range Start Double Value = " + searchRange.rangeStart.doubleValue() );
		LogUtil.info("Range End Double Value = " + searchRange.rangeEnd.doubleValue() );

		if (searchRange.rangeStart.doubleValue() > searchRange.rangeEnd
				.doubleValue()) {
			return true;
		}

		return false;
	}

	private static void setStrongSearchParams() {

		strongSearchParams.add(PropUtil.DateStamp);
		strongSearchParams.add(PropUtil.TitleId);
		strongSearchParams.add(PropUtil.Title);
		strongSearchParams.add(PropUtil.OfferName);
		strongSearchParams.add(PropUtil.OfferRegionName);
		strongSearchParams.add(PropUtil.OfferCountryName);

	}

	private static void validateRange(HashMap<String, Object> map)
			throws Exception {

		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String paramName = it.next();
			Object searchValue = map.get(paramName);
			if (searchValue instanceof SearchRange) {
				LogUtil.info("Validate Range - param value = "
						+ map.get(paramName));
				LogUtil.info("Validate Range - param value class = "
						+ map.get(paramName).getClass());
				SearchRange searchRange = (SearchRange) map.get(paramName);

				if (rangeStartLargerThanRangeEnd(searchRange)
						|| negativeRangeValue(searchRange)) {
					throw new Exception("INVALID ACTION_SEARCH - " + paramName
							+ " MUST HAVE A VALID RANGE.");
				}
			}

		}

	}

	private static void validateSize(HashMap<String, Object> map)
			throws Exception {

		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String paramName = it.next();
			Object paramValue = map.get(paramName);
			// if strongSearchParam, must be a String value
			if (paramValue instanceof String) {
				String searchValue = (String) paramValue;
				if (searchValue.trim().length() < PropUtil.minSearchStringLength) {
					LogUtil.info(paramName + " search value = " + searchValue);
					LogUtil.info(paramName + " search value length = "
							+ searchValue.trim().length());
					throw new Exception("INVALID ACTION_SEARCH - " + paramName
							+ " MUST HAVE A MINIMUM OF "
							+ PropUtil.minSearchStringLength + " CHARACTERS.");
				}
			}
		}
	}
	
	
	private boolean isStartField(Component c) {
		if (c != null && (c.getName() != null && c.getName().equals(startAtt))) {
				return true;
		}

		return false;
	}

	private boolean isEndField(Component c) {
		if (c != null && (c.getName() != null && c.getName().equals(endAtt))) {
				return true;
		}

		return false;
	}
	

	private boolean isEndField(Component c, ChangeEvent e) {
		if (c == null || (c.getName() != null && c.getName().equals(endAtt))) {
			if (e.getSource() instanceof UtilDateModel) { // stratAtt is the only other
				return true;
			}
		}

		return false;
	}

	
}
