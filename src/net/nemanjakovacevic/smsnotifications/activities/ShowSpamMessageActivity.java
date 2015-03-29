package net.nemanjakovacevic.smsnotifications.activities;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.dao.SpamDAO;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowSpamMessageActivity extends Activity implements Keys {

	private int spamMessageId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_spam_message_layout);
		
		this.spamMessageId = getIntent().getIntExtra(INTENT_KEY_SPAM_MESSAGE_ID, 0);
		String sender = getIntent().getStringExtra(INTENT_KEY_SPAM_MESSAGE_SENDER);
		String dateTime = getIntent().getStringExtra(INTENT_KEY_SPAM_MESSAGE_DATE_TIME);
		String body = getIntent().getStringExtra(INTENT_KEY_SPAM_MESSAGE_BODY);

		String senderLabel = getResources().getString(R.string.from);
		
		TextView senderView = (TextView) findViewById(R.id.spam_message_sender);
		senderView.setText(senderLabel + ": " + sender);
		TextView dateTimeView = (TextView) findViewById(R.id.spam_message_timestamp);
		dateTimeView.setText(dateTime);
		TextView bodyView = (TextView) findViewById(R.id.spam_message_body);
		bodyView.setText(body);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.show_spam_message_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.list_spam_message_menu_item_delete_spam_message:
			SpamDAO.deleteSpamMessage(spamMessageId, this);
			Intent data = new Intent();
			data.putExtra(INTENT_KEY_SPAM_MESSAGE_ID, spamMessageId);
			setResult(RESULT_DELETE_SPAM_MESSAGE, data);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
