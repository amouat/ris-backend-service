import { expect } from "@playwright/test"
import {
  checkIfProceedingDecisionCleared,
  fillProceedingDecisionInputs,
  navigateToCategories,
  toggleProceedingDecisionsSection,
} from "~/e2e/caselaw/e2e-utils"
import { testWithDocumentUnit as test } from "~/e2e/caselaw/fixtures"
import { generateString } from "~/test-helper/dataGenerators"

test.describe("Search proceeding decisions", () => {
  test("search for existing proceeding decision and add", async ({
    page,
    documentNumber,
    secondaryDocumentUnit,
  }) => {
    await navigateToCategories(page, documentNumber)
    await expect(page.getByText(documentNumber)).toBeVisible()
    await toggleProceedingDecisionsSection(page)

    await fillProceedingDecisionInputs(page, {
      court: secondaryDocumentUnit.coreData.court?.label,
      fileNumber: secondaryDocumentUnit.coreData.fileNumbers?.[0],
      documentType: secondaryDocumentUnit.coreData.documentType?.jurisShortcut,
      date: "01.01.2020",
    })

    await page
      .getByRole("button", { name: "Nach Entscheidungen suchen" })
      .click()

    await expect(page.getByText("Total 1 Items")).toBeVisible()

    const result = page.locator(".table-row", {
      hasText: `AG Aachen, AnU, 01.01.2020, ${secondaryDocumentUnit.coreData.fileNumbers?.[0]}`,
    })
    await expect(result).toBeVisible()
    await result.locator("[aria-label='Treffer übernehmen']").click()

    await checkIfProceedingDecisionCleared(page)

    await expect(
      page.getByText(
        `AG Aachen, AnU, 01.01.2020, ${secondaryDocumentUnit.coreData.fileNumbers?.[0]}`
      )
    ).toHaveCount(2)

    await expect(page.getByText("Bereits hinzugefügt")).toBeVisible()

    await page.getByText("delete_outline").click()
    await expect(page.getByText("Bereits hinzugefügt")).toBeHidden()
  })

  test("search with no results", async ({ page, documentNumber }) => {
    await navigateToCategories(page, documentNumber)
    await expect(page.getByText(documentNumber)).toBeVisible()
    await toggleProceedingDecisionsSection(page)

    await fillProceedingDecisionInputs(page, {
      fileNumber: generateString(),
    })

    await page
      .getByRole("button", { name: "Nach Entscheidungen suchen" })
      .click()

    await expect(
      page.getByText("Suche hat keine Treffer ergeben")
    ).toBeVisible()
  })
})
