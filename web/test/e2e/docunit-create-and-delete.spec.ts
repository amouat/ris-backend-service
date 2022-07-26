import { test, expect } from "@playwright/test"
import { generateDocUnit, deleteDocUnit } from "./e2e-utils"

test.describe("create a doc unit and delete it again", () => {
  test("generate doc unit", async ({ page }) => {
    const documentNumber = await generateDocUnit(page)
    await deleteDocUnit(page, documentNumber)
  })

  test("delete doc unit", async ({ page }) => {
    const documentNumber = await generateDocUnit(page)
    await page.goto("/")

    await expect(
      page.locator(`a[href*="/jurisdiction/docunit/${documentNumber}/files"]`)
    ).toBeVisible()

    await deleteDocUnit(page, documentNumber)
    await expect(
      page.locator(`a[href*="/jurisdiction/docunit/${documentNumber}/files"]`)
    ).not.toBeVisible()
  })
  test("cancel delete doc unit", async ({ page }) => {
    const documentNumber = await generateDocUnit(page)
    await page.goto("/")

    await expect(
      page.locator(`a[href*="/jurisdiction/docunit/${documentNumber}/files"]`)
    ).toBeVisible()
    await page
      .locator("tr", {
        hasText: documentNumber,
      })
      .locator("[aria-label='Dokumentationseinheit löschen']")
      .click()
    await page.locator('button:has-text("Abbrechen")').click()
    await expect(
      page.locator(`a[href*="/jurisdiction/docunit/${documentNumber}/files"]`)
    ).toBeVisible()
  })
})
