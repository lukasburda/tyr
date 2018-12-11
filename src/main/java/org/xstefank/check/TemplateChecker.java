package org.xstefank.check;

import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.logging.Logger;
import org.xstefank.check.additional.AdditionalChecks;
import org.xstefank.model.Utils;
import org.xstefank.model.yaml.FormatConfig;
import org.xstefank.model.yaml.Format;

import java.util.ArrayList;
import java.util.List;

public class TemplateChecker {

    public static final String TEMPLATE_FORMAT_FILE = "template.format.file";
    private static final String IDENTIFY_ERROR_MESSAGE = "Pull request is not identified with [BUG] or [FEATURE]";
    private static final String BUG = "[BUG]";
    private static final String FEATURE = "[FEATURE]";
    private static final Logger log = Logger.getLogger(TemplateChecker.class);

    private List<Check> bugPRChecks;
    private List<Check> featurePRChecks;
    private FormatConfig config;

    public TemplateChecker(FormatConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Argument config cannot be null");
        }
        this.config = config;
        bugPRChecks = registerChecks(config.getBugFormat());
        featurePRChecks = registerChecks(config.getFeatureFormat());
    }

    public String checkPR(JsonNode payload) {
        log.info("checking PR");
        String prBody = payload.get(Utils.PULL_REQUEST).get(Utils.BODY).asText();
        if (prBody.contains(BUG)) {
            return runChecks(payload, bugPRChecks);
        } else if (prBody.contains(FEATURE)) {
            return runChecks(payload, featurePRChecks);
        }
        return IDENTIFY_ERROR_MESSAGE;
    }

    private String runChecks(JsonNode payload, List<Check> checks) {
        String description = "";
        for (Check check : checks) {
            String message = check.check(payload);
            if (message != null) {
                description = message;
                break;
            }
        }
        return description;
    }

    private List<Check> registerChecks(Format format) {
        List<Check> checks = new ArrayList<>();

        format = config.getDefaultFormat();

        if (format.getTitle() != null) {
            checks.add(new TitleCheck(format.getTitle()));
        }

        if (format.getDescription() != null) {
            checks.add(new RequiredRowsCheck(format.getDescription().getRequiredRows()));
        }

        if (format.getCommit() != null) {
            checks.add(new LatestCommitCheck(format.getCommit()));
        }

        for (String additional : format.getAdditional()) {
            checks.add(AdditionalChecks.findCheck(additional));
        }

        return checks;
    }
}
