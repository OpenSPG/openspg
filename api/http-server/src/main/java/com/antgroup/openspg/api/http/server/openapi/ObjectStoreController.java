/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.api.http.server.openapi;

import com.antgroup.openspg.api.facade.dto.common.request.ObjectStoreRequest;
import com.antgroup.openspg.api.facade.dto.common.response.ObjectStoreResponse;
import com.antgroup.openspg.api.http.server.HttpBizCallback;
import com.antgroup.openspg.api.http.server.HttpBizTemplate;
import com.antgroup.openspg.biz.common.ObjectStoreManager;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/public/v1/objectStore")
public class ObjectStoreController {

  @Autowired private ObjectStoreManager objectStoreManager;

  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Object> upload(ObjectStoreRequest request, MultipartFile file) {
    return HttpBizTemplate.execute(
        new HttpBizCallback<ObjectStoreResponse>() {
          @Override
          public void check() {}

          @Override
          public ObjectStoreResponse action() {
            InputStream inputStream = null;
            try {
              inputStream = file.getInputStream();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            return objectStoreManager.objectStore(request, inputStream);
          }
        });
  }
}
