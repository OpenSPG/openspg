/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */


package com.antgroup.openspg.server.infra.dao.mapper;

import com.antgroup.openspg.server.infra.dao.dataobject.OntologyExtDO;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

/**
 * @author xcj01388694
 * @version OntologyExtDOMapper.java, v 0.1 2024年03月05日 下午4:53 xcj01388694
 */
public interface OntologyExtDOMapper {

  int insert(OntologyExtDO record);

  int deleteByPrimaryKey(Long id);

  int deleteByIds(@Param("ids") List<Long> ids);

  int updateByPrimaryKeySelective(OntologyExtDO record);

  OntologyExtDO selectByPrimaryKey(Long id);

  List<OntologyExtDO> selectByCondition(
      @Param("record") OntologyExtDO record, @Param("start") int start, @Param("size") int size);

  int selectCountByCondition(OntologyExtDO record);

  List<OntologyExtDO> selectByIds(@Param("ids") List<Long> ids);

  OntologyExtDO selectByUk(
      @Param("resourceId") String resourceId,
      @Param("resourceType") String resourceType,
      @Param("extType") String extType,
      @Param("field") String field);

  List<OntologyExtDO> selectByIdAndType(
      @Param("resourceId") String resourceId, @Param("resourceType") String resourceType);

  int deleteByUk(
      @Param("resourceId") String resourceId,
      @Param("resourceType") String resourceType,
      @Param("extType") String extType,
      @Param("field") String field);

  int updateConfig(
      @Param("resourceId") String resourceId,
      @Param("resourceType") String resourceType,
      @Param("extType") String extType,
      @Param("field") String field,
      @Param("userId") String userId,
      @Param("config") String config);

  List<OntologyExtDO> getExtInfoListByIds(
      @Param("resourceIds") Set<String> resourceIds,
      @Param("resourceType") String resourceType,
      @Param("extType") String extType,
      @Param("field") String field);

  /**
   * 批量新增扩展信息
   *
   * @param records
   * @return
   */
  int batchInsert(@Param("records") List<OntologyExtDO> records);
}
