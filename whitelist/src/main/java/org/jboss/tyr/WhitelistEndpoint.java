package org.jboss.tyr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jboss.tyr.model.TyrWhitelistConfiguration;
import org.jboss.tyr.model.Utils;
import org.jboss.tyr.model.yaml.FormatYaml;
import org.jboss.tyr.whitelist.WhitelistProcessing;

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
public class WhitelistEndpoint {

    @Inject
    TyrWhitelistConfiguration configuration;

    @Inject
    WhitelistProcessing whitelist;

    @PostConstruct
    public void init() {
        whitelist.init(readConfig());
    }

    @POST
    @Path("/whitelist")
    @Consumes(MediaType.APPLICATION_JSON)
    public void processRequest(JsonObject payload) throws InvalidPayloadException {
        if (payload.getJsonObject(Utils.ISSUE) == null) {
            return;
        }
        whitelist.processPRComment(payload);
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
            return formatYaml;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot load configuration file", e);
        }
    }
}
