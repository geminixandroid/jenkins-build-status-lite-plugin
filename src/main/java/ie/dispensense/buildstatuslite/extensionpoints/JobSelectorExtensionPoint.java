package ie.dispensense.buildstatuslite.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Job;

public interface JobSelectorExtensionPoint extends ExtensionPoint {
    Job select(String selector);
}
