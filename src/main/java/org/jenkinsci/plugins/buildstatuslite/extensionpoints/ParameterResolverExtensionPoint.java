package org.jenkinsci.plugins.buildstatuslite.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Actionable;

public interface ParameterResolverExtensionPoint extends ExtensionPoint {
    String resolve(Actionable actionable, String parameter);
}
