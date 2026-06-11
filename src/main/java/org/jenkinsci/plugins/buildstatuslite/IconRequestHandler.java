package org.jenkinsci.plugins.buildstatuslite;

import hudson.model.BallColor;
import hudson.model.Job;

@SuppressWarnings("rawtypes")
public class IconRequestHandler {

    private final ImageResolver iconResolver;
    private final ParameterResolver parameterResolver;

    public IconRequestHandler() {
        this.iconResolver = new ImageResolver();
        this.parameterResolver = new ParameterResolver();
    }

    public StatusImage handleIconRequest(String style, String subject, String status) {
        return iconResolver.getImage(BallColor.BLUE, style, subject, status, null, null, null);
    }

    public StatusImage handleIconRequestForJob(Job job, String style, String subject, String status) {
        if (job != null) {
            subject = parameterResolver.resolve(job, subject);
            status = parameterResolver.resolve(job, status);
            return iconResolver.getImage(job.getIconColor(), style, subject, status, null, null, null);
        } else {
            return iconResolver.getImage(BallColor.NOTBUILT, style, subject, null, null, null, null);
        }
    }
}
