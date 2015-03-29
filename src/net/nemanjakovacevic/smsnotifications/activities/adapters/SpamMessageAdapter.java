package net.nemanjakovacevic.smsnotifications.activities.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.nemanjakovacevic.smsnotifications.R;
import net.nemanjakovacevic.smsnotifications.domain.SpamMessage;
import net.nemanjakovacevic.smsnotifications.util.Util;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpamMessageAdapter extends BaseAdapter {

	private final static SimpleDateFormat SEPARATOR_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");

	private LayoutInflater mInflater;
	private List<Object> items;

	private Date todayMidnight;
	private Date yesterdayMidnight;

	public SpamMessageAdapter(Context context, List<SpamMessage> spamMessages) {
		this.items = createItems(spamMessages);
		this.mInflater = LayoutInflater.from(context);

		Date now = new Date();
		this.todayMidnight = Util.trim(now);
		this.yesterdayMidnight = Util.trim(new Date(now.getTime() - 24 * 60 * 60 * 1000));
	}

	private List<Object> createItems(List<SpamMessage> spamMessages) {
		if (spamMessages == null) {
			return null;
		}
		List<Object> items = new ArrayList<Object>();
		List<DateSeparatorAuxiliaryItem> addedDateSeparators = new ArrayList<DateSeparatorAuxiliaryItem>();
		for (int i = 0; i < spamMessages.size(); i++) {
			SpamMessage sm = spamMessages.get(i);
			DateSeparatorAuxiliaryItem dateSeparator = getDateSeparatorForNotification(sm);
			if (!addedDateSeparators.contains(dateSeparator)) {
				addedDateSeparators.add(dateSeparator);
				items.add(dateSeparator);
			}
			items.add(sm);
		}
		return items;
	}

	private DateSeparatorAuxiliaryItem getDateSeparatorForNotification(SpamMessage sm) {
		return new DateSeparatorAuxiliaryItem(Util.trim(sm.getTimestamp()));
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
		if (item instanceof SpamMessage) {
			ViewHolder holder;
			if (convertView == null) {
				holder = null;
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (holder == null) {
				convertView = mInflater.inflate(R.layout.list_spam_messages_item, null);
				holder = new ViewHolder();
				holder.spamMessageSender = (TextView) convertView.findViewById(R.id.list_spam_messages_item_sender);
				holder.spamMessageBody = (TextView) convertView.findViewById(R.id.list_spam_messages_item_body);
				convertView.setTag(holder);
			}

			SpamMessage spamMessage = (SpamMessage) getItem(position);

			holder.spamMessageSender.setText(spamMessage.getSender());
			holder.spamMessageBody.setText(spamMessage.getBody());
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
		TextView spamMessageSender;
		TextView spamMessageBody;
	}
	
	public void remove(SpamMessage spamMessage){
		int index = items.indexOf(spamMessage);
		// if this spam message is last in list and only in date group
		// remove both notification and date separator
		if(index == items.size()-1 && items.get(index-1) instanceof DateSeparatorAuxiliaryItem){
			items.remove(index-1);
			items.remove(spamMessage);
		// if this notification is only in its date group 
		// remove both spam message and date separator
		}else if(index > 0 && items.get(index-1) instanceof DateSeparatorAuxiliaryItem && items.get(index+1) instanceof DateSeparatorAuxiliaryItem){
			items.remove(index-1);
			items.remove(spamMessage);
		}else{
			items.remove(spamMessage);
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

}
