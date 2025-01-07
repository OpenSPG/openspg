package com.antgroup.openspg.common.util.converter;

import java.time.LocalTime;
import org.dozer.DozerConverter;

public class LocalTimeToLocalTimeDozerConverter extends DozerConverter<LocalTime, LocalTime> {

  /**
   * 时间转换
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/2/22 17:40
   */
  public LocalTimeToLocalTimeDozerConverter() {
    super(LocalTime.class, LocalTime.class);
  }

  /**
   * 转换来源
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/2/22 17:40
   */
  @Override
  public LocalTime convertFrom(LocalTime source, LocalTime destination) {
    return source;
  }

  /**
   * 转换目标
   *
   * @param
   * @return
   * @author 庄舟
   * @date 2021/2/22 17:40
   */
  @Override
  public LocalTime convertTo(LocalTime source, LocalTime destination) {
    return source;
  }
}
