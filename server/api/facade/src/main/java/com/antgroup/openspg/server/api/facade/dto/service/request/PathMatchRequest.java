/*
 * Ant Group
 * Copyright (c) 2004-2024 All Rights Reserved.
 */
package com.antgroup.openspg.server.api.facade.dto.service.request;

import com.antgroup.openspg.cloudext.interfaces.graphstore.cmd.PathMatchQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathMatchRequest {

    private Long projectId;

    private String typeName;

    private List<String> bizIds;

    private PathMatchQuery.VertexMatchRule startVertexRule;

    private List<PathMatchQuery.HopMatchRule> hops;

    private PathMatchQuery.PageRule pageRule;

    private PathMatchQuery.SortRule sortRule;

}