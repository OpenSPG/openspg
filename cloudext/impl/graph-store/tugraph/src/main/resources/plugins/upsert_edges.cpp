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

#include <iostream>
#include <chrono>
#include "lgraph/lgraph.h"
#include "tools/json.hpp"
//#include "fma-common/logger.h"

using json = nlohmann::json;

using namespace lgraph_api;
using namespace std;

VertexIterator findVertex(Transaction& txn, string label, string field, FieldData value) {
    vector<string> labels;
    if (label.find('|') != string::npos) {
        // multiple label
        istringstream iss(label);
        string l;
        while (getline(iss, l, '|')) {
            labels.push_back(l);
        }
    } else {
        labels.push_back(label);
    }
    for (auto l : labels) {
        try {
            auto vi = txn.GetVertexByUniqueIndex(l, field, value);
            return vi;
        } catch (const exception &e) {
            continue;
        }
    }
    throw -1;
}

FieldData getKgEIdValue(int64_t fromVertexId, int64_t toVertexId, int64_t version) {
   return FieldData(std::to_string(fromVertexId) + "|" + std::to_string(toVertexId) + "|" + std::to_string(version));
}

int64_t findVertexId(Transaction& txn, string label, string field, FieldData value) {
    try {
        auto vi = findVertex(txn, label, field, value);
        return vi.GetId();
    } catch (int e) {
        return -1;
    }
}

static FieldData JsonToFieldData(const json& j_object) {
    if (j_object.is_number_integer()) return FieldData(j_object.get<int64_t>());
    if (j_object.is_string()) return FieldData(j_object.get<string>());
    if (j_object.is_number_float()) return FieldData(j_object.get<float>());
    if (j_object.is_boolean()) return FieldData(j_object.get<bool>());
    return FieldData();
}

extern "C" LGAPI bool Process(GraphDB &db, const std::string &request, std::string &response) {
    try {
        int updateEdges = 0;
        auto t0 = std::chrono::steady_clock::now();
        json input = json::parse(request);
        string label = input["type"].get<string>();
        string fromLabel = input["srcType"].get<string>();
        string fromKey = input["srcKey"].get<string>();
        string toLabel = input["dstType"].get<string>();
        string toKey = input["dstKey"].get<string>();

        auto edgesCount = input["edges"].size();
        if (edgesCount == 0) {
            response = "No edges to update.";
            return true;
        }
        auto txn = db.CreateWriteTxn();
        auto labelId = txn.GetEdgeLabelId(label);
        if (labelId < 0) {
            response = "Label not found";
            return false;
        }
        for (auto& edge : input["edges"]) {
            FieldData fromId = JsonToFieldData(edge["srcId"]);
            FieldData toId = JsonToFieldData(edge["dstId"]);

            auto fromVertexId = findVertexId(txn, fromLabel, fromKey, fromId);
            if (fromVertexId < 0) {
//                FMA_LOG() << "Can not find vertex " << fromLabel << ":" << fromId.ToString();
                continue;
            }
            auto toVertexId = findVertexId(txn, toLabel, toKey, toId);
            if (toVertexId < 0) {
//                FMA_LOG() << "Can not find vertex " << toLabel << ":" << toId.ToString();
                continue;
            }

            int64_t version = edge["version"].get<int64_t>();
            FieldData kgEIdValue = getKgEIdValue(fromVertexId, toVertexId, version);

            vector<string> fieldNames;
            vector<FieldData> fieldValues;
            fieldNames.push_back("__eid__");
            fieldValues.push_back(kgEIdValue);
            for (auto& el : edge["properties"].items()) {
                fieldNames.push_back(el.key());
                fieldValues.push_back(JsonToFieldData(el.value()));
            }

            try {
                try {
                    auto edge = txn.GetEdgeByUniqueIndex(label, "__eid__", kgEIdValue);
                    edge.SetFields(fieldNames, fieldValues);
                } catch (const exception& e) {
                    // exception if edge not exist
                    txn.AddEdge(fromVertexId, toVertexId, label, fieldNames, fieldValues);
                }
            } catch (const exception &e) {
                response = string("Error on update edge ") + e.what();
                return false;
            }
            updateEdges++;
        }

        txn.Commit();
        json output;
        output["update_edges"] = updateEdges;
        response = output.dump();
        auto t2 = std::chrono::steady_clock::now();
        return true;
    } catch (const exception &e) {
        response = string("Error on processing: ") + e.what();
        return false;
    }
}
