import { NormData } from "../fixtures"

export const normData: NormData = {
  jurisZipFileName: "Tierarznei.3-0_multi.zip",
  articles: [
    {
      marker: "Art 2",
      title:
        "Änderung der Verordnung über Stoffe mit pharmakologischer Wirkung",
      paragraphs: [
        {
          marker: "",
          text: '§ 2 der Verordnung über Stoffe mit pharmakologischer Wirkung in der Fassung der Bekanntmachung vom 8. Juli 2009 (BGBl. I S. 1768) wird wie folgt geändert: 1. In Nummer 1 wird das Wort "Fertigarzneimittel" durch das Wort "Tierarzneimittel" ersetzt. 2. Nummer 2 wird wie folgt gefasst: "2. entsprechend der jeweiligen Zulassungsbedingungen angewendet werden."',
        },
      ],
    },
    {
      title: "Inkrafttreten, Außerkrafttreten",
      marker: "Art 3",
      paragraphs: [
        {
          marker: "(1)",
          text: "Diese Verordnung tritt am Tag nach der Verkündung in Kraft.",
        },
        {
          marker: "(2)",
          text: "Gleichzeitig treten außer Kraft 1. die Tierarzneimittel-Prüfrichtlinienverordnung vom 18. Februar 2010 (BGBl. I S. 130), 2. die AMG-Blauzungenkrankheit-Ausnahmeverordnung vom 7. April 2008 (BGBl. I S. 721), 3. die Tierarzneimittel-Mitteilungendurchführungsverordnung vom 18. Juni 2014 (BGBl. I S. 797) und 4. die Verordnung mit arzneimittelrechtlichen Vorschriften über die Arzneimittelverwendung in landwirtschaftlichen Betrieben vom 17. Juli 2015 (BGBl. I S. 1380).",
        },
      ],
    },
  ],
  metadataSections: {
    NORM: [
      {
        KEYWORD: ["Mantelverordnung", "BGBl I 2023, Nr 003"],
        DIVERGENT_DOCUMENT_NUMBER: ["BJNR0030A0023"],
        RIS_ABBREVIATION_INTERNATIONAL_LAW: [
          "RIS-Abkürzung für völkerrechtliche Vereinbarungen 1",
          "RIS-Abkürzung für völkerrechtliche Vereinbarungen 2",
        ],
        UNOFFICIAL_ABBREVIATION: [
          "Nichtamtliche Buchstabenabkürzung 1",
          "Nichtamtliche Buchstabenabkürzung 2",
        ],
        UNOFFICIAL_SHORT_TITLE: [
          "Nichtamtliche Kurzüberschrift 1",
          "Nichtamtliche Kurzüberschrift 2",
        ],
        UNOFFICIAL_LONG_TITLE: [
          "Nichtamtliche Langüberschrift1",
          "Nichtamtliche Langüberschrift2",
        ],
        UNOFFICIAL_REFERENCE: ["BGBl I 2009, 1102", "BGBl II 2022, 1351"],
        REFERENCE_NUMBER: ["Aktenzeichen 1", "Aktenzeichen 2"],
        DEFINITION: ["Definition 1", "Definition 2"],
        AGE_OF_MAJORITY_INDICATION: ["minderjährig", "volljährig"],
        VALIDITY_RULE: ["Gültigkeitsregelung 1", "Gültigkeitsregelung 2"],
      },
    ],
    SUBJECT_AREA: [
      { SUBJECT_FNA: ["FNA 315-12"], SUBJECT_GESTA: ["GESTA 123-45"] },
      { SUBJECT_FNA: ["FNA 671-34"], SUBJECT_GESTA: ["GESTA 678-90"] },
    ],
    LEAD: [
      { LEAD_JURISDICTION: ["BMVBS"], LEAD_UNIT: ["RS III 2"] },
      { LEAD_JURISDICTION: ["BMI"], LEAD_UNIT: ["Z I 2"] },
    ],
    PARTICIPATION: [
      { PARTICIPATION_TYPE: ["EZ"], PARTICIPATION_INSTITUTION: ["BR"] },
      { PARTICIPATION_TYPE: ["RU"], PARTICIPATION_INSTITUTION: ["NT"] },
    ],
  },
  officialLongTitle:
    "Verordnung zur Anpassung von Rechtsverordnungen an das Tierarzneimittelrecht",
  officialShortTitle: "Angepasstes Tierarzneimittelrecht",
  risAbbreviation: "Tierarznei",
  documentCategory: "N",
  providerEntity: "DEU",
  providerDecidingBody: "BT",
  providerIsResolutionMajority: false,
  entryIntoForceDate: "2007-01-01",
  principleEntryIntoForceDate: "2007-01-01",
  divergentEntryIntoForceDateState: "UNDEFINED_NOT_PRESENT",
  expirationDateState: "UNDEFINED_UNKNOWN",
  principleExpirationDateState: "UNDEFINED_UNKNOWN",
  divergentExpirationDateState: "UNDEFINED_NOT_PRESENT",
  announcementDate: "2023-01-06",
  citationDate: "2023-01-02",
  printAnnouncementGazette: "BGBl I",
  printAnnouncementYear: "2023",
  printAnnouncementPage: "3",
  eli: "eli/bgbl-1/2023/s3",
}
