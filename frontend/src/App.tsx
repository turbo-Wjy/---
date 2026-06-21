import { ConfigProvider } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import { RouterProvider } from 'react-router-dom'
import { router } from './router'

const App = () => (
  <ConfigProvider
    locale={zhCN}
    theme={{
      token: {
        colorPrimary: '#315bff',
        borderRadius: 8,
        fontFamily:
          'Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif',
      },
      components: {
        Layout: {
          bodyBg: '#f4f7ff',
          headerBg: '#ffffff',
          siderBg: '#06184a',
        },
        Menu: {
          itemBorderRadius: 8,
          itemHeight: 48,
          itemSelectedBg: '#315bff',
        },
      },
    }}
  >
    <RouterProvider router={router} />
  </ConfigProvider>
)

export default App
