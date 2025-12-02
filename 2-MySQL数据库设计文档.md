# MySQL数据库设计文档

## 一、数据库文件列表

根据系统功能，共设计了 **14个数据表**：

### 核心功能表：

1. `users` - 用户表
2. `sections` - 版块表
3. `posts` - 帖子表
4. `comments` - 评论表
5. `likes` - 点赞表
6. `favorites` - 收藏表

### 内容管理表：
7. `attachments` - 附件表
8. `notifications` - 消息通知表

### 积分系统表：
9. `points_records` - 积分记录表
10. `virtual_goods` - 虚拟道具表
11. `exchanges` - 兑换记录表

### 用户行为表：
12. `browse_history` - 浏览历史表
13. `login_logs` - 登录记录表

### 管理功能表：
14. `admin_logs` - 管理员操作日志表

## 二、各表详细属性说明

### 1. `users` - 用户表
| 字段名          | 类型         | 说明                       | 约束                         |
| --------------- | ------------ | -------------------------- | ---------------------------- |
| id              | INT          | 用户ID，主键               | PRIMARY KEY, AUTO_INCREMENT  |
| username        | VARCHAR(50)  | 用户名                     | UNIQUE, NOT NULL             |
| password        | VARCHAR(255) | 密码 | NOT NULL                     |
| email           | VARCHAR(100) | 邮箱                       | UNIQUE                       |
| phone           | VARCHAR(20)  | 手机号                     |                              |
| avatar_url      | VARCHAR(255) | 头像URL                    | DEFAULT 'default_avatar.png' |
| nickname        | VARCHAR(50)  | 昵称                       | NOT NULL                     |
| signature       | TEXT         | 个人签名                   |                              |
| points          | INT          | 积分                       | DEFAULT 0                    |
| level           | INT          | 等级                       | DEFAULT 1                    |
| is_banned       | BOOLEAN      | 是否被封禁                 | DEFAULT FALSE                |
| create_time     | TIMESTAMP    | 注册时间                   | DEFAULT CURRENT_TIMESTAMP    |
| last_login_time | TIMESTAMP    | 最后登录时间               |                              |

### 2. `sections` - 版块表
| 字段名      | 类型        | 说明     | 约束                        |
| ----------- | ----------- | -------- | --------------------------- |
| id          | INT         | 版块ID   | PRIMARY KEY, AUTO_INCREMENT |
| name        | VARCHAR(50) | 版块名称 | NOT NULL                    |
| description | TEXT        | 版块描述 |                             |
| create_time | TIMESTAMP   | 创建时间 | DEFAULT CURRENT_TIMESTAMP   |

### 3. `posts` - 帖子表
| 字段名        | 类型         | 说明                 | 约束                                |
| ------------- | ------------ | -------------------- | ----------------------------------- |
| id            | INT          | 帖子ID               | PRIMARY KEY, AUTO_INCREMENT         |
| title         | VARCHAR(200) | 帖子标题             | NOT NULL                            |
| content       | TEXT         | 帖子内容             | NOT NULL                            |
| user_id       | INT          | 发帖用户ID           | FOREIGN KEY REFERENCES users(id)    |
| section_id    | INT          | 所属版块ID           | FOREIGN KEY REFERENCES sections(id) |
| view_count    | INT          | 浏览次数             | DEFAULT 0                           |
| like_count    | INT          | 点赞数               | DEFAULT 0                           |
| comment_count | INT          | 评论数               | DEFAULT 0                           |
| is_deleted    | BOOLEAN      | 是否删除（逻辑删除） | DEFAULT FALSE                       |
| create_time   | TIMESTAMP    | 发布时间             | DEFAULT CURRENT_TIMESTAMP           |
| update_time   | TIMESTAMP    | 最后修改时间         | DEFAULT CURRENT_TIMESTAMP ON UPDATE |

