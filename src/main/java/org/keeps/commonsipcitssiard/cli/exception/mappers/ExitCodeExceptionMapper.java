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
