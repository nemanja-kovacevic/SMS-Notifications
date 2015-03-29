package net.nemanjakovacevic.smsnotifications.domain;

public class Rule {

	protected String serviceProvider;
	protected String serviceNumber;
	protected int notificationType;
	protected RecognitionPattern[] recognitionPatterns;
	protected String numberPattern;
	protected String numberOfCallsPattern;
	protected int numberOfCallsPosition;
	protected String dateTimeRecognitionPattern;
	protected String[] dateTimeParsingPatterns;
	protected int[] dateTimePositions;
	protected String dateRecognitionPattern;
	protected String[] dateParsingPatterns;
	protected int[] datePositions;
	protected String timeRecognitionPattern;
	protected String[] timeParsingPatterns;
	protected int[] timePositions;
	
	private String countryPrefix;
	private String countryCode;
	
	public String getServiceProvider() {
		return serviceProvider;
	}
	
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	
	public String getServiceNumber() {
		return serviceNumber;
	}
	
	public void setServiceNumber(String serviceNumber) {
		this.serviceNumber = serviceNumber;
	}
	
	public int getNotificationType() {
		return notificationType;
	}
	
	public void setNotificationType(int notificationType) {
		this.notificationType = notificationType;
	}
	
	public RecognitionPattern[] getRecognitionPatterns() {
		return recognitionPatterns;
	}
	
	public void setRecognitionPatterns(RecognitionPattern[] recognitionPatterns) {
		this.recognitionPatterns = recognitionPatterns;
	}
	
	public String getNumberPattern() {
		return numberPattern;
	}
	
	public void setNumberPattern(String numberPattern) {
		this.numberPattern = numberPattern;
	}
	
	public String getNumberOfCallsPattern() {
		return numberOfCallsPattern;
	}
	
	public void setNumberOfCallsPattern(String numberOfCallsPattern) {
		this.numberOfCallsPattern = numberOfCallsPattern;
	}
	
	public int getNumberOfCallsPosition() {
		return numberOfCallsPosition;
	}
	
	public void setNumberOfCallsPosition(int numberOfCallsPosition) {
		this.numberOfCallsPosition = numberOfCallsPosition;
	}
	
	public String getDateTimeRecognitionPattern() {
		return dateTimeRecognitionPattern;
	}
	
	public void setDateTimeRecognitionPattern(String dateTimeRecognitionPattern) {
		this.dateTimeRecognitionPattern = dateTimeRecognitionPattern;
	}
	
	public String[] getDateTimeParsingPatterns() {
		return dateTimeParsingPatterns;
	}
	
	public void setDateTimeParsingPatterns(String[] dateTimeParsingPatterns) {
		this.dateTimeParsingPatterns = dateTimeParsingPatterns;
	}
	
	public int[] getDateTimePositions() {
		return dateTimePositions;
	}
	
	public void setDateTimePositions(int[] dateTimePositions) {
		this.dateTimePositions = dateTimePositions;
	}
	
	public String getDateRecognitionPattern() {
		return dateRecognitionPattern;
	}
	
	public void setDateRecognitionPattern(String dateRecognitionPattern) {
		this.dateRecognitionPattern = dateRecognitionPattern;
	}
	
	public String[] getDateParsingPatterns() {
		return dateParsingPatterns;
	}
	
	public void setDateParsingPatterns(String[] dateParsingPattern) {
		this.dateParsingPatterns = dateParsingPattern;
	}
	
	public int[] getDatePositions() {
		return datePositions;
	}
	
	public void setDatePositions(int[] datePositions){
		this.datePositions = datePositions;
	}
	
	public String getTimeRecognitionPattern() {
		return timeRecognitionPattern;
	}
	
	public void setTimeRecognitionPattern(String timeRecognitionPattern) {
		this.timeRecognitionPattern = timeRecognitionPattern;
	}
	
	public String[] getTimeParsingPatterns() {
		return timeParsingPatterns;
	}
	
	public void setTimeParsingPatterns(String[] timeParsingPattern) {
		this.timeParsingPatterns = timeParsingPattern;
	}
	
