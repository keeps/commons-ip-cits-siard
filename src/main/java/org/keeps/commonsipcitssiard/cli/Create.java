package org.keeps.commonsipcitssiard.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
@CommandLine.Command(name = "create", description = "Creates E-ARK CITS SIARD packages%n", showDefaultValues = true)
public class Create implements Callable<Integer> {
  @Override
  public Integer call() throws Exception {
    return null;
  }
}
