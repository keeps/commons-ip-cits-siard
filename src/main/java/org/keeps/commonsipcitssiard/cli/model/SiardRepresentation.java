/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip-cits-siard
 */
package org.keeps.commonsipcitssiard.cli.model;

import picocli.CommandLine;

/**
 * @author Carlos Afonso <cafonso@keep.pt>
 */
public class SiardRepresentation {
  @CommandLine.Option(names = "--representation-data", required = true, paramLabel = "<path>", description = "Path to representation file")
  String representationData;
  @CommandLine.Option(names = "--representation-type", paramLabel = "<type>", description = "Representation type")
  String representationType;
  @CommandLine.Option(names = "--representation-id", paramLabel = "<id>", description = "Representation identifier. If not set a default value of rep<number> will be used")
  String representationId;
  @CommandLine.Option(names = "--representation-information-type", paramLabel = "<id>", description = "Representation identifier. If not set a default value of rep<number> will be used")
  String representationInformationType;


  public String getRepresentationData() {
    return representationData;
  }

  public String getRepresentationType() {
    return representationType;
  }

  public String getRepresentationId() {
    return representationId;
  }

  public String getRepresentationInformationType() { return representationInformationType; }

}
