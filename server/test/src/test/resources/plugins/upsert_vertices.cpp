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
#include "fma-common/logger.h"

using json = nlohmann::json;

using namespace lgraph_api;
using namespace std;

static FieldData JsonToFieldData(const json& j_object) {
    if (j_object.is_number_integer()) return FieldData(j_object.get<int64_t>());
    if (j_object.is_string()) return FieldData(j_object.get<string>());
    if (j_object.is_number_float()) return FieldData(j_object.get<float>());
    if (j_object.is_boolean()) return FieldData(j_object.get<bool>());
    return FieldData();
}

extern "C" LGAPI bool Process(GraphDB &db, const std::string &request, std::string &response) {
    try {
        auto t0 = std::chrono::steady_clock::now();
        int add_nodes = 0;
        int update_nodes = 0;
        json input = json::parse(request);
        string node_type = input["type"].get<string>();
        string primary_field = input["key"].get<string>();
        auto nodes_count = input["nodes"].size();
        //cout << "Gonna start to update nodes for " << node_type << ", update size:" << nodes_count << endl;
        if (nodes_count == 0) {
            response = "No nodes to update.";
            return true;
        }

        auto txn = db.CreateWriteTxn();
        for (auto& node : input["nodes"]) {
            auto id = JsonToFieldData(node[primary_field]);
            vector<string> field_names;
            vector<FieldData> field_values;
            for (auto& el : node["properties"].items()) {
                field_names.push_back(el.key());
                field_values.push_back(JsonToFieldData(el.value()));
            }
            try {
                // try to find the vertex and update the property
                auto vit = txn.GetVertexByUniqueIndex(node_type, primary_field, id);
                vit.SetFields(field_names, field_values);
                update_nodes++;
            } catch (const exception&) {
                // vertex not found, insert a node
                field_names.push_back(primary_field);
                field_values.push_back(id);
                txn.AddVertex(node_type, field_names, field_values);
                add_nodes++;
            }
        }
        txn.Commit();
        json output;
        output["add_nodes"] = add_nodes;
        output["update_nodes"] = update_nodes;
        response = output.dump();
        return true;
    } catch (const exception &e) {
        response = string("Error on processing: ") + e.what();
        return false;
    }
}
