import type { KnowledgePoint } from '../types/course'
import type { FusionNode } from '../types/fusion'

export const percentValue = (value?: number) => {
  if (value === undefined || value === null) return 0
  const normalized = value <= 1 ? value * 100 : value
  return Math.max(0, Math.min(100, Math.round(normalized)))
}

export const difficultyText = (value?: string) => {
  const map: Record<string, string> = {
    basic: '基础',
    medium: '进阶',
    advanced: '高级',
    easy: '简单',
    hard: '困难',
  }
  return value ? map[value] || value : '未标注'
}

export const difficultyColor = (value?: string) => {
  const map: Record<string, string> = {
    basic: 'green',
    easy: 'green',
    medium: 'blue',
    advanced: 'orange',
    hard: 'red',
  }
  return value ? map[value] || 'default' : 'default'
}

export const relationText = (value?: string) => {
  const map: Record<string, string> = {
    prerequisite: '前置',
    supports: '支撑',
    requires: '要求',
    supported_by: '由其支撑',
    contains: '包含',
    practice_with: '实训巩固',
  }
  return value ? map[value] || value : '关联'
}

export const nodeTypeText = (value?: string) => {
  const map: Record<string, string> = {
    job_role: '岗位',
    job_capability: '能力',
    course: '课程',
    course_knowledge_point: '知识点',
    competition: '竞赛',
    certificate: '证书',
    project: '项目',
  }
  return value ? map[value] || value : '节点'
}

export const masteryText = (value?: string) => {
  const map: Record<string, string> = {
    mastered: '已掌握',
    in_progress: '进行中',
    weak: '薄弱',
    not_started: '未开始',
  }
  return value ? map[value] || value : '未评估'
}

export const masteryColor = (value?: string) => {
  const map: Record<string, string> = {
    mastered: 'success',
    in_progress: 'processing',
    weak: 'warning',
    not_started: 'default',
  }
  return value ? map[value] || 'default' : 'default'
}

export const pointLabel = (point?: KnowledgePoint) => point?.name || `知识点 ${point?.id || '-'}`

export const nodeLabel = (node?: FusionNode) => node?.label || `节点 ${node?.nodeKey || node?.nodeId || '-'}`
