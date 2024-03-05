package com.antgroup.openspg.server.api.http.server.openapi;

import com.antgroup.openspg.cloudext.interfaces.searchengine.model.idx.record.IdxRecord;
import com.antgroup.openspg.server.api.facade.dto.service.request.SPGTypeSearchRequest;
import com.antgroup.openspg.server.api.http.server.BaseController;
import com.antgroup.openspg.server.api.http.server.HttpBizCallback;
import com.antgroup.openspg.server.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.server.biz.service.SearchManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/public/v1/search")
public class SearchController extends BaseController {

  @Autowired private SearchManager searchManager;

  @RequestMapping(method = RequestMethod.GET, value = "/spgType")
  public ResponseEntity<Object> spgTypeSearch(SPGTypeSearchRequest request) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<List<IdxRecord>>() {
          @Override
          public void check() {}

          @Override
          public List<IdxRecord> action() {
            return searchManager.spgTypeSearch(request);
          }
        });
  }
}
