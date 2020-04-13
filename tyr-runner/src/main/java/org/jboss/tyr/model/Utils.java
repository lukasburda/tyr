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
package org.jboss.tyr.model;

public class Utils {

    public static final String GITHUB_BASE = "https://api.github.com";
    public static final String TYR_CONFIG_DIR = "tyr.config.dir";
    public static final String LINE_SEPARATOR = System.lineSeparator();
    public static final String GITHUB_LINE_SEPARATOR = "\\r\\n";

    //PR payload
    public static final String PULL_REQUEST = "pull_request";
    public static final String BODY = "body";
    public static final String HEAD = "head";
    public static final String SHA = "sha";
    public static final String TITLE = "title";
    public static final String COMMITS_URL = "commits_url";

    //Commit payload
    public static final String COMMIT = "commit";
    public static final String MESSAGE = "message";

    public static String getConfigDirectory() {
        String path = System.getProperty(TYR_CONFIG_DIR);
        if (path == null) {
            return System.getProperty("user.dir");
        }
        return path;
    }
}
