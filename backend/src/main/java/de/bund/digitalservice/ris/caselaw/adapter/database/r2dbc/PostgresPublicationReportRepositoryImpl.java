package de.bund.digitalservice.ris.caselaw.adapter.database.r2dbc;

import de.bund.digitalservice.ris.caselaw.domain.PublicationReport;
import de.bund.digitalservice.ris.caselaw.domain.PublicationReportRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class PostgresPublicationReportRepositoryImpl implements PublicationReportRepository {

  private final DatabasePublicationReportRepository repository;
  private final DatabaseDocumentUnitRepository documentUnitRepository;

  public PostgresPublicationReportRepositoryImpl(
      DatabasePublicationReportRepository repository,
      DatabaseDocumentUnitRepository documentUnitRepository) {

    this.repository = repository;
    this.documentUnitRepository = documentUnitRepository;
  }

  @Override
  public Flux<PublicationReport> saveAll(List<PublicationReport> reports) {
    return Flux.fromIterable(reports)
        .flatMap(
            report ->
                documentUnitRepository
                    .findByDocumentnumber(report.documentNumber())
                    .map(
                        documentUnit ->
                            PublicationReportDTO.builder()
                                .id(UUID.randomUUID())
                                .documentUnitId(documentUnit.getUuid())
                                .receivedDate(report.receivedDate())
                                .content(report.content())
                                .newEntry(true)
                                .build()))
        .collectList()
        .flatMapMany(repository::saveAll)
        .map(
            report ->
                PublicationReport.builder()
                    .receivedDate(report.getReceivedDate())
                    .content(report.getContent())
                    .build());
  }

  @Override
  public Flux<PublicationReport> getAllByDocumentUnitUuid(UUID documentUnitUuid) {
    return repository
        .findAllByDocumentUnitId(documentUnitUuid)
        .map(
            report ->
                PublicationReport.builder()
                    .receivedDate(report.getReceivedDate())
                    .content(report.getContent())
                    .build());
  }
}
