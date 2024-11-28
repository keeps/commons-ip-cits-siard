package org.keeps.commonsipcitssiard.cli.validator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.roda_project.commons_ip2.cli.model.exception.UnmarshallerException;
import org.roda_project.commons_ip2.validator.common.InstatiateMets;
import org.roda_project.commons_ip2.validator.components.MetsValidator;
import org.roda_project.commons_ip2.validator.components.StructureValidatorImpl;
import org.roda_project.commons_ip2.validator.components.administritiveMetadataComponent.AdministritiveMetadataComponentValidator204;
import org.roda_project.commons_ip2.validator.components.administritiveMetadataComponent.AdministritiveMetadataComponentValidator210;
import org.roda_project.commons_ip2.validator.components.aipFileSectionComponent.AipFileSectionComponent204;
import org.roda_project.commons_ip2.validator.components.aipFileSectionComponent.AipFileSectionComponent210;
import org.roda_project.commons_ip2.validator.components.descriptiveMetadataComponent.DescriptiveMetadataComponentValidator204;
import org.roda_project.commons_ip2.validator.components.descriptiveMetadataComponent.DescriptiveMetadataComponentValidator210;
import org.roda_project.commons_ip2.validator.components.fileComponent.StructureComponentValidator204;
import org.roda_project.commons_ip2.validator.components.fileComponent.StructureComponentValidator210;
import org.roda_project.commons_ip2.validator.components.fileSectionComponent.FileSectionComponentValidator204;
import org.roda_project.commons_ip2.validator.components.fileSectionComponent.FileSectionComponentValidator210;
import org.roda_project.commons_ip2.validator.components.metsRootComponent.metsHeaderValidator.MetsHeaderComponentValidator204;
import org.roda_project.commons_ip2.validator.components.metsRootComponent.metsHeaderValidator.MetsHeaderComponentValidator210;
import org.roda_project.commons_ip2.validator.components.metsRootComponent.metsValidator.MetsComponentValidator204;
import org.roda_project.commons_ip2.validator.components.metsRootComponent.metsValidator.MetsComponentValidator210;
import org.roda_project.commons_ip2.validator.components.sipFileSectionComponent.SipFileSectionComponent204;
import org.roda_project.commons_ip2.validator.components.sipFileSectionComponent.SipFileSectionComponent210;
import org.roda_project.commons_ip2.validator.components.sipMetsRootComponent.sipMetsComponent.SipMetsComponent204;
import org.roda_project.commons_ip2.validator.components.sipMetsRootComponent.sipMetsComponent.SipMetsComponent210;
import org.roda_project.commons_ip2.validator.components.sipMetsRootComponent.sipMetsHdrComponent.SipMetsHdrComponent204;
import org.roda_project.commons_ip2.validator.components.sipMetsRootComponent.sipMetsHdrComponent.SipMetsHdrComponent210;
import org.roda_project.commons_ip2.validator.components.structuralMapComponent.StructuralMapComponentValidator204;
import org.roda_project.commons_ip2.validator.components.structuralMapComponent.StructuralMapComponentValidator210;
import org.roda_project.commons_ip2.validator.observer.ValidationObserver;
import org.roda_project.commons_ip2.validator.reporter.ReporterDetails;
import org.roda_project.commons_ip2.validator.reporter.ValidationReportOutputJson;
import org.roda_project.commons_ip2.validator.state.MetsValidatorState;
import org.roda_project.commons_ip2.validator.state.StructureValidatorState;
import org.roda_project.commons_ip2.validator.utils.ResultsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.databasepreservation.SIARDValidation;
import com.databasepreservation.model.exception.ModuleException;
import com.databasepreservation.model.modules.validate.ProgressValidationLoggerObserver;
import com.databasepreservation.model.reporters.Reporter;
import com.databasepreservation.modules.siard.SIARDValidateFactory;

import jakarta.xml.bind.JAXBException;

