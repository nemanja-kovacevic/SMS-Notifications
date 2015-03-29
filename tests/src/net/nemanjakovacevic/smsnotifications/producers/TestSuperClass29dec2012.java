package net.nemanjakovacevic.smsnotifications.producers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.nemanjakovacevic.smsnotifications.dao.RuleSetDAO;
import net.nemanjakovacevic.smsnotifications.domain.NotificationMessage;
import net.nemanjakovacevic.smsnotifications.domain.RuleSet;
import net.nemanjakovacevic.smsnotifications.util.HttpUtil;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.test.AndroidTestCase;

public class TestSuperClass29dec2012 extends AndroidTestCase implements Keys {

	NotificationMessage vipOneNumberOneCallNotificationSms;
	NotificationMessage vipOneNumberMultipleCallsNotificationSms;
	NotificationMessage vipMultipleNumbersMultipleCallsNotificationSms;
	NotificationMessage vipAvailableAgainNotificationSms;
	NotificationMessage vipNotBusyAnymoreNotificationSms;
	
	// variantB
	NotificationMessage telenorOneNumberOneCallNotificationSmsVariantB;
	NotificationMessage telenorOneNumberMultipleCallsNotificationSmsVariantB;
	NotificationMessage telenorMultipleNumbersMultipleCallsNotificationSmsVariantB;
	NotificationMessage telenorAvailableAgainNotificationSmsVariantB;
	
	NotificationMessage mtsOneNumberOneCallNotificationSmsNotAvailable;
	NotificationMessage mtsOneNumberOneCallNotificationSmsBusy;
	NotificationMessage mtsOneNumberMultipleCallsNotificationSmsNotAvailable;
	NotificationMessage mtsOneNumberMultipleCallsNotificationSmsBusy;
	NotificationMessage mtsMultipleNumbersMultipleCallsPart1NotificationSmsNotAvailable;
	NotificationMessage mtsMultipleNumbersMultipleCallsPart1NotificationSmsBusy;
	NotificationMessage mtsMultipleNumbersMultipleCallsPart2NotificationSmsNotAvailable;
	NotificationMessage mtsMultipleNumbersMultipleCallsPart2NotificationSmsBusy;
	NotificationMessage mtsAvailableAgainNotificationSms;
	NotificationMessage mtsNotBusyAnymoreMotificationSms;
	
	NotificationMessage randomSms1;
	NotificationMessage randomSms2;
	NotificationMessage randomSms3;
	NotificationMessage randomSms4;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
		String operatorCountryCode = tm.getSimCountryIso().toLowerCase();
		
		RuleSet ruleSet = RuleSetDAO.getGlobalRuleSetForCountryCode(operatorCountryCode);
		
