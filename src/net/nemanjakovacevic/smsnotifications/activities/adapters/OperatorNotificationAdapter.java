package net.nemanjakovacevic.smsnotifications.activities.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.domain.OperatorNotification;
import net.nemanjakovacevic.smsnotifications.util.Util;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class OperatorNotificationAdapter extends BaseAdapter {

	private final static SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm");
	private final static SimpleDateFormat SEPARATOR_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

	private LayoutInflater mInflater;
	private List<Object> items;
	private Bitmap defaultContact;

	private Date todayMidnight;
	private Date yesterdayMidnight;

	public OperatorNotificationAdapter(Context context, List<OperatorNotification> notifications, Hint hint) {
		this.items = createItems(notifications, hint);
		this.mInflater = LayoutInflater.from(context);
		this.defaultContact = BitmapFactory.decodeResource(context.getResources(), R.drawable.contact);

		Date now = new Date();
		this.todayMidnight = Util.trim(now);
		this.yesterdayMidnight = Util.trim(new Date(now.getTime() - 24 * 60 * 60 * 1000));
	}

	private List<Object> createItems(List<OperatorNotification> notifications, Hint hint) {
		if (notifications == null) {
			return null;
		}
		List<Object> items = new ArrayList<Object>();
		List<DateSeparatorAuxiliaryItem> addedDateSeparators = new ArrayList<DateSeparatorAuxiliaryItem>();
		for (int i = 0; i < notifications.size(); i++) {
			OperatorNotification on = notifications.get(i);
			DateSeparatorAuxiliaryItem dateSeparator = getDateSeparatorForNotification(on);
			if (!addedDateSeparators.contains(dateSeparator)) {
				addedDateSeparators.add(dateSeparator);
				items.add(dateSeparator);
			}
			items.add(on);
		}
		if (hint == Hint.SHOWING_NEW_NOTIFICATIONS) {
			items.add(new LoadAllAuxiliaryItem());
		}
		return items;
	}

	private DateSeparatorAuxiliaryItem getDateSeparatorForNotification(OperatorNotification on) {
		return new DateSeparatorAuxiliaryItem(Util.trim(on.getTimestamp()));
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object item = getItem(position);
		if (item instanceof OperatorNotification) {
			ViewHolder holder;
			if (convertView == null) {
				holder = null;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (holder == null) {
				convertView = mInflater.inflate(R.layout.list_notifications_item, null);
				holder = new ViewHolder();
				holder.notificationIcon = (ImageView) convertView
						.findViewById(R.id.list_notifications_item_type_icon);
				holder.contactBadge = (QuickContactBadge) convertView
						.findViewById(R.id.list_notifications_item_person_icon);
				holder.contactInfoPrimary = (TextView) convertView
						.findViewById(R.id.list_notifications_item_contact_info_primary);
				holder.contactInfoSecondary = (TextView) convertView
						.findViewById(R.id.list_notifications_item_contact_info_secondary);
				holder.numberOfCalls = (TextView) convertView
						.findViewById(R.id.list_notifications_item_number_of_calls);
				holder.time = (TextView) convertView.findViewById(R.id.list_notifications_item_time);
				convertView.setTag(holder);
			}

			OperatorNotification notification = (OperatorNotification) getItem(position);

			if (notification.getContact() != null) {
				Uri contactURI = ContentUris.withAppendedId(Contacts.CONTENT_URI, notification.getContact().getId());
				holder.contactBadge.assignContactUri(contactURI);
				if(notification.getContact().getPhoto() != null){
					Bitmap scaledPhoto = Bitmap.createScaledBitmap(notification.getContact().getPhoto(), defaultContact
							.getWidth(), defaultContact.getHeight(), false);
					holder.contactBadge.setImageBitmap(scaledPhoto);
				} else {
					holder.contactBadge.setImageBitmap(defaultContact);
				}
			} else {
				holder.contactBadge.setImageBitmap(defaultContact);
			}


			switch (notification.getType()) {
			case AVAILABILITY:
				holder.notificationIcon.setBackgroundResource(R.drawable.availability_standard);
				holder.numberOfCalls.setText("");
				break;
			case MISSED_CALL:
				holder.notificationIcon.setBackgroundResource(R.drawable.missed_call_standard);
				holder.numberOfCalls.setText("(" + notification.getNumberOfCalls() + ")");
				break;
			}

			if (notification.getContact() != null) {
				holder.contactInfoPrimary.setText(notification.getContact().getName());
				holder.contactInfoSecondary.setText(notification.getNumber());
			} else {
				holder.contactInfoPrimary.setText(notification.getNumber());
				holder.contactInfoSecondary.setText(R.string.contact_unknown);
			}
			holder.time.setText(FORMATTER.format(notification.getTimestamp()));
		} else if (item instanceof LoadAllAuxiliaryItem) {
			convertView = mInflater.inflate(R.layout.list_notifications_load_all_item, null);
		} else if (item instanceof DateSeparatorAuxiliaryItem) {
			convertView = mInflater.inflate(R.layout.list_date_separator_item, null);
			TextView separatorView = (TextView) convertView.findViewById(R.id.list_notifications_date_separator);
			DateSeparatorAuxiliaryItem dateSeparator = (DateSeparatorAuxiliaryItem) item;
			if (dateSeparator.getDate().equals(todayMidnight)) {
				separatorView.setText(R.string.today);
			} else if (dateSeparator.getDate().equals(yesterdayMidnight)) {
				separatorView.setText(R.string.yesterday);
			} else {
				separatorView.setText(SEPARATOR_FORMATTER.format(dateSeparator.getDate()));
			}
		}
		return convertView;
	}
	
	static class ViewHolder {
		ImageView notificationIcon;
		QuickContactBadge contactBadge;
		TextView contactInfoPrimary;
		TextView contactInfoSecondary;
		TextView numberOfCalls;
		TextView time;
	}

	public static class LoadAllAuxiliaryItem {

	}

	public static enum Hint {
		SHOWING_NEW_NOTIFICATIONS, SHOWING_ALL_NOTIFICATIONS
	}

	public void remove(OperatorNotification notification) {
		int index = items.indexOf(notification);
		// if this notification is last in list and only in date group
		// remove both notification and date separator
		if(index == items.size()-1 && items.get(index-1) instanceof DateSeparatorAuxiliaryItem){
			items.remove(index-1);
			items.remove(notification);
		// if this notification is only in its date group 
		// remove both notification and date separator
		}else if(index > 0 && items.get(index-1) instanceof DateSeparatorAuxiliaryItem && items.get(index+1) instanceof DateSeparatorAuxiliaryItem){
			items.remove(index-1);
			items.remove(notification);
		}else{
			items.remove(notification);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(items.get(position) instanceof DateSeparatorAuxiliaryItem){
			return false;
		}else{
			return true;
		}
	}

	public boolean isNotificationOfUnknownNumber(int position) {
		if (items.get(position) instanceof OperatorNotification){
			OperatorNotification notification = (OperatorNotification) items.get(position);
			if (notification.getContact() == null) {
				return true;
			}
		}
		return false;
	}

}
