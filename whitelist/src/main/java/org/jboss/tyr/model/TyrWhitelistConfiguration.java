package org.jboss.tyr.model;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

@ConfigProperties(prefix = "tyr")
public interface TyrWhitelistConfiguration {

    @ConfigProperty(name = "template.format.url")
    Optional<String> formatFileUrl();

    @ConfigProperty(name = "template.format.file")
    Optional<String> configFileName();

    @ConfigProperty(name = "github.oauth.token")
    String oauthToken();

    @ConfigProperty(name = "users.dir")
    Optional<String> whitelistDir();

    @ConfigProperty(name = "additional-resources")
    Optional<String> additionalResources();
}
