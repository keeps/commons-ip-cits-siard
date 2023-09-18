package org.keeps.commonsipcitssiard.cli.providers;

import picocli.CommandLine;

import java.util.Collections;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class VersionProvider implements CommandLine.IVersionProvider {
  @Override
  public String[] getVersion() {
    String implementationVersion = this.getClass().getPackage().getImplementationVersion();
    String version = String.format("commons-ip-cits-siard version %s", implementationVersion);
    return Collections.singletonList(version).toArray(String[]::new);
  }
}
