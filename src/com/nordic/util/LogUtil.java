package com.nordic.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.nordic.controller.NordicFeedApplication;

/**
 * LogUtil - configurable logging util.
 * 
 */

public class LogUtil {
	
	public static final String LOG_DATE_FORMAT = "yyyy/MM/dd kk:mm:ss:SSS";

	public static final class LogFormatter extends Formatter {
		private static final String LINE_SEPARATOR = System
				.getProperty("line.separator");

		private static final SimpleDateFormat sdf = new SimpleDateFormat(
				LOG_DATE_FORMAT);

		@Override
		public String format(LogRecord record) {

			StringBuilder sb = new StringBuilder();
			sb.append("[ ").append(logMessageLevel).append(" ]").append(" [ ")
					.append(sdf.format(new Date())).append(" ] ")
					.append(formatMessage(record)).append(LINE_SEPARATOR);
			if (record.getThrown() != null) {

				try {

					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					record.getThrown().printStackTrace(pw);
					pw.close();
					sb.append(sw.toString());

				} catch (Exception ex) {
					// ignore
				}
			}

			return sb.toString();
		}
	}

	private static final String DEBUG = "DEBUG";
	private static final String ERROR = "ERROR";
	private static final String INFO = "INFO";
	private static final String LOG_EXT = ".txt";
	private static String logLevel;

	private static String logMessageLevel;
	private static FileHandler outputFileHandler;
	private static Logger outputLogger;
	//private static FileHandler reportFileHandler;
	//private static Logger reportLogger;

	private static final String SPACER = "====================================================";
	private static final String WARNING = "WARNING";

	static {
		try {
			Class.forName("com.nordic.util.PropUtil");
			logLevel = PropUtil.logLevel;
			outputLogger = Logger.getLogger("");
			//reportLogger = Logger.getLogger("report");
			outputFileHandler = new FileHandler(PropUtil.logDir + "LOG_"
					+ PropUtil.logFile + LOG_EXT);
			//reportFileHandler = new FileHandler(PropUtil.reportDir + "LOG_SUMMARY_"
			//		+ PropUtil.getLogNameDateString() + LOG_EXT);
			outputLogger.addHandler(outputFileHandler);
			//reportLogger.addHandler(reportFileHandler);
			outputFileHandler.setFormatter(new LogFormatter());
			//reportFileHandler.setFormatter(new LogFormatter());
		} catch (Exception ex) {
			error(ex);
		}
	}

	/**
	 * For debug statements used during development.
	 * 
	 * @param m
	 */
	public static void debug(String m) {
		if (logLevel.equals(DEBUG)) {
			// System.out.println("<DEBUG> " + m);
			logMessageLevel = DEBUG;
			outputLogger.log(Level.INFO, m);
		}

	}

	/**
	 * Print error messages.
	 * 
	 * @param ex
	 */
	public static void error(Exception ex) {
		// System.err.println("<ERROR> " + m);
		NordicFeedApplication.errorDetected();
		logMessageLevel = ERROR;
		outputLogger.log(Level.SEVERE, ex.getMessage(), ex);

	}

	/**
	 * Print heading to stand out in log file.
	 * 
	 * @param m
	 */
	public static void heading(String m) {
		if (logLevel.equals(INFO)) {
			// System.out.println("<INFO> " + m);
			logMessageLevel = INFO;
			outputLogger.log(Level.INFO, SPACER);
			outputLogger.log(Level.INFO, m);
			outputLogger.log(Level.INFO, SPACER);
		}

	}

	/**
	 * Print informational messages.
	 * @param m
	 */
	public static void info(String m) {
		if (logLevel.equals(INFO)) {
			// System.out.println("<INFO> " + m);
			logMessageLevel = INFO;
			outputLogger.log(Level.INFO, m);
		}

	}

	/**
	 * Print to the report file.
	 * 
	 * @param m
	 */
	public static void report(String m) {

		 outputLogger.log(Level.INFO, m);
		//reportLogger.log(Level.INFO, m);
	}

	/**
	 * Print warnings.
	 * 
	 * @param m
	 */
	public static void warning(String m) {
		if (logLevel.equals(DEBUG) || logLevel.equals(INFO)
				|| logLevel.equals(WARNING)) {
			// System.out.println("<WARNING> " + m);
			logMessageLevel = WARNING;
			outputLogger.log(Level.WARNING, m);
		}

	}

}
