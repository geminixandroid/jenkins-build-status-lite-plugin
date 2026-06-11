package org.jenkinsci.plugins.buildstatuslite.actions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.ExtensionList;
import hudson.Functions;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import java.io.IOException;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.buildstatuslite.extensionpoints.JobSelectorExtensionPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.kohsuke.stapler.HttpResponse;

@WithJenkins
class PublicBuildStatusActionSecurityTest {

    private JenkinsRule j;
    private FreeStyleProject job;

    @BeforeEach
    void createJob(JenkinsRule j, TestInfo info) throws IOException {
        this.j = j;
        job = j.createFreeStyleProject("job-" + info.getTestMethod().orElseThrow().getName());
        job.getBuildersList()
                .add(
                        Functions.isWindows()
                                ? new BatchFile("echo hello from a batch file")
                                : new Shell("echo hello from a shell"));
    }

    @BeforeEach
    void setupSecurity() {
        JenkinsRule.DummySecurityRealm securityRealm = j.createDummySecurityRealm();
        MockAuthorizationStrategy authStrategy = new MockAuthorizationStrategy()
                .grant(Jenkins.READ, Item.READ)
                .everywhere()
                .to("user")
                .grant(PublicBuildStatusAction.VIEW_STATUS)
                .everywhere()
                .to("admin");

        j.jenkins.setSecurityRealm(securityRealm);
        j.jenkins.setAuthorizationStrategy(authStrategy);
    }

    @Test
    void testDoIcon_WhenJobHasNoPermissions() throws Exception {
        JobSelectorExtensionPoint jobSelector = (String jobName) -> job;
        ExtensionList.lookup(JobSelectorExtensionPoint.class).add(0, jobSelector);

        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            HttpResponse response = new PublicBuildStatusAction()
                    .doIcon(null, null, job.getName(), null, null, null);
            assertThat(response.toString(), containsString("StatusImage"));
        }
    }
}
