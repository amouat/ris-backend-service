package de.bund.digitalservice.ris.caselaw.adapter;

import de.bund.digitalservice.ris.caselaw.domain.Attachment;
import de.bund.digitalservice.ris.caselaw.domain.CoreData;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnit;
import de.bund.digitalservice.ris.caselaw.domain.DocumentUnitPublishException;
import de.bund.digitalservice.ris.caselaw.domain.EmailPublishService;
import de.bund.digitalservice.ris.caselaw.domain.HttpMailSender;
import de.bund.digitalservice.ris.caselaw.domain.Publication;
import de.bund.digitalservice.ris.caselaw.domain.PublishState;
import de.bund.digitalservice.ris.caselaw.domain.XmlExporter;
import de.bund.digitalservice.ris.caselaw.domain.XmlPublication;
import de.bund.digitalservice.ris.caselaw.domain.XmlPublicationRepository;
import de.bund.digitalservice.ris.caselaw.domain.XmlResultObject;
import de.bund.digitalservice.ris.caselaw.domain.lookuptable.court.Court;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class XmlEMailPublishService implements EmailPublishService {
  private static final Logger LOGGER = LoggerFactory.getLogger(XmlEMailPublishService.class);
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final XmlExporter xmlExporter;

  private final HttpMailSender mailSender;

  private final XmlPublicationRepository repository;

  private final Environment env;

  @Value("${mail.exporter.senderAddress:export.test@neuris}")
  private String senderAddress;

  public XmlEMailPublishService(
      XmlExporter xmlExporter,
      HttpMailSender mailSender,
      XmlPublicationRepository repository,
      Environment env) {

    this.xmlExporter = xmlExporter;
    this.mailSender = mailSender;
    this.repository = repository;
    this.env = env;
  }

  @Override
  public Mono<XmlPublication> publish(DocumentUnit documentUnit, String receiverAddress) {
    XmlResultObject xml;
    try {
      xml = xmlExporter.generateXml(getTestDocumentUnit(documentUnit));
    } catch (ParserConfigurationException | TransformerException ex) {
      return Mono.error(new DocumentUnitPublishException("Couldn't generate xml.", ex));
    }

    return generateMailSubject(documentUnit)
        .map(
            mailSubject ->
                generateXmlPublication(documentUnit.uuid(), receiverAddress, mailSubject, xml))
        .doOnNext(this::generateAndSendMail)
        .flatMap(this::savePublishInformation)
        .doOnError(ex -> LOGGER.error("Error by generation of mail message", ex));
  }

  @Override
  public Flux<Publication> getPublications(UUID documentUnitUuid) {
    return repository.getPublicationsByDocumentUnitUuid(documentUnitUuid);
  }

  private Mono<String> generateMailSubject(DocumentUnit documentUnit) {
    if (documentUnit.documentNumber() == null) {
      return Mono.error(
          new DocumentUnitPublishException("No document number has set in the document unit."));
    }

    String deliveryDate =
        LocalDate.now(Clock.system(ZoneId.of("Europe/Berlin"))).format(DATE_FORMATTER);

    String subject = "id=juris";
    subject += " name=NeuRIS";
    subject += " da=R";
    subject += " df=X";
    subject += " dt=N";
    subject += " mod=T";
    subject += " ld=" + deliveryDate;
    subject += " vg=";
    if (Stream.of(env.getActiveProfiles()).anyMatch("staging"::equals)) subject += "[stage]";
    subject += documentUnit.documentNumber();

    return Mono.just(subject);
  }

  private void generateAndSendMail(XmlPublication xmlPublication)
      throws DocumentUnitPublishException {
    if (xmlPublication == null) {
      throw new DocumentUnitPublishException("No xml mail is set");
    }

    if (xmlPublication.getStatusCode().equals("400")) {
      return;
    }

    if (xmlPublication.receiverAddress() == null) {
      throw new DocumentUnitPublishException("No receiver mail address is set");
    }

    mailSender.sendMail(
        senderAddress,
        xmlPublication.receiverAddress(),
        xmlPublication.mailSubject(),
        "neuris",
        Collections.singletonList(
            Attachment.builder()
                .fileName(xmlPublication.fileName())
                .fileContent(xmlPublication.xml())
                .build()),
        xmlPublication.documentUnitUuid().toString());
  }

  private XmlPublication generateXmlPublication(
      UUID documentUnitUuid, String receiverAddress, String mailSubject, XmlResultObject xml) {
    var publication =
        XmlPublication.builder()
            .documentUnitUuid(documentUnitUuid)
            .statusCode(xml.statusCode())
            .statusMessages(xml.statusMessages());

    if (xml.statusCode().equals("400")) {
      return publication.publishState(PublishState.UNKNOWN).build();
    }

    return publication
        .receiverAddress(receiverAddress)
        .mailSubject(mailSubject)
        .xml(xml.xml())
        .fileName(xml.fileName())
        .publishDate(xml.publishDate())
        .publishState(PublishState.SENT)
        .build();
  }

  private Mono<XmlPublication> savePublishInformation(XmlPublication xmlPublication) {
    if (xmlPublication.getStatusCode().equals("400")) {
      return Mono.just(xmlPublication);
    }
    return repository.save(xmlPublication);
  }

  private DocumentUnit getTestDocumentUnit(DocumentUnit documentUnit) {
    return documentUnit.toBuilder()
        .coreData(
            Optional.ofNullable(documentUnit.coreData())
                .map(
                    coreData ->
                        coreData.toBuilder()
                            .court(
                                Court.builder()
                                    .type("VGH")
                                    .location("Mannheim")
                                    .label("VGH Mannheim")
                                    .build())
                            .fileNumbers(
                                Stream.concat(
                                        Stream.of("TEST"),
                                        documentUnit.coreData().fileNumbers().stream())
                                    .collect(Collectors.toList()))
                            .build())
                .orElseGet(
                    () ->
                        CoreData.builder()
                            .court(
                                Court.builder()
                                    .type("VGH")
                                    .location("Mannheim")
                                    .label("VGH Mannheim")
                                    .build())
                            .fileNumbers(Collections.singletonList("TEST"))
                            .build()))
        .build();
  }
}
