package com.antgroup.openspgapp.infra.dao.mapper;

import com.antgroup.openspgapp.infra.dao.dataobject.ReasonSessionDO;
import com.antgroup.openspgapp.infra.dao.dataobject.ReasonSessionDOExample;
import com.github.pagehelper.Page;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/mapper/ReasonSessionDOMapper.class */
public interface ReasonSessionDOMapper {
  long countByExample(ReasonSessionDOExample example);

  int deleteByExample(ReasonSessionDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(ReasonSessionDO record);

  int insertSelective(ReasonSessionDO record);

  Page<ReasonSessionDO> selectByExampleWithBLOBs(ReasonSessionDOExample example);

  List<ReasonSessionDO> selectByExample(ReasonSessionDOExample example);

  ReasonSessionDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") ReasonSessionDO record, @Param("example") ReasonSessionDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") ReasonSessionDO record, @Param("example") ReasonSessionDOExample example);

  int updateByExample(
      @Param("record") ReasonSessionDO record, @Param("example") ReasonSessionDOExample example);

  int updateByPrimaryKeySelective(ReasonSessionDO record);

  int updateByPrimaryKeyWithBLOBs(ReasonSessionDO record);

  int updateByPrimaryKey(ReasonSessionDO record);
}
