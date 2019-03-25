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
package org.xstefank.whitelist;

import com.fasterxml.jackson.databind.JsonNode;
import org.xstefank.api.GitHubAPI;

public class RetestFailedCommand implements Command {

    public static final String NAME = "RetestFailedCommand";

    private String commandRegex;

    @Override
    public void process(JsonNode payload, WhitelistProcessing whitelistProcessing) {
        String pullRequestAuthor = whitelistProcessing.getPRAuthor(payload);
        String commentAuthor = whitelistProcessing.getCommentAuthor(payload);

        if (whitelistProcessing.isUserOnUserList(pullRequestAuthor) &&
                whitelistProcessing.isUserEligibleToRunCI(commentAuthor)) {
            JsonNode prPayload = GitHubAPI.getJsonWithPullRequest(payload);
            whitelistProcessing.triggerFailedCI(prPayload);
        }
    }

    @Override
    public String getCommandRegex() {
        return commandRegex;
    }

    @Override
    public void setCommandRegex(String commandRegex) {
        this.commandRegex = commandRegex;
    }
}