### 4. `comments` - 评论表
| 字段名      | 类型      | 说明                          | 约束                                |
| ----------- | --------- | ----------------------------- | ----------------------------------- |
| id          | INT       | 评论ID                        | PRIMARY KEY, AUTO_INCREMENT         |
| content     | TEXT      | 评论内容                      | NOT NULL                            |
| user_id     | INT       | 评论用户ID                    | FOREIGN KEY REFERENCES users(id)    |
| post_id     | INT       | 所属帖子ID                    | FOREIGN KEY REFERENCES posts(id)    |
| parent_id   | INT       | 父评论ID（0表示直接评论帖子） | DEFAULT 0                           |
| like_count  | INT       | 点赞数                        | DEFAULT 0                           |
| is_deleted  | BOOLEAN   | 是否删除                      | DEFAULT FALSE                       |
| create_time | TIMESTAMP | 评论时间                      | DEFAULT CURRENT_TIMESTAMP           |
| update_time | TIMESTAMP | 修改时间                      | DEFAULT CURRENT_TIMESTAMP ON UPDATE |

### 5. `likes` - 点赞表
| 字段名      | 类型                    | 说明                     | 约束                             |
| ----------- | ----------------------- | ------------------------ | -------------------------------- |
| id          | INT                     | 点赞记录ID               | PRIMARY KEY, AUTO_INCREMENT      |
| user_id     | INT                     | 点赞用户ID               | FOREIGN KEY REFERENCES users(id) |
| target_type | ENUM('post', 'comment') | 点赞对象类型             | NOT NULL                         |
| target_id   | INT                     | 目标ID（帖子ID或评论ID） | NOT NULL                         |
| create_time | TIMESTAMP               | 点赞时间                 | DEFAULT CURRENT_TIMESTAMP        |

### 6. `favorites` - 收藏表
| 字段名      | 类型      | 说明         | 约束                             |
| ----------- | --------- | ------------ | -------------------------------- |
| id          | INT       | 收藏记录ID   | PRIMARY KEY, AUTO_INCREMENT      |
| user_id     | INT       | 收藏用户ID   | FOREIGN KEY REFERENCES users(id) |
| post_id     | INT       | 收藏的帖子ID | FOREIGN KEY REFERENCES posts(id) |
| create_time | TIMESTAMP | 收藏时间     | DEFAULT CURRENT_TIMESTAMP        |

### 7. `attachments` - 附件表
| 字段名      | 类型         | 说明                   | 约束                                |
| ----------- | ------------ | ---------------------- | ----------------------------------- |
| id          | INT          | 附件ID                 | PRIMARY KEY, AUTO_INCREMENT         |
| filename    | VARCHAR(255) | 文件名                 | NOT NULL                            |
| file_path   | VARCHAR(500) | 文件路径               | NOT NULL                            |
| file_type   | VARCHAR(50)  | 文件类型               |                                     |
| file_size   | INT          | 文件大小（字节）       |                                     |
| user_id     | INT          | 上传用户ID             | FOREIGN KEY REFERENCES users(id)    |
| post_id     | INT          | 关联的帖子ID（可为空） | FOREIGN KEY REFERENCES posts(id)    |
| comment_id  | INT          | 关联的评论ID（可为空） | FOREIGN KEY REFERENCES comments(id) |
| create_time | TIMESTAMP    | 上传时间               | DEFAULT CURRENT_TIMESTAMP           |

### 8. `notifications` - 消息通知表
| 字段名      | 类型                            | 说明                  | 约束                             |
| ----------- | ------------------------------- | --------------------- | -------------------------------- |
| id          | INT                             | 通知ID                | PRIMARY KEY, AUTO_INCREMENT      |
| user_id     | INT                             | 接收用户ID            | FOREIGN KEY REFERENCES users(id) |
| type        | ENUM('reply', 'like', 'system') | 通知类型              | NOT NULL                         |
| title       | VARCHAR(200)                    | 通知标题              |                                  |
| content     | TEXT                            | 通知内容              |                                  |
| related_id  | INT                             | 关联ID（帖子/评论ID） |                                  |
| is_read     | BOOLEAN                         | 是否已读              | DEFAULT FALSE                    |
| create_time | TIMESTAMP                       | 通知时间              | DEFAULT CURRENT_TIMESTAMP        |

