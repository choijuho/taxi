package io.kt.cloud.tanda.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;

import io.kt.cloud.tanda.App;
import io.kt.cloud.tanda.enumeration.DispatchStatus;
import io.kt.cloud.tanda.event.TaxiDispatchChanged;
import io.kt.cloud.tanda.kafka.KafkaSender;
import io.kt.cloud.tanda.pay.Pay;
import io.kt.cloud.tanda.pay.PayService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class TaxiDispatch {

	@Id
	@GeneratedValue
	private Long dispatchId; // ID : 자동생성
	private Long bookId; // Book Entity와 relation
	private String taxiInfo; // 배차된 택시정보 : 차번호/전화번호
	private String dispatchStatus; // 배차됨, 운행시작됨, 운행종료됨, 배차취소됨
	private int price; //운행종료후 API로 전달됨
	private LocalDateTime lastModifyTime; // DB INSERT, UPDATE Time으로 @PreUpate Hook에서 셋팅

	// 이벤트 보낸다.
	@PostUpdate
	@PostPersist
	protected void taxiDispatchChanged() {

		TaxiDispatchRepository repository = App.applicationContext.getBean(TaxiDispatchRepository.class);

		repository.findById(dispatchId).ifPresent(f -> {

			TaxiDispatchChanged tc = new TaxiDispatchChanged();
			tc.setDispatchId(f.getDispatchId());
			tc.setBookId(f.getBookId());
			tc.setTaxiInfo(f.getTaxiInfo());
			tc.setLastModifyTime(f.getLastModifyTime());
			tc.setDispatchStatus(f.getDispatchStatus());
			KafkaSender.pub(tc);
		});

	}


	@PreUpdate
	protected void taxiStatusChanged() {
		
		
		if (DispatchStatus.DRIVE_ENDED.getHangul().equals(this.getDispatchStatus())) {
			
			TaxiDispatchRepository repository = App.applicationContext.getBean(TaxiDispatchRepository.class);

			repository.findById(dispatchId).ifPresent(f -> {

				Pay pay = new Pay();
				
				pay.setBookId(f.getBookId());  // 검색
				pay.setDispatchId(f.getDispatchId());
				pay.setPrice(this.getPrice());// API 입력

				try {
					PayService payService = App.applicationContext.getBean(PayService.class);
					payService.billRelease(pay);					
				} catch (Exception e) {
					throw new RuntimeException(String.format("결제실패가 실패했습니다(%s)\n%s", this, e.getMessage()));
				}
				
			});

		}
		
		
		this.setLastModifyTime(LocalDateTime.now());
	}
	
}
