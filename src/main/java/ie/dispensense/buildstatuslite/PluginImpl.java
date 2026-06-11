package ie.dispensense.buildstatuslite;

import hudson.Plugin;

public class PluginImpl extends Plugin {
    public static final IconRequestHandler iconRequestHandler = new IconRequestHandler();

    @Override
    public void start() throws Exception {}
}
