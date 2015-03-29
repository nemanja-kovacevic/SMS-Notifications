package net.nemanjakovacevic.smsnotifications.activities;

import java.util.List;

import com.flurry.android.FlurryAgent;

import net.nemanjakovacevic.smsnotifications.App;
import net.nemanjakovacevic.smsnotifications.App.NewNotificationsListener;
import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.activities.adapters.OperatorNotificationAdapter;
import net.nemanjakovacevic.smsnotifications.activities.adapters.OperatorNotificationAdapter.Hint;
import net.nemanjakovacevic.smsnotifications.dao.NotificationsDAO;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.util.Keys;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListOperatorsNotificationsActivity extends ListActivity implements NewNotificationsListener, Keys {

	private OperatorNotificationAdapter adapter;
	
	private static int CURRENT_MODE = 0;

	private int ADD_CONTACT = 0;
	
	private static String NUMBER_BEEING_ADDED_CACHE;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		App app = (App) getApplication();
		app.setNewNotificationsListener(this);
		
		CURRENT_MODE = getIntent().getIntExtra(MODE, 0);
		showNotifications();
 	}
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, FLURRY_API_KEY);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	private void showNotifications(){
		final ProgressDialog progressDialog = ProgressDialog.show(this, null, this.getString(R.string.fetching_notifications));
		
		new AsyncTask<Void, Void, List<OperatorNotification>>(){

			Context context = ListOperatorsNotificationsActivity.this;
			
			@Override
			protected List<OperatorNotification> doInBackground(Void... params) {
				List<OperatorNotification> notifications = null;
				if (CURRENT_MODE == NEW_NOTIFICATIONS) {
					notifications = NotificationsDAO.getNewNotifications(context);
				} else if (CURRENT_MODE == ALL_NOTIFICATIONS) {
					notifications = NotificationsDAO.getAllNotifications(context);
				}
				NotificationsDAO.clear(context);
				return notifications;
			}
			
			@Override
			protected void onPostExecute(List<OperatorNotification> notifications) {
				Hint hint = CURRENT_MODE == NEW_NOTIFICATIONS ? Hint.SHOWING_NEW_NOTIFICATIONS : Hint.SHOWING_ALL_NOTIFICATIONS;
				if(notifications.isEmpty()){
					if(CURRENT_MODE == ALL_NOTIFICATIONS){
						setContentView(R.layout.empty_notification_list);
					}
				}else{
					adapter = new OperatorNotificationAdapter(context, notifications, hint);
					attachListAdapter();
				}
				progressDialog.dismiss();
				if(!App.isInitialized(context)){
					App.initialize(context);
				}
			};
			
		}.execute();
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_notifications_context_menu, menu);
		
		if (CURRENT_MODE == NEW_NOTIFICATIONS) {
			menu.findItem(R.id.list_notifications_context_menu_item_delete_notification).setVisible(false);
		} else {
			menu.findItem(R.id.list_notifications_context_menu_item_delete_notification).setVisible(true);
		}
		
		AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterContextMenuInfo) menuInfo;
		int position = adapterContextMenuInfo.position;
		OperatorNotificationAdapter adapter = (OperatorNotificationAdapter) getListAdapter();
		if(adapter.isNotificationOfUnknownNumber(position)){
			menu.findItem(R.id.list_notifications_context_menu_item_add_to_contacts).setVisible(true);
		} else {
			menu.findItem(R.id.list_notifications_context_menu_item_add_to_contacts).setVisible(false);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int selectedPostion = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
		OperatorNotification notification = (OperatorNotification) getListAdapter().getItem(selectedPostion);
		switch (item.getItemId()) {
		case R.id.list_notifications_context_menu_item_call:
			intentCall(notification.getNumber());
			return true;
		case R.id.list_notifications_context_menu_item_sms:
			intentSms(notification.getNumber());
			return true;
		case R.id.list_notifications_context_menu_item_delete_notification:
			deleteNotification(notification);
			return true;
		case R.id.list_notifications_context_menu_item_add_to_contacts:
			intentAddToContacts(notification.getNumber());
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void deleteNotification(OperatorNotification notification) {
		NotificationsDAO.deleteNotification(notification.getId(), this);
		adapter.remove(notification);
		if (adapter.isEmpty()) {
			setContentView(R.layout.empty_notification_list);
		}
	}

	private void intentCall(String number) {
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
		startActivity(callIntent);
	}

	private void intentSms(String number) {
		Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + number));
		startActivity(smsIntent);
	}
	
	private void intentAddToContacts(String number) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

		intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
		NUMBER_BEEING_ADDED_CACHE = number;
		
		startActivityForResult(intent, ADD_CONTACT);
	}

	@Override
	public void newNotifications() {
		CURRENT_MODE = NEW_NOTIFICATIONS;
		showNotifications();
	}
	
	private void attachListAdapter(){
		setListAdapter(adapter);
		ListView lv = getListView();
		registerForContextMenu(lv);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long id) {
				Object item = adapterView.getAdapter().getItem(position);
				if (item instanceof OperatorNotificationAdapter.LoadAllAuxiliaryItem) {
					CURRENT_MODE = ALL_NOTIFICATIONS;
					showNotifications();
				} else if (item instanceof OperatorNotification) {
					OperatorNotification notification = (OperatorNotification) item;
					intentCall(notification.getNumber());
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.list_operators_notifications_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.list_operator_notifications_menu_item_delete_all:
			NotificationsDAO.deleteAllNotifications(this);
			setListAdapter(null);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == ADD_CONTACT){
			if(resultCode == RESULT_OK){
	            Uri contactUri = data.getData();
	            // user could have edited the number in contacts up, this is original
	            String numberToStartWith = NUMBER_BEEING_ADDED_CACHE;
	            NotificationsDAO.updateContactData(contactUri, numberToStartWith, this);
	            CURRENT_MODE = ALL_NOTIFICATIONS;
	    		showNotifications();
			}
		}
	}
	
}