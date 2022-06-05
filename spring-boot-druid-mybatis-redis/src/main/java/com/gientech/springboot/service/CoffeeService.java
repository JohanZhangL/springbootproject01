package com.gientech.springboot.service;


import com.gientech.springboot.mapper.CoffeeMapper;
import com.gientech.springboot.model.Coffee;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
public class CoffeeService {
    private static final String CACHE = "springbucks-coffee";
    @Autowired
    private CoffeeMapper coffeeMapper;
    @Autowired
    private RedisTemplate<String, Coffee> redisTemplate;

    public void saveCoffee(Coffee coffee){
         coffeeMapper.saveCoffee(coffee);
    }

    public List<Coffee> selectCoffeeListByIds(@Param("coffeeIds")List<Long> coffeeIds){
        return coffeeMapper.selectCoffeeListByIds(coffeeIds);
    }

    public Coffee selectCoffeeByName(String name){
        HashOperations<String, String, Coffee> hashOperations = redisTemplate.opsForHash();
        if (redisTemplate.hasKey(CACHE) && hashOperations.hasKey(CACHE, name)) {
            log.info("Get coffee {} from Redis.", name);
            return hashOperations.get(CACHE, name);
        }
        Coffee coffee = coffeeMapper.selectCoffeeByName(name);
        log.info("Coffee Found: {}", coffee);
        if (coffee!=null) {
            log.info("Put coffee {} to Redis.", name);
            hashOperations.put(CACHE, name, coffee);
            redisTemplate.expire(CACHE, 1, TimeUnit.MINUTES);
        }
        return coffee;
    }

    public List<Coffee> selectAllCoffee(){
        return coffeeMapper.selectAllCoffee();
    }

    public void updateCoffee(Coffee coffee){
        coffeeMapper.updateCoffee(coffee);
    }

    public int deleteCoffee(Coffee coffee){
        return coffeeMapper.deleteCoffee(coffee);
    }

    public List<Coffee> findAllWithParam(int pageNum,int pageSize){
        return coffeeMapper.findAllWithParam(pageNum,pageSize);
    }

}
