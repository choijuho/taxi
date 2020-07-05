package io.kt.cloud.tanda.pay;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Pay {
		
	private Long payId;						//ID 	: 자동생성
	private Long bookId;					//Book Entity와 relation
	private Long dispatchId;				//Dispatch Entity와 relation
	private int	 price;						//결제된 택시요금
	
}
