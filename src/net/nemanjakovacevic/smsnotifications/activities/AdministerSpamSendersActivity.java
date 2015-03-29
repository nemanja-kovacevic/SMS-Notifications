package net.nemanjakovacevic.smsnotifications.activities;

import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import net.nemanjakovacevic.smsnotifications.util.Util;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

public class AdministerSpamSendersActivity extends ListActivity implements Keys {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.administer_spam_senders_activity_layout);
		String rawSpamSenders = PreferenceManager.getDefaultSharedPreferences(this).getString(KEY_SPAM_SENDERS, "");
		List<String> spamSenders = Util.getSpamSenders(rawSpamSenders);
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_spam_sender_item, spamSenders));
		
		Button addButton = (Button) findViewById(R.id.spam_sender_add_button);
		addButton.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				EditText spamSenderEditText = (EditText) findViewById(R.id.spam_sender_edit_text);
				String spamSender = spamSenderEditText.getText().toString();
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AdministerSpamSendersActivity.this);
				String rawSpamSenders = preferences.getString(KEY_SPAM_SENDERS, "");
				rawSpamSenders = rawSpamSenders + spamSender + ";";
				Editor editor = preferences.edit();
				editor.putString(KEY_SPAM_SENDERS, rawSpamSenders);
				editor.commit();
				System.out.println(rawSpamSenders);
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
				adapter.add(spamSender);
				adapter.notifyDataSetChanged();
				spamSenderEditText.setText(null);
			}
		});
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parrent, View view, int position, long id) {
				String spamSenderToRemove = (String) getListAdapter().getItem(position);
				String forRemoval = spamSenderToRemove + ';';
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AdministerSpamSendersActivity.this);
				String rawSpamSenders = preferences.getString(KEY_SPAM_SENDERS, "");
				StringBuilder sb = new StringBuilder(rawSpamSenders);
				int start = sb.indexOf(forRemoval);
				sb.delete(start, start+forRemoval.length());
				Editor editor = preferences.edit();
				editor.putString(KEY_SPAM_SENDERS, sb.toString());
				editor.commit();
				System.out.println(sb.toString());
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) getListAdapter();
				adapter.remove(spamSenderToRemove);
				adapter.notifyDataSetChanged();
			}
			
		});
	}

	
}