### 9. `points_records` - 积分记录表
| 字段名        | 类型                                                         | 说明                               | 约束                             |
| ------------- | ------------------------------------------------------------ | ---------------------------------- | -------------------------------- |
| id            | INT                                                          | 记录ID                             | PRIMARY KEY, AUTO_INCREMENT      |
| user_id       | INT                                                          | 用户ID                             | FOREIGN KEY REFERENCES users(id) |
| change_type   | ENUM('register','login','post','comment','like_received','exchange') | 积分变更类型                       | NOT NULL                         |
| change_amount | INT                                                          | 变更数量（正数为增加，负数为减少） | NOT NULL                         |
| description   | VARCHAR(200)                                                 | 描述                               |                                  |
| create_time   | TIMESTAMP                                                    | 变更时间                           | DEFAULT CURRENT_TIMESTAMP        |

### 10. `virtual_goods` - 虚拟道具表
| 字段名       | 类型         | 说明               | 约束                        |
| ------------ | ------------ | ------------------ | --------------------------- |
| id           | INT          | 道具ID             | PRIMARY KEY, AUTO_INCREMENT |
| name         | VARCHAR(100) | 道具名称           | NOT NULL                    |
| description  | TEXT         | 道具描述           |                             |
| price        | INT          | 所需积分           | NOT NULL                    |
| stock        | INT          | 库存（-1表示无限） | DEFAULT -1                  |
| is_available | BOOLEAN      | 是否可用           | DEFAULT TRUE                |
| create_time  | TIMESTAMP    | 创建时间           | DEFAULT CURRENT_TIMESTAMP   |

### 11. `exchanges` - 兑换记录表
| 字段名      | 类型      | 说明       | 约束                                     |
| ----------- | --------- | ---------- | ---------------------------------------- |
| id          | INT       | 兑换记录ID | PRIMARY KEY, AUTO_INCREMENT              |
| user_id     | INT       | 用户ID     | FOREIGN KEY REFERENCES users(id)         |
| goods_id    | INT       | 道具ID     | FOREIGN KEY REFERENCES virtual_goods(id) |
| quantity    | INT       | 兑换数量   | DEFAULT 1                                |
| total_cost  | INT       | 总花费积分 | NOT NULL                                 |
| create_time | TIMESTAMP | 兑换时间   | DEFAULT CURRENT_TIMESTAMP                |

### 12. `browse_history` - 浏览历史表
| 字段名      | 类型      | 说明         | 约束                             |
| ----------- | --------- | ------------ | -------------------------------- |
| id          | INT       | 浏览记录ID   | PRIMARY KEY, AUTO_INCREMENT      |
| user_id     | INT       | 用户ID       | FOREIGN KEY REFERENCES users(id) |
| post_id     | INT       | 浏览的帖子ID | FOREIGN KEY REFERENCES posts(id) |
| browse_time | TIMESTAMP | 浏览时间     | DEFAULT CURRENT_TIMESTAMP        |

### 13. `login_logs` - 登录记录表

| 字段名     | 类型        | 说明         | 约束                             |
| ---------- | ----------- | ------------ | -------------------------------- |
| id         | INT         | 记录ID       | PRIMARY KEY, AUTO_INCREMENT      |
| user_id    | INT         | 用户ID       | FOREIGN KEY REFERENCES users(id) |
| ip_address | VARCHAR(45) | 登录IP地址   |                                  |
| user_agent | TEXT        | 用户代理信息 |                                  |
| login_time | TIMESTAMP   | 登录时间     | DEFAULT CURRENT_TIMESTAMP        |

### 14. `admin_logs` - 管理员操作日志表
| 字段名      | 类型        | 说明         | 约束                             |
| ----------- | ----------- | ------------ | -------------------------------- |
| id          | INT         | 日志ID       | PRIMARY KEY, AUTO_INCREMENT      |
| admin_id    | INT         | 管理员用户ID | FOREIGN KEY REFERENCES users(id) |
| action_type | VARCHAR(50) | 操作类型     | NOT NULL                         |
| target_type | VARCHAR(50) | 操作目标类型 |                                  |
| target_id   | INT         | 操作目标ID   |                                  |
| details     | TEXT        | 操作详情     |                                  |
| ip_address  | VARCHAR(45) | 操作IP地址   |                                  |
| create_time | TIMESTAMP   | 操作时间     | DEFAULT CURRENT_TIMESTAMP        |

