import { expect } from "@playwright/test"
import { navigateToCategories, waitForSaving } from "../../e2e-utils"
import { testWithDocumentUnit as test } from "../../fixtures"

test.describe("decision date", () => {
  test("invalid decision date shows error", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await page.locator("[aria-label='Entscheidungsdatum']").fill("03.02.2099")

    await expect(
      page.locator(
        "text=Das Entscheidungsdatum darf nicht in der Zukunft liegen"
      )
    ).toBeVisible()
  })

  test("backspace delete resets decision date", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await page.locator("[aria-label='Entscheidungsdatum']").fill("03.02.2022")
    expect(
      await page.locator("[aria-label='Entscheidungsdatum']").inputValue()
    ).toBe("03.02.2022")

    const infoPanel = page.getByText(
      new RegExp(`${documentNumber}.*Entscheidungsdatum.*`)
    )
    await expect(infoPanel.getByText("03.02.2022")).toBeVisible()

    await page.locator("[aria-label='Entscheidungsdatum']").click()
    await page.keyboard.press("Backspace")

    expect(
      await page.locator("[aria-label='Entscheidungsdatum']").inputValue()
    ).toBe("")

    await expect(
      infoPanel.getByText("Entscheidungsdatum - ", {
        exact: true,
      })
    ).toBeVisible()
  })

  test("backspace delete in deviating decision date", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await page
      .locator("[aria-label='Abweichendes Entscheidungsdatum anzeigen']")
      .click()

    await page
      .locator("[aria-label='Abweichendes Entscheidungsdatum']")
      .fill("03.02.2022")
    expect(
      await page
        .locator("[aria-label='Abweichendes Entscheidungsdatum']")
        .inputValue()
    ).toBe("03.02.2022")

    await page.keyboard.press("Backspace")
    await page.reload()

    expect(
      await page
        .locator("[aria-label='Abweichendes Entscheidungsdatum']")
        .inputValue()
    ).toBe("")
  })

  test("nested decision date input toggles child input and correctly saves and displays data", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await waitForSaving(
      async () => {
        await page
          .locator("[aria-label='Entscheidungsdatum']")
          .fill("03.02.2022")
        expect(
          await page.locator("[aria-label='Entscheidungsdatum']").inputValue()
        ).toBe("03.02.2022")

        await expect(
          page.locator("text=Abweichendes Entscheidungsdatum>")
        ).toBeHidden()

        await page
          .locator("[aria-label='Abweichendes Entscheidungsdatum anzeigen']")
          .click()

        await expect(
          page.locator("text=Abweichendes Entscheidungsdatum").first()
        ).toBeVisible()

        await page
          .locator("[aria-label='Abweichendes Entscheidungsdatum']")
          .fill("02.02.2022")
        await page.keyboard.press("Enter")
        await page
          .locator("[aria-label='Abweichendes Entscheidungsdatum']")
          .fill("01.02.2022")
        await page.keyboard.press("Enter")
      },
      page,
      { clickSaveButton: true }
    )

    await page.reload()

    await page
      .locator("[aria-label='Abweichendes Entscheidungsdatum anzeigen']")
      .click()

    await expect(page.locator(".label-wrapper").nth(0)).toHaveText("02.02.2022")

    await expect(page.locator(".label-wrapper").nth(1)).toHaveText("01.02.2022")

    await page
      .locator("[aria-label='Abweichendes Entscheidungsdatum schließen']")
      .click()

    await expect(
      page.locator("text=Abweichendes Entscheidungsdatum").first()
    ).toBeHidden()
  })
})
