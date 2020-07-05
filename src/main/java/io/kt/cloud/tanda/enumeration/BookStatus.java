package io.kt.cloud.tanda.enumeration;

import lombok.Getter;

@Getter
public enum BookStatus {
	ACCEPTED("접수됨"), CANCEL_PENDED("취소 요청 보류중"), CANCELED_BY_CUSTUMER("고객발 취소됨"); 
	
	private String hangul; 
	BookStatus(String hangul) {
		this.hangul = hangul;
	}
}
