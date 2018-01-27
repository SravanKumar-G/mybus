package com.mybus.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConverterConfig {

  @Bean
  public CustomConversions dateConversions()
  {
    List<Converter<?, ?>> converterList = new ArrayList<>();
    converterList.add(new DateConverter());
    return new CustomConversions(converterList);
  }

  @WritingConverter
  static class DateConverter implements Converter<Date, Date> {

    @Override
    public Date convert(Date input) {
      if (input == null) {
        return null;
      }
      return Date.from(Instant.ofEpochMilli(input.getTime()).truncatedTo(ChronoUnit.DAYS));
    }
  }
}