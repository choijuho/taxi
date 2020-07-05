package io.kt.cloud.tanda.kafka;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import io.kt.cloud.tanda.entity.TaxiDispatch;
import io.kt.cloud.tanda.entity.TaxiDispatchRepository;
import io.kt.cloud.tanda.enumeration.BookStatus;
import io.kt.cloud.tanda.enumeration.DispatchStatus;
import io.kt.cloud.tanda.event.BookCanceled;
import io.kt.cloud.tanda.event.BookPlaced;
import io.kt.cloud.tanda.event.CancelConfirmed;

@Service
public class KafkaListener {

	private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

	@Autowired
	TaxiDispatchRepository taxiDispatchRepository;

	@StreamListener(target = Processor.INPUT, condition = BookPlaced.ONLY_ME)
	public void handle(@Payload BookPlaced bookPlaced) {

		if (bookPlaced == null) {
			return;
		}

		if (bookPlaced.getBookId() == null) {
			return;
		}

		logger.info("[SUB] " + bookPlaced);

		if (bookPlaced.getBookStatus().equals(BookStatus.ACCEPTED.getHangul())) {

			Optional<TaxiDispatch> optTaxiDispatch = taxiDispatchRepository.findById(bookPlaced.getBookId());

			if (optTaxiDispatch.isPresent()) {
				throw new RuntimeException("동일한 bookId로 예약 이벤트가 발생했습니다.");
			} else {

				TaxiDispatch t = new TaxiDispatch();

				t.setBookId(bookPlaced.getBookId());
				t.setDispatchStatus(DispatchStatus.DISPATCHING.getHangul());
				t.setLastModifyTime(LocalDateTime.now());

				taxiDispatchRepository.save(t);

			}
		}

	}

	@StreamListener(target = Processor.INPUT, condition = BookCanceled.ONLY_ME)
	public void handle(@Payload BookCanceled bookCanceled) {
		if (bookCanceled == null) {
			return;
		}

		if (bookCanceled.getBookId() == null) {
			return;
		}

		logger.info("[SUB] " + bookCanceled);

		if (bookCanceled.getBookStatus().equals(BookStatus.CANCELED_BY_CUSTUMER.getHangul())) {

			Optional<TaxiDispatch> opt = taxiDispatchRepository.findByBookId(bookCanceled.getBookId());
			
			String status = null;
			if ( opt.isPresent() ) {
				status = opt.get().getDispatchStatus();
			}
			
			if (DispatchStatus.DISPATCHING.getHangul().equals(status)
						|| DispatchStatus.DISPATCHED.getHangul().equals(status)) {
				KafkaSender.pub(new CancelConfirmed(true, opt.get().getBookId()));
			} else {
				KafkaSender.pub(new CancelConfirmed(false, opt.get().getBookId()));
			}				

			if ( status != null && (DispatchStatus.DISPATCHING.getHangul().equals(status)
					|| DispatchStatus.DISPATCHED.getHangul().equals(status))) {
				opt.get().setDispatchStatus(DispatchStatus.DISPATCH_CANCELED.getHangul());
				taxiDispatchRepository.save(opt.get());
			}

		}
	}

	@StreamListener(target = Processor.INPUT)
	public void handle(@Payload String unknownEvent) {
		// System.out.println(unknownEvent);
	}
}
