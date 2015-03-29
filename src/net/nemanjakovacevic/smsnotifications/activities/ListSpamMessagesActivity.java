package net.nemanjakovacevic.smsnotifications.activities;

import java.text.SimpleDateFormat;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.activities.adapters.SpamMessageAdapter;
import net.nemanjakovacevic.smsnotifications.dao.SpamDAO;
import net.nemanjakovacevic.smsnotifications.domain.SpamMessage;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListSpamMessagesActivity extends ListActivity implements Keys {

	private SpamMessageAdapter adapter;
	private final static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<SpamMessage> allSpamMessages = SpamDAO.getAllSpamMessages(this);
		if (allSpamMessages.isEmpty()) {
			setContentView(R.layout.empty_spam_messages_list);
		} else {
			adapter = new SpamMessageAdapter(this, allSpamMessages);
			setListAdapter(adapter);
		}

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
				SpamMessage spamMessage = (SpamMessage) adapterView.getAdapter().getItem(position);
				Intent intent = new Intent(ListSpamMessagesActivity.this, ShowSpamMessageActivity.class);
				intent.putExtra(INTENT_KEY_SPAM_MESSAGE_ID, spamMessage.getId());
				intent.putExtra(INTENT_KEY_SPAM_MESSAGE_SENDER, spamMessage.getSender());
				intent.putExtra(INTENT_KEY_SPAM_MESSAGE_DATE_TIME, FORMATTER.format(spamMessage.getTimestamp()));
				intent.putExtra(INTENT_KEY_SPAM_MESSAGE_BODY, spamMessage.getBody());
				startActivityForResult(intent, REQUEST_SHOW_SPAM_MESSAGE);
			}
		});
		registerForContextMenu(lv);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_DELETE_SPAM_MESSAGE){
			int spamMessageId = data.getIntExtra(INTENT_KEY_SPAM_MESSAGE_ID, 0);
			SpamDAO.deleteSpamMessage(spamMessageId, this);
			adapter.remove(new SpamMessage(spamMessageId, null, null, null));
			if(adapter.isEmpty()){
				setContentView(R.layout.empty_spam_messages_list);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_spam_messages_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int selectedPostion = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		SpamMessage spamMessage = (SpamMessage) getListAdapter().getItem(selectedPostion);
		switch (item.getItemId()) {
		case R.id.list_spam_messages_context_menu_item_delete_spam_message:
			SpamDAO.deleteSpamMessage(spamMessage.getId(), this);
			adapter.remove(spamMessage);
			if(adapter.isEmpty()){
				setContentView(R.layout.empty_spam_messages_list);
			}
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
