import { Chart, type Chart as G2Chart } from '@antv/g2'
import { Empty, Spin } from 'antd'
import type { ReactNode } from 'react'
import { useEffect, useRef } from 'react'

export type G2ChartOptions = Record<string, unknown>

interface G2ChartShellProps {
  className?: string
  empty?: ReactNode
  height?: number
  loading?: boolean
  options?: G2ChartOptions | null
}

const G2ChartShell = ({
  className,
  empty,
  height = 300,
  loading = false,
  options,
}: G2ChartShellProps) => {
  const containerRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!containerRef.current || !options || loading) return undefined

    const chart = new Chart({
      autoFit: true,
      container: containerRef.current,
    }) as G2Chart

    chart.options(options)
    void chart.render()

    return () => {
      chart.destroy()
    }
  }, [loading, options])

  return (
    <div className={className ? `chart-shell ${className}` : 'chart-shell'} style={{ minHeight: height }}>
      {loading ? (
        <div className="visual-empty" style={{ minHeight: height }}>
          <Spin />
        </div>
      ) : options ? (
        <div ref={containerRef} className="chart-canvas" style={{ height }} />
      ) : (
        <div className="visual-empty" style={{ minHeight: height }}>
          {empty || <Empty description="暂无图表数据" image={Empty.PRESENTED_IMAGE_SIMPLE} />}
        </div>
      )}
    </div>
  )
}

export default G2ChartShell
