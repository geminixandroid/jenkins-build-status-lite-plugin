package org.jenkinsci.plugins.buildstatuslite.actions;

import hudson.model.Action;
import hudson.model.Job;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.jenkins.ui.icon.IconSpec;
import org.jenkinsci.plugins.buildstatuslite.ImageResolver;
import org.jenkinsci.plugins.buildstatuslite.Messages;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

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
        return "buildStatus";
    }

    public String getUrlEncodedFullName() {
        if (project == null) {
            return "";
        }
        return URLEncoder.encode(project.getFullName(), StandardCharsets.UTF_8);
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
