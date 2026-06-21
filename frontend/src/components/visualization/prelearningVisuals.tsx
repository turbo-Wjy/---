import type { EdgeData, GraphData, GraphOptions, NodeData } from '@antv/g6'
import { Empty } from 'antd'
import { useMemo } from 'react'
import type { CourseGraph, KnowledgePoint } from '../../types/course'
import type { FusionEdge, FusionGraph, FusionNode } from '../../types/fusion'
import {
  difficultyText,
  masteryText,
  nodeTypeText,
  percentValue,
  pointLabel,
  relationText,
} from '../../utils/prelearningDisplay'
import G2ChartShell, { type G2ChartOptions } from './G2ChartShell'
import G6GraphShell from './G6GraphShell'

const blue = '#315bff'
const cyan = '#16c7ff'
const green = '#17c983'
const orange = '#ff8a3d'
const red = '#ff4d4f'
const purple = '#6757ff'
const slate = '#6f7d9d'

const nodeTypeColors: Record<string, string> = {
  job_role: blue,
  job_capability: purple,
  course: cyan,
  course_knowledge_point: orange,
  competition: '#13c2c2',
  certificate: green,
  project: '#9254de',
}

const difficultyColors: Record<string, string> = {
  basic: green,
  easy: green,
  medium: blue,
  advanced: orange,
  hard: red,
}

const optionBase = {
  animate: false,
  legend: { color: { position: 'bottom' } },
  theme: 'classic',
}

const graphOptionsBase: Omit<GraphOptions, 'container' | 'data' | 'height' | 'width'> = {
  animation: false,
  behaviors: ['drag-canvas', 'zoom-canvas', 'drag-element', 'click-select'],
  edge: {
    style: {
      endArrow: true,
      labelFill: slate,
      labelFontSize: 11,
      lineWidth: 1.4,
      stroke: '#b7c4dd',
    },
  },
  node: {
    type: 'rect',
    style: {
      fill: '#ffffff',
      halo: true,
      haloStrokeOpacity: 0.12,
      labelFill: '#111b3f',
      labelFontSize: 12,
      labelFontWeight: 600,
      radius: 8,
      shadowBlur: 12,
      shadowColor: 'rgba(35, 72, 155, 0.12)',
      size: [138, 44],
      stroke: '#dbe5f4',
    },
  },
}

const countBy = <T,>(items: T[], getKey: (item: T) => string) =>
  items.reduce<Record<string, number>>((result, item) => {
    const key = getKey(item)
    result[key] = (result[key] || 0) + 1
    return result
  }, {})

export const DifficultyDistributionChart = ({
  height = 280,
  loading,
  points,
}: {
  height?: number
  loading?: boolean
  points: KnowledgePoint[]
}) => {
  const options = useMemo<G2ChartOptions | null>(() => {
    if (!points.length) return null
    const counts = countBy(points, (item) => difficultyText(item.difficultyLevel))
    return {
      ...optionBase,
      data: Object.entries(counts).map(([difficulty, value]) => ({ difficulty, value })),
      encode: { color: 'difficulty', x: 'difficulty', y: 'value' },
      scale: { color: { range: [green, blue, orange, red] } },
      type: 'interval',
    }
  }, [points])

  return <G2ChartShell height={height} loading={loading} options={options} />
}

export const MasteryDonutChart = ({
  height = 280,
  loading,
  nodes,
}: {
  height?: number
  loading?: boolean
  nodes: FusionNode[]
}) => {
  const options = useMemo<G2ChartOptions | null>(() => {
    if (!nodes.length) return null
    const counts = countBy(nodes, (item) => masteryText(item.masteryStatus))
    return {
      ...optionBase,
      coordinate: { innerRadius: 0.64, type: 'theta' },
      data: Object.entries(counts).map(([status, value]) => ({ status, value })),
      encode: { color: 'status', y: 'value' },
      scale: { color: { range: [green, blue, orange, slate] } },
      transform: [{ type: 'stackY' }],
      type: 'interval',
    }
  }, [nodes])

  return <G2ChartShell height={height} loading={loading} options={options} />
}

export const NodeTypeDonutChart = ({
  height = 280,
  loading,
  nodes,
}: {
  height?: number
  loading?: boolean
  nodes: FusionNode[]
}) => {
  const options = useMemo<G2ChartOptions | null>(() => {
    if (!nodes.length) return null
    const counts = countBy(nodes, (item) => nodeTypeText(item.nodeType))
    return {
      ...optionBase,
      coordinate: { innerRadius: 0.6, type: 'theta' },
      data: Object.entries(counts).map(([type, value]) => ({ type, value })),
      encode: { color: 'type', y: 'value' },
      transform: [{ type: 'stackY' }],
      type: 'interval',
    }
  }, [nodes])

  return <G2ChartShell height={height} loading={loading} options={options} />
}

