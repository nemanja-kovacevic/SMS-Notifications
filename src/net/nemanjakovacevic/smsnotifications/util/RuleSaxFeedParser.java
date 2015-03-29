package net.nemanjakovacevic.smsnotifications.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;

import net.nemanjakovacevic.smsnotifications.domain.Rule;
import net.nemanjakovacevic.smsnotifications.domain.Rule.RecognitionPattern;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;


/**
 * Parser for parsing rules from xml files.
 * 
 * @author Nemanja Kovacevic
 *
 */
public class RuleSaxFeedParser {

	/**
	 * Method for parsing rules from xml files.
	 * 
	 * @param inputStream
	 * @return
	 */
	public static Set<Rule> parse(InputStream inputStream) {
		final Set<Rule> rules = new HashSet<Rule>();
		
		final Rule currentRule = new Rule();
		final List<RecognitionPattern> recognitionPatterns = new ArrayList<RecognitionPattern>();
		final RecognitionPattern currentRecognitionPattern = new RecognitionPattern();
		final DateAndOrTimeExtractorHelper dateTimeHelper = new DateAndOrTimeExtractorHelper();
		final DateAndOrTimeExtractorHelper dateHelper = new DateAndOrTimeExtractorHelper();
		final DateAndOrTimeExtractorHelper timeHelper = new DateAndOrTimeExtractorHelper();
		final CountryPrefixExtractorHelper countryPrefixHelper = new CountryPrefixExtractorHelper();
		final CountryCodeExtractorHelper countryCodeHelper = new CountryCodeExtractorHelper();
		
		RootElement root = new RootElement(RULE_SET);
		
		root.setStartElementListener(new StartElementListener() {
			
			@Override
			public void start(Attributes attributes) {
				countryPrefixHelper.setCountryPrefix(attributes.getValue(COUNTRY_PREFIX));
				countryCodeHelper.setCountryCode(attributes.getValue(COUNTRY_CODE));
			}
		});
		
		Element rule = root.getChild(RULE);
		rule.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				currentRule.setRecognitionPatterns(recognitionPatterns.toArray(new RecognitionPattern[recognitionPatterns.size()]));
				currentRule.setCountryPrefix(countryPrefixHelper.getCountryPrefix());
				currentRule.setCountryCode(countryCodeHelper.getCountryCode());
				recognitionPatterns.clear();
				rules.add(currentRule.copy());
				currentRule.clear();
			}
		});
		rule.getChild(SERVICE_PROVIDER).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRule.setServiceProvider(body);
			}
		});
		rule.getChild(SERVICE_NUMBER).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRule.setServiceNumber(body);
			}
		});
		rule.getChild(NOTIFICATION_TYPE).setEndTextElementListener(new
				EndTextElementListener() {
					@Override
					public void end(String body) {
						currentRule.setNotificationType(Integer.parseInt(body));
					}
				});
		rule.getChild(RECOGNITION_PATTERN).setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				recognitionPatterns.add(currentRecognitionPattern.copy());
			}
		});
		rule.getChild(RECOGNITION_PATTERN).getChild(RECOGNITION_PATTERN_TEXT).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRecognitionPattern.setPattern(body);
			}
		});
		rule.getChild(RECOGNITION_PATTERN).getChild(RECOGNITION_PATTERN_TYPE).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRecognitionPattern.setType(RecognitionPattern.Type.getTypeFromCode(Integer.parseInt(body)));
			}
		});
		rule.getChild(NUMBER_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRule.setNumberPattern(body);
			}
		});
		rule.getChild(NUMBER_OF_CALLS_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRule.setNumberOfCallsPattern(body);
			}
		});
		rule.getChild(NUMBER_OF_CALLS_POSTIONS).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRule.setNumberOfCallsPosition(Integer.parseInt(body));
			}
		});
		rule.getChild(DATE_TIME_RECOGNITION).setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				currentRule.setDateTimeRecognitionPattern(dateTimeHelper.getRecognitionPattern());
				currentRule.setDateTimeParsingPatterns(dateTimeHelper.getParsingPatterns());
				currentRule.setDateTimePositions(dateTimeHelper.getPositions());
			}
		});
		rule.getChild(DATE_TIME_RECOGNITION).getChild(DATE_TIME_RECOGNITION_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				dateTimeHelper.setRecognitionPattern(body);
			}
		});
		rule.getChild(DATE_TIME_RECOGNITION).getChild(DATE_TIME_PARSING_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				dateTimeHelper.addParsingPattern(body);
			}
		});
		rule.getChild(DATE_TIME_RECOGNITION).getChild(DATE_TIME_POSITION).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				dateTimeHelper.addPosition(Integer.parseInt(body));
			}
		});
		rule.getChild(DATE_RECOGNITION).setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				currentRule.setDateRecognitionPattern(dateHelper.getRecognitionPattern());
				currentRule.setDateParsingPatterns(dateHelper.getParsingPatterns());
				currentRule.setDatePositions(dateHelper.getPositions());
			}
		});
		rule.getChild(DATE_RECOGNITION).getChild(DATE_RECOGNITION_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				dateHelper.setRecognitionPattern(body);
			}
		});
		rule.getChild(DATE_RECOGNITION).getChild(DATE_PARSING_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				dateHelper.addParsingPattern(body);
			}
		});
		rule.getChild(DATE_RECOGNITION).getChild(DATE_POSITION).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				dateHelper.addPosition(Integer.parseInt(body));
			}
		});
		rule.getChild(TIME_RECOGNITION).setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				currentRule.setTimeRecognitionPattern(timeHelper.getRecognitionPattern());
				currentRule.setTimeParsingPatterns(timeHelper.getParsingPatterns());
				currentRule.setTimePositions(timeHelper.getPositions());
			}
		});
		rule.getChild(TIME_RECOGNITION).getChild(TIME_RECOGNITION_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				timeHelper.setRecognitionPattern(body);
			}
		});
		rule.getChild(TIME_RECOGNITION).getChild(TIME_PARSING_PATTERN).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				timeHelper.addParsingPattern(body);
			}
		});
		rule.getChild(TIME_RECOGNITION).getChild(TIME_POSITION).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				timeHelper.addPosition(Integer.parseInt(body));
			}
		});
		try {
			Xml.parse(inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return rules;
	}
	
	private static class DateAndOrTimeExtractorHelper {

		private String recognitionPattern;
		private List<String> parsingPatterns = new ArrayList<String>();
		private List<Integer> positions = new ArrayList<Integer>();
		
		public String getRecognitionPattern() {
			return recognitionPattern;
		}
		
		public void setRecognitionPattern(String recognitionPattern) {
			this.recognitionPattern = recognitionPattern;
		}
		
		public void addParsingPattern(String pattern) {
			parsingPatterns.add(pattern);
		}
		
		public String[] getParsingPatterns() {
			String[] extractedParsingPatterns = parsingPatterns.toArray(new String[parsingPatterns.size()]);
			parsingPatterns.clear();
			return extractedParsingPatterns;
		}

		public void addPosition(int postion) {
			positions.add(new Integer(postion));		
		}

		public int[] getPositions() {
			int[] extractedPositions = new int[positions.size()];
			for(int i=0; i<extractedPositions.length; i++){
				extractedPositions[i] = positions.get(i);
			}
			positions.clear();
			return extractedPositions;
		}
	}
	
	public static class CountryPrefixExtractorHelper {
		
		private String countryPrefix;
		
		public String getCountryPrefix() {
			return countryPrefix;
		}
		
		public void setCountryPrefix(String countryPrefix) {
			this.countryPrefix = countryPrefix;
		}
		
	}
	
	public static class CountryCodeExtractorHelper {

		private String countryCode;

		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}

	}
	
	public static final String RULE_SET = "rule_set";
	public static final String COUNTRY_PREFIX = "country_prefix";
	public static final String COUNTRY_CODE = "country_code";
	public static final String RULE = "rule";
	public static final String SERVICE_PROVIDER = "service_provider";
	public static final String SERVICE_NUMBER = "service_number";
	public static final String NOTIFICATION_TYPE = "notification_type";
	public static final String RECOGNITION_PATTERN = "recognition_pattern";
	public static final String RECOGNITION_PATTERN_TYPE = "recognition_pattern_type";
	public static final String RECOGNITION_PATTERN_TEXT = "recognition_pattern_text";
	public static final String NUMBER_PATTERN = "number_pattern";
	public static final String NUMBER_OF_CALLS_PATTERN = "number_of_calls_pattern";
	public static final String NUMBER_OF_CALLS_POSTIONS = "number_of_calls_position";
	public static final String DATE_TIME_RECOGNITION = "date_time_recognition";
	public static final String DATE_TIME_RECOGNITION_PATTERN = "date_time_recognition_pattern";
	public static final String DATE_TIME_PARSING_PATTERN = "date_time_parsing_pattern";
	public static final String DATE_TIME_POSITION = "date_time_position";
	public static final String DATE_RECOGNITION = "date_recognition";
	public static final String DATE_RECOGNITION_PATTERN = "date_recognition_pattern";
	public static final String DATE_PARSING_PATTERN = "date_parsing_pattern";
	public static final String DATE_POSITION = "date_position";
	public static final String TIME_RECOGNITION = "time_recognition";
	public static final String TIME_RECOGNITION_PATTERN = "time_recognition_pattern";
	public static final String TIME_PARSING_PATTERN = "time_parsing_pattern";
	public static final String TIME_POSITION = "time_position";

}
