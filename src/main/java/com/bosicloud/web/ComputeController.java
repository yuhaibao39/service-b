package com.bosicloud.web;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import com.bosicloud.entity.InterfaceLimit;
import com.bosicloud.service.InterfaceLimitService;

import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

@RestController
public class ComputeController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private InterfaceLimitService service;

    //单位时间内的调用次数
    private final int flag = 10;
    //单位时间1000ms * 60 = 1min
    private static final int timeRound = 1000 * 60;
    //用来标记调用次数
    private static final AtomicLong num = new AtomicLong(0);

    @Autowired
    private DiscoveryClient client;


    @Autowired
    RestTemplate restTemplate;//定义为私有可能会报错


    static {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                num.set(0);
            }
        }, 0, timeRound);
    }

    //自身方法测试
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(@RequestParam Integer a, @RequestParam Integer b) {

        ServiceInstance instance = client.getLocalServiceInstance();

        Integer r = a + b;

        logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);

        return "计算结果 From Service-B, Result is " + r + "\nPort:" + instance.getPort();

    }


    //返回A服务的ribbin方法
    @RequestMapping(value = "/ribbon", method = RequestMethod.GET)
    public String ribbon(@RequestParam Integer a, @RequestParam Integer b) {

        ServiceInstance instance = client.getLocalServiceInstance();
        Integer r = a + b;

        logger.info("/ribbon, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);

        return "计算结果 From Service-B, Result is " + r +".   /ribbon, host:" + instance.getHost() + ", service_id:" + instance.getServiceId();

    }

    @RequestMapping(value = "/skywalking", method = RequestMethod.GET)
    public String addauthorizationok(@RequestHeader Map<String, String> headerMap, @RequestParam Integer a, @RequestParam Integer b) {

        logger.info("love B print HeaderMap:" + headerMap);

        ServiceInstance instance = client.getLocalServiceInstance();
        Integer r = a + b;

        logger.info("love Service-B, /love, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);

  /*      //远程服务调用测试
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://service-C:9997/skywalking?a=" + a + "&b=" + b, String.class);
*/
        //远程服务调用测试
        return restTemplate.getForObject("http://service-C:9997/skywalking?a=" + a + "&b=" + b, String.class);


    }


    @RequestMapping(value = "/addbackup", method = RequestMethod.GET)
    public String addbackup(@RequestParam Integer a, @RequestParam Integer b) {

        num.incrementAndGet();

//        if (num.get() <= flag) {
//	        ServiceInstance instance = client.getLocalServiceInstance();
//	        Integer r = a + b;
//	        logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
//	        return "From Service-B, Result is " + r+"\nPort:"+instance.getPort();
//        }
//        //return "调用次数超限，一分钟内最多只能调用10次！";


        InterfaceLimit limit = service.getEntityByPri(1);

        Jedis jedis = RedisUtils.getJedis();

        //redis存的超时时间
        String timeRound_1 = jedis.get("timeRound_1");
        //如果不存在或者是不等于数据库设置值
        if (!limit.getUnitTime().toString().equals(timeRound_1)) {
            //重新设置超时时间
            jedis.set("timeRound_1", limit.getUnitTime().toString());
            jedis.expire("num_1", limit.getUnitTime());
        }
        String num_1 = jedis.get("num_1");
        if (num_1 == null) {
            jedis.set("num_1", String.valueOf(0));
            jedis.expire("num_1", limit.getUnitTime());
        }

        jedis.incr("num_1");

        if (Integer.parseInt(jedis.get("num_1")) <= limit.getUnitNum()) {
            ServiceInstance instance = client.getLocalServiceInstance();
            Integer r = a + b;
            logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
            return "计算结果 From Service-B, Result is " + r + "\nPort:" + instance.getPort();
        }
        return "调用次数超限！";


    }

}