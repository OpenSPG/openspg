package com.antgroup.openspg.common.util.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.dozer.DozerConverter;

public class LocalDateTimeToDateDozerConverter extends DozerConverter<LocalDateTime, Date> {

  /**
   * 转换信息
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/1/29 14:34
   */
  public LocalDateTimeToDateDozerConverter() {
    super(LocalDateTime.class, Date.class);
  }

  /**
   * 转换信息
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/1/29 14:34
   */
  @Override
  public LocalDateTime convertFrom(Date source, LocalDateTime destination) {
    return source == null
        ? null
        : LocalDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
  }

  /**
   * 转换信息
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/1/29 14:34
   */
  @Override
  public Date convertTo(LocalDateTime source, Date destination) {
    return source == null ? null : Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
  }
}
