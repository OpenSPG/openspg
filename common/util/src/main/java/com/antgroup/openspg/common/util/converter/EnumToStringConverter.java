package com.antgroup.openspg.common.util.converter;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dozer.CustomConverter;
import org.dozer.MappingException;

@Slf4j
public class EnumToStringConverter implements CustomConverter {

  @Override
  public Object convert(
      Object destination, Object source, Class<?> destinationClass, Class<?> sourceClass) {
    if (source == null) {
      return null;
    }
    if (source instanceof Enum) {
      return getString(source);
    } else if (source instanceof String) {
      return getEnum(destinationClass, source.toString());
    } else {
      throw new MappingException(
          new StringBuilder("Converter ")
              .append(this.getClass().getSimpleName())
              .append(" was used incorrectly. Arguments were: ")
              .append(destinationClass.getClass().getName())
              .append(" and ")
              .append(source)
              .toString());
    }
  }

  private Object getString(Object source) {
    Enum<?> em = (Enum<?>) source;
    return em.name();
  }

  private Object getEnum(Class<?> destinationClass, String source) {
    if (StringUtils.isBlank(source)) {
      return null;
    }
    try {
      Method m = destinationClass.getDeclaredMethod("valueOf", String.class);
      Object enumeration = m.invoke(destinationClass.getClass(), source);
      return enumeration;
    } catch (Exception e) {
      log.warn("EnumToStringConverter getEnum Exception source:" + source);
    }
    return null;
  }
}
