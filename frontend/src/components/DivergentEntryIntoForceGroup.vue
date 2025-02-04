<script lang="ts" setup>
import { ref, watch } from "vue"
import DivergentDefinedInputGroup from "@/components/DivergentDefinedInputGroup.vue"
import DivergentUndefinedInputGroup from "@/components/DivergentUndefinedInputGroup.vue"
import { Metadata, MetadataSectionName, MetadataSections } from "@/domain/Norm"

interface Props {
  modelValue: MetadataSections
}

const props = defineProps<Props>()

const emit = defineEmits<{
  "update:modelValue": [value: MetadataSections]
}>()

type ChildSectionName =
  | MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_DEFINED
  | MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED

const childSection = ref<Metadata>({})
const selectedChildSectionName = ref<ChildSectionName>(
  MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_DEFINED
)

watch(
  childSection,
  () =>
    emit("update:modelValue", {
      [selectedChildSectionName.value]: [childSection.value],
    }),
  {
    deep: true,
  }
)

watch(
  () => props.modelValue,
  (modelValue) => {
    if (modelValue.DIVERGENT_ENTRY_INTO_FORCE_DEFINED) {
      selectedChildSectionName.value =
        MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_DEFINED
      childSection.value = modelValue.DIVERGENT_ENTRY_INTO_FORCE_DEFINED[0]
    } else if (modelValue.DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED) {
      selectedChildSectionName.value =
        MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED
      childSection.value = modelValue.DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED[0]
    }
  },
  {
    immediate: true,
    deep: true,
  }
)

watch(selectedChildSectionName, () => (childSection.value = {}))
</script>

<template>
  <div>
    <div class="flex justify-between mb-24 w-320">
      <label class="form-control">
        <input
          id="divergentEntryIntoForceDefinedSelection"
          v-model="selectedChildSectionName"
          aria-label="Bestimmtes abweichendes Inkrafttretedatum"
          name="DivergentEntryIntoForceDefined"
          type="radio"
          :value="MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_DEFINED"
        />
        bestimmt
      </label>
      <label class="form-control">
        <input
          id="divergentEntryIntoForceUndefinedSelection"
          v-model="selectedChildSectionName"
          aria-label="Unbestimmtes Abweichendes Inkrafttretedatum"
          name="DivergentEntryIntoForceUndefined"
          type="radio"
          :value="MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED"
        />
        unbestimmt
      </label>
    </div>
    <DivergentDefinedInputGroup
      v-if="
        selectedChildSectionName ===
        MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_DEFINED
      "
      id="divergentEntryIntoForceDefinedDate"
      v-model="childSection"
      label="Bestimmtes abweichendes Inkrafttretedatum"
      :section-name="MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_DEFINED"
    />
    <DivergentUndefinedInputGroup
      v-if="
        selectedChildSectionName ===
        MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED
      "
      id="divergentEntryIntoForceUndefinedDate"
      v-model="childSection"
      label="Unbestimmtes abweichendes Inkrafttretedatum"
      :section-name="MetadataSectionName.DIVERGENT_ENTRY_INTO_FORCE_UNDEFINED"
    />
  </div>
</template>

<style lang="scss" scoped>
.form-control {
  display: flex;
  flex-direction: row;
  align-items: center;
}

input[type="radio"] {
  display: grid;
  width: 1.5em;
  height: 1.5em;
  border: 0.15em solid currentcolor;
  border-radius: 50%;
  margin-right: 10px;
  appearance: none;
  background-color: white;
  color: #004b76;
  place-content: center;
}

input[type="radio"]:hover,
input[type="radio"]:focus {
  border: 4px solid #004b76;
  outline: none;
}

input[type="radio"]::before {
  width: 0.9em;
  height: 0.9em;
  border-radius: 50%;
  background-color: #004b76;
  content: "";
  transform: scale(0);
}

input[type="radio"]:checked::before {
  transform: scale(1);
}
</style>
