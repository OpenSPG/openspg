package com.antgroup.openspg.server.api.http.server.openapi;

import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.EdgeRecord;
import com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record.VertexRecord;
import com.antgroup.openspg.server.api.facade.dto.service.request.RelationSamplingRequest;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSamplingRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.service.SamplingManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/public/v1/sampling")
public class SamplingController extends BaseController {

  @Autowired private SamplingManager samplingManager;

  @RequestMapping(method = RequestMethod.GET, value = "/spgType")
  public ResponseEntity<Object> spgTypeSampling(SPGTypeSamplingRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<VertexRecord>>() {
          @Override
          public void check() {}

          @Override
          public List<VertexRecord> action() {
            return samplingManager.spgTypeSampling(request);
          }
        });
  }

  @RequestMapping(method = RequestMethod.GET, value = "/relation")
  public ResponseEntity<Object> relationSampling(RelationSamplingRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<EdgeRecord>>() {
          @Override
          public void check() {}

          @Override
          public List<EdgeRecord> action() {
            return samplingManager.relationSampling(request);
          }
        });
  }
}
