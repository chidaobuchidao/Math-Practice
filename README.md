# 数学练习系统 - 后端

## 项目简介

数学练习系统后端是一个基于 Spring Boot 3.5.7 构建的 RESTful API 服务，提供用户管理、题目管理、试卷生成、答题批改等核心功能。

## 技术栈

- **框架**: Spring Boot 3.5.7
- **语言**: Java 17
- **ORM**: MyBatis Plus 3.5.14
- **数据库**: MySQL 8.0+
- **安全**: Spring Security
- **构建工具**: Maven
- **其他**: Lombok, PageHelper, Spring Validation

## 环境要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- IDE（推荐 IntelliJ IDEA 或 Eclipse）

## 项目结构

```
Math-Practice-system/
├── src/
│   └── main/
│       ├── java/com/mathpractice/
│       │   ├── config/              # 配置类
│       │   │   ├── CorsConfig.java           # CORS 跨域配置
│       │   │   ├── MyMetaObjectHandler.java  # MyBatis Plus 自动填充
│       │   │   └── WebMvcConfig.java         # Web MVC 配置
│       │   ├── controller/          # 控制器层
│       │   │   ├── ChoiceQuestionController.java    # 选择题管理
│       │   │   ├── FileUploadController.java        # 文件上传
│       │   │   ├── PaperController.java            # 试卷管理
│       │   │   ├── QuestionController.java         # 题目管理
│       │   │   ├── QuestionGenerationController.java # 题目生成
│       │   │   ├── UserController.java             # 用户管理
│       │   │   └── WrongQuestionController.java    # 错题管理
│       │   ├── dto/                 # 数据传输对象
│       │   │   ├── ChoiceQuestionRequest.java
│       │   │   ├── GeneratePaperRequest.java
│       │   │   ├── QuestionGenerationRequest.java
│       │   │   └── SubmitPaperRequest.java
│       │   ├── entity/              # 实体类
│       │   │   ├── DifficultyLevel.java
│       │   │   ├── Paper.java
│       │   │   ├── PaperQuestion.java
│       │   │   ├── Question.java
│       │   │   ├── QuestionAnswer.java
│       │   │   ├── QuestionImage.java
│       │   │   ├── QuestionOption.java
│       │   │   ├── QuestionType.java
│       │   │   ├── User.java
│       │   │   └── WrongQuestion.java
│       │   ├── exception/           # 异常处理
│       │   │   ├── BusinessException.java
│       │   │   └── controllerExceptionHandler.java
│       │   ├── mapper/              # MyBatis Mapper 接口
│       │   │   ├── DifficultyLevelMapper.java
│       │   │   ├── PaperMapper.java
│       │   │   ├── PaperQuestionMapper.java
│       │   │   ├── QuestionAnswerMapper.java
│       │   │   ├── QuestionImageMapper.java
│       │   │   ├── QuestionMapper.java
│       │   │   ├── QuestionOptionMapper.java
│       │   │   ├── QuestionTypeMapper.java
│       │   │   ├── UserMapper.java
│       │   │   └── WrongQuestionMapper.java
│       │   ├── response/            # 响应封装
│       │   │   ├── ApiResponse.java
│       │   │   └── ResponseCode.java
│       │   ├── service/             # 服务层
│       │   │   ├── impl/            # 服务实现
│       │   │   │   ├── FileUploadServiceImpl.java
│       │   │   │   ├── PaperServiceImpl.java
│       │   │   │   ├── QuestionServiceImpl.java
│       │   │   │   ├── UserServiceImpl.java
│       │   │   │   └── WrongQuestionServiceImpl.java
│       │   │   ├── FileUploadService.java
│       │   │   ├── PaperService.java
│       │   │   ├── QuestionService.java
│       │   │   ├── UserService.java
│       │   │   └── WrongQuestionService.java
│       │   ├── util/                # 工具类
│       │   │   └── QuestionGeneratorTool.java  # 题目生成工具
│       │   └── MathPracticeApplication.java    # 启动类
│       └── resources/
│           ├── application.yml       # 应用配置
│           ├── static/              # 静态资源
│           └── templates/           # 模板文件
├── uploads/                          # 文件上传目录
│   └── images/                       # 图片存储
├── pom.xml                           # Maven 配置
└── README.md                         # 项目说明
```

## 数据库配置

### 1. 创建数据库

