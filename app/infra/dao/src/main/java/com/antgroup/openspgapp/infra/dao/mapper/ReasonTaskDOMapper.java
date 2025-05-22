package com.antgroup.openspgapp.infra.dao.mapper;

import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDO;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOExample;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTaskDOWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/mapper/ReasonTaskDOMapper.class */
public interface ReasonTaskDOMapper {
  long countByExample(ReasonTaskDOExample example);

  int deleteByExample(ReasonTaskDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(ReasonTaskDOWithBLOBs record);

  int insertSelective(ReasonTaskDOWithBLOBs record);

  List<ReasonTaskDOWithBLOBs> selectByExampleWithBLOBs(ReasonTaskDOExample example);

  List<ReasonTaskDO> selectByExample(ReasonTaskDOExample example);

  ReasonTaskDOWithBLOBs selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") ReasonTaskDOWithBLOBs record, @Param("example") ReasonTaskDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") ReasonTaskDOWithBLOBs record, @Param("example") ReasonTaskDOExample example);

  int updateByExample(
      @Param("record") ReasonTaskDO record, @Param("example") ReasonTaskDOExample example);

  int updateByPrimaryKeySelective(ReasonTaskDOWithBLOBs record);

  int updateByPrimaryKeyWithBLOBs(ReasonTaskDOWithBLOBs record);

  int updateByPrimaryKey(ReasonTaskDO record);
}
