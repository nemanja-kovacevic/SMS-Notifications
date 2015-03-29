package net.nemanjakovacevic.smsnotifications.domain;

import java.util.Date;

public class SpamMessage {

	private int id;
	private String sender;
	private String body;
	private Date timestamp;
	
	public SpamMessage() {
	}

	public SpamMessage(int id, String sender, String body, Date timestamp) {
		super();
		this.id = id;
		this.sender = sender;
		this.body = body;
		this.timestamp = timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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
		SpamMessage other = (SpamMessage) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
