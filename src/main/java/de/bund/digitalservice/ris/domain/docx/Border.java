package de.bund.digitalservice.ris.domain.docx;

public record Border(String color, Integer width, String type) {
  public String toString() {
    return width + "px " + type + " " + color;
  }
}
