# UI 组件说明

## `AppCard`

统一白色卡片容器，沿用 AntD `Card` 的 `variant="outlined"` 边框风格。

```tsx
<AppCard title="学习提醒" extra={<Button type="link">查看全部</Button>}>
  ...
</AppCard>
```

禁止在业务页面中直接复制卡片阴影和圆角。需要卡片容器时优先使用 `AppCard`。

## `SectionHeader`

统一区块标题，支持左侧图标、标题、描述和右侧操作。

```tsx
<SectionHeader icon={<BellOutlined />} title="学习提醒" action={<Button type="link">查看全部</Button>} />
```

## `MetricCard`

首页和统计页的指标展示组件，包含图标、标题、数值、单位和进度条。

```tsx
<MetricCard title="路径进度" value={62} unit="%" color="#315bff" icon={LineChartOutlined} />
```

## `StatusTag`

业务状态标签，支持 `blue`、`purple`、`green`、`orange`、`red`、`gray`。

```tsx
<StatusTag tone="blue">课程学习</StatusTag>
```

## `ProgressBar`

统一进度条，默认隐藏百分比，只表现进度。

```tsx
<ProgressBar percent={72} color="#2f78ff" />
```

## `AppSidebar`

左侧全局导航，消费 `menuGroups` 和当前用户权限。只在布局层使用。

## `AppTopbar`

顶部工具栏，包含搜索框、通知、消息、头像和退出入口。只在布局层使用。

## `PageContainer`

业务页面统一外壳，负责页面标题区、描述、右侧操作和整体铺满布局。顶部面包屑仍由 `MainLayout` 根据菜单自动生成。

```tsx
<PageContainer title="动态画像" description="展示当前学生画像摘要和维度完整度">
  ...
</PageContainer>
```

页面按钮必须下钻到真实页面、打开详情抽屉或提交真实接口。

## 学习画像页面

学习画像 4 个页面优先调用真实画像接口：

- 对话式画像构建：画像会话创建、消息发送、草稿抽取、画像确认。
- 动态画像：当前画像摘要、完整度、维度卡片。
- 画像维度分析：维度筛选、详情抽屉和推荐资源/路径下钻。
- 画像更新记录：画像版本、证据会话和详情抽屉。

接口数据不足时允许使用 `src/mocks/profile.ts` 进行展示补齐；所有确认和发送动作必须走真实接口。

## 前置数据页面

前置数据页为 AI 学习中心提供真实输入，包含课程图谱和融合图谱相关页面。

- 课程图谱：调用 `/courses` 和 `/courses/{id}/graph`，用 G6 展示知识点网络，用 G2 展示难度分布。
- 岗课赛证关联图谱：调用 `/job-roles`、`/fusion-graph/me`、`/fusion-relations`，用 G6 展示岗位、能力、课程、竞赛、证书、项目关系。
- 知识短板定位：调用融合图谱接口，使用 G2 热力图和排名图展示 `weakPoints`，并下钻到资源生成和学习路径。
- 能力成长路径：调用融合图谱接口，使用 G6 展示 `recommendedPath`，使用 G2 展示成长趋势和能力雷达。

接口无数据或失败时使用 `src/mocks/prelearning.ts` 做展示补齐；所有新增关系、生成资源、生成路径等业务动作仍必须进入真实业务页面或真实接口。

## `G2ChartShell` / `G6GraphShell`

可视化页面统一使用这两个壳组件管理第三方实例生命周期。

- `G2ChartShell` 接收 G2 options，负责容器、加载态、空状态和 `destroy()`。
- `G6GraphShell` 接收 G6 graph data/options，负责图谱渲染、节点点击、边点击、加载态、空状态和 `destroy()`。
- 业务页面只做接口请求和数据适配，不在页面内直接初始化 `new Chart()` 或 `new Graph()`。
- 图表按钮和节点点击必须下钻到详情 Drawer、业务页面或真实接口动作。

## `DashboardPage`

首页工作台样板页，由多个 dashboard 组件拼装。页面优先请求 `GET /dashboard/overview`，接口数据不足时可使用 `src/mocks/**` 做展示补齐。

首页默认页只展示轻量概览：

- Hero 欢迎区。
- 4 个关键指标。
- 今日优先处理。
- 学习路径进度。
- 学习提醒预览。
- 推荐资源预览。
- AI 助手入口。

交互要求：

- Hero 主按钮进入对应业务页，学生进入在线学习，教师进入待办审核。
- 今日优先处理、学习提醒、推荐资源的按钮必须下钻到列表页或对应业务占位页。
- 顶部通知、消息按钮也必须有跳转目标。
- 后续新增页面不得出现无动作按钮；暂未接业务时跳转到对应占位页或返回今日概览。
- Dashboard 和业务页面都必须先调用真实接口；接口为空或失败时可以展示空状态或展示补齐数据，但不得把 mock 结果作为提交、审核、删除、确认等业务动作的成功依据。
