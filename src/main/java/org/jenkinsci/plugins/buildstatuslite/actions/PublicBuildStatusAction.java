package org.jenkinsci.plugins.buildstatuslite.actions;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.UnprotectedRootAction;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.Permission;
import hudson.security.PermissionScope;
import hudson.util.HttpResponses;
import java.io.IOException;
import java.util.Arrays;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.buildstatuslite.Messages;
import org.jenkinsci.plugins.buildstatuslite.PluginImpl;
import org.jenkinsci.plugins.buildstatuslite.extensionpoints.JobSelectorExtensionPoint;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.WebMethod;

@Extension
public class PublicBuildStatusAction implements UnprotectedRootAction {
    public static final Permission VIEW_STATUS = new Permission(
            Item.PERMISSIONS, "ViewStatus", Messages._ViewStatus_Permission(), Item.READ, PermissionScope.ITEM);
    private static final Jenkins jInstance = Jenkins.get();

    public PublicBuildStatusAction() throws IOException {}

    @Override
    public String getUrlName() {
        return "buildStatus";
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @WebMethod(name = "icon")
    public HttpResponse doIcon(
            StaplerRequest2 req,
            StaplerResponse2 rsp,
            @QueryParameter String job,
            @QueryParameter String style,
            @QueryParameter String subject,
            @QueryParameter String status) {
        if (job == null) {
            return PluginImpl.iconRequestHandler.handleIconRequest(style, subject, status);
        }
        Job<?, ?> project = getProject(job, false);
        return PluginImpl.iconRequestHandler.handleIconRequestForJob(project, style, subject, status);
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(
            StaplerRequest2 req,
            StaplerResponse2 rsp,
            @QueryParameter String job,
            @QueryParameter String style,
            @QueryParameter String subject,
            @QueryParameter String status) {
        return doIcon(req, rsp, job, style, subject, status);
    }

    private static Job<?, ?> getProject(String job, boolean throwErrorWhenNotFound) {
        Job<?, ?> p = null;
        if (job != null) {
            try (ACLContext ctx = ACL.as2(ACL.SYSTEM2)) {
                for (JobSelectorExtensionPoint jobSelector : ExtensionList.lookup(JobSelectorExtensionPoint.class)) {
                    p = jobSelector.select(job);
                    if (p != null) {
                        break;
                    }
                }

                if (p == null) {
                    p = jInstance.getItemByFullName(job, Job.class);
                }

                // Fallback for Multibranch Pipeline branches whose names contain slashes.
                // Jenkins stores such branches with %2F in their item name (e.g. "feature%2Fmy-branch").
                // We retry by progressively treating more trailing segments as parts of a single
                // branch name joined with %2F, until we find a match.
                if (p == null && job.contains("/")) {
                    String[] parts = job.split("/");
                    for (int i = parts.length - 1; i >= 0 && p == null; i--) {
                        String parentPath = i > 0 ? String.join("/", Arrays.copyOf(parts, i)) : "";
                        String branchName = String.join("%2F", Arrays.copyOfRange(parts, i, parts.length));
                        String candidateName = parentPath.isEmpty() ? branchName : parentPath + "/" + branchName;
                        if (!candidateName.equals(job)) {
                            p = jInstance.getItemByFullName(candidateName, Job.class);
                        }
                    }
                }
            }
        }

        if (p == null || !p.hasPermission(VIEW_STATUS)) {
            if (throwErrorWhenNotFound) {
                throw HttpResponses.notFound();
            }
            return null;
        }

        return p;
    }
}