public class SIARDValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(SIARDValidator.class);

  private final Path siardSipPath;
  private final ValidationReportOutputJson validationReportOutputJson;
  private final StructureValidatorImpl structureComponent;
  private final StructureValidatorState structureValidatorState;
  private final List<MetsValidator> csipComponents = new ArrayList();
  private final List<MetsValidator> sipComponents = new ArrayList();
  private final List<MetsValidator> aipComponents = new ArrayList();
  private final MetsValidatorState metsValidatorState;
  private final String version;

  public SIARDValidator(ValidationReportOutputJson reportOutputJson, String version)
    throws IOException, ParserConfigurationException, SAXException {
    this.siardSipPath = reportOutputJson.getSipPath().toAbsolutePath().normalize();
    this.validationReportOutputJson = reportOutputJson;
    this.version = version;
    this.structureValidatorState = new StructureValidatorState(
      reportOutputJson.getSipPath().toAbsolutePath().normalize());
    if (version.equals("1.0.0")) {
      this.structureComponent = new StructureComponentValidator210();
    } else {
      this.structureComponent = new StructureComponentValidator204();
    }

    this.metsValidatorState = new MetsValidatorState();
    this.setupComponents(version);
  }

  private void setupComponents(String version) throws IOException, ParserConfigurationException, SAXException {
    this.csipComponents.addAll(this.getComponentsForVersion(version, "csipComponents"));
    this.sipComponents.addAll(this.getComponentsForVersion(version, "sipComponents"));
    this.aipComponents.addAll(this.getComponentsForVersion(version, "aipComponents"));
  }

  public void addObserver(ValidationObserver observer) {
    this.structureComponent.addObserver(observer);
    this.csipComponents.forEach((c) -> {
      c.addObserver(observer);
    });
    this.sipComponents.forEach((c) -> {
      c.addObserver(observer);
    });
  }

  public void removeObserver(ValidationObserver observer) {
    this.structureComponent.removeObserver(observer);
    this.csipComponents.forEach((c) -> {
      c.removeObserver(observer);
    });
    this.sipComponents.forEach((c) -> {
      c.removeObserver(observer);
    });
  }

  public boolean validate(String version) throws IOException {
    this.structureComponent.notifyObserversIPValidationStarted();
    Map<String, ReporterDetails> structureValidationResults = this.structureComponent
      .validate(this.structureValidatorState);
    this.validationReportOutputJson.getResults().putAll(structureValidationResults);
    if (this.validationReportOutputJson.validFileComponent()) {
      Object subMets;
      if (this.structureValidatorState.isZipFileFlag()) {
        this.metsValidatorState.setMetsFiles(this.structureValidatorState.getZipManager().getFiles(this.siardSipPath));
        subMets = this.structureValidatorState.getZipManager().getSubMets(this.siardSipPath);
      } else {
        this.metsValidatorState
          .setMetsFiles(this.structureValidatorState.getFolderManager().getFiles(this.siardSipPath));
        subMets = this.structureValidatorState.getFolderManager().getSubMets(this.siardSipPath);
      }

      if (((Map) subMets).size() > 0) {
        this.validateSubMets((Map) subMets, this.structureValidatorState.isZipFileFlag());
      }

      this.validateRootMets();
      if (!this.validationReportOutputJson.getResults().containsKey("CSIP0")) {
        ReporterDetails csipStr0 = new ReporterDetails("CSIP-" + version, "", true, false);
        csipStr0.setSpecification("CSIP-" + version);
        this.validationReportOutputJson.getResults().put("CSIP0", csipStr0);
      }
    }

    this.writeReport(version);
    return this.validationReportOutputJson.getErrors() == 0;
  }

  private void validateComponents() throws IOException {
    Iterator var1 = this.csipComponents.iterator();

    while (var1.hasNext()) {
      MetsValidator component = (MetsValidator) var1.next();
      Map<String, ReporterDetails> componentResults = component.validate(this.structureValidatorState,
        this.metsValidatorState);
      ResultsUtils.mergeResults(this.validationReportOutputJson.getResults(), componentResults);
    }

    this.metsValidatorState.flushEntries();
    this.validateIpTypeExtendedComponents();
  }

  private void validateSubMets(Map<String, InputStream> subMets, boolean isZip) {
    Iterator var3 = subMets.entrySet().iterator();

    while (var3.hasNext()) {
      Map.Entry<String, InputStream> entry = (Map.Entry) var3.next();
      InstatiateMets instatiateMets = new InstatiateMets((InputStream) entry.getValue());

      try {
        this.metsValidatorState.setMets(instatiateMets.instatiateMetsFile(""));
        this.metsValidatorState.setIpType(this.metsValidatorState.getMets().getMetsHdr().getOAISPACKAGETYPE());
        this.setupMetsValidatorState((String) entry.getKey(), isZip, false);
        this.validateComponents();
      } catch (IOException | UnmarshallerException var9) {
        Exception e = var9;
        String message = this.createExceptionMessage(e, (String) entry.getKey());
        ReporterDetails csipStr0 = new ReporterDetails("CSIP-", message, false, false);
        csipStr0.setSpecification("CSIP-");
        ResultsUtils.addResult(this.validationReportOutputJson.getResults(), "CSIP0", csipStr0);
      }
    }

  }

  private String createExceptionMessage(Exception e, String mets) {
    StringBuilder message = new StringBuilder();
    Throwable cause = e;
    if (e.getMessage() != null) {
      message.append("[").append(e.getClass().getSimpleName()).append("]").append(" ").append(e.getMessage());
    }

    while (((Throwable) cause).getCause() != null) {
      cause = ((Throwable) cause).getCause();
      if (message.length() > 0) {
        message.append(" caused by ");
      }

      message.append("[").append(cause.getClass().getSimpleName()).append("]").append(" ")
        .append(((Throwable) cause).getMessage());
      if (cause instanceof SAXParseException e1) {
        message.append(" (file: ").append(mets).append(", line: ").append(e1.getLineNumber()).append(", column: ")
          .append(e1.getColumnNumber()).append(")");
      }
    }

    return message.toString();
  }

  private void validateRootMets() {
    try {
      InputStream metsRootStream;
      String ipPath;
      if (this.structureValidatorState.isZipFileFlag()) {
        metsRootStream = this.structureValidatorState.getZipManager().getMetsRootInputStream(this.siardSipPath);
        ipPath = this.siardSipPath.toString();
      } else {
        metsRootStream = this.structureValidatorState.getFolderManager().getMetsRootInputStream(this.siardSipPath);
        ipPath = this.siardSipPath.resolve("METS.xml").toString();
      }

      InstatiateMets metsRoot = new InstatiateMets(metsRootStream);
      this.metsValidatorState.setMetsPath(this.siardSipPath.toString());
      this.metsValidatorState.setMetsName(ipPath);
      this.metsValidatorState.setIsRootMets(true);
      this.metsValidatorState.setMets(metsRoot.instatiateMetsFile("")); //to do
      this.metsValidatorState.setIpType(this.metsValidatorState.getMets().getMetsHdr().getOAISPACKAGETYPE());
      this.validateComponents();
    } catch (IOException | UnmarshallerException var6) {
      Exception e = var6;
      String message = this.createExceptionMessage(e, this.siardSipPath.toString() + "/METS.xml");
      ReporterDetails csipStr0 = new ReporterDetails("CSIP-", message, false, false);
      csipStr0.setSpecification("CSIP-");
      ResultsUtils.addResult(this.validationReportOutputJson.getResults(), "CSIP0", csipStr0);
    }

  }

  private void setupMetsValidatorState(String key, boolean isZip, boolean isRootMets) {
    this.metsValidatorState.setMetsName(key);
    this.metsValidatorState.setIsRootMets(isRootMets);
    if (isZip) {
      StringBuilder metsPath = new StringBuilder();
      String[] var5 = key.split("/");
      int var6 = var5.length;

      for (int var7 = 0; var7 < var6; ++var7) {
        String path = var5[var7];
        if (!path.equals("METS.xml")) {
          metsPath.append(path).append("/");
        }
      }

      this.metsValidatorState.setMetsPath(metsPath.toString());
    } else {
      this.metsValidatorState.setMetsPath(Paths.get(key).getParent().toString());
    }

  }

  public void notifyIndicatorsObservers() {
    this.structureComponent.notifyIndicators(this.validationReportOutputJson.getErrors(),
      this.validationReportOutputJson.getSuccess(), this.validationReportOutputJson.getWarnings(),
      this.validationReportOutputJson.getNotes(), this.validationReportOutputJson.getSkipped());
  }

  private void validateIpTypeExtendedComponents() throws IOException {
    if (this.metsValidatorState.getIpType() != null && this.metsValidatorState.getIpType().equals("SIP")) {
      this.validateSIPComponents();
    } else if (this.metsValidatorState.getIpType() != null && this.metsValidatorState.getIpType().equals("AIP")) {
      this.validateAIPComponets();
    }

  }

  private void validateSIPComponents() throws IOException {
    this.aipComponents.clear();
    Iterator var1 = this.sipComponents.iterator();

    while (var1.hasNext()) {
      MetsValidator component = (MetsValidator) var1.next();
      if (component instanceof SipFileSectionComponent204 component1) {
        ((SipFileSectionComponent204) component)
          .setIsToValidate(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP58"));
      }

      if (component instanceof SipFileSectionComponent210 component1) {
        ((SipFileSectionComponent210) component)
          .setIsToValidate(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP58"));
      }

      if (component instanceof SipMetsHdrComponent204) {
        ((SipMetsHdrComponent204) component)
          .setIsToValidateMetsHdr(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP117"));
        if (this.validationReportOutputJson.getResults().get("CSIP10") != null) {
          ((SipMetsHdrComponent204) component)
            .setIsToValidateAgents(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP10"));
        }
      }

      if (component instanceof SipMetsHdrComponent210) {
        ((SipMetsHdrComponent210) component)
          .setIsToValidateMetsHdr(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP117"));
        if (this.validationReportOutputJson.getResults().get("CSIP10") != null) {
          ((SipMetsHdrComponent210) component)
            .setIsToValidateAgents(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP10"));
        }
      }

      Map<String, ReporterDetails> sipComponentResults = component.validate(this.structureValidatorState,
        this.metsValidatorState);
      ResultsUtils.mergeResults(this.validationReportOutputJson.getResults(), sipComponentResults);
    }

  }

  private void validateAIPComponets() throws IOException {
    this.sipComponents.clear();
    Iterator var1 = this.aipComponents.iterator();

    while (var1.hasNext()) {
      MetsValidator component = (MetsValidator) var1.next();
      if (component instanceof AipFileSectionComponent204) {
        ((AipFileSectionComponent204) component)
          .setIsToValidate(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP58"));
      }

      if (component instanceof AipFileSectionComponent210) {
        ((AipFileSectionComponent210) component)
          .setIsToValidate(ResultsUtils.isResultValid(this.validationReportOutputJson.getResults(), "CSIP58"));
      }

      Map<String, ReporterDetails> aipComponentResults = component.validate(this.structureValidatorState,
        this.metsValidatorState);
      ResultsUtils.mergeResults(this.validationReportOutputJson.getResults(), aipComponentResults);
    }

  }

  private void writeReport(String version) throws IOException {
    if (this.metsValidatorState.getMets() != null) {
      this.validationReportOutputJson.setIpType(this.metsValidatorState.getIpType());
    }

    this.validationReportOutputJson.init(version);
    this.validationReportOutputJson.validationResults();
    this.validationReportOutputJson.writeFinalResult();
    this.notifyIndicatorsObservers();
    this.validationReportOutputJson.close();
    this.structureComponent.notifyObserversIPValidationFinished();
  }

  private List<MetsValidator> getComponentsForVersion(String version, String type)
    throws IOException, ParserConfigurationException, SAXException {
    List<MetsValidator> values = new ArrayList();
    if (version.equals("2.0.4")) {
      if (type.equals("csipComponents")) {
        values.add(new MetsComponentValidator204());
        values.add(new MetsHeaderComponentValidator204());
        values.add(new DescriptiveMetadataComponentValidator204());
        values.add(new AdministritiveMetadataComponentValidator204());
        values.add(new FileSectionComponentValidator204());
        values.add(new StructuralMapComponentValidator204());
      } else if (type.equals("sipComponents")) {
        values.add(new SipMetsComponent204());
        values.add(new SipMetsHdrComponent204());
        values.add(new SipFileSectionComponent204());
      } else {
        values.add(new AipFileSectionComponent204());
      }
    } else if (type.equals("csipComponents")) {
      values.add(new MetsComponentValidator210());
      values.add(new MetsHeaderComponentValidator210());
      values.add(new DescriptiveMetadataComponentValidator210());
      values.add(new AdministritiveMetadataComponentValidator210());
      values.add(new FileSectionComponentValidator210());
      values.add(new StructuralMapComponentValidator210());
    } else if (type.equals("sipComponents")) {
      values.add(new SipMetsComponent210());
      values.add(new SipMetsHdrComponent210());
      values.add(new SipFileSectionComponent210());
    } else {
      values.add(new AipFileSectionComponent210());
    }

    return values;
  }

  public static boolean validateSIARD(String siardPath, String validationReportPath, String allowedTypesPath,
    boolean skipAdditionalChecks) {

    boolean valid;

    SIARDValidation siardValidation = SIARDValidation.newInstance();
    Reporter reporter = new Reporter("/home/cafonso/Desktop/github.com/commons-ip-cits-siard", "validate.md");
    siardValidation.reporter(reporter);

    siardValidation.validateModule(new SIARDValidateFactory())
      .validateModuleParameter(SIARDValidateFactory.PARAMETER_FILE, siardPath)
      .validateModuleParameter(SIARDValidateFactory.PARAMETER_ALLOWED, allowedTypesPath)
      .validateModuleParameter(SIARDValidateFactory.PARAMETER_REPORT, validationReportPath);

    if (skipAdditionalChecks) {
      siardValidation.validateModuleParameter(SIARDValidateFactory.PARAMETER_SKIP_ADDITIONAL_CHECKS, "true");
    }

    siardValidation.observer(new ProgressValidationLoggerObserver());

    try {

      valid = siardValidation.validate();
    } catch (ModuleException e) {
      throw new RuntimeException(e);
    }

    return valid;
  }

}

