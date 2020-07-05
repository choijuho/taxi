package io.kt.cloud.tanda.enumeration;

import lombok.Getter;

@Getter
public enum DispatchStatus {
	DISPATCHING("배차중"), DISPATCHED("배차됨"), DRIVE_STARTED("운행시작됨"), DRIVE_ENDED("운행종료됨"), DISPATCH_CANCELED("배차취소됨");

	private String hangul;

	DispatchStatus(String hangul) {
		this.hangul = hangul;
	}
}
