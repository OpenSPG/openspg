package com.antgroup.openspgapp.api.http.server.chat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.reasoner.DialogManager;
import com.antgroup.openspgapp.biz.reasoner.TaskManager;
import com.antgroup.openspgapp.common.model.data.dto.ChatStreamData;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"v1/chat"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/chat/ChatController.class */
public class ChatController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(ChatController.class);
  static List<StatusEnum> TASK_NOT_FINISH_STATUS =
      Lists.newArrayList(
          new StatusEnum[] {StatusEnum.INIT, StatusEnum.RUNNING, StatusEnum.EXTRACTING});

  @Value("${chat_output_interval_ms:1000}")
  private Long chatOutputInterval;

  @Value("${chat_timeout_seconds:600}")
  private Long chatTimeoutSeconds;

  @Value("${update_timeout_seconds:180}")
  private Long updateTimeoutSeconds;

  @Autowired private DialogManager dialogManager;

  @Autowired private TaskManager taskManager;

  @PostMapping({"/completions"})
  public void completions(HttpServletResponse response, @RequestBody Map<String, Object> params)
      throws IOException {
    Task task = null;
    try {
      try {
        try {
          log.info("completions params:{}", JSON.toJSONString(params));
          Long sessionId = (Long) params.get("session_id");
          List<Map<String, Object>> prompts = (List) params.get("prompt");
          String question = extractQuestionFromPrompts(prompts);
          JSONObject message = new JSONObject();
          HashMap map = new HashMap();
          Boolean thinkEnabled = true;
          Object object = params.get("thinking_enabled");
          if (object != null) {
            thinkEnabled = (Boolean) object;
          }
          map.put("thinking_enabled", object);
          map.put("search_enabled", params.get("search_enabled"));
          Map hashMap = fillQaParam(map);
          log.info("map:" + JSONObject.toJSONString(hashMap));
          task =
              this.dialogManager.submit(
                  sessionId, question, question, message.toJSONString(), hashMap);
          streamToPage(task, response, thinkEnabled);
          response.getOutputStream().close();
        } catch (ClientAbortException e) {
          log.warn("client abort chat");
          updateTaskStatus(task, StatusEnum.CANCELED);
          response.getOutputStream().close();
        } catch (TimeoutException e2) {
          updateTaskStatus(task, StatusEnum.TIMEOUT);
          response
              .getOutputStream()
              .write("data: [TIMEOUT]\n\n".getBytes(StandardCharsets.UTF_8.name()));
          response.getOutputStream().flush();
          response.getOutputStream().close();
        }
      } catch (SpgException e4) {
        log.warn("kag error", e4.getMessage());
        response
            .getOutputStream()
            .write("data: [ERROR]\n\n".getBytes(StandardCharsets.UTF_8.name()));
        response.getOutputStream().flush();
        response.getOutputStream().close();
      } catch (Exception e3) {
        log.error("chat failed", e3);
        HttpResult result = new HttpResult();
        result.setSuccess(false);
        result.setErrorMsg(e3.getMessage());
        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(JSON.toJSONBytes(result, new SerializerFeature[0]));
        response.getOutputStream().flush();
        response.getOutputStream().close();
      }
    } catch (Throwable th) {
      response.getOutputStream().close();
      throw th;
    }
  }

  private void streamToPage(Task task, HttpServletResponse response, Boolean thinkEnabled)
      throws Exception {
    Long id = task.getId();
    AssertUtils.assertParamObjectIsNotNull("id", id);
    ChatStreamData output = new ChatStreamData(id);
    boolean running = true;
    boolean thinkFinish = !thinkEnabled.booleanValue();
    int pointer = 1;
    Instant startTime = Instant.now();
    Date lastUpdateTime = new Date();
    String think = "";
    String answer = "";
    Task latestTask = null;
    ChatStreamData latestData = new ChatStreamData();
    do {
      if (latestTask != null) {
        task = latestTask;
        lastUpdateTime = task.getGmtModified();
        think = latestData.getThink();
        answer = latestData.getAnswer();
      }
      if (task.getStatus().equals(StatusEnum.INIT)) {
        String line = streamDataToLine(output);
        streamOutput(response, line);
        sleepBeforeNextStep(null);
        latestTask = fetchTaskData(id, latestData);
      } else {
        output.setThinkCost(latestData.getThinkCost() == null ? "" : latestData.getThinkCost());
        output.setReasoner(latestData.getReasoner());
        output.setReference(latestData.getReference());
        output.setSubgraph(latestData.getSubgraph());
        if (!thinkFinish) {
          if (StringUtils.isBlank(think)) {
            sleepBeforeNextStep(null);
            latestTask = fetchTaskData(id, latestData);
          } else {
            if (pointer >= think.length()) {
              if (think.trim().endsWith("</think>")) {
                thinkFinish = true;
              } else {
                sleepBeforeNextStep(null);
                latestTask = fetchTaskData(id, latestData);
              }
            }
            if (pointer > think.length()) {
              pointer = think.length();
            }
            String subStr = think.substring(0, pointer);
            output.setThink(subStr);
            pointer += 2;
            if (thinkFinish) {
              pointer = 1;
            }
            String line2 = streamDataToLine(output);
            streamOutput(response, line2);
            sleepBeforeNextStep(100L);
          }
        } else if (StringUtils.isBlank(answer)) {
          sleepBeforeNextStep(null);
          latestTask = fetchTaskData(id, latestData);
        } else {
          if (pointer >= answer.length()) {
            running = TASK_NOT_FINISH_STATUS.contains(task.getStatus());
            if (running) {
              sleepBeforeNextStep(null);
              latestTask = fetchTaskData(id, latestData);
            }
          }
          if (pointer > answer.length()) {
            pointer = answer.length();
          }
          String subStr2 = answer.substring(0, pointer);
          output.setThink(think);
          output.setAnswer(subStr2);
          pointer += 2;
          String line22 = streamDataToLine(output);
          streamOutput(response, line22);
          sleepBeforeNextStep(100L);
        }
      }
      if (!running) {
        break;
      }
    } while (checkTimeout(latestTask, startTime, lastUpdateTime));
    streamOutput(response, "data: [DONE]\n\n");
  }

  private void streamOutput(HttpServletResponse response, String message) throws Exception {
    response.setContentType("text/event-stream;charset=UTF-8");
    response.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8.name()));
    response.getOutputStream().flush();
  }

  public static String streamDataToLine(ChatStreamData streamData) {
    return "data: " + JSONObject.toJSONString(streamData) + "\n\n";
  }

  private void updateTaskStatus(Task task, StatusEnum statusEnum) {
    if (task != null) {
      task.setStatus(statusEnum);
      int update = this.taskManager.update(task);
      log.warn(
          "chat abort or timeout, task status update, id={}|status={}|result={}",
          new Object[] {task.getId(), statusEnum, Integer.valueOf(update)});
    }
  }

  private boolean checkTimeout(Task latestTask, Instant startTime, Date lastUpdateTime)
      throws TimeoutException {
    if (null != latestTask && StatusEnum.FINISH.equals(latestTask.getStatus())) {
      return true;
    }
    Instant instant = lastUpdateTime.toInstant();
    boolean updateTimeout =
        Duration.between(instant, Instant.now()).getSeconds()
            > this.updateTimeoutSeconds.longValue();
    boolean chatTimeout =
        Duration.between(startTime, Instant.now()).getSeconds()
            > this.chatTimeoutSeconds.longValue();
    if (updateTimeout || chatTimeout) {
      log.warn(
          "chat think timeout, updateTimeout={},chatTimeout={}",
          Boolean.valueOf(updateTimeout),
          Boolean.valueOf(chatTimeout));
      throw new TimeoutException("chat timeout");
    }
    return true;
  }

  private Task fetchTaskData(Long id, ChatStreamData chatStreamData) {
    Task result = this.taskManager.query(id);
    if (null == result || StatusEnum.ERROR.equals(result.getStatus())) {
      log.info("kag status error", id, result.getResultMessage());
      throw new SpgException(SpgMessageEnum.KAG_ERROR);
    }
    try {
      JSONObject jsonObject = JSONObject.parseObject(result.getResultMessage());
      chatStreamData.setAnswer(jsonObject.getString("answer"));
      chatStreamData.setThink(jsonObject.getString("think"));
      JSONObject metrics = jsonObject.getJSONObject("metrics");
      if (metrics != null) {
        chatStreamData.setThinkCost(metrics.getString("thinkCost"));
      }
      chatStreamData.setReasoner(jsonObject.getString("reasoner"));
      JSONArray subgraph = jsonObject.getJSONArray("subgraph");
      if (subgraph != null) {
        chatStreamData.setSubgraph(subgraph.toJavaList(JSONObject.class));
      }
      JSONArray reference = jsonObject.getJSONArray("reference");
      if (reference != null) {
        chatStreamData.setReference(reference.toJavaList(JSONObject.class));
      }
    } catch (Exception e) {
      log.warn(
          "fetchTaskData failed,{},{} ",
          null != result ? result.getResultMessage() : null,
          e.getMessage());
    }
    return result;
  }

  private Map fillQaParam(HashMap map) {
    String usePipeline = "default_pipeline";
    Object think = map.get("thinking_enabled");
    if (null != think && ((Boolean) think).booleanValue()) {
      usePipeline = "think_pipeline";
    }
    HashMap map2 = new HashMap();
    map2.put("usePipeline", usePipeline);
    return map2;
  }

  private String extractQuestionFromPrompts(List<Map<String, Object>> prompts)
      throws IllegalArgumentException {
    for (Map<String, Object> prompt : prompts) {
      String type = (String) prompt.get("type");
      String content = (String) prompt.get("content");
      if (type.equalsIgnoreCase("text") && StringUtils.isNotBlank(content)) {
        return content;
      }
    }
    log.warn("extractQuestionFromPrompts failed, prompts: {}", JSON.toJSONString(prompts));
    throw new IllegalArgumentException("Missing text content");
  }

  private void sleepBeforeNextStep(Long sleepTime) {
    Long sleepTime2;
    if (sleepTime == null) {
      try {
        sleepTime2 = this.chatOutputInterval;
        Thread.sleep(sleepTime2.longValue());
      } catch (InterruptedException e) {
        log.warn("doInputSleep failed, {}", e.getMessage());
        Thread.currentThread().interrupt();
        return;
      }
    } else {
      sleepTime2 = sleepTime;
    }
  }
}
