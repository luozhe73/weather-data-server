package com.cloud.weather.service.impl;

import com.cloud.weather.service.WeatherDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherDataServiceImpl implements WeatherDataService {

    private final static Logger logger = LoggerFactory.getLogger(WeatherDataServiceImpl.class);

    private static final String WEATHER_URI = "http://wthrcdn.etouch.cn/weather_mini?";


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public WeatherResponse getDataByCityId(String cityId) {
        String uri = WEATHER_URI + "citykey=" + cityId;
        return this.doGetWeather(uri);
    }

    @Override
    public WeatherResponse getDataByCityName(String cityName) {
        String uri = WEATHER_URI + "city=" + cityName;
        return this.doGetWeather(uri);
    }

    private WeatherResponse doGetWeather(String uri) {

        ObjectMapper mapper = new ObjectMapper();
        WeatherResponse weatherResponse = null;
        String strBody;
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();

        //先查缓存,缓存中没有再调用第三方服务
        if (stringRedisTemplate.hasKey(uri)) {

            logger.info("redis has data");
            strBody = opsForValue.get(uri);
        } else {
            logger.error("redis has no data");
            throw new RuntimeException("");
        }

        try {
            weatherResponse = mapper.readValue(strBody, WeatherResponse.class);
        } catch (IOException e) {
            //e.printStackTrace();
            logger.error("ERROR", e);
        }

        return weatherResponse;
    }
}
