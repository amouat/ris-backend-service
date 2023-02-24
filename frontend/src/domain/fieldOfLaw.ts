export type FieldOfLawNode = {
  identifier: string
  subjectFieldText: string
  linkedFields?: string[]
  children: FieldOfLawNode[]
  depth: number
  isExpanded: boolean
  inDirectPathMode?: boolean
  isLeaf: boolean
}

export const ROOT_ID = "root"

export function buildRoot(children: FieldOfLawNode[] = []): FieldOfLawNode {
  return {
    identifier: ROOT_ID,
    subjectFieldText: "Alle Sachgebiete anzeigen",
    children: children,
    depth: 0,
    isExpanded: false,
    isLeaf: false,
  }
}
