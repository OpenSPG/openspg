package com.antgroup.openspg.reasoner.common.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author fuyu.bfy
 * @date 2023/03/13
 */
public class SetsUtils {

  public static <I, O> Set<O> map(Set<I> input, Function<I, O> func) {
    if (CollectionUtils.isEmpty(input)) {
      return new HashSet<>(0);
    }
    return input.stream().map(func).collect(Collectors.toSet());
  }
}
