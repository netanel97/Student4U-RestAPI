package controllersSrc;

import java.util.Date;
import java.util.Map;

public class Message {
	private String message;
	private Long id;
	private Date timestamp;
	private Boolean important;
	private Map<String, Object> moreData;

	public Message() {
	}

	public Message(String message) {
		super();
		this.message = message;
		timestamp = new Date();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getImportant() {
		return important;
	}

	public void setImportant(Boolean important) {
		this.important = important;
	}

	public Map<String, Object> getMoreData() {
		return moreData;
	}

	public void setMoreData(Map<String, Object> moreData) {
		this.moreData = moreData;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", id=" + id + ", timestamp=" + timestamp + ", important=" + important
				+ ", moreData=" + moreData + "]";
	}

}
