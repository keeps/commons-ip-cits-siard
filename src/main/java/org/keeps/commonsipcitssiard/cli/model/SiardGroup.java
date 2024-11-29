/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip-cits-siard
 */
package org.keeps.commonsipcitssiard.cli.model;

import org.roda_project.commons_ip2.cli.model.args.Representation;
import picocli.CommandLine;

/**
 * @author Carlos Afonso <cafonso@keep.pt>
 */
public class SiardGroup {
  @CommandLine.ArgGroup(exclusive = false)
  SiardRepresentation representation;

  public SiardRepresentation getRepresentation() {
    return representation;
  }
}
