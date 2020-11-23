
package com.itmayiedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.itmayeidu.hystrix.OrderHystrixCommand;
import com.itmayeidu.hystrix.OrderHystrixCommand2;
import com.itmayiedu.service.MemberService;


@RestController
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private MemberService memberService;

	@RequestMapping("/orderIndex")
	public Object orderIndex() throws InterruptedException {
		JSONObject member = memberService.getMember();
		System.out.println("当前线程名称:" + Thread.currentThread().getName() + ",订单服务调用会员服务:member:" + member);
		return member;
	}

	//订单服务调用会员服务  解决服务雪崩效应 底层使用服务隔离线程池方式
	@RequestMapping("/orderIndexHystrix")
	public Object orderIndexHystrix() throws InterruptedException {
		return new OrderHystrixCommand(memberService).execute();
	}

	@RequestMapping("/orderIndexHystrix2")
	public Object orderIndexHystrix2() throws InterruptedException {
		return new OrderHystrixCommand2(memberService).execute();
	}

	@RequestMapping("/findOrderIndex")
	public Object findIndex() {
		System.out.println("当前线程:" + Thread.currentThread().getName() + ",findOrderIndex");
		return "findOrderIndex";
	}
}
