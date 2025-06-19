package com.antgroup.openspgapp.api.http.server.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.common.constants.SpgAppConstant;
import com.antgroup.openspg.server.api.facade.dto.common.request.DataReasonerRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.ReasonerTaskRequest;
import com.antgroup.openspg.server.api.facade.dto.service.response.ReasonerTaskResponse;
import com.antgroup.openspg.server.common.model.data.DataRecord;
import com.antgroup.openspg.server.common.model.reasoner.ReasonerTask;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.antgroup.openspgapp.core.reasoner.model.task.result.TableResult;
import com.antgroup.openspgapp.server.api.facade.dto.reasoner.task.TaskResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/data/Utils.class */
public class Utils {
  private static final Logger log = LoggerFactory.getLogger(Utils.class);

  public static TaskResponse npmRow2TaskResponse(ReasonerTaskResponse response) {
    ReasonerTask task = response.getTask();
    TaskResponse res = new TaskResponse();
    res.setProjectId(response.getProjectId());
    res.setDsl(task.getDsl());
    res.setParams(task.getParams());
    res.setStatus(StatusEnum.valueOf(task.getStatus().name()));
    res.setResultMessage(task.getResultMessage());
    TableResult resultTable = new TableResult();
    resultTable.setTotal(
        null != task.getResultTableResult() ? task.getResultTableResult().getTotal() : 0L);
    resultTable.setHeader(
        null != task.getResultTableResult()
            ? task.getResultTableResult().getHeader()
            : new String[0]);
    resultTable.setRows(
        null != task.getResultTableResult()
            ? task.getResultTableResult().getRows()
            : new ArrayList());
    res.setResultTable(resultTable);
    List<Object[]> rows = resultTable.getRows();
    if (CollectionUtils.isEmpty(rows)) {
      return res;
    }
    String[] header = resultTable.getHeader();
    int sIndex = -1;
    int oIndex = -1;
    int pIndex = -1;
    for (int i = 0; i < header.length; i++) {
      if (Objects.equals(header[i], "n")) {
        sIndex = i;
      } else if (Objects.equals(header[i], "m")) {
        oIndex = i;
      } else if (Objects.equals(header[i], "p")) {
        pIndex = i;
      }
    }
    if (sIndex == -1 && oIndex == -1 && pIndex == -1) {
      throw new RuntimeException("not found " + Arrays.toString(header));
    }
    List<Node> nodes = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    Set<String> nodeFilter = new HashSet<>();
    for (Object[] objs : rows) {
      getNode(objs, sIndex, nodeFilter, nodes);
      getNode(objs, oIndex, nodeFilter, nodes);
      if (pIndex != -1) {
        JSONObject pObj = JSON.parseObject(objs[pIndex].toString());
        Edge edge = new Edge();
        edge.setLabel(pObj.getString("__label__"));
        edge.setFrom(pObj.getString("__from_internal_id__"));
        edge.setFromId(pObj.getString("__from_id__"));
        edge.setFromType(pObj.getString("__from_id_type__"));
        edge.setTo(pObj.getString("__to_internal_id__"));
        edge.setToId(pObj.getString("__to_id__"));
        edge.setToType(pObj.getString("__to_id_type__"));
        edge.setDocId(edge.getFrom() + edge.getLabel() + edge.getTo());
        edge.setId(edge.getFrom() + edge.getLabel() + edge.getTo());
        edges.add(edge);
      }
    }
    res.getResultTable().setRows(new ArrayList());
    res.setResultNodes(nodes);
    res.setResultEdges(edges);
    return res;
  }

  private static void getNode(Object[] objs, int index, Set<String> nodeFilter, List<Node> nodes) {
    if (index < 0) {
      return;
    }
    JSONObject obj = JSON.parseObject(objs[index].toString());
    if (nodeFilter.contains(obj.getString("__id__"))) {
      return;
    }
    Node node = new Node();
    Iterator ite = obj.keySet().iterator();
    while (ite.hasNext()) {
      String key = ite.next().toString();
      if ("__id__".equals(key)) {
        node.setId(obj.getString(key));
      } else if ("__label__".equals(key)) {
        node.setLabel(obj.getString(key));
      } else if (!SpgAppConstant.HIDDEN_PROPERTY.contains(key)) {
        node.getProperties().put(key, obj.get(key));
      }
    }
    node.setName(obj.getString("name"));
    nodeFilter.add(node.getId());
    nodes.add(node);
  }

  public static DataRecord toRecord(IdxRecord idxRecord) {
    DataRecord dataRecord = new DataRecord();
    dataRecord.setDocId(idxRecord.getDocId());
    dataRecord.setScore(idxRecord.getScore());
    dataRecord.setFields(idxRecord.getFields());
    Map<String, Object> labels = dataRecord.getFields();
    if (!labels.isEmpty()) {
      labels
          .keySet()
          .removeIf(
              key -> {
                return SpgAppConstant.HIDDEN_PROPERTY.contains(key);
              });
      if (null != labels.get("__labels__")) {
        Object obj = labels.get("__labels__");
        String[] arr = (String[]) obj;
        dataRecord.setLabel(arr[0]);
        labels.remove("__labels__");
        dataRecord.setFields(labels);
        if (null != labels.get("name")) {
          dataRecord.setName(labels.get("name").toString());
        }
      }
    }
    return dataRecord;
  }

  public static ReasonerTaskRequest toReasonerTask(DataReasonerRequest request) {
    ReasonerTaskRequest drt = new ReasonerTaskRequest();
    drt.setDsl(request.getDsl());
    drt.setProjectId(request.getProjectId());
    drt.setParams(request.getParams());
    return drt;
  }

  public static void addChunkRes(TaskResponse res, TaskResponse chunkRes) {
    List<Node> nodes = chunkRes.getResultNodes();
    if (CollectionUtils.isEmpty(nodes)) {
      return;
    }
    for (Node node : nodes) {
      if (node.getLabel().contains("Chunk")) {
        res.getResultNodes().add(node);
      }
    }
  }
}
