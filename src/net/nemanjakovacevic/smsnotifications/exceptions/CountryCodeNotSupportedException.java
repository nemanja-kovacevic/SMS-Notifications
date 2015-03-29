package net.nemanjakovacevic.smsnotifications.exceptions;

public class CountryCodeNotSupportedException extends Exception {

	private String countryCode;
	
	private static final long serialVersionUID = 1L;

	public CountryCodeNotSupportedException(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
}