		try {
			InputStream is = HttpUtil.openHttpConnection(URL_RULE_SET_BASE + ruleSet.getCountryCode() + ".xml");
			FileOutputStream fos = getContext().openFileOutput(ruleSet.getCountryCode()+".xml", Context.MODE_PRIVATE);
			byte[] buffer = new byte[3072];
			int len = 0;
			while ( (len = is.read(buffer)) != -1 ) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
			
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		SmsMessage sms = null;
		
		/*
		 * serviceNumber: 123
		 * 
		 * Propusten poziv od: +381644755281, 29.12./11:14. Vas Vip
		 */
		sms = SmsMessage.createFromPdu(new byte[]{6, -111, -125, 97, 1, 4, -13, 4, 18, -48, -21, 119, -85, 93, 110, -23, -19, -31, 55, 0, 0, 33, 33, -110, 17, 81, -123, 64, 56, 80, -7, 27, 94, -97, -45, -53, 110, 16, -4, -83, 79, -37, 65, 111, -78, 14, -76, -102, -31, 98, 54, 26, -19, 86, -85, -55, 112, 49, 22, 72, -106, 115, -59, 100, -82, 87, 44, -90, -117, -47, 92, 32, 107, 120, 14, -78, -90, -31});
		vipOneNumberOneCallNotificationSms = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 123
		 * 
		 * Propusteni pozivi od: +381644755281, (2) 29.12./11:18. Vas Vip
		 */
		sms = SmsMessage.createFromPdu(new byte[]{6, -111, -125, 97, 1, 4, -13, 4, 18, -48, -21, 119, -85, 93, 110, -23, -19, -31, 55, 0, 0, 33, 33, -110, 17, -111, 84, 64, 62, 80, -7, 27, 94, -97, -45, -53, -18, 52, 8, -2, -42, -89, -19, 105, -48, -101, -84, 3, -83, 102, -72, -104, -115, 70, -69, -43, 106, 50, 92, -116, 5, 66, -55, 82, 32, 89, -50, 21, -109, -71, 94, -79, -104, 46, -122, 115, -127, -84, -31, 57, -56, -102, -122, 3});
		vipOneNumberMultipleCallsNotificationSms = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 123
		 * 
		 *  Propusteni pozivi od: +381637083620, (2) 29.12./22:33, +381642016105, 29.12./22:28, +381621654066, 29.12./22:27, +381112623606, 29.12./22:26, +381644755281, 29.12./22:24. Vas Vip
		 */
		
		/*
		 * Vip sends one long message with all of the notifications. 
		 */
		
		sms = SmsMessage.createFromPdu(new byte[]{6, -111, -125, 97, 1, 4, -13, 68, 18, -48, -21, 119, -85, 93, 110, -23, -19, -31, 55, 0, 0, 33, 33, -110, 34, 67, 66, 64, -106, 6, 8, 4, 78, -121, 2, 1, 80, -7, 27, 94, -97, -45, -53, -18, 52, 8, -2, -42, -89, -19, 105, -48, -101, -84, 3, -83, 102, -72, -104, 109, 118, -125, -31, 102, 54, 25, -116, 5, 66, -55, 82, 32, 89, -50, 21, -109, -71, 94, 50, -103, 110, 54, 99, -127, 86, 51, 92, -52, 70, -109, -63, 98, -74, 24, -84, -58, 2, -55, 114, -82, -104, -52, -11, -110, -55, 116, 50, 28, 11, -76, -102, -31, 98, 54, 89, -52, 86, -93, -63, 108, 54, 22, 72, -106, 115, -59, 100, -82, -105, 76, -90, -109, -35, 88, -96, -43, 12, 23, -117, -59, 100, 54, -39, -52, 6, -77, -79, 64, -78, -100, 43, 38, 115, -67, 100, 50, -99, -52, -58, 2, 1});
		vipMultipleNumbersMultipleCallsNotificationSms = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		sms = SmsMessage.createFromPdu(new byte[]{6, -111, -125, 97, 1, 4, -13, 68, 18, -48, -21, 119, -85, 93, 110, -23, -19, -31, 55, 0, 0, 33, 33, -110, 34, 67, 66, 64, 44, 6, 8, 4, 78, -121, 2, 2, -85, 25, 46, 102, -93, -47, 110, -75, -102, 12, 23, 99, -127, 100, 57, 87, 76, -26, 122, -55, 100, 58, 25, -51, 5, -78, -122, -25, 32, 107, 26, 14});
		vipMultipleNumbersMultipleCallsNotificationSms.setBody(vipMultipleNumbersMultipleCallsNotificationSms.getBody() + sms.getMessageBody());
		
		
		/*
		 * serviceNumber: 234
		 * 
		 * Vip korisnik ciji je broj +381604749959, je ponovo dostupan.
		 */
		sms = SmsMessage.createFromPdu(new byte[]{6, -111, -125, 97, 1, 4, -13, 4, 6, -48, -42, 52, 28, 0, 0, 33, 33, -110, 17, 19, 32, 64, 60, -42, 52, 28, -76, 126, -53, -45, 115, 119, 122, 13, 26, -89, -43, 105, -112, -70, 12, 18, -53, -33, 106, -48, 106, -122, -117, -39, 96, -76, 27, 45, -105, -85, -27, 88, 32, 117, 25, 4, 127, -69, -33, -10, 55, -120, -4, -98, -45, -21, -16, -80, -37, 5});
		vipAvailableAgainNotificationSms = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 234
		 * 
		 * Pretplatnik +381604749959, nije vise zauzet. Uslugu pruzio Vip.
		 */
		sms = SmsMessage.createFromPdu(new byte[]{6, -111, -125, 97, 1, 4, -13, 4, 6, -48, -42, 52, 28, 0, 0, 33, 33, -110, 17, 115, 16, 64, 63, 80, 121, -103, 14, 103, -121, -23, -18, -12, 26, -76, -102, -31, 98, 54, 24, -19, 70, -53, -27, 106, 57, 22, -56, -99, 86, -105, 65, -10, -12, -68, 12, -46, -121, -21, -6, 50, -35, 5, -86, -50, -39, -11, 115, 29, 4, -105, -41, -11, -23, 55, -56, -102, -122, -69, 0});
		vipNotBusyAnymoreNotificationSms = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		
		
		// VARIANT B
		
		
		/*
		 * Vas propusten poziv:
		 * +381604749959 < 1 >, 29/12 12:29
		 *
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 19, 0, 1, -16, 36, 13, -48, -44, 50, -69, -20, 126, -53, 1, 0, 0, 33, 33, -110, 33, 19, -110, 64, 54, -42, -16, 28, 4, -105, -65, -31, -11, 57, -67, -20, 6, -63, -33, -6, -76, 93, -89, 88, -51, 112, 49, 27, -116, 118, -93, -27, 114, -75, 28, -120, 7, -118, -127, 124, 44, -112, 44, -9, -118, -55, 64, 49, -103, 78, -106, 83, 0});
		telenorOneNumberOneCallNotificationSmsVariantB = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 345
		 * 
		 * Vas propusten poziv:
		 * +381604749959 < 2 >, 29/12 12:33
		 *
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 19, 0, 1, -16, 36, 13, -48, -44, 50, -69, -20, 126, -53, 1, 0, 0, 33, 33, -110, 33, 83, 2, 64, 54, -42, -16, 28, 4, -105, -65, -31, -11, 57, -67, -20, 6, -63, -33, -6, -76, 93, -89, 88, -51, 112, 49, 27, -116, 118, -93, -27, 114, -75, 28, -120, 7, -110, -127, 124, 44, -112, 44, -9, -118, -55, 64, 49, -103, 110, 54, 83, 0});
		telenorOneNumberMultipleCallsNotificationSmsVariantB = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 345
		 *
		 * Vas propusten poziv:
		 * +381644755281 < 1 >, 29/12 22:06
		 * +381604749959 < 1 >, 29/12 22:06
		 * +381642016105 < 1 >, 29/12 22:07
		 */
		
		/*
		 * When more than 3 missed calls telenor sends them in separate individual messages.
		 */
		
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 19, 0, 1, -16, 36, 13, -48, -44, 50, -69, -20, 126, -53, 1, 0, 0, 33, 33, -110, 34, 1, -109, 64, 120, -42, -16, 28, 4, -105, -65, -31, -11, 57, -67, -20, 6, -63, -33, -6, -76, 93, -89, 88, -51, 112, 49, 27, -115, 118, -85, -43, 100, -72, 24, -120, 7, -118, -127, 124, 44, -112, 44, -9, -118, -55, 64, 50, -103, 14, 102, 83, -84, 102, -72, -104, 13, 70, -69, -47, 114, -71, 90, 14, -60, 3, -59, 64, 62, 22, 72, -106, 123, -59, 100, 32, -103, 76, 7, -77, 41, 86, 51, 92, -52, 70, -109, -63, 98, -74, 24, -84, 6, -30, -127, 98, 32, 31, 11, 36, -53, -67, 98, 50, -112, 76, -90, -125, -35, 20});
		telenorMultipleNumbersMultipleCallsNotificationSmsVariantB = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		
		/*
		 * serviceNumber: 345
		 * 
		 *  Korisnik +381637083620 je ponovo dostupan.
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 19, 0, 1, -16, 36, 13, -48, -44, 50, -69, -20, 126, -53, 1, 0, 0, 33, 33, -110, 33, 68, 53, 64, 42, -53, -73, 60, 61, 119, -89, -41, -96, -43, 12, 23, -77, -51, 110, 48, -36, -52, 38, -125, -127, -44, 101, 16, -4, -19, 126, -37, -33, 32, -14, 123, 78, -81, -61, -61, 110, 23});
		telenorAvailableAgainNotificationSmsVariantB = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		
		
		
		
		/*
		 * serviceNumber: 456
		 * 
		 * Propusten poziv:
		 * 0604749959
		 * 11:51h 29.12.2012
		 * Pratite mts i na Facebooku: www.facebook.com/imateprijatelje
		 * 
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 17, 37, 112, 64, 109, 80, -7, 27, 94, -97, -45, -53, 110, 16, -4, -83, 79, -37, 117, 10, 16, -52, 6, -93, -35, 104, -71, 92, 45, -89, -120, -59, 116, -75, 24, 26, 36, -53, -71, 98, 50, -105, 12, 22, -109, 41, 20, 80, 121, -104, -98, -90, -105, 65, 109, -6, 28, -108, 6, -71, -61, 32, 99, 120, 92, 22, -65, -33, -21, -70, 14, 116, -65, -33, 93, -26, -16, -72, 44, 126, -65, -41, -82, -15, -69, -3, 74, -73, -61, -12, 50, 92, -98, 86, -121, -23, 101, -74, -70, -84, 0});
		mtsOneNumberOneCallNotificationSmsNotAvailable = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * Bili ste zauzeti.Propusten poziv:
		 * +381604749959
		 * 11:57h 29.12.2012
		 * Pratite mts i na Facebooku: www.facebook.com/imateprijatelje
		 * 
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 17, -123, 117, 64, -127, -62, 52, 59, 13, -102, -45, -53, 32, 125, -72, -82, 47, -45, -45, 46, -88, -4, 13, -81, -49, -23, 101, 55, 8, -2, -42, -89, -19, 58, 5, 104, 53, -61, -59, 108, 48, -38, -115, -106, -53, -43, 114, -118, 88, 76, 87, -69, -95, 65, -78, -100, 43, 38, 115, -55, 96, 49, -103, 66, 1, -107, -121, -23, 105, 122, 25, -44, -90, -49, 65, 105, -112, 59, 12, 50, -122, -57, 101, -15, -5, -67, -82, -21, 64, -9, -5, -35, 101, 14, -113, -53, -30, -9, 123, -19, 26, -65, -37, -81, 116, 59, 76, 47, -61, -27, 105, 117, -104, 94, 102, -85, -53, 10});
		mtsOneNumberOneCallNotificationSmsBusy = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * Propusteni pozivi 2:
		 * 2: 0604749959
		 * 12:03h 29.12.2012
		 * Pratite mts i na Facebooku: www.facebook.com/imateprijatelje
		 * 
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 33, 64, 0, 64, 115, 80, -7, 27, 94, -97, -45, -53, -18, 52, 8, -2, -42, -89, -19, 105, -112, 76, -89, -112, -23, 64, 48, 27, -116, 118, -93, -27, 114, -75, -100, 34, 38, -45, -63, 102, 104, -112, 44, -25, -118, -55, 92, 50, 88, 76, -90, 80, 64, -27, 97, 122, -102, 94, 6, -75, -23, 115, 80, 26, -28, 14, -125, -116, -31, 113, 89, -4, 126, -81, -21, 58, -48, -3, 126, 119, -103, -61, -29, -78, -8, -3, 94, -69, -58, -17, -10, 43, -35, 14, -45, -53, 112, 121, 90, 29, -90, -105, -39, -22, -78, 2});
		mtsOneNumberMultipleCallsNotificationSmsNotAvailable = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * Bili ste zauzeti.Propusteni pozivi 2:
		 * 2: +381604749959
		 * 12:06h 29.12.2012
		 * Pratite mts i na Facebooku: www.facebook.com/imateprijatelje
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 33, -128, 17, 64, -121, -62, 52, 59, 13, -102, -45, -53, 32, 125, -72, -82, 47, -45, -45, 46, -88, -4, 13, -81, -49, -23, 101, 119, 26, 4, 127, -21, -45, -10, 52, 72, -90, 83, -56, 116, -96, -43, 12, 23, -77, -63, 104, 55, 90, 46, 87, -53, 41, 98, 50, 29, -52, -122, 6, -55, 114, -82, -104, -52, 37, -125, -59, 100, 10, 5, 84, 30, -90, -89, -23, 101, 80, -101, 62, 7, -91, 65, -18, 48, -56, 24, 30, -105, -59, -17, -9, -70, -82, 3, -35, -17, 119, -105, 57, 60, 46, -117, -33, -17, -75, 107, -4, 110, -65, -46, -19, 48, -67, 12, -105, -89, -43, 97, 122, -103, -83, 46, 43, 0});
		mtsOneNumberMultipleCallsNotificationSmsBusy = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * 1/2
		 * Propusteni pozivi 6:
		 * 2: 0604749959
		 * 22:47h 29.12.2012
		 *  0642016105
		 * 22:47h 29.12.2012
		 *  0112623606
		 * 22:46h 29.12.2012
		 *  0621654066
		 * 22:46h 29.12.2012
		 * 
		 *             
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 34, -124, 36, 64, -96, -79, -105, 76, 1, -107, -65, -31, -11, 57, -67, -20, 78, -125, -32, 111, 125, -38, -98, 6, -39, 116, 10, -103, 14, 4, -77, -63, 104, 55, 90, 46, 87, -53, 41, 100, 50, 29, -19, -122, 6, -55, 114, -82, -104, -52, 37, -125, -59, 100, 10, 5, 8, 102, -93, -55, 96, 49, 91, 12, 86, 83, -56, 100, 58, -38, 13, 13, -110, -27, 92, 49, -103, 75, 6, -117, -55, 20, 10, 16, 44, 22, -109, -39, 100, 51, 27, -52, -90, -112, -55, 116, 52, 27, 26, 36, -53, -71, 98, 50, -105, 12, 22, -109, 41, 20, 32, -104, 77, 22, -77, -43, 104, 48, -101, 77, 33, -109, -23, 104, 54, 52, 72, -106, 115, -59, 100, 46, 25, 44, 38, 83, 40, 64, 32, 16, 8, 4, 2, -127, 64});
		mtsMultipleNumbersMultipleCallsPart1NotificationSmsNotAvailable = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * 2/2
		 *  0637083620
		 * 22:45h 29.12.2012
		 * Pratite mts i na Facebooku: www.facebook.com/imateprijatelje
		 * 
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 34, -124, 68, 64, 96, -78, -105, 76, 1, -126, -39, 102, 55, 24, 110, 102, -109, -63, 20, 50, -103, -114, 86, 67, -125, 100, 57, 87, 76, -26, -110, -63, 98, 50, -123, 2, 42, 15, -45, -45, -12, 50, -88, 77, -97, -125, -46, 32, 119, 24, 100, 12, -113, -53, -30, -9, 123, 93, -41, -127, -18, -9, -69, -53, 28, 30, -105, -59, -17, -9, -38, 53, 126, -73, 95, -23, 118, -104, 94, -122, -53, -45, -22, 48, -67, -52, 86, -105, 21});
		mtsMultipleNumbersMultipleCallsPart2NotificationSmsNotAvailable = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * 1/2
		 * Bili ste zauzeti.Propusteni pozivi 6:
		 * 2: +381637083620
		 * 22:58h 29.12.2012
		 *  0112623606
		 * 22:58h 29.12.2012
		 *  +381604749959
		 * 22:57h 29.12.2012
		 *             
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 50, 0, 96, 64, -96, -79, -105, 76, 33, 76, -77, -45, -96, 57, -67, 12, -46, -121, -21, -6, 50, 61, -19, -126, -54, -33, -16, -6, -100, 94, 118, -89, 65, -16, -73, 62, 109, 79, -125, 108, 58, -123, 76, 7, 90, -51, 112, 49, -37, -20, 6, -61, -51, 108, 50, -104, 66, 38, -45, -43, 112, 104, -112, 44, -25, -118, -55, 92, 50, 88, 76, -90, 80, -128, 96, -79, -104, -52, 38, -101, -39, 96, 54, -123, 76, -90, -85, -31, -48, 32, 89, -50, 21, -109, -71, 100, -80, -104, 76, -95, 0, -83, 102, -72, -104, 13, 70, -69, -47, 114, -71, 90, 78, 33, -109, -23, 106, 55, 52, 72, -106, 115, -59, 100, 46, 25, 44, 38, 83, 40, 64, 32, 16, 8, 4, 2, -127, 64, 32, 16, 8, 4, 2, -127, 64});
		mtsMultipleNumbersMultipleCallsPart1NotificationSmsBusy = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * 2/2
		 *  +381642016105
		 * 22:57h 29.12.2012
		 *  +381621654066
		 * 22:57h 29.12.2012
		 * Pratite mts i na Facebooku: www.facebook.com/imateprijatelje
		 * 
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 50, 0, -128, 64, -123, -78, -105, 76, 1, 90, -51, 112, 49, 27, 77, 6, -117, -39, 98, -80, -102, 66, 38, -45, -43, 110, 104, -112, 44, -25, -118, -55, 92, 50, 88, 76, -90, 80, -128, 86, 51, 92, -52, 38, -117, -39, 106, 52, -104, -51, -90, -112, -55, 116, -75, 27, 26, 36, -53, -71, 98, 50, -105, 12, 22, -109, 41, 20, 80, 121, -104, -98, -90, -105, 65, 109, -6, 28, -108, 6, -71, -61, 32, 99, 120, 92, 22, -65, -33, -21, -70, 14, 116, -65, -33, 93, -26, -16, -72, 44, 126, -65, -41, -82, -15, -69, -3, 74, -73, -61, -12, 50, 92, -98, 86, -121, -23, 101, -74, -70, -84, 0});
		mtsMultipleNumbersMultipleCallsPart2NotificationSmsBusy = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * Korisnik ciji je broj 0644755281 je ponovo dostupan.
		 */
		sms = SmsMessage.createFromPdu(new byte[] {7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 33, 65, 66, 64, 52, -53, -73, 60, 61, 119, -89, -41, -96, 113, 90, -99, 6, -87, -53, 32, -79, -4, -83, 6, -63, 108, 52, -38, -83, 86, -109, -31, 98, 32, 117, 25, 4, 127, -69, -33, -10, 55, -120, -4, -98, -45, -21, -16, -80, -37, 5});
		mtsAvailableAgainNotificationSms = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * Korisnik ciji je broj +381644755281 vise nije zauzet, mozete ponovo da ga pozovete.
		 */
		sms = SmsMessage.createFromPdu(new byte[]{7, -111, -125, 97, 5, 0, -112, 64, 4, 6, -48, 109, -6, 28, 0, 0, 33, 33, -110, 33, -111, 65, 64, 83, -53, -73, 60, 61, 119, -89, -41, -96, 113, 90, -99, 6, -87, -53, 32, -79, -4, -83, 6, -83, 102, -72, -104, -115, 70, -69, -43, 106, 50, 92, 12, 100, 79, -49, -53, 32, 119, 90, 93, 6, -23, -61, 117, 125, -103, -50, 2, -75, -33, -6, 50, -67, 12, -126, -65, -35, 111, -5, 27, 68, 14, -125, -50, 97, 16, -4, -83, 127, -37, -53, -12, -78, 11});
		mtsNotBusyAnymoreMotificationSms = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		
		
		
		
		
		/*
		 * serviceNumber: 0629124632
		 * 
		 * Korisnik ciji je broj +381629124632 vise nije zauzet, mozete ponovo da ga pozovete.
		 */
		sms = SmsMessage.createFromPdu(new byte[]{0, 32, 10, -127, 96, -110, 33, 100, 35, 0, 0, 17, 64, 48, 2, 117, 37, -128, 83, -53, -73, 60, 61, 119, -89, -41, -96, 113, 90, -99, 6, -87, -53, 32, -79, -4, -83, 6, -83, 102, -72, -104, 77, -106, -117, -55, 104, -74, -103, 12, 100, 79, -49, -53, 32, 119, 90, 93, 6, -23, -61, 117, 125, -103, -50, 2, -75, -33, -6, 50, -67, 12, -126, -65, -35, 111, -5, 27, 68, 14, -125, -50, 97, 16, -4, -83, 127, -37, -53, -12, -78, 11});
		randomSms1 = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 0629124632
		 * 
		 * Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt 
		 * ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation 
		 * ullamco laboris nisi ut aliquip ex ea commodo consequat. 
		 */
		sms = SmsMessage.createFromPdu(new byte[]{0, 96, 10, -127, 96, -110, 33, 100, 35, 0, 0, 17, 64, 48, 18, 112, 1, -128, -95, 5, 0, 3, 22, 2, 1, -104, 111, 121, -71, 13, 74, -61, -25, -11, 54, -120, -4, 102, -65, -27, -96, 121, -102, 14, 10, -73, -53, 116, 22, 104, -4, 118, -49, -53, 99, 122, -103, 94, -105, -125, -62, -28, 52, 60, 61, 79, -113, -45, -18, 51, -88, -52, 78, -45, 89, -96, 121, -103, 12, 34, -65, 65, -27, 116, 125, -34, 126, -109, 65, -12, 114, 27, -2, -106, -125, -46, -18, 113, -102, -100, 38, -41, -35, 116, 80, -99, 14, 98, -121, -59, 111, 121, 25, 84, -90, -125, -56, 111, -10, 91, 94, 6, -75, -61, 103, 119, 24, 20, 102, -89, -29, -11, -80, 11, 84, -91, -125, -54, -18, 116, 27, 20, 38, -125, -38, 105, 119, -70, 13, -78, -105, -35, -23, 112, -101, 5, -118, -41, -45, 115});
		randomSms2 = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 123123
		 * 
		 * Udruzimo se u podrsci narodu Japana, 
		 * pokazimo da nismo zaboravili njihovu nesebicnu pomoc proteklih godina.
		 */
		sms = SmsMessage.createFromPdu(new byte[]{0, 32, 6, -127, 33, 19, 50, 0, 0, 17, 64, 48, 18, 48, 17, -128, 107, 85, -78, -68, -82, 79, -73, -33, -96, 121, 25, 84, 7, -63, -33, 100, -7, 124, -100, 6, -71, -61, -14, 55, -71, 14, 82, -122, -31, 97, 119, -104, 5, -126, -65, -41, 97, 125, -70, -3, 6, -111, -61, 32, 119, 122, -34, 126, -125, -12, 97, -15, 91, 30, -74, -89, -39, 105, -112, 91, -99, 70, -65, -19, 117, -112, -69, 60, 47, -117, -45, 99, 119, 29, 4, 127, -73, -33, 99, 16, 92, -2, -90, -105, -41, -20, 52, 26, 116, 126, -109, -45, -18, -80, 11});
		randomSms3 = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
		
		/*
		 * serviceNumber: 456
		 * 
		 * Dopunite kredit! Pozovite sa fiksnog tel.0800 100 900 i dobijate 10% vise!
		 */
		sms = SmsMessage.createFromPdu(new byte[]{0, 32, 3, -127, 84, -10, 0, 0, 17, 64, 48, 2, -107, -107, -128, 74, -60, 55, -68, -18, 78, -45, -53, -96, -75, -68, 76, 78, -45, 67, 32, -24, 91, -1, -74, -89, -23, 101, -48, 60, 12, 50, -89, -41, 115, -9, -5, 12, -94, -105, -39, 46, 24, 14, 6, 3, -59, 96, 48, 80, 14, 6, 3, -91, 65, -28, -73, 56, -83, 14, -45, -53, -96, 24, -84, 4, -78, -89, -25, -27, 16});
		randomSms4 = new NotificationMessage(sms.getOriginatingAddress(), sms.getMessageBody());
	}
	
}
