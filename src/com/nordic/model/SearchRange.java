package com.nordic.model;

/**
 * SearchRange is used to hold search parameters (int/double) that
 * can be searched on using a range.
 * 
 * @author Mike Sioda
 *
 */
public class SearchRange {

	public Number rangeEnd;
	public Number rangeStart;

	public SearchRange(Object rangeStart, Object rangeEnd) {
		this.rangeStart = (Number)rangeStart;
		this.rangeEnd = (Number)rangeEnd;
	}
	
	
}
