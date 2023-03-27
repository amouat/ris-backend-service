package de.bund.digitalservice.ris.caselaw.adapter.transformer;

import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.DocumentUnitDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.DocumentUnitMetadataDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.FileNumberDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.lookuptable.DocumentTypeDTO;
import de.bund.digitalservice.ris.caselaw.domain.CoreData;
import de.bund.digitalservice.ris.caselaw.domain.DataSource;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnit;
import de.bund.digitalservice.ris.caselaw.domain.Texts;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.court.Court;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.documenttype.DocumentType;
import java.util.List;

public class DocumentUnitTransformer {
  private DocumentUnitTransformer() {}

  public static DocumentUnitDTO enrichDTO(
      DocumentUnitDTO documentUnitDTO, DocumentUnit documentUnit) {

    DataSource dataSource = DataSource.NEURIS;
    if (documentUnit.dataSource() != null) {
      dataSource = documentUnit.dataSource();
    }

    DocumentUnitDTO.DocumentUnitDTOBuilder builder =
        documentUnitDTO.toBuilder()
            .uuid(documentUnit.uuid())
            .documentnumber(documentUnit.documentNumber())
            .creationtimestamp(documentUnit.creationtimestamp())
            .fileuploadtimestamp(documentUnit.fileuploadtimestamp())
            .dataSource(dataSource)
            .s3path(documentUnit.s3path())
            .filetype(documentUnit.filetype())
            .filename(documentUnit.filename());

    if (documentUnit.coreData() != null) {
      CoreData coreData = documentUnit.coreData();

      builder
          .procedure(coreData.procedure())
          .ecli(coreData.ecli())
          .appraisalBody(coreData.appraisalBody())
          .decisionDate(coreData.decisionDate())
          .inputType(coreData.inputType())
          .center(coreData.center());

      if (coreData.court() != null) {
        builder
            .courtType(documentUnit.coreData().court().type())
            .courtLocation(coreData.court().location());
      } else {
        builder.courtType(null);
        builder.courtLocation(null);
      }
    } else {
      builder
          .procedure(null)
          .ecli(null)
          .appraisalBody(null)
          .decisionDate(null)
          .inputType(null)
          .center(null)
          .courtType(null)
          .courtLocation(null);
    }

    if (documentUnitDTO.getId() == null
        && documentUnit.proceedingDecisions() != null
        && !documentUnit.proceedingDecisions().isEmpty()) {

      throw new DocumentUnitTransformerException(
          "Transformation of a document unit with previous decisions only allowed by update. "
              + "Document unit must have a database id!");
    }

    if (documentUnit.texts() != null) {
      Texts texts = documentUnit.texts();

      builder
          .decisionName(texts.decisionName())
          .headline(texts.headline())
          .guidingPrinciple(texts.guidingPrinciple())
          .headnote(texts.headnote())
          .tenor(texts.tenor())
          .reasons(texts.reasons())
          .caseFacts(texts.caseFacts())
          .decisionReasons(texts.decisionReasons());
    } else {
      builder
          .decisionName(null)
          .headline(null)
          .guidingPrinciple(null)
          .headnote(null)
          .tenor(null)
          .reasons(null)
          .caseFacts(null)
          .decisionReasons(null);
    }

    return builder.build();
  }

  public static Court getCourtObject(String courtType, String courtLocation) {
    Court court = null;
    if (courtType != null) {
      String label = (courtType + " " + (courtLocation == null ? "" : courtLocation)).trim();
      court = new Court(courtType, courtLocation, label, null);
    }
    return court;
  }

  public static DocumentUnit transformMetadataToDomain(
      DocumentUnitMetadataDTO documentUnitMetadataDTO) {
    if (documentUnitMetadataDTO == null) {
      return DocumentUnit.EMPTY;
    }

    DocumentType documentType = null;
    DocumentTypeDTO documentTypeDTO = documentUnitMetadataDTO.getDocumentTypeDTO();
    if (documentTypeDTO != null) {
      documentType =
          new DocumentType(documentTypeDTO.getJurisShortcut(), documentTypeDTO.getLabel());
    }

    List<String> fileNumbers = null;
    if (documentUnitMetadataDTO.getFileNumbers() != null) {
      fileNumbers =
          documentUnitMetadataDTO.getFileNumbers().stream()
              .map(FileNumberDTO::getFileNumber)
              .toList();
    }

    DataSource dataSource = DataSource.NEURIS;
    if (documentUnitMetadataDTO.getDataSource() != null) {
      dataSource = documentUnitMetadataDTO.getDataSource();
    }

    return new DocumentUnit(
        documentUnitMetadataDTO.getUuid(),
        documentUnitMetadataDTO.getDocumentnumber(),
        documentUnitMetadataDTO.getCreationtimestamp(),
        documentUnitMetadataDTO.getFileuploadtimestamp(),
        dataSource,
        documentUnitMetadataDTO.getS3path(),
        documentUnitMetadataDTO.getFiletype(),
        documentUnitMetadataDTO.getFilename(),
        new CoreData(
            fileNumbers,
            null,
            getCourtObject(
                documentUnitMetadataDTO.getCourtType(), documentUnitMetadataDTO.getCourtLocation()),
            null,
            documentType,
            documentUnitMetadataDTO.getProcedure(),
            documentUnitMetadataDTO.getEcli(),
            null,
            documentUnitMetadataDTO.getAppraisalBody(),
            documentUnitMetadataDTO.getDecisionDate(),
            null,
            documentUnitMetadataDTO.getLegalEffect(),
            documentUnitMetadataDTO.getInputType(),
            documentUnitMetadataDTO.getCenter(),
            documentUnitMetadataDTO.getRegion()),
        null,
        null,
        null);
  }
}
