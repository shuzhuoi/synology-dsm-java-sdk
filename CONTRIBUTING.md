# 贡献指南

感谢关注 Synology DSM Java SDK！欢迎通过 issue 和 Pull Request 参与贡献。

## 开发环境

- JDK 17（完整 Reactor 统一用 JDK 17 构建；core 等模块通过 `--release 8` 约束为 Java 8 语法与 API）
- Maven 3.8+（项目未提交 wrapper，使用本机 Maven 即可）
- IDE 无强制要求；`.gitignore` 已覆盖 IDEA / VS Code / NetBeans 配置

## 构建与测试

```bash
# 本地构建并运行全部单元测试（local-build profile 仅用于跳过 GPG 签名，不要用于正式发布）
mvn -B -ntp -P local-build test
```

CI 会在每次推送 main 和每个 PR 上运行同样的命令，提交前请确保本地通过。

## 代码约定

- 代码必须写好**中文注释**，公共 API 的 Javadoc 需要说明用途、参数含义和异常行为。
- 优先使用 Lombok 注解减少样板代码。
- core 模块不允许依赖任何具体 JSON / HTTP / Spring 实现，扩展一律通过 `SynologyJsonCodec`、`SynologyHttpClient` 等 SPI 接口接入。
- 强类型 Request / Response 优先；新增 API 时同步补充单元测试与示例。
- **任何代码、注释、测试、文档中都不允许出现真实的 DSM 地址、账号、密码或 SID**，示例一律使用占位符。

## 提交规范

- 提交信息使用 Conventional Commits 风格，如 `feat(filestation): 新增 xxx`、`fix(auth): 修复 xxx`。
- 一个 PR 聚焦一件事；较大的改动建议先开 issue 讨论。
- 新增公共 API 需要在 `CHANGELOG.md` 的 `Unreleased` 章节补充说明。

## 流程

1. Fork 仓库或从 main 拉出功能分支。
2. 开发并确认 `mvn -B -ntp -P local-build test` 通过。
3. 提交 PR，描述改动动机、方案和验证方式。
4. CI 通过并经维护者 review 后合并。

## 许可

提交即表示同意代码以 [Apache-2.0](LICENSE) 许可证发布。
