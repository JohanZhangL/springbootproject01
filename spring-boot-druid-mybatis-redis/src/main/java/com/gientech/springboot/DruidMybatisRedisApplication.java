package com.gientech.springboot;

import com.gientech.springboot.mapper.CoffeeMapper;
import com.gientech.springboot.model.Coffee;
import com.gientech.springboot.service.CoffeeService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement
@MapperScan("com.gientech.springboot.mapper")
@EnableRedisRepositories
public class DruidMybatisRedisApplication {

    @Autowired
    private CoffeeMapper coffeeMapper;

    @Autowired
    private CoffeeService coffeeService;

    public static void main(String[] args) {
        SpringApplication.run(DruidMybatisRedisApplication.class, args);
    }

    @Bean
    public RedisTemplate<String, Coffee> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Coffee> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public RedisCustomConversions redisCustomConversions() {
        return new RedisCustomConversions(
                Arrays.asList(new MoneyToBytesConverter(), new BytesToMoneyConverter()));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Coffee c = Coffee.builder().name("ruixin")
                .price(Money.of(CurrencyUnit.of("CNY"), 20.0)).build();
        coffeeService.saveCoffee(c);
        log.info("Save {} Coffee: {}", c);

        c = Coffee.builder().name("espresso")
                .price(Money.of(CurrencyUnit.of("CNY"), 25.0)).build();
        coffeeService.saveCoffee(c);
        log.info("Save {} Coffee: {}", c);

        c = Coffee.builder().name("latte")
                .price(Money.of(CurrencyUnit.of("CNY"), 30.0)).build();
        coffeeService.saveCoffee(c);
        log.info("Save {} Coffee: {}", c);

        c = Coffee.builder().name("mocha")
                .price(Money.of(CurrencyUnit.of("CNY"), 30.0)).build();
        coffeeService.saveCoffee(c);
        log.info("Save {} Coffee: {}", c);

        c.setPrice(Money.of(CurrencyUnit.of("CNY"), 60.0));
        coffeeService.updateCoffee(c);

        Long[] array = {1L,2L,3L,4L};
        List<Long> coffeeIds = Arrays.asList(array);
        List<Coffee> coffees = coffeeService.selectCoffeeListByIds(coffeeIds);
        log.info("根据主键批量查询  {}",coffees);

        log.info("所有coffee2222  {}",coffees);
        int count = coffeeService.deleteCoffee(c);
        log.info("删除条数  {}",count);

        List<Coffee> coffees1 = coffeeService.selectAllCoffee();
        log.info("所有coffee  {}",coffees);
        coffeeService.selectCoffeeByName("ruixin");

        List<Coffee> allWithParam = coffeeService.findAllWithParam(1, 3);
        log.info("翻页查询结果 {}",allWithParam);
    }

}
