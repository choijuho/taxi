package io.kt.cloud.tanda.entity;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;




public interface TaxiDispatchRepository extends PagingAndSortingRepository<TaxiDispatch, Long>{

	Optional<TaxiDispatch> findByBookId(Long bookId);
	
}
