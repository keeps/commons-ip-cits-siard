/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip-cits-siard
 */
package org.keeps.commonsipcitssiard.builder;


import org.keeps.commonsipcitssiard.cli.CSIPBuilderUtils;
import org.keeps.commonsipcitssiard.cli.model.SiardGroup;
import org.roda_project.commons_ip.utils.IPEnums;
import org.roda_project.commons_ip.utils.IPException;
import org.roda_project.commons_ip2.cli.model.args.MetadataGroup;
import org.roda_project.commons_ip2.cli.model.exception.SIPBuilderException;
import org.roda_project.commons_ip2.cli.utils.SIPBuilderUtils;
import org.roda_project.commons_ip2.model.IPContentInformationType;
import org.roda_project.commons_ip2.model.IPContentType;
import org.roda_project.commons_ip2.model.impl.eark.EARKSIP;
import org.roda_project.commons_ip2.model.impl.eark.out.writers.factory.ZipWriteStrategyFactory;
import org.roda_project.commons_ip2.model.impl.eark.out.writers.strategy.WriteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SIARDBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(SIARDBuilder.class);
  ;
  private List<MetadataGroup> metadataArgs = new ArrayList<>();
  private List<SiardGroup> representationArgs = new ArrayList<>();
  private boolean targetOnly;
  private String path;
  private String submitterAgentName;
  private String otherContentInformationType;
  private String submitterAgentId;
  private String sipId;
  private List<String> ancestors;
  private List<String> documentation = new ArrayList<>();

  private String softwareVersion;
  private Boolean overrideSchema;


  public SIARDBuilder setMetadataArgs(List<MetadataGroup> metadataArgs) {
    this.metadataArgs = metadataArgs;
    return this;
  }


  public SIARDBuilder setRepresentationArgs(List<SiardGroup> representationArgs) {
    this.representationArgs = representationArgs;
    return this;
  }


  public SIARDBuilder setSubmitterAgentName(String submitterAgentName) {
    this.submitterAgentName = submitterAgentName;
    return this;
  }

  public SIARDBuilder setOtherContentInformationType(String otherContentInformationType) {
    this.otherContentInformationType = otherContentInformationType;
    return this;
  }

  public void build() throws SIPBuilderException, IPException, InterruptedException {
    EARKSIP earksip = new EARKSIP();
    earksip.setProfile("https://citssiard.dilcis.eu/profile/E-ARK-SIARD-ROOT.xml");


    IPContentInformationType siard = new IPContentInformationType("citssiard_v1_0");


    earksip.addSubmitterAgent(submitterAgentName, submitterAgentId);

    earksip.setContentType(IPContentType.getDatabase());

    earksip.setContentInformationType(siard);


    earksip.setType(IPEnums.IPType.SIARD);


    try {
      SIPBuilderUtils.addMetadataGroupsToSIP(earksip, metadataArgs);
    } catch (IPException e) {
      LOGGER.debug("Cannot add metadata to the SIP", e);
      throw new SIPBuilderException("Cannot add metadata to the SIP.");
    }

    try {
      CSIPBuilderUtils.addRepresentationGroupsToSiardSIP(earksip, representationArgs, true);
    } catch (IPException e) {
      LOGGER.debug("Cannot add representation to the SIP", e);
      throw new SIPBuilderException("Cannot add representation to the SIP");
    }

    WriteStrategy writeStrategy = new ZipWriteStrategyFactory().create(Paths.get(System.getProperty("user.dir")));

    Path build = earksip.build(writeStrategy, null, IPEnums.SipType.SIARD);

    System.out.printf(build.toString());
  }
}
