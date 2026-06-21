import { Graph, type EdgeData, type GraphData, type GraphOptions, type IEvent, type NodeData } from '@antv/g6'
import { Empty, Spin } from 'antd'
import type { ReactNode } from 'react'
import { useEffect, useRef } from 'react'

interface G6GraphShellProps {
  className?: string
  data?: GraphData | null
  empty?: ReactNode
  height?: number
  loading?: boolean
  options?: Omit<GraphOptions, 'container' | 'data' | 'height' | 'width'>
  onEdgeClick?: (edge: EdgeData) => void
  onNodeClick?: (node: NodeData) => void
}

const G6GraphShell = ({
  className,
  data,
  empty,
  height = 500,
  loading = false,
  options,
  onEdgeClick,
  onNodeClick,
}: G6GraphShellProps) => {
  const containerRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!containerRef.current || !data || loading) return undefined

    let disposed = false
    const graph = new Graph({
      autoResize: true,
      background: '#fbfdff',
      container: containerRef.current,
      data,
      height,
      ...options,
    })

    graph.on('node:click', (event: IEvent) => {
      const id = (event as IEvent & { target?: { id?: string } }).target?.id
      if (id && onNodeClick) onNodeClick(graph.getNodeData(id))
    })

    graph.on('edge:click', (event: IEvent) => {
      const id = (event as IEvent & { target?: { id?: string } }).target?.id
      if (id && onEdgeClick) onEdgeClick(graph.getEdgeData(id))
    })

    const renderPromise = graph
      .render()
      .then(() => {
        if (!disposed) {
          return graph.fitView({ direction: 'both', when: 'overflow' }).then(() => graph.fitCenter(false))
        }
        return undefined
      })
      .catch(() => {
        // G6 rendering can finish after React has already unmounted the route.
      })

    return () => {
      disposed = true
      void renderPromise.finally(() => {
        try {
          graph.destroy()
        } catch {
          // Ignore teardown races caused by route changes during async rendering.
        }
      })
    }
  }, [data, height, loading, onEdgeClick, onNodeClick, options])

  return (
    <div className={className ? `graph-shell ${className}` : 'graph-shell'} style={{ minHeight: height }}>
      {loading ? (
        <div className="visual-empty" style={{ minHeight: height }}>
          <Spin />
        </div>
      ) : data?.nodes?.length ? (
        <div ref={containerRef} className="graph-canvas" style={{ height }} />
      ) : (
        <div className="visual-empty" style={{ minHeight: height }}>
          {empty || <Empty description="暂无图谱数据" image={Empty.PRESENTED_IMAGE_SIMPLE} />}
        </div>
      )}
    </div>
  )
}

export default G6GraphShell
