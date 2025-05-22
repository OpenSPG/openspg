package com.antgroup.openspgapp.api.http.server.concept;

import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.api.http.server.HttpResult;
import com.antgroup.openspg.server.biz.common.util.AssertUtils;
import com.antgroup.openspgapp.biz.schema.AkgConceptManager;
import com.antgroup.openspgapp.biz.schema.dto.ConceptNodeDTO;
import com.antgroup.openspgapp.biz.schema.dto.ConceptTreeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"/concept/api/"})
@RestController
/* loaded from: com.antgroup.openspgapp-api-http-server-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/api/http/server/concept/AkgConceptController.class */
public class AkgConceptController {

  @Autowired private AkgConceptManager akgConceptManager;

  @GetMapping({"/getConceptTree.json"})
  public HttpResult<ConceptTreeDTO> getConceptTree(final Long projectId) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<ConceptTreeDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.concept.AkgConceptController.1
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("projectId", projectId);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public ConceptTreeDTO action() {
            return AkgConceptController.this.akgConceptManager.getConceptTree(projectId);
          }
        });
  }

  @GetMapping({"/getConceptDetail.json"})
  public HttpResult<ConceptNodeDTO> getConceptDetail(
      final String primaryKey, final String metaType) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<ConceptNodeDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.concept.AkgConceptController.2
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("primaryKey", primaryKey);
            AssertUtils.assertParamObjectIsNotNull("metaType", metaType);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public ConceptNodeDTO action() {
            return AkgConceptController.this.akgConceptManager.getConceptDetail(
                primaryKey, metaType);
          }
        });
  }

  @GetMapping({"/expandConcept.json"})
  public HttpResult<ConceptTreeDTO> expandConcept(final String primaryKey, final String metaType) {
    return HttpBizTemplate.execute2(
        new HttpBizCallback<ConceptTreeDTO>() { // from class:
          // com.antgroup.openspgapp.api.http.server.concept.AkgConceptController.3
          public void check() {
            AssertUtils.assertParamObjectIsNotNull("primaryKey", primaryKey);
            AssertUtils.assertParamObjectIsNotNull("metaType", metaType);
          }

          /* renamed from: action, reason: merged with bridge method [inline-methods] */
          public ConceptTreeDTO action() {
            return AkgConceptController.this.akgConceptManager.expandConcept(primaryKey, metaType);
          }
        });
  }
}
