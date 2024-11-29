/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip-cits-siard
 */
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
