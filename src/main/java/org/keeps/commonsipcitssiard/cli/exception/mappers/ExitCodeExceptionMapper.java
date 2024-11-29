/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip-cits-siard
 */
package org.keeps.commonsipcitssiard.cli.exception.mappers;

import picocli.CommandLine;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class ExitCodeExceptionMapper implements CommandLine.IExitCodeExceptionMapper {

  @Override
  public int getExitCode(Throwable t) {


    return 1;
  }
}
