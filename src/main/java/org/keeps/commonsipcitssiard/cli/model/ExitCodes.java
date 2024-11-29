/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip-cits-siard
 */
package org.keeps.commonsipcitssiard.cli.model;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class ExitCodes {
  /**
   * Exit code success.
   */
  public static final int EXIT_CODE_OK = 0;
  /**
   * Exit code when can't create the report.
   */
  public static final int EXIT_CANNOT_CREATE_REPORT = 3;
  /**
   * Exit code when can't create the directory.
   */
  public static final int EXIT_CODE_CREATE_DIRECTORY_FAILS = 4;
  /**
   * Exit code when the date format is invalid.
   */
  public static final int EXIT_CODE_INVALID_DATE_FORMAT = 5;
  /**
   * Exit code when can't create EARK REPORT object.
   */
  public static final int EXIT_CANNOT_CREATE_EARKVALIDATOR_OBJECT = 7;

  /**
   * Exit code when missing args to execute the CLI.
   */
  public static final int EXIT_CODE_CREATE_MISSING_ARGS = 2;

  /**
   * Exit code when fails to create the SIP.
   */
  public static final int EXIT_CODE_CREATE_CANNOT_SIP = 3;

  /**
   * Exit code when the given paths are invalid.
   */
  public static final int EXIT_CODE_CREATE_INVALID_PATHS = 4;

  private ExitCodes() {
    // do nothing.
  }
}
