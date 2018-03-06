package org.xstefank;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.jboss.logging.Logger;
import org.xstefank.check.TemplateChecker;
import org.xstefank.check.Violation;
import org.xstefank.model.StatusPayload;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

@Path("/")
public class WebHookEndpoint {

    private static final String GITHUB_BASE = "https://api.github.com";

    private static final Logger log = Logger.getLogger(WebHookEndpoint.class);

    private static String oauthToken;

    @Context
    private UriInfo uriInfo;

    static {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("config.properties");

        Properties config = new Properties();
        try {
            config.load(is);
        } catch (Exception e) {
            log.info("properties cannot be loaded");
        }

        oauthToken = config.getProperty("github.oauth.token");
        if (oauthToken == null) {
            oauthToken = System.getProperty("GITHUB_OAUTH_TOKEN");
        }
    }

    @POST
    @Path("/pull-request")
    @Consumes(MediaType.APPLICATION_JSON)
    public void processPullRequest(JsonNode pullRequestPayload) throws IOException, URISyntaxException {
        log.info("pr received");
        JsonNode pullRequest = pullRequestPayload.get("pull_request");

        List<Violation> violations = TemplateChecker.check(pullRequest.get("body").asText());

        if (!violations.isEmpty()) {
            log.info("invalid desc");
            GitHubClient client = new GitHubClient();
            client.setOAuth2Token(oauthToken);

            CommitService commitService = new CommitService(client);

            JsonNode head = pullRequest.get("head");
            RepositoryId repo = RepositoryId.createFromId(head.get("repo").get("full_name").asText());
            String sha = head.get("sha").asText();

            Client resteasyClient = ClientBuilder.newClient();
            URI statusUri = UriBuilder
                    .fromUri(GITHUB_BASE)
                    .path("/repos")
                    .path("/" + repo.generateId())
                    .path("/statuses")
                    .path("/" + sha)
                    .build();


            WebTarget target = resteasyClient.target(statusUri);

            Entity<StatusPayload> json = Entity.json(new StatusPayload("error",
                    uriInfo.getBaseUri().toString(), violations.toString(), "jboss-set"));

            Response response = target.request()
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Authorization", "token " + oauthToken)
                    .post(json);


            log.info("status updated " + response.getStatus() + response.getEntity() + response.toString());
        } else {
            log.info("valid desc");
            GitHubClient client = new GitHubClient();
            client.setOAuth2Token(oauthToken);

            CommitService commitService = new CommitService(client);

            JsonNode head = pullRequest.get("head");
            RepositoryId repo = RepositoryId.createFromId(head.get("repo").get("full_name").asText());
            String sha = head.get("sha").asText();

            Client resteasyClient = ClientBuilder.newClient();
            URI statusUri = UriBuilder
                    .fromUri(GITHUB_BASE)
                    .path("/repos")
                    .path("/" + repo.generateId())
                    .path("/statuses")
                    .path("/" + sha)
                    .build();


            WebTarget target = resteasyClient.target(statusUri);

            Entity<StatusPayload> json = Entity.json(new StatusPayload("success",
                    uriInfo.getBaseUri().toString(), "", "jboss-set"));

            Response response = target.request()
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .header("Authorization", "token " + oauthToken)
                    .post(json);


            log.info("status updated " + response.getStatus() + response.getEntity() + response.toString());

        }

    }

}