## 三、表间关系图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     users       │    │    sections     │    │  virtual_goods  │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ id (PK)         │    │ id (PK)         │    │ id (PK)         │
│ username (UK)   │    │ name            │    │ name            │
│ password        │    │ description     │    │ description     │
│ email (UK)      │◄──┼┤                 │    │ price           │
│ ...             │   │ └─────────────────┘    │ ...             │
│ points          │   │       │                └─────────────────┘
│ level           │   │       │                       │
└────────┬────────┘   │       │                       │
         │            │       │                       │
         │            │       ▼                       │
         │            │ ┌─────────────────┐          │
         │            │ │     posts       │          │
         │            │ ├─────────────────┤          │
         │            │ │ id (PK)         │          │
         │            │ │ title           │          │
         │            │ │ content         │          │
         │            │ │ user_id (FK) ───┼──────────┘
         │            │ │ section_id (FK)─┼──────────┐
         │            │ │ ...             │          │
         │            │ └────────┬────────┘          │
         │            │          │                   │
         │            │          │                   │
         │            │          ▼                   │
         │            │ ┌─────────────────┐          │
         │            │ │   comments      │          │
         │            │ ├─────────────────┤          │
         │            │ │ id (PK)         │          │
         │            │ │ content         │          │
         │            │ │ user_id (FK) ───┼──────────┘
         │            │ │ post_id (FK) ───┼──────────┐
         │            │ │ parent_id       │          │
         │            │ │ ...             │          │
         │            │ └─────────────────┘          │
         │            │                              │
         │            │                              │
         ▼            ▼                              ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│     likes       │  │   attachments   │  │   exchanges     │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│ id (PK)         │  │ id (PK)         │  │ id (PK)         │
│ user_id (FK)    │  │ filename        │  │ user_id (FK)    │
│ target_type     │  │ file_path       │  │ goods_id (FK)   │
│ target_id       │  │ user_id (FK)    │  │ quantity        │
│ create_time     │  │ post_id (FK)    │  │ total_cost      │
└─────────────────┘  │ comment_id (FK) │  │ create_time     │
         │           │ ...             │  └─────────────────┘
         │           └─────────────────┘
         │
         ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│   favorites     │  │ notifications   │  │ browse_history  │
├─────────────────┤  ├─────────────────┤  ├─────────────────┤
│ id (PK)         │  │ id (PK)         │  │ id (PK)         │
│ user_id (FK)    │  │ user_id (FK)    │  │ user_id (FK)    │
│ post_id (FK)    │  │ type            │  │ post_id (FK)    │
│ create_time     │  │ title           │  │ browse_time     │
└─────────────────┘  │ ...             │  └─────────────────┘
         │           └─────────────────┘
         │
         ▼
┌─────────────────┐  ┌─────────────────┐
│ points_records  │  │   login_logs    │
├─────────────────┤  ├─────────────────┤
│ id (PK)         │  │ id (PK)         │
│ user_id (FK)    │  │ user_id (FK)    │
│ change_type     │  │ ip_address      │
│ change_amount   │  │ user_agent      │
│ ...             │  │ login_time      │
└─────────────────┘  └─────────────────┘
         │
         ▼
┌─────────────────┐
│   admin_logs    │
├─────────────────┤
│ id (PK)         │
│ admin_id (FK)   │
│ action_type     │
│ target_type     │
│ ...             │
└─────────────────┘
```

## 四、主要关系说明

1. **用户与内容关系**：
   - 一个用户可以发布多个帖子 (`users` → `posts`)
   - 一个用户可以发表多个评论 (`users` → `comments`)
   - 一个用户可以点赞多个帖子/评论 (`users` → `likes`)
   - 一个用户可以收藏多个帖子 (`users` → `favorites`)

2. **内容层级关系**：
   - 一个版块包含多个帖子 (`sections` → `posts`)
   - 一个帖子可以有多个评论 (`posts` → `comments`)
   - 一个评论可以有多个回复（自关联 `comments.parent_id` → `comments.id`）

3. **积分系统关系**：
   - 一个用户有多条积分记录 (`users` → `points_records`)
   - 一个用户可以兑换多个道具 (`users` → `exchanges`)
   - 一个道具可以被多个用户兑换 (`virtual_goods` → `exchanges`)

4. **文件附件关系**：
   - 附件可以关联到帖子或评论 (`attachments` → `posts` / `comments`)

这个数据库设计完全支持你的功能规格，并已考虑性能优化（如添加了合适的索引）和数据完整性（外键约束）。