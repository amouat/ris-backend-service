import { InputType } from "./types"
import type { InputField } from "./types"
import comboboxItemService from "@/services/comboboxItemService"

export const previousDecisionFields: InputField[] = [
  {
    name: "court",
    type: InputType.COMBOBOX,
    label: "Gericht",
    required: true,
    inputAttributes: {
      ariaLabel: "Gericht Rechtszug",
      placeholder: "Gerichtstyp Gerichtsort",
      itemService: comboboxItemService.getCourts,
    },
  },
  {
    name: "date",
    type: InputType.DATE,
    label: "Datum",
    inputAttributes: {
      ariaLabel: "Datum Rechtszug",
    },
  },
  {
    name: "fileNumbers",
    type: InputType.CHIPS,
    label: "Aktenzeichen",
    inputAttributes: {
      ariaLabel: "Aktenzeichen Rechtszug",
    },
  },
]
