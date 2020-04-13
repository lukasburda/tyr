/*
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.tyr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jboss.tyr.model.yaml.FormatYaml;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class TestUtils {

    public static final String YAML_DIR = "yaml";
    public static final String JSON_DIR = "json";

    public static final JsonObject TEST_PAYLOAD = loadJson(JSON_DIR + "/testPayload.json");
    public static final JsonObject PULL_REQUEST_PAYLOAD = loadJson(JSON_DIR + "/testPullRequestPayload.json");
    public static final JsonObject EMPTY_PAYLOAD = createEmptyJsonPayload();

    public static final JsonArray TEST_COMMITS_PAYLOAD = loadJsonArray(JSON_DIR + "/testCommitsPayload.json");

    public static final FormatYaml FORMAT_CONFIG = loadFormatFromYamlFile(YAML_DIR + "/testTemplate.yaml");

    public static FormatYaml loadFormatFromYamlFile(String fileName) {
        try {
            File file = getFile(fileName);
            return new ObjectMapper(new YAMLFactory()).readValue(file, FormatYaml.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load file " + fileName);
        }
    }

    private static JsonObject loadJson(String fileName) {
        try {
            return Json.createReader(new FileReader(getFile(fileName))).readObject();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot load json", e);
        }
    }

    private static JsonArray loadJsonArray(String fileName) {
        try {
            return Json.createReader(new FileReader(getFile(fileName))).readArray();
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Cannot load json", e);
        }
    }

    private static JsonObject createEmptyJsonPayload() {
        return Json.createObjectBuilder().build();
    }

    private static File getFile(String fileName) {
        try {
            String path = TestUtils.class.getClassLoader().getResource(fileName).getFile();
            return new File(URLDecoder.decode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot get file " + fileName, e);
        }
    }
}
