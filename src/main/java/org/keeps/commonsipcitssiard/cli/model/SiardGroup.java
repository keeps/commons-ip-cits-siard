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
