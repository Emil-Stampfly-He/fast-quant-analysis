package org.imperial.fastquantanalysis;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.imperial.fastquantanalysis.entity.QuantStrategy;
import org.imperial.fastquantanalysis.util.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
class FastQuantAnalysisApplicationTests {

    @Resource
    private RandomStringGenerator randomStringGenerator;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testRandomStringGenerator() {
        String s = randomStringGenerator.generateRandomString();
        System.out.println(s);
    }

    @Test
    void testRedisGetHash() {
        String key = "login:user:7ccf95a1bb654e4286bf364ca6fae01a";
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);

        System.out.println(map);
    }

    @Test
    void testConstructor() {
        QuantStrategy quantStrategy = new QuantStrategy("id", "name",
                LocalDateTime.now(), LocalDateTime.now(),
                0.1, 0.2,
                0.3, 0.4,
                0.5, 10);
        System.out.println(quantStrategy);
    }

    @Test
    void testGetAvgBarPrices() {
        List<Double> list1 = List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        List<Double> list2 = List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        List<Double> list3 = List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        List<Double> list4 = List.of(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);

        List<List<Double>> lists = List.of(list1, list2, list3, list4);
        List<Double> averages = IntStream.range(0, lists.get(0).size())
                .mapToDouble(i -> lists.stream().mapToDouble(list -> list.get(i)).average().orElse(0))
                .boxed()
                .toList();

        System.out.println(averages);
    }

}