	public int[] getTimePositions() {
		return timePositions;
	}
	
	public void setTimePositions(int[] timePositions) {
		this.timePositions = timePositions;
	}
	
	
	public void setCountryPrefix(String countryPrefix) {
		this.countryPrefix = countryPrefix;
	}
	
	public String getCountryPrefix() {
		return countryPrefix;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public Rule copy() {
		Rule copy = new Rule();
		copy.setServiceProvider(this.getServiceProvider());
		copy.setServiceNumber(this.getServiceNumber());
		copy.setNotificationType(this.getNotificationType());
		copy.setRecognitionPatterns(this.getRecognitionPatterns());
		copy.setNumberPattern(this.getNumberPattern());
		copy.setNumberOfCallsPattern(this.getNumberOfCallsPattern());
		copy.setNumberOfCallsPosition(this.getNumberOfCallsPosition());
		copy.setDateTimeRecognitionPattern(this.getDateTimeRecognitionPattern());
		copy.setDateTimeParsingPatterns(this.getDateTimeParsingPatterns());
		copy.setDateTimePositions(this.getDateTimePositions());
		copy.setDateRecognitionPattern(this.getDateRecognitionPattern());
		copy.setDateParsingPatterns(this.getDateParsingPatterns());
		copy.setDatePositions(this.getDatePositions());
		copy.setTimeRecognitionPattern(this.getTimeRecognitionPattern());
		copy.setTimeParsingPatterns(this.getTimeParsingPatterns());
		copy.setTimePositions(this.getTimePositions());
		copy.setCountryPrefix(this.getCountryPrefix());
		copy.setCountryCode(this.getCountryCode());
		return copy;
	}

	public void clear() {
		this.serviceProvider = null;
		this.serviceNumber = null;
		this.notificationType = 0;
		this.recognitionPatterns = null;
		this.numberPattern = null;
		this.numberOfCallsPattern = null;
		this.numberOfCallsPosition = 0;
		this.dateTimeRecognitionPattern = null;
		this.dateTimeParsingPatterns = null;
		this.dateTimePositions = null;
		this.dateRecognitionPattern = null;
		this.dateParsingPatterns = null;
		this.datePositions = null;
		this.timeRecognitionPattern = null;
		this.timeParsingPatterns = null;
		this.timePositions = null;
		this.countryPrefix = null;
	}
	
	public static class RecognitionPattern {

		private String pattern;
		private Type type;

		public RecognitionPattern() {
		}

		public RecognitionPattern(String pattern, Type type) {
			this.pattern = pattern;
			this.type = type;
		}

		public String getPattern() {
			return pattern;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public RecognitionPattern copy() {
			return new RecognitionPattern(this.getPattern(), this.getType());
		}

		public enum Type {

			STARTS_WITH(1),
			ANYWHERE_IN_MESSAGE_BODY(2);

			private int code;

			private Type(int code) {
				this.code = code;
			}

			public int code() {
				return code;
			}

			public static Type getTypeFromCode(int code) {
				switch (code) {
				case 1:
					return STARTS_WITH;
				case 2:
					return ANYWHERE_IN_MESSAGE_BODY;
				default:
					return null;
				}
			}

		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((countryPrefix == null) ? 0 : countryPrefix.hashCode());
		result = prime * result + notificationType;
		result = prime * result + ((serviceNumber == null) ? 0 : serviceNumber.hashCode());
		result = prime * result + ((serviceProvider == null) ? 0 : serviceProvider.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rule other = (Rule) obj;
		if (countryPrefix == null) {
			if (other.countryPrefix != null)
				return false;
		} else if (!countryPrefix.equals(other.countryPrefix))
			return false;
		if (notificationType != other.notificationType)
			return false;
		if (serviceNumber == null) {
			if (other.serviceNumber != null)
				return false;
		} else if (!serviceNumber.equals(other.serviceNumber))
			return false;
		if (serviceProvider == null) {
			if (other.serviceProvider != null)
				return false;
		} else if (!serviceProvider.equals(other.serviceProvider))
			return false;
		return true;
	}

}
