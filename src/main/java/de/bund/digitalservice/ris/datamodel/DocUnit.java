package de.bund.digitalservice.ris.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocUnit {
  public static final DocUnit EMPTY = new DocUnit();

  @Id Integer id;

  // Original file
  String s3path;
  String filetype;
  String filename;

  // Stammdaten
  String aktenzeichen;
  String entscheidungsdatum;
  String gericht;
  String dokumenttyp;
  String gerichtssitz;
  String spruchkoerper;
  String vorgang;
  String eclinummer;
  String eingangsart;

  // Rubrikenfelder fuer Langtexte
  String tenor;
  String gruende;
  String tatbestand;
  String entscheidungsgruende;
  String abweichendemeinung;
  String sonstigerlangtext;
  String gliederung;
  String berichtigung;
}