```sql
CREATE DATABASE math_practice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/math_practice?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai
    username: root        # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### 3. 数据库表结构

项目使用 JPA 自动建表（`ddl-auto: update`），首次运行会自动创建表结构。主要表包括：

- `users` - 用户表
- `questions` - 题目表
- `question_options` - 题目选项表
- `question_answers` - 题目答案表
- `question_images` - 题目图片表
- `question_types` - 题目类型表
- `difficulty_levels` - 难度等级表
- `papers` - 试卷表
- `paper_questions` - 试卷题目关联表
- `wrong_questions` - 错题表

## 安装与运行

### 1. 克隆项目

```bash
git clone <repository-url>
cd Math-Practice-system
```

### 2. 配置数据库

按照上述数据库配置步骤配置数据库连接。

### 3. 构建项目

```bash
# Windows
mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

### 4. 运行项目

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

或者使用 IDE 直接运行 `MathPracticeApplication.java` 的 `main` 方法。

### 5. 访问应用

启动成功后，访问 `http://localhost:8080`

## API 接口文档

### 用户相关

- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `GET /api/users/{id}` - 获取用户信息
- `PUT /api/users/{id}` - 更新用户信息

### 题目相关

- `GET /api/questions` - 获取题目列表
- `GET /api/questions/{id}` - 获取题目详情
- `POST /api/questions/generate` - 自动生成题目
- `POST /api/choice-questions/single` - 创建单选题
- `POST /api/choice-questions/multiple` - 创建多选题
- `PUT /api/choice-questions/{id}` - 更新选择题
- `DELETE /api/questions/{id}` - 删除题目

### 试卷相关

- `POST /api/papers/generate` - 生成试卷
- `POST /api/papers/{id}/submit` - 提交试卷
- `GET /api/papers/student/{studentId}` - 获取学生的试卷列表
- `GET /api/papers/{id}` - 获取试卷详情

### 错题相关

- `GET /api/wrong-questions/student/{studentId}` - 获取学生错题列表
- `DELETE /api/wrong-questions/{id}` - 删除错题

### 文件上传

- `POST /api/upload/image` - 上传图片

## 配置说明

### 文件上传配置

在 `application.yml` 中配置：

```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 5MB      # 单个文件最大大小
      max-request-size: 10MB   # 请求最大大小

file:
  upload:
    path: uploads/images       # 文件上传路径
    url-prefix: /api/images/   # 文件访问URL前缀
```

### MyBatis Plus 配置

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 开启驼峰命名
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 打印SQL
```

## 核心功能

### 1. 题目管理
- 支持单选题、多选题、填空题、计算题等多种题型
- 支持题目图片上传
- 支持题目难度、知识点分类

### 2. 自动题目生成
- 支持加减法、乘除法、混合运算题目自动生成
- 可配置数字范围和难度等级

### 3. 试卷生成与批改
- 从题库中选择题目生成试卷
- 自动批改学生答案
- 支持单选题、多选题、数值题等多种题型的答案验证
- 自动记录错题

### 4. 错题管理
- 自动记录学生错题
- 支持错题查看和删除

## 开发规范

### 代码风格
- 遵循 Java 编码规范
- 使用 Lombok 简化代码
- 统一使用 MyBatis Plus 进行数据库操作

### 异常处理
- 使用 `BusinessException` 处理业务异常
- 使用 `controllerExceptionHandler` 统一处理异常响应

### API 响应格式
统一使用 `ApiResponse` 封装响应：

```java
{
  "code": 200,
  "message": "操作成功",
  "data": {...}
}
```

## 常见问题

### 1. 数据库连接失败

- 检查数据库服务是否启动
- 检查 `application.yml` 中的数据库配置是否正确
- 检查数据库用户权限

### 2. 端口被占用

修改 `application.yml` 中的端口：

```yaml
server:
  port: 8081  # 修改为其他端口
```

### 3. 文件上传失败

- 检查 `uploads/images` 目录是否存在且有写权限
- 检查文件大小是否超过配置限制

### 4. CORS 跨域问题

项目已配置 CORS，如果仍有问题，检查 `CorsConfig.java` 中的配置。

## 部署说明

### 打包应用

```bash
mvnw.cmd clean package
```

打包后的 JAR 文件位于 `target/Math-Practice-0.0.1-SNAPSHOT.jar`

### 运行 JAR 文件

```bash
java -jar target/Math-Practice-0.0.1-SNAPSHOT.jar
```

### 生产环境配置

建议在生产环境中：
1. 修改数据库密码
2. 关闭 SQL 日志输出
3. 配置合适的文件上传路径
4. 配置 HTTPS
5. 配置日志文件输出

## 许可证

本项目仅用于学习交流，禁止商用。

## 联系方式

如有问题，请联系项目维护者。
欢迎给我留言喵~
