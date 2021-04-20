package com.nordic.view.listener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.nordic.controller.PurchaseDao;
import com.nordic.controller.ReportBuilder;
import com.nordic.model.PurchaseBo;
import com.nordic.model.SearchRange;
import com.nordic.thread.NotifyingThread;
import com.nordic.thread.ThreadCompleteInterface;
import com.nordic.util.AudioUtil;
import com.nordic.util.DateUtil;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;
import com.nordic.view.DoubleField;
import com.nordic.view.IntegerField;
import com.nordic.view.NordicGui;

import net.sourceforge.jdatepicker.impl.JDatePickerImpl;

public class SearchActionListener implements ActionListener, ThreadCompleteInterface {


	private static HashMap<String, Object> searchParams;
	private static HashMap<String, Object> formValues;
	private static List<PurchaseBo> searchResults;
	private static NotifyingThread queryThread;
	
	public static NotifyingThread getQueryThread(){
		return queryThread;
	}
	
	public void actionPerformed(ActionEvent e) {

		try {
			AudioUtil.playStart();
			setSearchParams();
			
			if (RangeChangeListener.validSearchParams(searchParams)) {
			
				NordicGui.showProgramWorkingPanel();
				
				queryThread = new NotifyingThread() {
				    public void doRun()
				    {
				    	try{
				    		searchResults = PurchaseDao.search(searchParams);
							String msg = "DATABASE QUERY RETURNED " + searchResults.size() + " ROWS - BUILDING CSV FILE."; 
							LogUtil.info(msg);
							ReportBuilder.createFile(searchResults);
				    	}catch(Exception ex){
				    		LogUtil.error(ex);
				    	}
				    }
				 };
				 
			queryThread.addListener(this); 
			queryThread.start();
			
			}
			
		} catch (Exception ex) {
			LogUtil.error(ex);
			JOptionPane.showMessageDialog(NordicGui.frame, ex.getMessage(),
					"Dialog", JOptionPane.ERROR_MESSAGE);
		}
		
		System.gc();
	}
	

	private void setSearchParams() {

		searchParams = new HashMap<String, Object>();
		formValues = new HashMap<String, Object>();
		
		Iterator<JComponent> it = NordicGui.fieldList.iterator();
		while (it.hasNext()) {
			JComponent c = it.next();
			if (c instanceof JDatePickerImpl){
				Date dStart = (Date) NordicGui.startDateStamp.getModel().getValue();
				Date dEnd = (Date) NordicGui.endDateStamp.getModel().getValue();
				if (dStart != null && dEnd != null) {
					SearchRange sr = new SearchRange(DateUtil.getDateAsInteger(dStart),
							DateUtil.getDateAsInteger(dEnd));
					formValues.put(PropUtil.DateStamp, sr);
				}
			}else if (c instanceof IntegerField){
				IntegerField field = (IntegerField)c;
				if(!isEmpty(field.getText())){
					formValues.put(field.getName(), Integer.parseInt(field.getText()));
				}
			}else if (c instanceof DoubleField){
				DoubleField field = (DoubleField)c;
				if(!isEmpty(field.getText())){
					formValues.put(field.getName(), Double.parseDouble(field.getText()));
				}
			}else if (c instanceof JTextField){
				JTextField field = (JTextField)c;
				if(!isEmpty(field.getText())){
					formValues.put(field.getName(), field.getText());
				}
			}else if (c instanceof JComboBox){
				JComboBox field = (JComboBox)c;
				if(!isEmpty(field.getSelectedItem())){
					formValues.put(field.getName(), field.getSelectedItem().toString());
				}
			}
			
		}
		
		Iterator<String> params = formValues.keySet().iterator();
		LogUtil.info("TOTAL # FORM VALUES = " + formValues.keySet().size());
		while (params.hasNext()) {
			setSearchValue(params.next());
		}


	}

	
	private void setSearchValue(String key){
		String baseKey = key;
		String startKey = null;
		String endKey = null;
		LogUtil.info("setSearchValue(" + key + ")");
		
		if(key.startsWith("start")){
			baseKey = key.substring(5);
			startKey = key;
			endKey = "end" + baseKey;
		}else if(key.startsWith("end")){
			baseKey = key.substring(3);
			startKey = "start" + baseKey;
			endKey = key;
		}else{ // not a range value
			LogUtil.info("setSearchValue(" + key + ") = " + formValues.get(key));
			searchParams.put(key, formValues.get(key));
			return;
		}	
		
		//LogUtil.info("baseKey = " + baseKey);
		//LogUtil.info("startKey = " + startKey);
		//LogUtil.info("endKey = " + endKey);
		
		// form has start & end range values & searchParam isn't already set
		if(formValues.containsKey(startKey) && formValues.containsKey(endKey)){
			LogUtil.info("setSearchValue(" + key + ") = has start & end values");
			// each range can have 2 values, if already set when 1st was read, skip it here
			if(searchParams.containsKey(baseKey)){
				LogUtil.info("searchParams.containsKey(baseKey) = true, DO NOTHING");
				return;
			}
			
			Object start = formValues.get(startKey);
			Object end = formValues.get(endKey);
	
			if(start.equals(end)){ // values are same, so can user equals in search
				LogUtil.debug("start.equals(end) = true, set baseKey = " + start);
				searchParams.put(baseKey, start);
				return;
			}
			
			LogUtil.debug("start.equals(end) = false, set baseKey = RANGE");
			searchParams.put(baseKey, new SearchRange(start, end));
			return;
			
		}
		
		LogUtil.info("setSearchValue(" + key + ") = " + formValues.get(key));
		// must be only start or end range value - insert as is, 
		// DAO will use >= or < when it sees "start" or "end" strings
		searchParams.put(key, formValues.get(key));

	}
	
	private boolean isEmpty(Object x) {

		if (x == null)
			return true;
		String val = x.toString();
		if (val.trim().length() > 0) {
			LogUtil.debug("Search Value Length = " + val.trim().length());
			return false;
		}

		return true;
	}


	public void notifyOfThreadComplete(Thread thread) {
		EventQueue.invokeLater(new Runnable() {
		    public void run()
		    {
		    	try{
		    		LogUtil.info("CSV REPORT COMPLETE");
		    		AudioUtil.playOk();
		    		NordicGui.frame.dispose();
					NordicGui.showScreen();
					JOptionPane.showMessageDialog(NordicGui.frame,
					 "CSV REPORT COMPLETE", "STATUS",
					 JOptionPane.OK_OPTION);
		    	}catch(Exception ex){
		    		LogUtil.error(ex);
		    	}
		    }
		  });
		
	}
}