package io.kt.cloud.tanda.event;

import lombok.Data;

@Data
public class CancelConfirmed {

	public static final String ONLY_ME = "headers['type']=='CancelConfirmed'";
	private String type;
	private boolean confirmed;
	private Long bookId;
	
	public CancelConfirmed(boolean confirmed, Long bookId) {
		this.confirmed = confirmed;
		this.bookId = bookId;
		this.type = CancelConfirmed.class.getSimpleName();
	}
}
