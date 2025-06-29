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
package com.antgroup.openspg.server.infra.dao.repository.common.convertor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.server.common.model.feedback.Feedback;
import com.antgroup.openspg.server.infra.dao.dataobject.FeedbackDO;
import com.google.common.collect.Lists;
import java.util.List;

public class FeedbackConvertor {

  public static FeedbackDO toDO(Feedback feedback) {
    if (feedback == null) {
      return null;
    }
    FeedbackDO feedbackDO = new FeedbackDO();
    feedbackDO.setId(feedback.getId());
    feedbackDO.setGmtCreate(feedback.getGmtCreate());
    feedbackDO.setGmtModified(feedback.getGmtModified());
    feedbackDO.setCreator(feedback.getCreator());
    feedbackDO.setModifier(feedback.getModifier());
    feedbackDO.setModuleType(feedback.getModuleType());
    feedbackDO.setOneCategory(feedback.getOneCategory());
    feedbackDO.setTwoCategory(feedback.getTwoCategory());
    feedbackDO.setThreeCategory(feedback.getThreeCategory());
    feedbackDO.setFourCategory(feedback.getFourCategory());
    feedbackDO.setFiveCategory(feedback.getFiveCategory());
    feedbackDO.setReactionType(feedback.getReactionType());
    if (feedback.getReason() != null) {
      String reason = JSON.toJSONString(feedback.getReason());
      feedbackDO.setReason(reason);
    }
    return feedbackDO;
  }

  public static Feedback toModel(FeedbackDO feedbackDO) {
    if (feedbackDO == null) {
      return null;
    }
    Feedback feedback = new Feedback();
    feedback.setId(feedbackDO.getId());
    feedback.setGmtCreate(feedbackDO.getGmtCreate());
    feedback.setGmtModified(feedbackDO.getGmtModified());
    feedback.setCreator(feedbackDO.getCreator());
    feedback.setModifier(feedbackDO.getModifier());
    feedback.setModuleType(feedbackDO.getModuleType());
    feedback.setOneCategory(feedbackDO.getOneCategory());
    feedback.setTwoCategory(feedbackDO.getTwoCategory());
    feedback.setThreeCategory(feedbackDO.getThreeCategory());
    feedback.setFourCategory(feedbackDO.getFourCategory());
    feedback.setFiveCategory(feedbackDO.getFiveCategory());
    feedback.setReactionType(feedbackDO.getReactionType());
    if (StringUtils.isNotBlank(feedbackDO.getReason())) {
      JSONObject parseObject = JSON.parseObject(feedbackDO.getReason());
      feedback.setReason(parseObject);
    }
    return feedback;
  }

  public static List<FeedbackDO> toDoList(List<Feedback> feedbacks) {
    if (feedbacks == null) {
      return null;
    }
    List<FeedbackDO> dos = Lists.newArrayList();
    for (Feedback feedback : feedbacks) {
      dos.add(toDO(feedback));
    }
    return dos;
  }

  public static List<Feedback> toModelList(List<FeedbackDO> feedbackDOS) {
    if (feedbackDOS == null) {
      return null;
    }
    List<Feedback> jobs = Lists.newArrayList();
    for (FeedbackDO feedbackDO : feedbackDOS) {
      jobs.add(toModel(feedbackDO));
    }
    return jobs;
  }
}
