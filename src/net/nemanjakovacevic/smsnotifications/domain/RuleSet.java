package net.nemanjakovacevic.smsnotifications.domain;

public class RuleSet {

	private String countryCode;
	private String countryName;
	private int localVersion;
	private int globalVersion;
	private String fileName;
	
	public RuleSet() {
	}

	public RuleSet(String countryCode, String countryName, int localVersion, int globalVersion, String fileName) {
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.localVersion = localVersion;
		this.globalVersion = globalVersion;
		this.fileName = fileName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public int getLocalVersion() {
		return localVersion;
	}

	public void setLocalVersion(int localVersion) {
		this.localVersion = localVersion;
	}
	
	public int getGlobalVersion() {
		return globalVersion;
	}
	
	public void setGlobalVersion(int globalVersion) {
		this.globalVersion = globalVersion;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public RuleSet copy() {
		return new RuleSet(this.getCountryCode(), this.getCountryName(), this.getLocalVersion(), this.getGlobalVersion(), this.getFileName());
	}

	public boolean isUpdateable() {
		if(globalVersion > localVersion){
			return true;
		}else{
			return false;
		}
	}

}
