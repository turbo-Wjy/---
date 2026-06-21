export interface JobRole {
  id?: number
  majorId?: number
  roleCode?: string
  roleName: string
  description?: string
  typicalTasks?: string
  abilityTags?: string
  sortOrder?: number
  status?: string
  displayOnly?: boolean
}

export interface JobCapability {
  id?: number
  jobRoleId?: number
  parentId?: number
  capabilityCode?: string
  capabilityName: string
  description?: string
  level?: string
  weight?: number
  sortOrder?: number
  status?: string
  displayOnly?: boolean
}

export interface FusionNode {
  nodeKey?: string
  nodeType?: string
  nodeId?: number
  label: string
  description?: string
  score?: number
  masteryStatus?: string
  displayOnly?: boolean
}

export interface FusionEdge {
  sourceKey?: string
  targetKey?: string
  sourceType?: string
  sourceId?: number
  targetType?: string
  targetId?: number
  relationType?: string
  weight?: number
  description?: string
  displayOnly?: boolean
}

export interface FusionGraph {
  nodes: FusionNode[]
  edges: FusionEdge[]
  weakPoints: FusionNode[]
  recommendedPath: string[]
  displayOnly?: boolean
}

export interface FusionRelation {
  id?: number
  sourceType?: string
  sourceId?: number
  targetType?: string
  targetId?: number
  relationType?: string
  weight?: number
  description?: string
  evidence?: string
  status?: string
  displayOnly?: boolean
}

export interface FusionRelationQuery {
  page?: number
  pageSize?: number
  sourceType?: string
  sourceId?: number
  targetType?: string
  targetId?: number
  relationType?: string
}
