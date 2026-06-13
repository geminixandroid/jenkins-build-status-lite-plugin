package org.jenkinsci.plugins.buildstatuslite.actions;

import hudson.model.Action;
import hudson.model.Job;
import org.jenkins.ui.icon.IconSpec;
import org.jenkinsci.plugins.buildstatuslite.ImageResolver;
import org.jenkinsci.plugins.buildstatuslite.Messages;
import org.jenkinsci.plugins.buildstatuslite.PluginImpl;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.WebMethod;

@SuppressWarnings("rawtypes")
public class JobBadgeAction implements Action, IconSpec {
    public final Job project;

    public JobBadgeAction(Job project) {
        this.project = project;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getIconClassName() {
        return "symbol-shield-outline plugin-ionicons-api";
    }

    @Override
    public String getDisplayName() {
        return Messages.JobBadgeAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "badge";
    }

    public String getUrl() {
        String url = "";
        StaplerRequest2 req = Stapler.getCurrentRequest2();
        if (req != null && req.getRequestURL() != null) {
            url = req.getRequestURL().toString();
            int badgeIndex = url.lastIndexOf("badge/");
            if (badgeIndex != -1) {
                url = url.substring(0, badgeIndex);
            }
        }
        return url;
    }

    @WebMethod(name = "icon")
    public HttpResponse doIcon(@QueryParameter String style,
                               @QueryParameter String subject,
                               @QueryParameter String status) {
        return PluginImpl.iconRequestHandler.handleIconRequestForJob(project, style, subject, status);
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(@QueryParameter String style,
                                     @QueryParameter String subject,
                                     @QueryParameter String status) {
        return doIcon(style, subject, status);
    }

    public String getUrlEncodedFullName() {
        if (project == null) {
            return "";
        }
        return project.getFullName().replace("%2F", "/");
    }

    @Restricted(NoExternalUse.class)
    public String getStatus() {
        return ImageResolver.getStatus(project.getIconColor());
    }

    @Restricted(NoExternalUse.class)
    public String getColorVariable() {
        String colorName = project.getIconColor().getIconName();
        if (colorName.contains("-anime")) {
            return "light-blue";
        }
        return switch (colorName) {
            case "blue" -> "light-green";
            case "aborted", "disabled", "notbuilt" -> "text-color-secondary";
            default -> "light-" + colorName;
        };
    }
}
