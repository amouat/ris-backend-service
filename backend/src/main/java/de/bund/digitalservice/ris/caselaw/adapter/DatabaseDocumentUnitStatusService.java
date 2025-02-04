package de.bund.digitalservice.ris.caselaw.adapter;

import static de.bund.digitalservice.ris.caselaw.domain.DocumentUnitStatus.UNPUBLISHED;

import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.DatabaseDocumentUnitStatusRepository;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.DocumentUnitException;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.DocumentUnitStatusDTO;
import de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc.PostgresDocumentUnitRepositoryImpl;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnit;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnitStatus;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnitStatusService;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DatabaseDocumentUnitStatusService implements DocumentUnitStatusService {

  private final DatabaseDocumentUnitStatusRepository repository;
  private final PostgresDocumentUnitRepositoryImpl documentUnitRepository;

  public DatabaseDocumentUnitStatusService(
      DatabaseDocumentUnitStatusRepository repository,
      PostgresDocumentUnitRepositoryImpl documentUnitRepository) {
    this.repository = repository;
    this.documentUnitRepository = documentUnitRepository;
  }

  public Mono<DocumentUnit> setInitialStatus(DocumentUnit documentUnit) {
    return repository
        .save(
            DocumentUnitStatusDTO.builder()
                .newEntry(true)
                .id(UUID.randomUUID())
                .createdAt(documentUnit.creationtimestamp())
                .documentUnitId(documentUnit.uuid())
                .status(UNPUBLISHED)
                .build())
        .then(documentUnitRepository.findByUuid(documentUnit.uuid()));
  }

  public Mono<DocumentUnit> updateStatus(
      DocumentUnit documentUnit,
      DocumentUnitStatus status,
      Instant publishDate,
      String issuerAddress) {
    return repository
        .save(
            DocumentUnitStatusDTO.builder()
                .newEntry(true)
                .id(UUID.randomUUID())
                .createdAt(publishDate)
                .documentUnitId(documentUnit.uuid())
                .status(status)
                .issuerAddress(issuerAddress)
                .build())
        .then(documentUnitRepository.findByUuid(documentUnit.uuid()));
  }

  public Mono<String> getIssuerAddressOfLatestStatus(String documentNumber) {
    return documentUnitRepository
        .findByDocumentNumber(documentNumber)
        .switchIfEmpty(
            Mono.error(
                new DocumentUnitException(
                    "Could not find Document Unit with documentNumber " + documentNumber)))
        .flatMap(
            documentUnit ->
                repository.findFirstByDocumentUnitIdOrderByCreatedAtDesc(documentUnit.uuid()))
        .map(DocumentUnitStatusDTO::getIssuerAddress);
  }
}
