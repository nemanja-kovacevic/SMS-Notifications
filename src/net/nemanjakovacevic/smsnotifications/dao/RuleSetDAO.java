package net.nemanjakovacevic.smsnotifications.dao;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.nemanjakovacevic.smsnotifications.db.DatabaseOpenHelper;
import net.nemanjakovacevic.smsnotifications.domain.Rule;
import net.nemanjakovacevic.smsnotifications.domain.RuleSet;
import net.nemanjakovacevic.smsnotifications.exceptions.CountryCodeNotSupportedException;
import net.nemanjakovacevic.smsnotifications.exceptions.InternetConnenctionException;
import net.nemanjakovacevic.smsnotifications.producers.NotificationProducingEngine;
import net.nemanjakovacevic.smsnotifications.util.HttpUtil;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import net.nemanjakovacevic.smsnotifications.util.RuleSaxFeedParser;
import net.nemanjakovacevic.smsnotifications.util.RuleSetSaxFeedParser;
import net.nemanjakovacevic.smsnotifications.util.Util;
import android.content.Context;

public class RuleSetDAO implements Keys {

	public static List<RuleSet> getLocalRuleSets(Context context) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		List<RuleSet> localRuleSets = db.loadLocalRuleSets();
		db.close();
		return localRuleSets;
	}

	/**
	 * Method for loading rules from all present rule files.
	 * 
	 */
	public static Set<Rule> loadRules(Context context) {
		Set<Rule> rules = new HashSet<Rule>();
		String[] files = context.fileList();
		for(String file : files){
			try {
				rules.addAll(RuleSaxFeedParser.parse(context.openFileInput(file)));
			} catch (FileNotFoundException ignore) {
				// should not happen
			} catch (RuntimeException ignore) {
				// if parsing fails, possibly because of non-rules file
			}
		}
		return rules;
	}

	public static List<RuleSet> mergeRuleSets(List<RuleSet> localRuleSets, List<RuleSet> globalRuleSets) {
		List<RuleSet> mergedRuleSets = new ArrayList<RuleSet>();
		for(RuleSet globalRuleSet : globalRuleSets){
			for(RuleSet localRuleSet : localRuleSets){
				if(globalRuleSet.getCountryCode().equalsIgnoreCase(localRuleSet.getCountryCode())){
					globalRuleSet.setLocalVersion(localRuleSet.getLocalVersion());
					globalRuleSet.setFileName(localRuleSet.getFileName());
					break;
				}
			}
			mergedRuleSets.add(globalRuleSet);
		}
		return mergedRuleSets;
	}

	public static void installRuleSet(Context context, RuleSet ruleSet) throws InternetConnenctionException {
		try {
			String urlString = URL_RULE_SET_BASE + ruleSet.getCountryCode().toLowerCase() + ".xml";
			String fileName = ruleSet.getCountryCode() + ".xml";
			InputStream is = HttpUtil.openHttpConnection(urlString);
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			byte[] buffer = new byte[3072];
			int len = 0;
			while ( (len = is.read(buffer)) != -1 ) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
			
			ruleSet.setFileName(fileName);
			
			DatabaseOpenHelper db = new DatabaseOpenHelper(context);
			db.insertRuleSet(ruleSet);
			db.close();
			
			Set<Rule> newRules = RuleSaxFeedParser.parse(context.openFileInput(fileName));
			NotificationProducingEngine.getInstance(context).add(newRules);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void installDefaultRuleSet(Context context) throws InternetConnenctionException, CountryCodeNotSupportedException {
		String countryCodeByOperator = Util.getCountryCodeByOperator(context);
		RuleSet ruleSet = getGlobalRuleSetForCountryCode(countryCodeByOperator);
		ruleSet.setLocalVersion(ruleSet.getGlobalVersion());
		installRuleSet(context, ruleSet);
		PreferenceDAO.rulesAreInitialyInstalled(context);
	}
	
	public static void removeRuleSet(Context context, RuleSet ruleSet) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		db.deleteRuleSet(ruleSet);
		db.close();
		try {
			Set<Rule> rules = RuleSaxFeedParser.parse(context.openFileInput(ruleSet.getFileName()));
			NotificationProducingEngine.getInstance(context).remove(rules);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		context.deleteFile(ruleSet.getFileName());
	}

	public static void updateRuleSet(Context context, RuleSet ruleSet) throws InternetConnenctionException {
		removeRuleSet(context, ruleSet);
		ruleSet.setLocalVersion(ruleSet.getGlobalVersion());
		installRuleSet(context, ruleSet);
	}

	public static List<RuleSet> getMergedRuleSets(Context context) throws InternetConnenctionException {
		InputStream is = null;
		try {
			is = HttpUtil.openHttpConnection(URL_RULE_SET_INDEX);
		} catch (IOException ex) {
			throw new InternetConnenctionException(ex.getMessage());
		}
		List<RuleSet> globalRuleSets = RuleSetSaxFeedParser.parse(is);
		List<RuleSet> localRuleSets = getLocalRuleSets(context);
		return mergeRuleSets(localRuleSets, globalRuleSets);
	}

	public static RuleSet getMergedRuleSetForCountryCode(Context context, String countryCode) throws InternetConnenctionException {
		List<RuleSet> mergedRuleSets = getMergedRuleSets(context);
		for(RuleSet ruleSet : mergedRuleSets){
			if(ruleSet.getCountryCode().equals(countryCode)){
				return ruleSet;
			}
		}
		return null;
	}

	public static int getLocalVersionOfRulesForCountryCode(Context context, String countryCode) {
		DatabaseOpenHelper db = new DatabaseOpenHelper(context);
		int localVersion = db.getLocalVersionOfRulesForCountryCode(countryCode);
		db.close();
		return localVersion;
	}

	public static RuleSet getGlobalRuleSetForCountryCode(String countryCode) throws InternetConnenctionException, CountryCodeNotSupportedException {
		InputStream is = null;
		try {
			is = HttpUtil.openHttpConnection(URL_RULE_SET_INDEX);
		} catch (IOException ex) {
			throw new InternetConnenctionException(ex.getMessage());
		}
		List<RuleSet> globalRuleSets = RuleSetSaxFeedParser.parse(is);
		for(RuleSet ruleSet : globalRuleSets){
			if(ruleSet.getCountryCode().equalsIgnoreCase(countryCode)){
				return ruleSet;
			}
		}
		throw new CountryCodeNotSupportedException(countryCode);
	}

}
