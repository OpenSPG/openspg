package com.antgroup.openspg.common.util.converter;

import java.time.LocalDate;
import org.dozer.DozerConverter;

public class LocalDateToLocalDateDozerConverter extends DozerConverter<LocalDate, LocalDate> {

  /**
   * 获取转换信息
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/1/29 14:34
   */
  public LocalDateToLocalDateDozerConverter() {
    super(LocalDate.class, LocalDate.class);
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
  public LocalDate convertFrom(LocalDate source, LocalDate destination) {
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
  public LocalDate convertTo(LocalDate source, LocalDate destination) {
    return source;
  }
}
