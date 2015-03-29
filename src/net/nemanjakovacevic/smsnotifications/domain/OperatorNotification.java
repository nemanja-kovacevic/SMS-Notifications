package net.nemanjakovacevic.smsnotifications.domain;

import java.util.Date;

public class OperatorNotification implements Comparable<OperatorNotification> {

	private int id;
	private Type type;
	private String number;
	private Contact contact;
	private int numberOfCalls;
	private Date timestamp;
	private boolean seen;

	public OperatorNotification() {
	}

	public OperatorNotification(int id, Type type, String number, Contact contact, int numberOfCalls, Date timestamp,
			boolean seen) {
		super();
		this.id = id;
		this.type = type;
		this.number = number;
		this.contact = contact;
		this.numberOfCalls = numberOfCalls;
		this.timestamp = timestamp;
		this.seen = seen;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public int getNumberOfCalls() {
		return numberOfCalls;
	}

	public void setNumberOfCalls(int numberOfCalls) {
		this.numberOfCalls = numberOfCalls;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean getSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (contact != null && contact.getName() != null) {
			sb.append(contact.getName());
			sb.append(" ");
		}
		sb.append(number);
		if (type == Type.MISSED_CALL) {
			sb.append(" ");
			sb.append("(");
			sb.append(numberOfCalls);
			sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(OperatorNotification another) {
		if (another == null) {
			return Integer.MAX_VALUE;
		}
		if (this.getTimestamp() == null) {
			return Integer.MIN_VALUE;
		}
		if (another.getTimestamp() == null) {
			return Integer.MAX_VALUE;
		}
		return (-1)*this.getTimestamp().compareTo(another.getTimestamp());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperatorNotification other = (OperatorNotification) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public enum Type {

		MISSED_CALL(1),
		AVAILABILITY(2);

		private int code;

		private Type(int code) {
			this.code = code;
		}

		public int code() {
			return code;
		}

		public static Type getTypeFromCode(int code) {
			switch (code) {
			case 1:
				return MISSED_CALL;
			case 2:
				return AVAILABILITY;
			default:
				return null;
			}
		}

	}
	
}
