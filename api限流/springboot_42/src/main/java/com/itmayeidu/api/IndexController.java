
package com.itmayeidu.api;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.RateLimiter;
import com.itmayiedu.annotation.ExtRateLimiter;
import com.itmayiedu.service.OrderService;

/*
 * 使用RateLimiter 实现令牌通方式限流
 */
@RestController
public class IndexController {
	@Autowired
	private OrderService orderService;
	// create 方法中传入一个参数 以每秒为单位固定的速率值 1r/s 每秒中往桶中存入一个令牌
	RateLimiter rateLimiter = RateLimiter.create(1); // 独立线程

	public IndexController() {

	}

	// 相当于该接口每秒钟时间 只能支持一个客户端访问
	@RequestMapping("/addOrder")
	public String addOrder() {

		// 1.限流处理 限流正常要放在网关 客户端从桶中获取对应的令牌，为什么返回double结果，这个结果表示 从桶中拿到令牌等待时间.
		// 2. 如果获取不到令牌，就会一直等待.设置服务降级处理（相当于配置在规定时间内如果没有获取到令牌的话，直接走服务降级。）
		// double acquire = rateLimiter.acquire();
		//
		// System.out.println("从桶中获取令牌等待的时间:" + acquire);
		// 如果在500毫秒内如果没有获取到令牌的话，则直接走服务降级处理
		boolean tryAcquire = rateLimiter.tryAcquire(500, TimeUnit.MILLISECONDS);
		if (!tryAcquire) {
			System.out.println("别抢了， 在抢也是一直等待的， 还是放弃吧！！！");
			return "别抢了， 在抢也是一直等待的， 还是放弃吧！！！";
		}
		// 2. 业务逻辑处理
		boolean addOrderResult = orderService.addOrder();
		if (addOrderResult) {
			System.out.println("恭喜您，抢购成功! ");
			return "恭喜您，抢购成功!";
		}
		return "抢购失败!";
	}

	// 使用注解方式实现服务令牌桶限流
	@RequestMapping("/findOrder")
	@ExtRateLimiter(value = 0.5, timeOut = 500)
	public String findOrder() throws InterruptedException {
		System.out.println("findOrder");
		return "SUCCESS";
	}

	// 使用注解方式实现服务令牌桶限流
	@RequestMapping("/myOrder")
	@ExtRateLimiter(value = 10.0, timeOut = 500)
	public String myOrder() throws InterruptedException {
		System.out.println("myOrder");
		return "SUCCESS";
	}

	// 没有实现限流
	@RequestMapping("/orderIndex")
	public String orderIndex() throws InterruptedException {
		System.out.println("orderIndex");
		return "orderIndex_success";
	}

}