export const AbilityRadarChart = ({
  height = 280,
  loading,
  nodes,
}: {
  height?: number
  loading?: boolean
  nodes: FusionNode[]
}) => {
  const options = useMemo<G2ChartOptions | null>(() => {
    const capabilityNodes = nodes.filter((item) => item.nodeType === 'job_capability' || item.nodeType === 'course_knowledge_point')
    if (!capabilityNodes.length) return null
    const data = capabilityNodes.slice(0, 6).map((item) => ({
      axis: item.label,
      group: '能力覆盖',
      value: percentValue(item.score),
    }))
    return {
      ...optionBase,
      children: [
        {
          data,
          encode: { color: 'group', x: 'axis', y: 'value' },
          scale: { y: { domain: [0, 100] } },
          style: { fillOpacity: 0.18 },
          type: 'area',
        },
        {
          data,
          encode: { color: 'group', x: 'axis', y: 'value' },
          scale: { y: { domain: [0, 100] } },
          style: { lineWidth: 2 },
          type: 'line',
        },
        {
          data,
          encode: { color: 'group', x: 'axis', y: 'value' },
          scale: { y: { domain: [0, 100] } },
          type: 'point',
        },
      ],
      coordinate: { type: 'polar' },
      type: 'view',
    }
  }, [nodes])

  return <G2ChartShell height={height} loading={loading} options={options} />
}

export const WeaknessHeatmapChart = ({
  height = 310,
  loading,
  weakPoints,
}: {
  height?: number
  loading?: boolean
  weakPoints: FusionNode[]
}) => {
  const options = useMemo<G2ChartOptions | null>(() => {
    if (!weakPoints.length) return null
    const stages = ['理解', '练习', '应用']
    const data = weakPoints.slice(0, 6).flatMap((node, index) =>
      stages.map((stage, stageIndex) => ({
        node: node.label,
        stage,
        value: Math.max(12, 100 - percentValue(node.score) - stageIndex * 8 + index * 2),
      })),
    )
    return {
      ...optionBase,
      data,
      encode: { color: 'value', x: 'stage', y: 'node' },
      scale: { color: { range: ['#eff6ff', '#ffd8bf', red] } },
      style: { inset: 2, radius: 5 },
      type: 'cell',
    }
  }, [weakPoints])

  return <G2ChartShell height={height} loading={loading} options={options} />
}

export const WeaknessRankingChart = ({
  height = 310,
  loading,
  weakPoints,
}: {
  height?: number
  loading?: boolean
  weakPoints: FusionNode[]
}) => {
  const options = useMemo<G2ChartOptions | null>(() => {
    if (!weakPoints.length) return null
    return {
      ...optionBase,
      coordinate: { transform: [{ type: 'transpose' }] },
      data: weakPoints.slice(0, 8).map((node) => ({
        label: node.label,
        risk: 100 - percentValue(node.score),
        type: nodeTypeText(node.nodeType),
      })),
      encode: { color: 'type', x: 'label', y: 'risk' },
      type: 'interval',
    }
  }, [weakPoints])

  return <G2ChartShell height={height} loading={loading} options={options} />
}

export const GrowthTrendChart = ({
  height = 280,
  loading,
  nodes,
}: {
  height?: number
  loading?: boolean
  nodes: FusionNode[]
}) => {
  const options = useMemo<G2ChartOptions | null>(() => {
    if (!nodes.length) return null
    const data = nodes.slice(0, 7).map((node, index) => ({
      stage: `阶段${index + 1}`,
      value: Math.min(100, Math.max(20, percentValue(node.score) + index * 4)),
    }))
    return {
      ...optionBase,
      children: [
        {
          data,
          encode: { x: 'stage', y: 'value' },
          scale: { y: { domain: [0, 100] } },
          style: { lineWidth: 2, stroke: blue },
          type: 'line',
        },
        {
          data,
          encode: { x: 'stage', y: 'value' },
          style: { fill: blue },
          type: 'point',
        },
      ],
      type: 'view',
    }
  }, [nodes])

  return <G2ChartShell height={height} loading={loading} options={options} />
}

