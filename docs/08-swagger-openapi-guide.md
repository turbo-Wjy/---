# Swagger OpenAPI 接口文档说明

## 访问地址

启动 Spring Boot 后端后访问：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

如果需要查看指定分组的 OpenAPI JSON：

```text
http://localhost:8080/v3/api-docs/{group}
```

例如：

```text
http://localhost:8080/v3/api-docs/03-画像与AI学习
```

## 鉴权方式

除登录接口外，业务接口均需要 JWT。

1. 调用 `POST /api/v1/auth/login` 获取 `token`。
2. 在 Swagger UI 右上角点击 `Authorize`。
3. 填入：

```text
Bearer {token}
```

## 接口分组

| 分组 | 说明 |
| --- | --- |
| `00-全部接口` | 所有 `/api/v1/**` 接口 |
| `01-认证权限` | 登录、当前用户、用户、角色、权限 |
| `02-基础数据` | 学院、专业、班级、学生、教师、师生分组 |
| `03-画像与AI学习` | 对话画像、AI资源生成、资源包、学习路径、智能辅导、学习效果评估 |
| `04-课程学习` | 课程、知识点、课程资料、学习记录、答题、错题 |
| `05-岗课赛证融合` | 岗位能力模型、融合关系、融合图谱 |
| `06-竞赛证书成果` | 竞赛发布、竞赛成果、证书标准、证书成果审核 |
| `07-就业扩展` | 企业岗位、AI简历、岗位投递、教师审核、企业审核 |
| `08-统计导出` | 专业统计导出、导出记录 |
| `09-首页工作台` | 学生首页工作台、教师工作台 |

## 前端联调主线

建议前端优先按下面顺序联调：

```text
登录
→ 首页工作台
→ 对话式学习画像
→ 岗课赛证融合图谱
→ 学习路径
→ AI资源包
→ 学习记录/答题/错题
→ 学习效果评估
→ 智能辅导
→ 竞赛/证书成果审核
→ 统计导出
→ AI简历与岗位投递
```

## 统一响应结构

所有接口统一返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "traceId": "..."
}
```

分页接口的 `data` 通常为：

```json
{
  "items": [],
  "page": 1,
  "pageSize": 10,
  "total": 0
}
```

## 注意事项

- 敏感字段默认脱敏或加密存储，Swagger 中不会返回密码、身份证号等明文。
- 文件上传第一版只保存 URL 和元数据，接口中传 `proofFileUrl`、`resourceUrl` 等地址字段。
- AI 生成接口第一版为可演示同步/占位生成，后续接真实大模型时保持接口结构不变。
- 统计导出文件生成到后端本地 `exports/statistics/` 目录，数据库 `export_records.file_url` 保存文件路径。
