package net.nemanjakovacevic.smsnotifications.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.domain.RuleSet;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class RuleSetSaxFeedParser {

	public static List<RuleSet> parse(InputStream inputStream) {
		final List<RuleSet> ruleSets = new ArrayList<RuleSet>();

		final RuleSet currentRuleSet = new RuleSet();

		RootElement root = new RootElement(RULE_SETS);
		Element ruleSet = root.getChild(RULE_SET);
		ruleSet.setEndElementListener(new EndElementListener() {
			@Override
			public void end() {
				ruleSets.add(currentRuleSet.copy());
			}
		});
		ruleSet.getChild(COUNTRY_CODE).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRuleSet.setCountryCode(body);
			}
		});
		ruleSet.getChild(COUNTRY_NAME).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRuleSet.setCountryName(body);
			}
		});
		ruleSet.getChild(VERSION).setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				currentRuleSet.setGlobalVersion(Integer.parseInt(body));
			}
		});
		try {
			Xml.parse(inputStream, Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return ruleSets;
	}

	public static final String RULE_SETS = "rule_sets";
	public static final String RULE_SET = "rule_set";
	public static final String COUNTRY_NAME = "country_name";
	public static final String COUNTRY_CODE = "country_code";
	public static final String VERSION = "version";

}
