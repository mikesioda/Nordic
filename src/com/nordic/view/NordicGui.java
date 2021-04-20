package com.nordic.view;

import java.awt.EventQueue;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import com.nordic.controller.NordicFeedApplication;
import com.nordic.util.DateUtil;
import com.nordic.util.LogUtil;
import com.nordic.util.PropUtil;
import com.nordic.view.listener.CancelActionListener;
import com.nordic.view.listener.MainActionListener;
import com.nordic.view.listener.QuitActionListener;
import com.nordic.view.listener.RangeChangeListener;
import com.nordic.view.listener.ResetActionListener;
import com.nordic.view.listener.SearchActionListener;
import com.nordic.view.listener.ShowFormActionListener;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class NordicGui {

	public static String[] titlePicklist = null;
	public static String[] titleIdPicklist = null;
	public static String[] countryPicklist = null;
	public static String[] regionPicklist = null;
	public static String[] mediaTypePicklist = null;
	
	public static SpringLayout searchLayout;
	public static SpringLayout programWorkingLayout;
	public static SpringLayout reportMsgLayout;
	public static JFrame frame;
	public static JPanel searchPanel;
	public static JPanel reportMsgPanel;
	public static JPanel programWorkingPanel;
	public static JDatePickerImpl startDateStamp;
	public static JDatePickerImpl endDateStamp;
	
	public static JComboBox MediaType;
	public static JComboBox OfferCountryName;
	public static JComboBox OfferRegionName;
	public static JComboBox Title;
	public static JComboBox TitleId;
	public static JTextField OfferGuid;
	public static JTextField OfferName;

	public static IntegerField startFreePurchases;
	public static IntegerField startMicrosoftBalancePurchases;
	public static IntegerField startMonetaryPurchases;
	public static IntegerField startMSFTPointPurchases;
	public static IntegerField startNumberOfPurchasesMadeInGame;
	public static IntegerField startPointsPrice;
	public static IntegerField startTokenPurchases;
	public static IntegerField startTotalInGamePurchasesSince20090617;
	public static IntegerField startTotalPointsLTD;
	public static IntegerField startTotalPurchases;
	public static IntegerField startTotalPurchasesLTD;
	public static IntegerField endFreePurchases;
	public static IntegerField endMicrosoftBalancePurchases;
	public static IntegerField endMonetaryPurchases;
	public static IntegerField endMSFTPointPurchases;
	public static IntegerField endNumberOfPurchasesMadeInGame;
	public static IntegerField endPointsPrice;
	public static IntegerField endTokenPurchases;
	public static IntegerField endTotalInGamePurchasesSince20090617;
	public static IntegerField endTotalPointsLTD;
	public static IntegerField endTotalPurchases;
	public static IntegerField endTotalPurchasesLTD;
	
	public static DoubleField startMonetarySalesAmountLTDUSD;
	public static DoubleField startMonetarySalesAmountUSD;
	public static DoubleField startMonetaryUnitPriceUSD;
	public static DoubleField startMSBalancePurchSalesAmtLTDUSD;
	public static DoubleField startMSBalancePurchSalesAmtUSD;
	public static DoubleField startMSBalanceUnitPriceUSD;
	public static DoubleField endMonetarySalesAmountLTDUSD;
	public static DoubleField endMonetarySalesAmountUSD;
	public static DoubleField endMonetaryUnitPriceUSD;
	public static DoubleField endMSBalancePurchSalesAmtLTDUSD;
	public static DoubleField endMSBalancePurchSalesAmtUSD;
	public static DoubleField endMSBalanceUnitPriceUSD;
	
	public static ArrayList<JComponent> fieldList;
	public static HashMap<Integer, JComponent> fieldMap;
	//public static HashMap<String, String> labels; 
	private static int fieldIndex;

	private static final int WINDOW_MARGIN = 10;
	private static final int FIRST_LABEL_NORTH_MARGIN = 55;
	private static final int FIRST_FIELD_NORTH_MARGIN = 50;
	private static final int LABEL_NORTH_MARGIN = 6;
	private static final int FIELD_NORTH_MARGIN = 0;
	private static final int FIELD_WEST_MARGIN = 300;
	private static final int FIELD_EAST_MARGIN = -100;
	private static final int RANGE_FIELD_MARGIN = 150;
	private static final int TO_LABEL_MARGIN = 50;
	
	private static final String PROGRAM_WORKING_MSG = " PROGRAM IN PROGRESS ";
	private static final String REPORT_MSG = " SELECT  ACTION ";


	public static void showScreen() {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initLayout();
					initFields();
					initPicklists();
					createSearchPanel();
					createProgramWorkingPanel();
					createReportMsgPanel();
					setTestSearchValues();
					//new ProgressBar().showProgress();
				} catch (Exception ex) {
					LogUtil.error(ex);
				}
			}
		});
	}
	


	public static void showSearchPanel() {
		try{
			frame.remove(programWorkingPanel);
		}catch(Exception ex){
			LogUtil.error(ex);
		}
		
		try{
			frame.remove(reportMsgPanel);
		}catch(Exception ex){
			LogUtil.error(ex);
		}
		
		frame.add(searchPanel);
		frame.setVisible(true);
		frame.repaint();
	}
	
	public static void showReportPanel() {
		try{
			frame.remove(programWorkingPanel);
		}catch(Exception ex){
			LogUtil.error(ex);
		}
		
		try{
			frame.remove(searchPanel);
		}catch(Exception ex){
			LogUtil.error(ex);
		}

		frame.add(reportMsgPanel);
		frame.setVisible(true);
		frame.repaint();
	}
	


	public static void showProgramWorkingPanel(){
		try{
			frame.remove(reportMsgPanel);
		}catch(Exception ex){
			LogUtil.error(ex);
		}
		
		try{
			frame.remove(searchPanel);
		}catch(Exception ex){
			LogUtil.error(ex);
		}

		frame.add(programWorkingPanel);
		frame.setVisible(true);
		frame.repaint();
	}


	private static void createSearchPanel() {

	
		addHeading();
		TitleId = getFirstField(PropUtil.TitleId, new JComboBox(titleIdPicklist));
		Title = getPicklistField(PropUtil.Title, new JComboBox(titlePicklist));
		addDateRange(PropUtil.DateStamp, startDateStamp, endDateStamp);
		OfferName = getTextField(PropUtil.OfferName, new JTextField());
		OfferGuid = getTextField(PropUtil.OfferGuid, new JTextField());
		OfferRegionName = getPicklistField(PropUtil.OfferRegionName, new JComboBox(regionPicklist));
		OfferCountryName = getPicklistField(PropUtil.OfferCountryName, new JComboBox(countryPicklist));
		MediaType = getPicklistField(PropUtil.MediaType, new JComboBox(mediaTypePicklist));
//		addIntegerRange(PropUtil.FreePurchases, startFreePurchases, endFreePurchases);
//		addIntegerRange(PropUtil.MonetaryPurchases, startMonetaryPurchases, endMonetaryPurchases);
//		addIntegerRange(PropUtil.MicrosoftBalancePurchases, startMicrosoftBalancePurchases, endMicrosoftBalancePurchases);
//		addIntegerRange(PropUtil.TokenPurchases, startTokenPurchases, endTokenPurchases);
//		addIntegerRange(PropUtil.MSFTPointPurchases, startMSFTPointPurchases, endMSFTPointPurchases);
//		addIntegerRange(PropUtil.TotalPurchases, startTotalPurchases, endTotalPurchases);
//		addIntegerRange(PropUtil.TotalPurchasesLTD, startTotalPurchasesLTD, endTotalPurchasesLTD);
//		addIntegerRange(PropUtil.NumberOfPurchasesMadeInGame, startNumberOfPurchasesMadeInGame, endNumberOfPurchasesMadeInGame);
//		addIntegerRange(PropUtil.TotalInGamePurchasesSince20090617, startTotalInGamePurchasesSince20090617, endTotalInGamePurchasesSince20090617);
//		addIntegerRange(PropUtil.PointsPrice, startPointsPrice, endPointsPrice);
//		addIntegerRange(PropUtil.TotalPointsLTD, startTotalPointsLTD, endTotalPointsLTD);
//		addDoubleRange(PropUtil.MonetaryUnitPriceUSD, startMonetaryUnitPriceUSD, endMonetaryUnitPriceUSD);
//		addDoubleRange(PropUtil.MonetarySalesAmountUSD, startMonetarySalesAmountUSD, endMonetarySalesAmountUSD);
//		addDoubleRange(PropUtil.MonetarySalesAmountLTDUSD, startMonetarySalesAmountLTDUSD, endMonetarySalesAmountLTDUSD);
//		addDoubleRange(PropUtil.MSBalanceUnitPriceUSD, startMSBalanceUnitPriceUSD, endMSBalanceUnitPriceUSD);
//		addDoubleRange(PropUtil.MSBalancePurchSalesAmtUSD, startMSBalancePurchSalesAmtUSD, endMSBalancePurchSalesAmtUSD);
//		addDoubleRange(PropUtil.MSBalancePurchSalesAmtLTDUSD, startMSBalancePurchSalesAmtLTDUSD, endMSBalancePurchSalesAmtLTDUSD);
		
		addSearchFormButtons();
	}
	
	
	
	
	
	private static void initLayout() throws Exception {
		fieldList = new ArrayList<JComponent>();
		fieldMap = new HashMap<Integer, JComponent>();
		fieldIndex = -1;
		
		frame = new JFrame();
		
		programWorkingLayout = new SpringLayout(); 
		programWorkingPanel = new JPanel(); 
		programWorkingPanel.setLayout(programWorkingLayout);
		programWorkingPanel.setBounds(200, 50, 850, 825);
		
		
		reportMsgLayout = new SpringLayout(); 
		reportMsgPanel = new JPanel(); 
		reportMsgPanel.setLayout(reportMsgLayout);
		reportMsgPanel.setBounds(200, 50, 850, 825);
		
		searchLayout = new SpringLayout(); 
		searchPanel = new JPanel(); 
		searchPanel.setLayout(searchLayout);
		searchPanel.setBounds(200, 50, 850, 825);

		String action = NordicFeedApplication.getAction();
		if(action.equals(NordicFeedApplication.ACTION_COMMAND_SCREEN)){
			frame.getContentPane().add(reportMsgPanel);
		}else if(action.equals(NordicFeedApplication.ACTION_SEARCH)){
			frame.getContentPane().add(searchPanel);
		}else{ // MUST BE ARCHIVE OR FEED OR OTHER DB LOAD OR OTHER WORK
			frame.getContentPane().add(programWorkingPanel);
		}
		
		
		frame.setVisible(true);
		frame.setBounds(200, 50, 850, 825);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new QuitActionListener());
	}
	
	
	
	private static void initPicklists(){
		if (mediaTypePicklist == null)
			mediaTypePicklist = RangeChangeListener.getPicklist(PropUtil.MediaType);
		if (regionPicklist == null)
			regionPicklist = RangeChangeListener.getPicklist(PropUtil.OfferRegionName);
		if (countryPicklist == null)
			countryPicklist = RangeChangeListener.getPicklist(PropUtil.OfferCountryName);
		if (titlePicklist == null)
			titlePicklist = RangeChangeListener.getPicklist(PropUtil.Title);
		if (titleIdPicklist == null)
			titleIdPicklist = RangeChangeListener.getPicklist(PropUtil.TitleId);
	}
	
	/**
	 * Initial fields here to support simple Reset Button code.  If declared
	 * statically, these will not be reset when form is reloaded.
	 */
	private static void initFields(){
		
		startFreePurchases = new IntegerField();
		startMicrosoftBalancePurchases = new IntegerField();
		startMonetaryPurchases = new IntegerField();
		startMonetarySalesAmountLTDUSD = new DoubleField();
		startMonetarySalesAmountUSD = new DoubleField();
		startMonetaryUnitPriceUSD = new DoubleField();
		startMSBalancePurchSalesAmtLTDUSD = new DoubleField();
		startMSBalancePurchSalesAmtUSD = new DoubleField();
		startMSBalanceUnitPriceUSD = new DoubleField();
		startMSFTPointPurchases = new IntegerField();
		startNumberOfPurchasesMadeInGame = new IntegerField();
		startPointsPrice = new IntegerField();
		startTokenPurchases = new IntegerField();
		startTotalInGamePurchasesSince20090617 = new IntegerField();
		startTotalPointsLTD = new IntegerField();
		startTotalPurchases = new IntegerField();
		startTotalPurchasesLTD = new IntegerField();
		endFreePurchases = new IntegerField();
		endMicrosoftBalancePurchases = new IntegerField();
		endMonetaryPurchases = new IntegerField();
		endMonetarySalesAmountLTDUSD = new DoubleField();
		endMonetarySalesAmountUSD = new DoubleField();
		endMonetaryUnitPriceUSD = new DoubleField();
		endMSBalancePurchSalesAmtLTDUSD = new DoubleField();
		endMSBalancePurchSalesAmtUSD = new DoubleField();
		endMSBalanceUnitPriceUSD = new DoubleField();
		endMSFTPointPurchases = new IntegerField();
		endNumberOfPurchasesMadeInGame = new IntegerField();
		endPointsPrice = new IntegerField();
		endTokenPurchases = new IntegerField();
		endTotalInGamePurchasesSince20090617 = new IntegerField();
		endTotalPointsLTD = new IntegerField();
		endTotalPurchases = new IntegerField();
		endTotalPurchasesLTD = new IntegerField();

		UtilDateModel startDateModel = new UtilDateModel();
		UtilDateModel endDateModel = new UtilDateModel();
		JDatePanelImpl startDatePanel = new JDatePanelImpl(startDateModel);
		JDatePanelImpl endDatePanel = new JDatePanelImpl(endDateModel);
		startDateStamp = new JDatePickerImpl(startDatePanel, new DateUtil());
		startDateStamp.setShowYearButtons(true);
		endDateStamp = new JDatePickerImpl(endDatePanel, new DateUtil());
		endDateStamp.setShowYearButtons(true);
		startDatePanel.getModel().addChangeListener(
				new RangeChangeListener("startDateStamp", "endDateStamp"));
	}
	
	
	private static void addHeading(){
		JLabel label = new JLabel(" SEARCH DATABASE ");
		searchLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
				label, 0, SpringLayout.HORIZONTAL_CENTER, searchPanel);
		searchLayout.putConstraint(SpringLayout.NORTH, label, WINDOW_MARGIN,
				SpringLayout.NORTH, searchPanel);
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		searchPanel.add(label);
	}
	
	private static void addFirstLabel(String name){
		JLabel field = new JLabel(RangeChangeListener.labels.get(name) + ": ");
		searchLayout.putConstraint(SpringLayout.EAST, field, FIELD_WEST_MARGIN,
				SpringLayout.WEST, searchPanel);
		searchLayout.putConstraint(SpringLayout.NORTH, field, FIRST_LABEL_NORTH_MARGIN,
				SpringLayout.NORTH, searchPanel);
		searchPanel.add(field);
	}
	
	private static JComboBox getFirstField(String name, JComboBox field){
		addFirstLabel(name);
		field.setName(name);
		searchLayout.putConstraint(SpringLayout.WEST, field, FIELD_WEST_MARGIN,
				SpringLayout.WEST, searchPanel);
		searchLayout.putConstraint(SpringLayout.EAST, field, FIELD_EAST_MARGIN,
				SpringLayout.EAST, searchPanel);
		searchLayout.putConstraint(SpringLayout.NORTH, field, FIRST_FIELD_NORTH_MARGIN,
				SpringLayout.NORTH, searchPanel);
		searchPanel.add(field);
		fieldList.add(field);
		fieldMap.put(++fieldIndex, field);
		return field;
	}

	private static void addLabel(String name){
		JLabel label = new JLabel(RangeChangeListener.labels.get(name) + ": ");
		searchLayout.putConstraint(SpringLayout.EAST, label, FIELD_WEST_MARGIN,
				SpringLayout.WEST, searchPanel);
		searchLayout.putConstraint(SpringLayout.NORTH, label, LABEL_NORTH_MARGIN,
				SpringLayout.SOUTH, fieldList.get(fieldIndex));
		searchPanel.add(label);
	}
	

	private static JTextField getTextField(String name, JTextField field){
		return (JTextField)getField(name, field);
	}
	
	private static JComboBox getPicklistField(String name, JComboBox field){
		return (JComboBox)getField(name, field);
	}
	
	private static JComponent getField(String name, JComponent field){
		addLabel(name);
		field.setName(name);
		searchLayout.putConstraint(SpringLayout.WEST, field, FIELD_WEST_MARGIN,
				SpringLayout.WEST, searchPanel);
		searchLayout.putConstraint(SpringLayout.EAST, field, FIELD_EAST_MARGIN,
				SpringLayout.EAST, searchPanel);
		searchLayout.putConstraint(SpringLayout.NORTH, field, FIELD_NORTH_MARGIN,
				SpringLayout.SOUTH, fieldList.get(fieldIndex));
		searchPanel.add(field);
		fieldList.add(field);
		fieldMap.put(++fieldIndex, field);
		return field;
	}

	
	private static void addDateRange(String name, JDatePickerImpl start, JDatePickerImpl end){
		
		addLabel(name);
		start.setName("start" + name);
		addStartField(start);

		end.setName("end" + name);
		addEndField(end);
		addToField(new JLabel("TO  "));
		
		fieldList.add(start);
		fieldMap.put(++fieldIndex, start);
		fieldList.add(end);
		fieldMap.put(++fieldIndex, end);
	}
	
	private static void addDoubleRange(String name, JTextField start, JTextField end){
		addNumberRange(name, start, end);
	}
		
	
	private static void addIntegerRange(String name, JTextField start, JTextField end){
		addNumberRange(name, start, end);
	}
	
	private static void addNumberRange(String name, JTextField start, JTextField end){
		
		addLabel(name);
		
		start.setName("start" + name);
		start.getDocument().addDocumentListener(
				new RangeChangeListener("start" + name, "end" + name));
		addStartField(start);

		end.setName("end" + name);
		addEndField(end);
		addToField(new JLabel(" TO  "));
		
		fieldList.add(start);
		fieldMap.put(++fieldIndex, start);
		fieldList.add(end);
		fieldMap.put(++fieldIndex, end);
	}
		
	
	private static void addStartField(JComponent field){
		searchLayout.putConstraint(SpringLayout.WEST, field, FIELD_WEST_MARGIN,
				SpringLayout.WEST, searchPanel);
		searchLayout.putConstraint(SpringLayout.EAST, field, RANGE_FIELD_MARGIN,
				SpringLayout.WEST, getFullSizeField());
		searchLayout.putConstraint(SpringLayout.NORTH, field, FIELD_NORTH_MARGIN,
				SpringLayout.SOUTH, fieldList.get(fieldIndex));
		searchPanel.add(field);
	}
	
	private static void addEndField(JComponent field){
		searchLayout.putConstraint(SpringLayout.WEST, field, (-1*RANGE_FIELD_MARGIN),
				SpringLayout.EAST, getFullSizeField());
		searchLayout.putConstraint(SpringLayout.EAST, field, FIELD_EAST_MARGIN,
				SpringLayout.EAST, searchPanel);
		searchLayout.putConstraint(SpringLayout.NORTH, field, FIELD_NORTH_MARGIN,
				SpringLayout.SOUTH, fieldList.get(fieldIndex));
		searchPanel.add(field);
	}
	
	private static void addToField(JLabel toLabel){
		
		searchLayout.putConstraint(SpringLayout.WEST, toLabel, 
				TO_LABEL_MARGIN, SpringLayout.EAST, getToRangeLeft());
		searchLayout.putConstraint(SpringLayout.EAST, toLabel, 
				TO_LABEL_MARGIN, SpringLayout.WEST, getToRangeRight());
		searchLayout.putConstraint(SpringLayout.NORTH, toLabel, LABEL_NORTH_MARGIN,
				SpringLayout.SOUTH, fieldList.get(fieldIndex));
		searchPanel.add(toLabel);
	}
	
	
	private static void addSearchFormButtons(){
		addSearchButton();
		addResetButton();
		addBackButton();
		addQuitButton();
	}
	
	private static void addBackButton(){
    	JButton button = new JButton(" BACK  ");
    	button.setToolTipText("Back to main panel.");
    	button.addActionListener(new CancelActionListener());
    	searchLayout.putConstraint(SpringLayout.SOUTH, button, (-1 * WINDOW_MARGIN),
				SpringLayout.SOUTH, searchPanel);
    	searchLayout.putConstraint(SpringLayout.EAST, button, -300,
				SpringLayout.EAST, searchPanel);
    	searchPanel.add(button);
    }
	
	private static void addResetButton(){
		JButton button = new JButton(" RESET FORM ");
		button.setToolTipText("Reset search values.");
		button.addActionListener(new ResetActionListener());
		searchLayout.putConstraint(SpringLayout.SOUTH, button, (-1 * WINDOW_MARGIN),
				SpringLayout.SOUTH, searchPanel);
		searchLayout.putConstraint(SpringLayout.WEST, button, 50,
				SpringLayout.WEST, searchPanel);
		searchPanel.add(button);
	}

	private static void addSearchButton(){
		JButton button = new JButton(" BUILD REPORT ");
		button.setToolTipText("Build report with given search criteria.");
		button.addActionListener(new SearchActionListener());
		searchLayout.putConstraint(SpringLayout.SOUTH, button, -1*WINDOW_MARGIN,
				SpringLayout.SOUTH, searchPanel);
		searchLayout.putConstraint(SpringLayout.EAST, button, -100,
				SpringLayout.EAST, searchPanel);
		searchPanel.add(button);
	}
	
	private static void addQuitButton(){
		JButton button = new JButton(" QUIT APP ");
		button.setToolTipText("Quit application.");
		button.addActionListener(new QuitActionListener());
		searchLayout.putConstraint(SpringLayout.SOUTH, button, -1*WINDOW_MARGIN,
				SpringLayout.SOUTH, searchPanel);
		searchLayout.putConstraint(SpringLayout.WEST, button, 250,
				SpringLayout.WEST, searchPanel);
		searchPanel.add(button);
	}
	

	private static void createReportMsgPanel(){ 
		addReprotMsgLabel();
		addBuildReportButton();
		addDailyFeedButton();
		addFullFeedButton();
		//addArchiveButton();
		//addFeedOnlyButton();
		//addDatabaseLoadButton();
		addQuitReportButton();
    }
	
	private static void addBuildReportButton(){
    	JButton button = new JButton(" SHOW SEARCH ");
    	button.setToolTipText("Show search form used to generate reports.");
    	button.addActionListener(new ShowFormActionListener(
    			NordicFeedApplication.ACTION_SEARCH));
    	reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
				SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
    	reportMsgLayout.putConstraint(SpringLayout.NORTH,
    			button, 150, SpringLayout.NORTH, reportMsgLabel);
    	reportMsgPanel.add(button);
    }
	
	private static void addFullFeedButton(){
    	JButton button = new JButton(" FULL FEED ");
    	button.setToolTipText("Initial feed/database load - over 4M rows!");
    	button.addActionListener(new MainActionListener(
    			NordicFeedApplication.ACTION_INITIAL_FEED_AND_DB_LOAD));
    	reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
				SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
    	reportMsgLayout.putConstraint(SpringLayout.NORTH,
    			button, 225, SpringLayout.NORTH, reportMsgLabel);
    	reportMsgPanel.add(button);
    }
	
	private static void addDailyFeedButton(){
    	JButton button = new JButton(" DAILY FEED ");
    	button.setToolTipText("Manually start the daily feed.");
    	button.addActionListener(new MainActionListener(
    			NordicFeedApplication.ACTION_DAILY_FEED_AND_DB_LOAD));
    	reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
				SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
    	reportMsgLayout.putConstraint(SpringLayout.NORTH,
    			button, 300, SpringLayout.NORTH, reportMsgLabel);
    	reportMsgPanel.add(button);
    }
	
	private static void addArchiveButton(){
    	JButton button = new JButton(" ARCHIVE ");
    	button.setToolTipText("Archive output/feed & output/feed_metadata folders.");
    	button.addActionListener(new MainActionListener(
    			NordicFeedApplication.ACTION_ARCHIVE));
    	reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
				SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
    	reportMsgLayout.putConstraint(SpringLayout.NORTH,
    			button, 375, SpringLayout.NORTH, reportMsgLabel);
    	reportMsgPanel.add(button);
    }
	
	
	private static void addDatabaseLoadButton(){
    	JButton button = new JButton(" DB LOAD ONLY ");
    	button.setToolTipText("Load files that already exist in output/feed");
    	button.addActionListener(new MainActionListener(
    			NordicFeedApplication.ACTION_DB_LOAD));
    	reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
				SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
    	reportMsgLayout.putConstraint(SpringLayout.NORTH,
    			button, 450, SpringLayout.NORTH, reportMsgLabel);
    	reportMsgPanel.add(button);
    }
	
	private static void addFeedOnlyButton(){
    	JButton button = new JButton(" FEED ONLY ");
    	button.setToolTipText("Run daily feed but do not load database.");
    	button.addActionListener(new MainActionListener(
    			NordicFeedApplication.ACTION_DAILY_FEED_ONLY));
    	reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
				SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
    	reportMsgLayout.putConstraint(SpringLayout.NORTH,
    			button, 525, SpringLayout.NORTH, reportMsgLabel);
    	reportMsgPanel.add(button);
    }
	
	 
	 
	 private static void addQuitReportButton(){
	    	JButton button = new JButton(" QUIT APP ");
	    	button.setToolTipText("Quit application.");
	    	button.addActionListener(new QuitActionListener());
	    	reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
					SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
	    	//reportMsgLayout.putConstraint(SpringLayout.NORTH,
	    	//		button, 650, SpringLayout.NORTH, reportMsgPanel);
	    	reportMsgLayout.putConstraint(SpringLayout.NORTH,
	    			button, 425, SpringLayout.NORTH, reportMsgPanel);
	    	reportMsgPanel.add(button);
	    }
	
	
	private static void createProgramWorkingPanel(){ 
		addProgramWorkingLabel();
		addCancelQueryButton();
    }
	
	//private static  reportMsgLabel; 
	// private static JLabel programWorkingLabel; 
	private static JLabel reportMsgLabel = new JLabel(REPORT_MSG);
	private static void addReprotMsgLabel(){
		reportMsgLabel = new JLabel(REPORT_MSG);
		reportMsgLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 28));
		reportMsgLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
				reportMsgLabel, 0, SpringLayout.HORIZONTAL_CENTER, reportMsgPanel);
		reportMsgLayout.putConstraint(SpringLayout.NORTH, reportMsgLabel, 45,
				SpringLayout.NORTH, reportMsgPanel);
		reportMsgPanel.add(reportMsgLabel);
	}

	private static void addProgramWorkingLabel(){
		JLabel label = new JLabel(PROGRAM_WORKING_MSG);
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 22));
		programWorkingLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER,
				label, 0, SpringLayout.HORIZONTAL_CENTER, programWorkingPanel);
		programWorkingLayout.putConstraint(SpringLayout.SOUTH, label, -50, 
				SpringLayout.VERTICAL_CENTER, programWorkingPanel);
		programWorkingPanel.add(label);
	}
    
    
   
    
    private static void addCancelQueryButton(){
    	JButton button = new JButton(" CANCEL ");
    	button.setToolTipText("Cancel current action.");
    	button.addActionListener(new CancelActionListener());
    	programWorkingLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, button, 0,
				SpringLayout.HORIZONTAL_CENTER, programWorkingPanel);
    	programWorkingLayout.putConstraint(SpringLayout.NORTH,
    			button, 50, SpringLayout.VERTICAL_CENTER, programWorkingPanel);
    	programWorkingPanel.add(button);
    }

    
	private static JComponent getToRangeLeft(){
		return startDateStamp;
	}
	
	private static JComponent getToRangeRight(){
		return endDateStamp;
	}
	
	private static JComponent getFullSizeField(){
		return Title;
	}
	
	private static void setTestSearchValues(){
		//TitleId.setSelectedItem("354807D1");
		//OfferRegionName.setSelectedItem("Japan");
		//OfferGuid.setText("{782E3A00-0000-4000-8000-0000354807D1}");
		
	}
	


}
