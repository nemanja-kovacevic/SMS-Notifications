package net.nemanjakovacevic.smsnotifications.activities;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.dao.PreferenceDAO;
import net.nemanjakovacevic.smsnotifications.dao.RuleSetDAO;
import net.nemanjakovacevic.smsnotifications.exceptions.CountryCodeNotSupportedException;
import net.nemanjakovacevic.smsnotifications.exceptions.InternetConnenctionException;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import net.nemanjakovacevic.smsnotifications.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;

public class InitializeAppActivity extends Activity implements Keys {

	public static final int DIALOG_POSSIBLE_APP_COLLISION = 1;
	public static final int DIALOG_INFO = 2;
	
	private String infoMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(DIALOG_POSSIBLE_APP_COLLISION);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
			case DIALOG_POSSIBLE_APP_COLLISION : return createDialogPossibleAppCollision();
			case DIALOG_INFO : return createInfoDialog();
			default : return null;
		}
	}

	private Dialog createInfoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name);
		builder.setMessage(infoMessage);
		builder.setPositiveButton(this.getString(R.string.ok), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PreferenceDAO.userAcknowledgedLackOfRules(InitializeAppActivity.this);
				InitializeAppActivity.this.finish();
			}
		});
		return builder.create();
	}

	private Dialog createDialogPossibleAppCollision() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name);
		builder.setMessage(R.string.possible_colision_with_sms_app);
		builder.setPositiveButton(this.getString(R.string.ok), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				asyncInstallDefaultRules();
			}

		});
		return builder.create();
	}
	
	private void asyncInstallDefaultRules() {
		if(!PreferenceDAO.areRulesInitialyInstalled(this) && !PreferenceDAO.hasUserAcknowledgedLackOfRules(this)){
			final ProgressDialog progressDialog = ProgressDialog.show(this, null, this.getString(R.string.installing_default_rules, Util.getCountryNameByOperator(this)));
			
			new AsyncTask<Void, Void, Exception>() {

				private Context context = InitializeAppActivity.this;
				
				@Override
				protected Exception doInBackground(Void... params) {
					try {
						RuleSetDAO.installDefaultRuleSet(context);
						return null;
					} catch (InternetConnenctionException ex) {
						return ex;
					}catch(CountryCodeNotSupportedException ex){
						return ex;
					}
				}
				
				@Override
				protected void onPostExecute(Exception ex) {
					progressDialog.dismiss();
					if(ex == null){
						InitializeAppActivity.this.finish();
					}else{
						if(ex instanceof InternetConnenctionException){
							infoMessage = context.getString(R.string.default_rules_install_internet_connection_problem);
						}else if(ex instanceof CountryCodeNotSupportedException){
							infoMessage = context.getString(R.string.country_code_not_supported_problem, ((CountryCodeNotSupportedException)ex).getCountryCode());
						}
						showDialog(DIALOG_INFO);
					}
				};
				
			}.execute();
		}else{
			InitializeAppActivity.this.finish();
		}
	}
	
}
