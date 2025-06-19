package com.antgroup.openspgapp.api.http.server.schema;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspgapp.api.http.server.BaseController;
import com.antgroup.openspgapp.biz.schema.AppSchemaManager;
import com.antgroup.openspgapp.biz.schema.dto.EntityTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.ProjectSchemaDTO;
import com.antgroup.openspgapp.biz.schema.dto.RelationTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.SchemaTreeDTO;
import com.antgroup.openspgapp.server.api.facade.dto.schema.SchemaSaveRequest;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"/v1/schemas"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/schema/SchemasController.class */
public class SchemasController extends BaseController {

  @Autowired private AppSchemaManager appSchemaManager;

  @GetMapping({"/tree/{projectId}"})
  public HttpResult<SchemaTreeDTO> getSchemaTreeWithProject(@PathVariable final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<SchemaTreeDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public SchemaTreeDTO action() {
            SchemaTreeDTO treeDTO =
                SchemasController.this.appSchemaManager.getProjectEntityType(projectId);
            return treeDTO;
          }
        });
  }

  @GetMapping({"/graph/{projectId}"})
  public HttpResult<ProjectSchemaDTO> getSchemaAndRelation(@PathVariable final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<ProjectSchemaDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public ProjectSchemaDTO action() {
            ProjectSchemaDTO simpleProjectSchema =
                SchemasController.this.appSchemaManager.getProjectSchemaDetail(projectId);
            return simpleProjectSchema;
          }
        });
  }

  @GetMapping({"/entity/{id}"})
  public HttpResult<EntityTypeDTO> getSchemaEntityProperties(@PathVariable final Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<EntityTypeDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public EntityTypeDTO action() {
            EntityTypeDTO entityTypeDTO =
                SchemasController.this.appSchemaManager.getEntityTypeById(id);
            return entityTypeDTO;
          }
        });
  }

  @GetMapping({"/relation/{id}"})
  public HttpResult<RelationTypeDTO> getSchemaRelationProperties(@PathVariable final Long id) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<RelationTypeDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.4
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("id", id);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public RelationTypeDTO action() {
            return SchemasController.this.appSchemaManager.getRelationTypeById(id);
          }
        });
  }

  @PostMapping
  @ResponseBody
  public HttpResult<Map<String, Object>> saveSchema(@RequestBody final SchemaSaveRequest request) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Map<String, Object>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.5
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("request", request);
            AssertUtils.assertParamStringIsNotBlank("data", request.getData());
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Map<String, Object> action() {
            return SchemasController.this.appSchemaManager.saveSchema(request.getData());
          }
        });
  }

  @GetMapping({"/getSchemaScript"})
  public HttpResult<String> getSchemaScript(final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<String>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.6
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public String action() {
            return SchemasController.this.appSchemaManager.getSchemaScript(projectId);
          }
        });
  }

  @GetMapping({"/getSchemaNameMap"})
  public HttpResult<Map<String, String>> getSchemaNameMap(final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Map<String, String>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.7
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Map<String, String> action() {
            return SchemasController.this.appSchemaManager.getSchemaPropertyNameMap(projectId);
          }
        });
  }

  @GetMapping({"/getProjectDetailSchemaInfo"})
  public HttpResult<ProjectSchemaDTO> getProjectDetailSchemaInfo(final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<ProjectSchemaDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.8
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public ProjectSchemaDTO action() {
            ProjectSchemaDTO projectSchemaDetail =
                SchemasController.this.appSchemaManager.getProjectSchemaDetail(projectId);
            return projectSchemaDetail;
          }
        });
  }

  @GetMapping({"/getDynamicConfig"})
  public HttpResult<Map<String, JSONObject>> getDynamicConfig(
      final String type, @RequestParam final List<Long> ids) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<Map<String, JSONObject>>() { // from class:
          // com.antgroup.openspgapp.api.http.server.schema.SchemasController.9
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("type", type);
            AssertUtils.assertParamCollectionIsNotEmpty("ids", ids);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public Map<String, JSONObject> action() {
            return SchemasController.this.appSchemaManager.getDynamicConfig(type, ids);
          }
        });
  }
}
