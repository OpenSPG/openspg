package com.antgroup.openspgapp.infra.dao.mapper;

import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDO;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOExample;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonTutorialDOWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/mapper/ReasonTutorialDOMapper.class */
public interface ReasonTutorialDOMapper {
  long countByExample(ReasonTutorialDOExample example);

  int deleteByExample(ReasonTutorialDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(ReasonTutorialDOWithBLOBs record);

  int insertSelective(ReasonTutorialDOWithBLOBs record);

  List<ReasonTutorialDOWithBLOBs> selectByExampleWithBLOBs(ReasonTutorialDOExample example);

  List<ReasonTutorialDO> selectByExample(ReasonTutorialDOExample example);

  ReasonTutorialDOWithBLOBs selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") ReasonTutorialDOWithBLOBs record,
      @Param("example") ReasonTutorialDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") ReasonTutorialDOWithBLOBs record,
      @Param("example") ReasonTutorialDOExample example);

  int updateByExample(
      @Param("record") ReasonTutorialDO record, @Param("example") ReasonTutorialDOExample example);

  int updateByPrimaryKeySelective(ReasonTutorialDOWithBLOBs record);

  int updateByPrimaryKeyWithBLOBs(ReasonTutorialDOWithBLOBs record);

  int updateByPrimaryKey(ReasonTutorialDO record);
}
