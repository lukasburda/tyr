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
package org.jboss.tyr.check;

import org.jboss.tyr.InvalidPayloadException;
import org.jboss.tyr.TestUtils;
import org.jboss.tyr.model.yaml.Format;
import org.jboss.tyr.model.yaml.FormatYaml;
import org.jboss.tyr.model.yaml.SkipPatterns;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

// Temporarily disable powermock tests as it doesn't support JUnit 5 yet

public class SkipCheckTest {

    private static FormatYaml formatYaml;
    private SkipPatterns skipPatterns;

    @BeforeEach
    public void before() {
        skipPatterns = new SkipPatterns();
//        PowerMockito.suppress(method(GitHubAPI.class, TestUtils.READ_TOKEN));
//        PowerMockito.stub(method(GitHubAPI.class, TestUtils.GET_JSON_WITH_COMMITS, JsonObject.class)).toReturn(TestUtils.TEST_COMMITS_PAYLOAD);
    }

    @Test
    public void testNullConfigParameter() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, null));
    }

    @Test
    public void testNullPayloadParameter() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> SkipCheck.shouldSkip(null, formatYaml));
    }

    @Test
    public void testSkipByTitleRegexMatch() throws InvalidPayloadException {
        skipPatterns.setTitle(Pattern.compile("Test PR"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assertions.assertTrue(SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml), "Method cannot match valid title regex");
    }

    @Test
    public void testSkipByTitleRegexNonMatch() throws InvalidPayloadException {
        skipPatterns.setTitle(Pattern.compile("can't.*match.*this"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assertions.assertFalse(SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml), "Method matched invalid title regex");
    }

    @Test
    @Disabled("powermock")
    public void testSkipByCommitRegexMatch() throws InvalidPayloadException {
        skipPatterns.setCommit(Pattern.compile("Test commit"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assertions.assertTrue(SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml), "Method cannot match valid commit regex");
    }

    @Test
    @Disabled("powermock")
    public void testSkipByCommitRegexNonMatch() throws InvalidPayloadException {
        skipPatterns.setCommit(Pattern.compile("can't.*match.*this"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assertions.assertFalse(SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml), "Method matched invalid commit regex");
    }

    @Test
    public void testSkipByPullRequestDescriptionRegexMatch() throws InvalidPayloadException {
        skipPatterns.setDescription(Pattern.compile("Test description"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assertions.assertTrue(SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml), "Method cannot match valid description regex");
    }

    @Test
    public void testSkipByPullRequestDescriptionRegexNonMatch() throws InvalidPayloadException {
        skipPatterns.setDescription(Pattern.compile("can't.*match.*this"));
        formatYaml = setUpFormatConfig(skipPatterns);

        Assertions.assertFalse(SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml), "Method matched invalid description regex");
    }

    @Test
    public void testShouldSkipEmptySkipPatterns() throws InvalidPayloadException {
        formatYaml = setUpFormatConfig(skipPatterns);

        Assertions.assertFalse(SkipCheck.shouldSkip(TestUtils.TEST_PAYLOAD, formatYaml), "Invalid result after empty skipping patterns");
    }

    private static FormatYaml setUpFormatConfig(SkipPatterns testSkipPatterns) {
        Format testFormat = new Format();
        testFormat.setSkipPatterns(testSkipPatterns);
        FormatYaml testFormatYaml = new FormatYaml();
        testFormatYaml.setFormat(testFormat);
        return testFormatYaml;
    }
}
