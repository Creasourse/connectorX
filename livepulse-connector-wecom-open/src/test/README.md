# 单元测试说明

## 测试目录结构

```
src/test/java/com/cs/
├── BaseTest.java                      # 测试基类
├── ApplicationTest.java               # 应用启动测试
├── config/
│   └── TestConfig.java                # 测试配置类
├── controller/
│   ├── WecomCorpControllerTest.java   # 企业微信账户 Controller 测试
│   └── WecomSyncControllerTest.java   # 数据同步 Controller 测试
├── service/
│   ├── WecomCorpServiceTest.java      # 企业微信账户 Service 测试
│   ├── WecomSyncServiceTest.java      # 数据同步 Service 测试
│   ├── WecomCdpTagMappingServiceTest.java  # CDP标签映射 Service 测试
│   └── MetaTableServiceTest.java      # MetaTable Service 测试
└── util/
    └── WecomXmlUtilsTest.java         # XML解析工具测试
```

## 运行测试

### 1. Maven 命令运行

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=WecomCorpControllerTest

# 运行指定测试方法
mvn test -Dtest=WecomCorpControllerTest#testPageList_Success

# 跳过测试
mvn clean package -DskipTests
```

### 2. IDE 中运行

- **IntelliJ IDEA**: 右键测试类或测试方法，选择 "Run" 或 "Debug"
- **Eclipse**: 右键测试类，选择 "Run As" -> "JUnit Test"

### 3. 运行特定包的测试

```bash
# 运行 controller 包下所有测试
mvn test -Dtest=com.cs.controller.*

# 运行 service 包下所有测试
mvn test -Dtest=com.cs.service.*
```

## 测试说明

### Controller 测试

使用 `MockMvc` 进行 HTTP 接口测试：

```java
@Autowired
private MockMvc mockMvc;

@Test
public void testApi() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/path"))
           .andExpect(status().isOk());
}
```

### Service 测试

直接注入 Service 进行业务逻辑测试：

```java
@Autowired
private WecomCorpService wecomCorpService;

@Test
public void testService() {
    // 测试业务逻辑
}
```

## 测试配置

### 测试环境配置

`src/test/resources/application-test.yml`:

```yaml
spring:
  profiles:
    active: test

logging:
  level:
    com.cs: DEBUG
```

### 数据库配置

测试使用 `@Transactional` 注解，测试后自动回滚数据：

```java
@Transactional
public class BaseTest {
    // 测试数据会自动回滚，不会污染数据库
}
```

## 测试覆盖范围

| 模块 | 测试类 | 覆盖内容 |
|------|--------|----------|
| Controller | WecomCorpControllerTest | 企业微信账户管理接口 |
| Controller | WecomSyncControllerTest | 数据同步接口 |
| Service | WecomCorpServiceTest | 企业微信账户业务逻辑 |
| Service | WecomSyncServiceTest | 数据同步业务逻辑 |
| Service | WecomCdpTagMappingServiceTest | CDP标签映射业务逻辑 |
| Service | MetaTableServiceTest | 元数据表业务逻辑 |
| Util | WecomXmlUtilsTest | XML解析工具 |

## 注意事项

1. **测试隔离**: 每个测试方法独立运行，互不影响
2. **数据回滚**: 测试数据自动回滚，不污染数据库
3. **Mock 外部依赖**: 需要时使用 `@MockBean` Mock 外部服务
4. **测试命名**: 使用 `@DisplayName` 注解提供清晰的测试名称

## 扩展测试

添加新的测试用例：

1. 在相应包下创建测试类
2. 继承 `BaseTest` 基类
3. 使用 `@Test` 和 `@DisplayName` 注解

```java
@DisplayName("XXX 测试")
public class XXXTest extends BaseTest {

    @Test
    @DisplayName("测试XXX - 成功")
    public void testXXX_Success() {
        // 测试逻辑
    }
}
```