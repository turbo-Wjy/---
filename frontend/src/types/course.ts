export interface Course {
  id?: number
  courseCode?: string
  courseName: string
  majorId?: number
  credit?: number
  semester?: string
  status?: string
  displayOnly?: boolean
}

export interface KnowledgePoint {
  id?: number
  courseId?: number
  parentId?: number
  name: string
  description?: string
  difficultyLevel?: string
  sortOrder?: number
  status?: string
  displayOnly?: boolean
}

export interface KnowledgePointRelation {
  id?: number
  sourceKnowledgePointId?: number
  targetKnowledgePointId?: number
  relationType?: string
  weight?: number
  description?: string
  status?: string
  displayOnly?: boolean
}

export interface CourseGraph {
  nodes: KnowledgePoint[]
  edges: KnowledgePointRelation[]
  displayOnly?: boolean
}
