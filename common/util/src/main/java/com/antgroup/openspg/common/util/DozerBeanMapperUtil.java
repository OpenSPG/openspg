package com.antgroup.openspg.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.dozer.DozerBeanMapper;
import org.springframework.util.CollectionUtils;

public class DozerBeanMapperUtil {

  private static final DozerBeanMapper DOZER_BEAN_MAPPER;

  static {
    List<String> mappingFileUrls = Collections.singletonList("dozer-custom-convert.xml");
    DOZER_BEAN_MAPPER = new DozerBeanMapper();
    DOZER_BEAN_MAPPER.setMappingFiles(mappingFileUrls);
  }

  public static <T> T map(Object source, Class<T> destinationClass) {
    T destinationBean = null;
    if (source != null) {
      destinationBean = DOZER_BEAN_MAPPER.map(source, destinationClass);
    }
    return destinationBean;
  }

  public static <T> List<T> mapList(
      @SuppressWarnings("rawtypes") Collection sourceList, Class<T> destinationClass) {
    if (CollectionUtils.isEmpty(sourceList)) {
      return Collections.emptyList();
    }

    List<T> destinationList = new ArrayList<>(sourceList.size());
    for (Object sourceObject : sourceList) {
      T destinationObject = map(sourceObject, destinationClass);
      destinationList.add(destinationObject);
    }
    return destinationList;
  }

  private DozerBeanMapperUtil() {}
}
