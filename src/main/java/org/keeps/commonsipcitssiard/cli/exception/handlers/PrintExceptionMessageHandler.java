/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip-cits-siard
 */
package org.keeps.commonsipcitssiard.cli.exception.handlers;

import picocli.CommandLine;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class PrintExceptionMessageHandler implements CommandLine.IExecutionExceptionHandler {
  public int handleExecutionException(Exception ex, CommandLine cmd, CommandLine.ParseResult parseResult) {

    // bold red error message
    cmd.getErr().println(cmd.getColorScheme().errorText(ex.getMessage()));

    CommandLine.Model.CommandSpec spec = cmd.getCommandSpec();

    cmd.getOut().printf("Try '%s --help' for more information.%n", spec.qualifiedName());

    return cmd.getExitCodeExceptionMapper() != null ? cmd.getExitCodeExceptionMapper().getExitCode(ex)
        : cmd.getCommandSpec().exitCodeOnExecutionException();
  }
}
