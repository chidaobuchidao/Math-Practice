# 刷题系统 - 后端

## 项目简介

刷题系统后端是一个基于 Spring Boot 构建的 RESTful API 服务，提供完整的在线练习解决方案。系统支持多种题型管理、智能试卷生成、自动批改和错题管理等功能，适用于数学、英语、考公、驾照考试等多种学习场景。

**前端项目**: [Math-Practice-view](https://github.com/chidaobuchidao/Math-Practice-view)

## 🛠 技术栈

| 类别 | 技术 |
|------|------|
| **核心框架** | Spring Boot 3.5.7 |
| **开发语言** | Java 17 |
| **数据持久化** | MyBatis Plus 3.5.14 |
| **数据库** | MySQL 8.0+ |
| **安全框架** | Spring Security |
| **构建工具** | Maven |
| **开发工具** | Lombok, PageHelper, Spring Validation |

## 📋 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **IDE**: IntelliJ IDEA (推荐) 或 Eclipse

## 🗂 项目结构

```
Math-Practice-system/
├── src/main/java/com/mathpractice/
│   ├── config/                          # 配置类
│   │   ├── CorsConfig.java              # 跨域配置
│   │   ├── MyMetaObjectHandler.java     # MyBatis Plus 自动填充
│   │   └── WebMvcConfig.java            # Web MVC 配置
│   ├── controller/                      # 控制层
│   │   ├── ChoiceQuestionController.java
│   │   ├── FileUploadController.java
│   │   ├── PaperController.java
│   │   ├── QuestionController.java
│   │   ├── QuestionGenerationController.java
│   │   ├── UserController.java
│   │   └── WrongQuestionController.java
│   ├── dto/                            # 数据传输对象
│   │   ├── ChoiceQuestionRequest.java
│   │   ├── GeneratePaperRequest.java
│   │   ├── QuestionGenerationRequest.java
│   │   └── SubmitPaperRequest.java
│   ├── entity/                         # 实体类
│   │   ├── DifficultyLevel.java
│   │   ├── Paper.java
│   │   ├── PaperQuestion.java
│   │   ├── Question.java
│   │   ├── QuestionAnswer.java
│   │   ├── QuestionImage.java
│   │   ├── QuestionOption.java
│   │   ├── QuestionType.java
│   │   ├── User.java
│   │   └── WrongQuestion.java
│   ├── exception/                      # 异常处理
│   │   ├── BusinessException.java
│   │   └── ControllerExceptionHandler.java
│   ├── mapper/                         # 数据访问层
│   │   ├── DifficultyLevelMapper.java
│   │   ├── PaperMapper.java
│   │   ├── PaperQuestionMapper.java
│   │   ├── QuestionAnswerMapper.java
│   │   ├── QuestionImageMapper.java
│   │   ├── QuestionMapper.java
│   │   ├── QuestionOptionMapper.java
│   │   ├── QuestionTypeMapper.java
│   │   ├── UserMapper.java
│   │   └── WrongQuestionMapper.java
│   ├── response/                       # 响应封装
│   │   ├── ApiResponse.java
│   │   └── ResponseCode.java
│   ├── service/                        # 业务逻辑层
│   │   ├── impl/                       # 服务实现
│   │   │   ├── FileUploadServiceImpl.java
│   │   │   ├── PaperServiceImpl.java
│   │   │   ├── QuestionServiceImpl.java
│   │   │   ├── UserServiceImpl.java
│   │   │   └── WrongQuestionServiceImpl.java
│   │   ├── FileUploadService.java
│   │   ├── PaperService.java
│   │   ├── QuestionService.java
│   │   ├── UserService.java
│   │   └── WrongQuestionService.java
│   ├── util/                          # 工具类
│   │   └── QuestionGeneratorTool.java  # 题目生成工具
│   └── MathPracticeApplication.java    # 启动类
├── src/main/resources/
│   ├── application.yml                 # 应用配置
│   ├── static/                        # 静态资源
│   └── templates/                     # 模板文件
├── uploads/                           # 文件上传目录
│   └── images/                        # 图片存储
├── pom.xml                            # Maven 配置
└── README.md                          # 项目说明
```

## 🗄 数据库配置

### 1. 创建数据库

```sql
CREATE DATABASE math_practice 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 2. 数据库连接配置

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/math_practice?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
    username: root                    # 修改为实际用户名
    password: your_password           # 修改为实际密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 数据表说明

项目使用 JPA 自动建表，主要数据表包括：

- **用户管理**: `users`
- **题目管理**: `questions`, `question_options`, `question_answers`, `question_images`
- **分类管理**: `question_types`, `difficulty_levels`
- **试卷管理**: `papers`, `paper_questions`
- **学习记录**: `wrong_questions`

## 🚀 快速开始

### 1. 获取代码

```bash
git clone <repository-url>
cd Math-Practice-system
```

### 2. 数据库配置

创建数据库并修改 `application.yml` 中的数据库连接信息。

### 3. 构建项目

```bash
# Windows
mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

### 4. 启动应用

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

或通过 IDE 运行 `MathPracticeApplication.java`。

### 5. 访问应用

启动成功后访问：`http://localhost:8080`

## 📡 API 接口

### 用户管理
| 方法 | 端点 | 说明 |
|------|------|------|
| POST | `/api/users/register` | 用户注册 |
| POST | `/api/users/login` | 用户登录 |
| GET | `/api/users/{id}` | 获取用户信息 |
| PUT | `/api/users/{id}` | 更新用户信息 |

### 题目管理
| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/questions` | 题目列表 |
| GET | `/api/questions/{id}` | 题目详情 |
| POST | `/api/questions/generate` | 自动生成题目 |
| POST | `/api/choice-questions/single` | 创建单选题 |
| POST | `/api/choice-questions/multiple` | 创建多选题 |
| PUT | `/api/choice-questions/{id}` | 更新选择题 |
| DELETE | `/api/questions/{id}` | 删除题目 |

### 试卷管理
| 方法 | 端点 | 说明 |
|------|------|------|
| POST | `/api/papers/generate` | 生成试卷 |
| POST | `/api/papers/{id}/submit` | 提交试卷 |
| GET | `/api/papers/student/{studentId}` | 学生试卷列表 |
| GET | `/api/papers/{id}` | 试卷详情 |

### 错题管理
| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/wrong-questions/student/{studentId}` | 错题列表 |
| DELETE | `/api/wrong-questions/{id}` | 删除错题 |

### 文件管理
| 方法 | 端点 | 说明 |
|------|------|------|
| POST | `/api/upload/image` | 上传图片 |

## ⚙️ 配置说明

### 文件上传配置

```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 5MB      # 单个文件大小限制
      max-request-size: 10MB  # 请求大小限制

file:
  upload:
    path: uploads/images      # 上传路径
    url-prefix: /api/images/  # 访问前缀
```

### MyBatis Plus 配置

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 驼峰命名转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL 日志
```

## 💡 核心功能

### 🎯 题目管理
- **多题型支持**: 单选题、多选题、填空题、计算题
- **多媒体支持**: 题目图片上传和管理
- **智能分类**: 按难度、知识点、题型分类管理

### 🤖 智能题目生成
- **数学运算**: 加减法、乘除法、混合运算
- **灵活配置**: 可调节数字范围、难度等级
- **批量生成**: 支持一次性生成多道题目

### 📝 试卷管理
- **智能组卷**: 从题库按条件筛选题目组卷
- **自动批改**: 支持多种题型的自动评分
- **答案验证**: 智能判断单选、多选、数值题答案

### 📊 学习分析
- **错题记录**: 自动记录和分析错题
- **进度跟踪**: 学习进度和成绩统计
- **个性化推荐**: 基于错题的知识点强化

## 📝 开发规范

### 代码规范
- 遵循 Java 编码规范
- 使用 Lombok 减少样板代码
- 统一的命名规范和代码风格

### 异常处理
```java
// 业务异常示例
throw new BusinessException(ResponseCode.QUESTION_NOT_EXIST);
```

### 统一响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 业务数据
  }
}
```

## ❓ 常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务状态
- 验证数据库连接配置
- 确认用户权限和数据库存在

### 2. 端口冲突
```yaml
server:
  port: 8081  # 修改端口号
```

### 3. 文件上传失败
- 检查上传目录权限
- 确认文件大小限制
- 验证存储路径配置

### 4. 跨域访问问题
已配置 CORS，如需调整请修改 `CorsConfig.java`

## 🚢 部署指南

### 打包应用
```bash
mvnw.cmd clean package
```

生成文件：`target/Math-Practice-0.0.1-SNAPSHOT.jar`

### 运行应用
```bash
java -jar target/Math-Practice-0.0.1-SNAPSHOT.jar
```

### 生产环境建议
1. **安全配置**: 修改默认密码，启用 HTTPS
2. **性能优化**: 配置数据库连接池，缓存策略
3. **日志管理**: 配置日志文件和轮转策略
4. **监控告警**: 添加应用健康检查和监控

## 📄 许可证

本项目仅用于学习交流，禁止商业用途。

## 📞 技术支持

如有问题或建议，请联系项目维护者，邮箱：2980933590@qq.com

---
**欢迎贡献代码，共同完善项目喵~** ✨