export const CourseKnowledgeGraph = ({
  graph,
  loading,
  onNodeClick,
}: {
  graph: CourseGraph
  loading?: boolean
  onNodeClick: (node: KnowledgePoint) => void
}) => {
  const graphData = useMemo<GraphData | null>(() => {
    if (!graph.nodes.length) return null
    const idMap = new Map(graph.nodes.map((node) => [node.id, `point-${node.id}`]))
    return {
      edges: graph.edges
        .filter((edge) => idMap.has(edge.sourceKnowledgePointId) && idMap.has(edge.targetKnowledgePointId))
        .map((edge, index) => ({
          data: { raw: edge },
          id: `course-edge-${edge.id || index}`,
          source: idMap.get(edge.sourceKnowledgePointId)!,
          style: {
            labelText: relationText(edge.relationType),
            lineWidth: Math.max(1, percentValue(edge.weight) / 42),
          },
          target: idMap.get(edge.targetKnowledgePointId)!,
        })),
      nodes: graph.nodes.map((node) => ({
        data: { raw: node },
        id: `point-${node.id || node.name}`,
        style: {
          fill: '#ffffff',
          labelText: pointLabel(node),
          stroke: difficultyColors[node.difficultyLevel || ''] || blue,
        },
      })),
    }
  }, [graph])

  const options = useMemo(
    () => ({
      ...graphOptionsBase,
      layout: { nodesep: 46, rankdir: 'LR', ranksep: 86, type: 'dagre' },
    }),
    [],
  )

  return (
    <G6GraphShell
      data={graphData}
      height={500}
      loading={loading}
      options={options}
      onNodeClick={(node: NodeData) => onNodeClick(node.data?.raw as KnowledgePoint)}
    />
  )
}

export const FusionRelationGraph = ({
  graph,
  loading,
  onEdgeClick,
  onNodeClick,
}: {
  graph: FusionGraph
  loading?: boolean
  onEdgeClick: (edge: FusionEdge) => void
  onNodeClick: (node: FusionNode) => void
}) => {
  const graphData = useMemo<GraphData | null>(() => {
    if (!graph.nodes.length) return null
    const nodeKeys = new Set(graph.nodes.map((node) => node.nodeKey))
    return {
      edges: graph.edges
        .filter((edge) => edge.sourceKey && edge.targetKey && nodeKeys.has(edge.sourceKey) && nodeKeys.has(edge.targetKey))
        .map((edge, index) => ({
          data: { raw: edge },
          id: `fusion-edge-${index}-${edge.sourceKey}-${edge.targetKey}`,
          source: edge.sourceKey!,
          style: {
            labelText: relationText(edge.relationType),
            lineWidth: Math.max(1, percentValue(edge.weight) / 45),
          },
          target: edge.targetKey!,
        })),
      nodes: graph.nodes.map((node) => ({
        data: { raw: node },
        id: node.nodeKey || `${node.nodeType}-${node.nodeId || node.label}`,
        style: {
          fill: node.masteryStatus === 'weak' ? '#fff7e6' : '#ffffff',
          labelText: node.label,
          stroke: nodeTypeColors[node.nodeType || ''] || blue,
        },
      })),
    }
  }, [graph])

  const options = useMemo(
    () => ({
      ...graphOptionsBase,
      layout: { linkDistance: 128, nodeStrength: -260, preventOverlap: true, type: 'force' },
    }),
    [],
  )

  return (
    <G6GraphShell
      data={graphData}
      height={520}
      loading={loading}
      options={options}
      onEdgeClick={(edge: EdgeData) => onEdgeClick(edge.data?.raw as FusionEdge)}
      onNodeClick={(node: NodeData) => onNodeClick(node.data?.raw as FusionNode)}
    />
  )
}

export const GrowthPathGraph = ({
  graph,
  loading,
  onNodeClick,
}: {
  graph: FusionGraph
  loading?: boolean
  onNodeClick: (node: FusionNode) => void
}) => {
  const graphData = useMemo<GraphData | null>(() => {
    const path = graph.recommendedPath.length ? graph.recommendedPath : graph.nodes.map((node) => node.label)
    if (!path.length) return null
    const byLabel = new Map(graph.nodes.map((node) => [node.label, node]))
    return {
      edges: path.slice(0, -1).map((item, index) => ({
        id: `path-edge-${index}`,
        source: `path-${index}-${item}`,
        style: { labelText: index === 0 ? '开始' : '推进' },
        target: `path-${index + 1}-${path[index + 1]}`,
      })),
      nodes: path.map((item, index) => {
        const raw = byLabel.get(item) || graph.nodes[index] || { label: item }
        return {
          data: { raw },
          id: `path-${index}-${item}`,
          style: {
            fill: index === 0 ? '#eff6ff' : index === path.length - 1 ? '#f0fff8' : '#ffffff',
            labelText: item,
            size: [150, 46],
            stroke: index === 0 ? blue : index === path.length - 1 ? green : '#dbe5f4',
          },
        }
      }),
    }
  }, [graph])

  const options = useMemo(
    () => ({
      ...graphOptionsBase,
      layout: { nodesep: 46, rankdir: 'LR', ranksep: 88, type: 'dagre' },
    }),
    [],
  )

  return (
    <G6GraphShell
      data={graphData}
      empty={<Empty description="暂无成长路径" image={Empty.PRESENTED_IMAGE_SIMPLE} />}
      height={500}
      loading={loading}
      options={options}
      onNodeClick={(node: NodeData) => onNodeClick(node.data?.raw as FusionNode)}
    />
  )
}
