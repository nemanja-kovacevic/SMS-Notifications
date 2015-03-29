package net.nemanjakovacevic.smsnotifications.activities.adapters;

import java.util.Date;

public class DateSeparatorAuxiliaryItem {

	Date date;

	public DateSeparatorAuxiliaryItem(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DateSeparatorAuxiliaryItem) {
			DateSeparatorAuxiliaryItem other = (DateSeparatorAuxiliaryItem) o;
			return this.getDate().equals(other.getDate());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getDate().hashCode();
	}
	
}
