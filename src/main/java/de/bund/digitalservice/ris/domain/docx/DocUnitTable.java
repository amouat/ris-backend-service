package de.bund.digitalservice.ris.domain.docx;

import lombok.Data;

@Data
public class DocUnitTable implements DocUnitDocx {
  private String textContent;

  @Override
  public String toHtmlString() {
    return textContent;
  }
}
