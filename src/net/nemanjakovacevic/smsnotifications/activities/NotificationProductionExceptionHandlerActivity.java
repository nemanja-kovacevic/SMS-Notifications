package net.nemanjakovacevic.smsnotifications.activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.dao.NotificationProductionExceptionDAO;
import net.nemanjakovacevic.smsnotifications.dao.NotificationsDAO;
import net.nemanjakovacevic.smsnotifications.dao.RuleSetDAO;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.domain.RuleSet;
import net.nemanjakovacevic.smsnotifications.exceptions.InternetConnenctionException;
import net.nemanjakovacevic.smsnotifications.exceptions.NotNotificationException;
import net.nemanjakovacevic.smsnotifications.exceptions.NotificationProductionException;
import net.nemanjakovacevic.smsnotifications.producers.NotificationProducingEngine;
import net.nemanjakovacevic.smsnotifications.receivers.SmsInterceptor;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Activity to be called on notification production exception.
 * 
 * @author nemanjak
 * 
 */
public class NotificationProductionExceptionHandlerActivity extends Activity implements Keys {

	private final static int DIALOG_CHECK_FOR_UPDATES = 1;
	private final static int DIALOG_REPORT_ISSUE = 2;
	private final static int DIALOG_REPORT_PERSISTING_ISSUE = 3;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		
		showDialog(DIALOG_CHECK_FOR_UPDATES);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CHECK_FOR_UPDATES:
			return createCheckForUpdatesDialog();
		case DIALOG_REPORT_ISSUE:
		case DIALOG_REPORT_PERSISTING_ISSUE:
			return createReportIssueDialog(id);
		default:
			return null;
		}
	}

	private Dialog createReportIssueDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name);
		if (id == DIALOG_REPORT_ISSUE) {
			builder.setMessage(R.string.report_issue);
		} else if (id == DIALOG_REPORT_PERSISTING_ISSUE) {
			builder.setMessage(R.string.report_persisting_issue);
		}
		builder.setPositiveButton(this.getString(R.string.yes), new SubmitErrorReport(this));
		builder.setNegativeButton(this.getString(R.string.no), new DontSubmitErrorReport(this));
		return builder.create();
	}

	private Dialog createCheckForUpdatesDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name);
		builder.setMessage(R.string.notification_production_exception);
		builder.setPositiveButton(this.getString(R.string.yes), new CheckForUpdates(this));
		builder.setNegativeButton(this.getString(R.string.no), new DontCheckForUpdates());
		return builder.create();
	}

	private class CheckForUpdates implements OnClickListener {

		private Activity activity;

		public CheckForUpdates(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			progressDialog = ProgressDialog.show(activity, null, activity.getString(R.string.checking_for_updates));
			new AsyncTask<Void, Void, Result>() {

				@Override
				protected Result doInBackground(Void... params) {
					List<NotificationProductionException> npes = NotificationProductionExceptionDAO.loadActiveExceptions(activity);
					Result result = new Result();
					for(NotificationProductionException npe : npes){
						try{
							String countryCode = npe.getRulesCountryCode();
							RuleSet ruleSet = RuleSetDAO.getMergedRuleSetForCountryCode(activity, countryCode);
							if (ruleSet.isUpdateable()) {
								RuleSetDAO.updateRuleSet(activity, ruleSet);
								NotificationProducingEngine engine = NotificationProducingEngine.getInstance(activity);
								try {
									List<OperatorNotification> notifications = engine.produceNotifications(npe.getNotificationMessage());
									NotificationsDAO.storeNewNotifications(notifications, activity);
									NotificationProductionExceptionDAO.resolved(activity, npe);
									result.setFlagNewNotifiations(true);
								} catch (NotNotificationException e) {
									result.setFlagErrorPersists(true);
								} catch (NotificationProductionException npe2) {
									NotificationProductionExceptionDAO.storeNotificationProductionException(activity, npe2);
									result.setFlagErrorPersists(true);
								}
							} else {
								result.setFlagErrorPersists(true);
							}
						}catch(InternetConnenctionException ex){
							result.setException(ex);
						}
					}
					return result;
				}

				@Override
				protected void onPostExecute(Result result) {
					progressDialog.cancel();
					
					if(result.getException() != null){
						result.setFlagNewNotifiations(false);
						result.setFlagErrorPersists(true);
					}
					
					if (result.getFlagNewNotifiations()) {
						SmsInterceptor.broadcastNotifications(activity);
					} 
					
					if (result.getFlagErrorPersists()) {
						showDialog(DIALOG_REPORT_PERSISTING_ISSUE);
					}else{
						NotificationProductionExceptionHandlerActivity.this.finish();
					}
				};

			}.execute();
		}
		
		private class Result {
			
			private boolean flagNewNotifiations;
			private boolean flagErrorPersists;
			
			private InternetConnenctionException exception;
			
			public void setFlagNewNotifiations(boolean flagNewNotifiations) {
				this.flagNewNotifiations = flagNewNotifiations;
			}
			
			public void setException(InternetConnenctionException exception) {
				this.exception = exception;
			}
			
			public InternetConnenctionException getException() {
				return exception;
			}

			public void setFlagErrorPersists(boolean flagErrorPersists) {
				this.flagErrorPersists = flagErrorPersists;
			}
			
			public boolean getFlagNewNotifiations() {
				return flagNewNotifiations;
			}
			
			public boolean getFlagErrorPersists() {
				return flagErrorPersists;
			}
			
		}

	}

	private class DontCheckForUpdates implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			showDialog(DIALOG_REPORT_ISSUE);
		}

	}

	private class SubmitErrorReport implements OnClickListener {

		private Activity activity;

		public SubmitErrorReport(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			String[] recipients = new String[] { EMAIL_ERROR_REPORT, "", };
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "ERROR");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getEmail());
			emailIntent.setType("text/plain");

			try {
				startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.send_mail)));
				NotificationProductionExceptionDAO.reported(activity);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(activity, activity.getString(R.string.no_email_client_installed), Toast.LENGTH_SHORT).show();
			}

			finish();
		}

		private String getEmail() {
			List<NotificationProductionException> npes = NotificationProductionExceptionDAO.loadActiveExceptions(activity);
			StringBuilder sb = new StringBuilder();
			for(NotificationProductionException npe : npes){
				sb.append("SMS Notification error report");
				sb.append("\n\n");
				sb.append("Timestamp : " + new SimpleDateFormat().format(new Date()));
				sb.append("\n\n");
				sb.append("Sender : " + npe.getNotificationMessage().getSender());
				sb.append("\n\n");
				sb.append("Message : " + npe.getNotificationMessage().getBody());
				sb.append("\n\n");
				sb.append("Rules country code : " + npe.getRulesCountryCode());
				sb.append("\n\n");
				sb.append("Rules version : " + npe.getRulesVersion());
				sb.append("\n\n");
				sb.append("\n\n");
				sb.append("\n\n");
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				npe.printStackTrace(pw);
				sb.append("Notification production exception stack trace : " + sw.toString());
				sb.append("\n\n");
				sb.append("\n\n");
			}
			return sb.toString();
		}

	}

	private class DontSubmitErrorReport implements OnClickListener {

		private Activity activity;

		public DontSubmitErrorReport(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			activity.finish();
		}

	}

}
