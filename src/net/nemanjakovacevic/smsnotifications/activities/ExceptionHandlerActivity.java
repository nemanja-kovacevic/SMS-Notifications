package net.nemanjakovacevic.smsnotifications.activities;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.dao.PreferenceDAO;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import net.nemanjakovacevic.smsnotifications.util.Util.MessageType;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ExceptionHandlerActivity extends Activity implements Keys {

	private String message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		message = getIntent().getStringExtra(MESSAGE);
		int messageType = getIntent().getIntExtra(MESSAGE_TYPE, 0);
		if(messageType == MessageType.INTERNET_CONECTION_PROBLEM.getCode()){
			PreferenceDAO.userAcknowledgedLackOfRules(this);
		}
		
		showDialog(1);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name);
		builder.setMessage(message);
		builder.setPositiveButton(this.getString(R.string.ok), new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ExceptionHandlerActivity.this.finish();
			}
		});
		return builder.create();
	}
	
}
