package io.kt.cloud.tanda.pay;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "pay", url = "${api.url.pay}")
public interface PayService {
	
	@RequestMapping(method = RequestMethod.POST, path = "/pays", consumes = "application/json")
	void billRelease(Pay pay);
	
}