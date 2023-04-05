import { InputField, InputType } from "@/shared/components/input/types"

export const text: InputField[] = [
  {
    name: "text",
    id: "text",
    type: InputType.TEXT,
    label: "Text",
    inputAttributes: {
      ariaLabel: "Text",
    },
  },
]
