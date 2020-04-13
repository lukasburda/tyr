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
package org.jboss.tyr.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.github.GitHubService;
import org.jboss.tyr.check.SkipCheck;
import org.jboss.tyr.check.TemplateChecker;
import org.jboss.tyr.model.CommitStatus;
import org.jboss.tyr.model.TyrConfiguration;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.FormatYaml;
import org.jboss.tyr.verification.InvalidConfigurationException;
import org.jboss.tyr.verification.VerificationHandler;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Path("/")
@ApplicationScoped
public class WebHookEndpoint {

    @Inject
    TyrConfiguration configuration;

    @Inject
    GitHubService gitHubService;

    @Inject
    TemplateChecker templateChecker;

    @Inject
    SkipCheck skipCheck;

    private FormatYaml format;

    @PostConstruct
    public void init() {
        format = readConfig();
        templateChecker.init(format);
    }

    @POST
    @Path("/pull-request")
    @Consumes(MediaType.APPLICATION_JSON)
    public void processRequest(JsonObject payload) throws InvalidPayloadException {
        if (payload.getJsonObject(Utils.PULL_REQUEST) != null || !skipCheck.shouldSkip(payload, format)) {
            String errorMessage = templateChecker.checkPR(payload);
            if (errorMessage != null) {
                gitHubService.updateCommitStatus(format.getRepository(),
                        payload.getJsonObject(Utils.PULL_REQUEST).getJsonObject(Utils.HEAD).getString(Utils.SHA),
                        errorMessage.isEmpty() ? CommitStatus.SUCCESS : CommitStatus.ERROR,
                        format.getStatusUrl(),
                        errorMessage.isEmpty() ? "valid" : errorMessage, "PR format");
            }
        }
    }

    private FormatYaml readConfig() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        FormatYaml formatYaml;
        try {
            if (configuration.formatFileUrl().isPresent())
                formatYaml = mapper.readValue(new URL(configuration.formatFileUrl().get()).openStream(), FormatYaml.class);
            else {
                File configFile = new File(configuration.configFileName().orElse(Utils.getConfigDirectory() + "/format.yaml"));
                formatYaml = mapper.readValue(configFile, FormatYaml.class);
            }
            VerificationHandler.verifyConfiguration(formatYaml);
            return formatYaml;
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalArgumentException("Cannot load configuration file", e);
        }
    }
}
