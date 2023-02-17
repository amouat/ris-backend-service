import fs from "fs"
import { tmpdir } from "os"
import path from "path"
import { APIRequestContext, expect } from "@playwright/test"
import { Page } from "playwright"

const REMOTE_JURIS_TEST_FILE_FOLDER_URL =
  "raw.githubusercontent.com/digitalservicebund/ris-norms-juris-extractor/main/src/test/resources/juris"

async function getLocalJurisTestFileFolderPath(): Promise<string> {
  const folderPath = path.join(tmpdir(), "ris-norms_juris-test-files")

  try {
    await fs.promises.access(folderPath, fs.constants.F_OK)
  } catch {
    await fs.promises.mkdir(folderPath)
  }

  return folderPath
}

async function downloadJurisTestFile(
  request: APIRequestContext,
  fileName: string,
  localPath: string
): Promise<void> {
  const username = process.env.GH_READ_JURIS_TEST_FILES_USER
  const password = process.env.GH_READ_JURIS_TEST_FILES_TOKEN
  console.warn(
    `process.envusername is defined? `,
    process.env.GH_READ_JURIS_TEST_FILES_USER !== undefined
  )
  console.warn(`username is defined? `, username !== undefined)
  console.warn(`username is: `, username?.substring(0, 5))
  console.warn(
    `process.env.password is defined? `,
    process.env.GH_READ_JURIS_TEST_FILES_TOKEN !== undefined
  )
  console.warn(`password is defined? `, password !== undefined)
  console.warn(`password is: `, password?.substring(0, 5))
  const remoteUrl = `https://${process.env.GH_READ_JURIS_TEST_FILES_USER}:${process.env.GH_READ_JURIS_TEST_FILES_TOKEN}@${REMOTE_JURIS_TEST_FILE_FOLDER_URL}/${fileName}`
  console.warn(remoteUrl)
  const response = await request.get(remoteUrl)

  if (!response.ok()) {
    const text = await response.text()
    console.error(`Download of the following Juris file failed: ${fileName}`, {
      status: response.status(),
      text,
      headers: response.headers(),
    })
  }

  expect(response.ok()).toBeTruthy()

  const body = await response.body()
  await fs.promises.writeFile(localPath, body)
}

export async function loadJurisTestFile(
  request: APIRequestContext,
  fileName: string
): Promise<{ filePath: string; fileContent: Buffer }> {
  const folderPath = await getLocalJurisTestFileFolderPath()
  const filePath = path.join(folderPath, fileName)

  if (!fs.existsSync(filePath)) {
    await downloadJurisTestFile(request, fileName, filePath)
  }

  const fileContent = await fs.promises.readFile(filePath)
  return { filePath, fileContent }
}

export async function importNormViaApi(
  request: APIRequestContext,
  fileContent: Buffer
): Promise<{ guid: string }> {
  const response = await request.post(`/api/v1/norms`, {
    headers: { "Content-Type": "application/zip" },
    data: fileContent,
  })

  expect(response.ok()).toBeTruthy()

  const body = await response.text()
  return JSON.parse(body)
}

export const openNorm = async (
  page: Page,
  officialLongTitle: string,
  guid: string
) => {
  await page.goto("/norms")
  await expect(page.getByText(officialLongTitle).first()).toBeVisible()
  const locatorA = page.locator(`a[href*="/norms/norm/${guid}"]`)
  await expect(locatorA).toBeVisible()
  await locatorA.click()
}
