/**
 * 
 */
package com.selflearn.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author ViKumar
 *
 */
public class ExchangeNotificationFilter {
	
//	private FilterField salary;
//	private FilterField age;
//	private FilterField position;
	public String[] getExchangeCode() {
		return exchangeCode;
	}

	@JsonProperty("exchangeCode")
	public void setExchangeCode(String[] exchangeCode) {
		this.exchangeCode = exchangeCode;
	}


	public String[] getSegmentMic() {
		return segmentMic;
	}

	@JsonProperty("segmentMic")
	public void setSegmentMic(String[] segmentMic) {
		this.segmentMic = segmentMic;
	}

	private String[] exchangeCode;
	private String[] segmentMic;
//	private FilterField newExchangeCode;
//	private FilterField eventInsertDate; 
	
}


