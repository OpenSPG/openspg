package com.antgroup.openspg.common.util.converter;

import java.time.LocalDateTime;
import org.dozer.DozerConverter;

public class LocalDateTimeToLocalDateTimeDozerConverter
    extends DozerConverter<LocalDateTime, LocalDateTime> {

  /**
   * 转换信息
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/1/29 14:34
   */
  public LocalDateTimeToLocalDateTimeDozerConverter() {
    super(LocalDateTime.class, LocalDateTime.class);
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
  public LocalDateTime convertTo(LocalDateTime source, LocalDateTime destination) {
    return source;
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
  public LocalDateTime convertFrom(LocalDateTime source, LocalDateTime destination) {
    return source;
  }
}
