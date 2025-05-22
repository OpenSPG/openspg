package com.antgroup.openspgapp.api.http.server.reasoner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClient;
import com.antgroup.openspg.cloudext.interfaces.objectstorage.ObjectStorageClientDriverManager;
import com.antgroup.openspg.common.util.Md5Utils;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspg.server.common.service.config.DefaultValue;
import com.antgroup.openspg.server.common.service.project.ProjectService;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.reasoner.DialogManager;
import com.antgroup.openspgapp.biz.reasoner.TaskManager;
import com.antgroup.openspgapp.core.builder.model.CaPipeline;
import com.antgroup.openspgapp.core.reasoner.model.Instruction;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.Task;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.QueryTaskRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.ReportCompletionRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.ReportLogRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.ReportPipelineRequest;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.TaskResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping({"/public/v1/reasoner/dialog"})
@Controller
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/reasoner/DialogController.class */
public class DialogController extends BaseController {

  @Autowired private TaskManager taskManager;

  @Autowired private ProjectService projectService;

  @Autowired private DialogManager dialogManager;

  @Autowired private DefaultValue value;

  @RequestMapping(
      value = {"/submit"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<TaskResponse> submit(@RequestBody final Instruction instruction) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.1
          public void check() {}

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            Map<String, String> params = new HashMap<>();
            JSONArray message = new JSONArray();
            Task task =
                DialogController.this.dialogManager.submit(
                    instruction.getSessionId(),
                    instruction.getInstruction(),
                    instruction.getInstruction(),
                    message.toJSONString(),
                    params);
            return Utils.convert(task);
          }
        });
  }

  @RequestMapping(
      value = {"/query"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<TaskResponse> query(final QueryTaskRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<TaskResponse>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("id", request.getId());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public TaskResponse action() {
            Task task = DialogController.this.taskManager.query(request.getId());
            return Utils.convert(task);
          }
        });
  }

  @RequestMapping(
      value = {"/report"},
      method = {RequestMethod.GET})
  @ResponseBody
  public HttpResult<Boolean> report(final ReportLogRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("taskId", request.getTaskId());
            AssertUtils.assertParamObjectIsNotNull("content", request.getContent());
            AssertUtils.assertParamObjectIsNotNull("executeStatus", request.getExecuteStatus());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            Task task = DialogController.this.taskManager.query(request.getTaskId());
            if (task == null) {
              throw new RuntimeException("task not found id:" + request.getTaskId());
            }
            String resultMessage = task.getResultMessage();
            JSONArray message = JSONArray.parseArray(resultMessage);
            message.add(request);
            Task updateTask = new Task();
            updateTask.setId(task.getId());
            updateTask.setResultMessage(message.toJSONString());
            DialogController.this.taskManager.update(updateTask);
            return Boolean.TRUE;
          }
        });
  }

  @RequestMapping(
      value = {"/report/markdown"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Boolean> reportMarkDown(@RequestBody final ReportLogRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.4
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("taskId", request.getTaskId());
            AssertUtils.assertParamObjectIsNotNull("content", request.getContent());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            Task task = DialogController.this.taskManager.query(request.getTaskId());
            if (task == null) {
              throw new RuntimeException("task not found id:" + request.getTaskId());
            }
            Task updateTask = new Task();
            updateTask.setId(task.getId());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "markdown");
            if (CollectionUtils.isNotEmpty(request.getSubgraph())) {
              jsonObject.put("subgraph", request.getSubgraph());
            }
            jsonObject.put("content", request.getContent());
            updateTask.setResultMessage(jsonObject.toJSONString());
            DialogController.this.taskManager.update(updateTask);
            return Boolean.TRUE;
          }
        });
  }

  @RequestMapping(
      value = {"/report/completions"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Boolean> reportCompletion(@RequestBody final ReportCompletionRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.5
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("taskId", request.getTaskId());
            AssertUtils.assertParamObjectIsNotNull("content", request.getContent());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            Task task = DialogController.this.taskManager.query(request.getTaskId());
            if (task == null) {
              throw new RuntimeException("task not found id:" + request.getTaskId());
            }
            Task updateTask = new Task();
            updateTask.setId(task.getId());
            updateTask.setStatus(request.getStatusEnum());
            updateTask.setResultMessage(JSONObject.toJSONString(request.getContent()));
            DialogController.this.taskManager.update(updateTask);
            return Boolean.TRUE;
          }
        });
  }

  @RequestMapping(
      value = {"/report/pipeline"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Boolean> reportPipeline(@RequestBody final ReportPipelineRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.6
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("pipeline", request.getPipeline());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            Task task = DialogController.this.taskManager.query(request.getTaskId());
            if (task == null) {
              throw new RuntimeException("task not found id:" + request.getTaskId());
            }
            Task updateTask = new Task();
            updateTask.setId(task.getId());
            CaPipeline pipeline = request.getPipeline();
            pipeline.setType("pipeline");
            updateTask.setResultMessage(JSON.toJSONString(pipeline));
            DialogController.this.taskManager.update(updateTask);
            return Boolean.TRUE;
          }
        });
  }

  @RequestMapping(
      value = {"/report/node"},
      method = {RequestMethod.POST})
  @ResponseBody
  public HttpResult<Boolean> reportNode(@RequestBody final ReportPipelineRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Boolean>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.7
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamObjectIsNotNull("node", request.getNode());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Boolean action() {
            Task task = DialogController.this.taskManager.query(request.getTaskId());
            if (task == null) {
              throw new RuntimeException("task not found id:" + request.getTaskId());
            }
            String resultMessage = task.getResultMessage();
            CaPipeline pipeline =
                StringUtils.isBlank(resultMessage)
                    ? new CaPipeline()
                    : (CaPipeline) JSON.parseObject(resultMessage, CaPipeline.class);
            CaPipeline.Node node = request.getNode();
            CaPipeline.Node oldNode = pipeline.getNode(node.getId());
            if (oldNode == null) {
              throw new RuntimeException("pipeline not found node id:" + node.getId());
            }
            oldNode.setQuestion(node.getQuestion());
            oldNode.setAnswer(node.getAnswer());
            oldNode.setState(node.getState());
            oldNode.setLogs(node.getLogs());
            oldNode.setTitle(node.getTitle());
            oldNode.setSubgraph(node.getSubgraph());
            boolean finish = true;
            boolean lastFinish = false;
            for (CaPipeline.Node node1 : pipeline.getNodes()) {
              if (!StatusEnum.FINISH.name().equalsIgnoreCase(node1.getState())) {
                finish = false;
              }
              if ("0".equals(node1.getId())
                  && StatusEnum.FINISH.name().equalsIgnoreCase(node1.getState())) {
                lastFinish = true;
              }
            }
            Task updateTask = new Task();
            updateTask.setId(task.getId());
            if (finish || lastFinish) {
              updateTask.setStatus(StatusEnum.FINISH);
            }
            updateTask.setResultMessage(JSON.toJSONString(pipeline));
            DialogController.this.taskManager.update(updateTask);
            return Boolean.TRUE;
          }
        });
  }

  @PostMapping({"/uploadFile"})
  @ResponseBody
  public HttpResult<String> uploadFile(@RequestParam("file") final MultipartFile file) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<String>() { // from class:
          // com.antgroup.openspgapp.api.http.server.reasoner.DialogController.8
          public void check() {
            AssertUtils.assertParamIsTrue("file", !file.isEmpty());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public String action() {
            try {
              String fileName =
                  org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
              String url = DialogController.this.upload(file, fileName);
              return url;
            } catch (IOException e) {
              throw new RuntimeException("uploadFile IOException:" + e.getMessage(), e);
            }
          }
        });
  }

  /* JADX INFO: Access modifiers changed from: private */
  public String upload(MultipartFile file, String fileName) throws IOException {
    StringBuffer path = new StringBuffer();
    String uuid = Md5Utils.md5Of(new String[] {UUID.randomUUID().toString()});
    path.append("upload")
        .append(File.separator)
        .append(uuid)
        .append(File.separator)
        .append(fileName);
    ObjectStorageClient objectStorageClient =
        ObjectStorageClientDriverManager.getClient(this.value.getObjectStorageUrl());
    objectStorageClient.saveFile(
        this.value.getUploadBucketName(), file.getInputStream(), file.getSize(), path.toString());
    String url =
        objectStorageClient.getUrlWithoutExpiration(
            this.value.getUploadBucketName(), path.toString());
    return url;
  }
}
