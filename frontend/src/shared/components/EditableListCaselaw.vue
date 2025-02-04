<script lang="ts" setup generic="T extends ListItem">
import type { Component, Ref } from "vue"
import { ref, watch } from "vue"
import ListItem from "@/domain/editableListItem"
import DataSetSummary from "@/shared/components/DataSetSummary.vue"

interface Props {
  editComponent: Component
  summaryComponent?: Component
  modelValue?: T[]
  defaultValue: T
  disableMultiEntry?: boolean
  addEntryLabel?: string
  noHorizontalSeparators?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  summaryComponent: DataSetSummary,
  modelValue: () => [],
  disableMultiEntry: false,
  addEntryLabel: "Weitere Angabe",
  noHorizontalSeparators: false,
})

const emit = defineEmits<{
  "update:modelValue": [value: T[]]
  deleteLastEntry: [void]
}>()

const modelValueList = ref<T[]>([]) as Ref<T[]>
const elementList = ref<HTMLElement[]>([])
const editIndex = ref<number | undefined>(undefined)

function setEditIndex(index: number | undefined) {
  editIndex.value = index
}

function addNewModelEntry() {
  const { defaultValue } = props
  const newEntry =
    typeof defaultValue === "object" ? { ...defaultValue } : defaultValue
  modelValueList.value.push(newEntry)
  editIndex.value = modelValueList.value.length - 1
}

function removeModelEntry(index: number) {
  modelValueList.value.splice(index, 1)

  if (editIndex.value !== undefined && index < editIndex.value) {
    editIndex.value -= 1
  }
}

watch(
  () => props.modelValue,
  () => {
    modelValueList.value = props.modelValue
    if (editIndex.value && editIndex.value >= props.modelValue.length)
      addNewModelEntry()
  },
  { immediate: true, deep: true }
)

watch(
  modelValueList,
  () => {
    if (modelValueList.value.length == 0) {
      addNewModelEntry()
    }
  },
  { deep: true, immediate: true }
)

watch(modelValueList, () => emit("update:modelValue", modelValueList.value), {
  deep: true,
})
</script>

<template>
  <div class="w-full">
    <div
      v-for="(entry, index) in modelValueList"
      :key="index"
      ref="elementList"
      aria-label="Listen Eintrag"
    >
      <div
        v-if="index !== editIndex"
        :key="index"
        class="border-b-1 border-gray-400 cursor-pointer flex gap-8 group group-first:pt-0 items-center justify-between py-8"
        :class="{ '!border-none': noHorizontalSeparators }"
      >
        <component
          :is="summaryComponent"
          class="focus-visible:outline-blue-800 focus:outline-none"
          :data="entry"
          tabindex="0"
          @click="
            entry.isReadOnly
              ? (e: Event) => e.preventDefault()
              : setEditIndex(index)
          "
          @keypress.enter="setEditIndex(index)"
        />

        <div class="flex gap-8">
          <button
            v-if="!entry.isReadOnly"
            aria-label="Eintrag bearbeiten"
            class="active:bg-blue-500 active:outline-none focus:outline-2 focus:outline-blue-800 hover:bg-blue-200 material-icons outline-none outline-offset-2 p-2 text-blue-800"
            @click="setEditIndex(index)"
          >
            edit_note
          </button>

          <button
            aria-label="Eintrag löschen"
            class="active:bg-blue-500 active:outline-none focus:outline-2 focus:outline-blue-800 hover:bg-blue-200 material-icons outline-none outline-offset-2 p-2 text-blue-800"
            @click="removeModelEntry(index)"
          >
            delete_outline
          </button>
        </div>
      </div>

      <component
        :is="editComponent"
        v-else
        v-model="modelValueList[index]"
        class="group-first:pt-0 py-16"
        :model-value-list="modelValue"
        @close-entry="setEditIndex(undefined)"
      />
    </div>

    <button
      v-if="!disableMultiEntry && editIndex === undefined"
      aria-label="Weitere Angabe"
      class="add-button bg-blue-300 flex focus:outline-4 font-bold gap-0.5 hover:bg-blue-800 hover:text-white items-center leading-18 mt-16 outline-0 outline-blue-800 outline-none outline-offset-4 px-8 py-2 text-14 text-blue-800 whitespace-nowrap"
      @click="addNewModelEntry"
    >
      <span class="material-icons text-14">add</span>
      Weitere Angabe
    </button>
  </div>
</template>

<style lang="scss" scoped>
.add-button {
  &:focus:not(:focus-visible) {
    @apply outline-transparent;
  }
}
</style>
