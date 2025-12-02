# 各包详细类方法功能说明

## 一、config包 - 配置类

### DatabaseConfig.java
**作用**：数据库连接配置管理
**方法/功能**：
- 定义数据库连接参数（驱动、URL、用户名、密码）
- 连接池配置管理
- 初始化数据库驱动
- 提供数据库连接的静态配置

### FilterInitializer.java
**作用**：过滤器初始化配置
**方法/功能**：
- 定义过滤器执行顺序
- 设置不需要登录验证的URL列表
- 配置管理员URL前缀
- 初始化过滤器参数

## 二、controller包 - Servlet控制器

### UserServlet.java
**作用**：处理所有用户相关请求
**方法**：
- **doGet**：处理GET请求（显示注册、登录、个人资料等页面）
- **doPost**：处理POST请求（注册、登录、修改资料等操作）
- **showRegisterPage**：显示注册页面
- **showLoginPage**：显示登录页面
- **showProfilePage**：显示个人资料页面
- **showMyPosts**：显示用户发布的帖子
- **showMyComments**：显示用户发表的评论
- **handleRegister**：处理用户注册请求
- **handleLogin**：处理用户登录请求
- **handleUpdateProfile**：处理更新个人资料请求
- **handleChangePassword**：处理修改密码请求
- **handleUploadAvatar**：处理上传头像请求
- **logout**：处理退出登录请求

### PostServlet.java
**作用**：处理所有帖子相关请求
**方法**：
- **doGet**：显示帖子列表、详情、发布页面等
- **doPost**：处理帖子创建、编辑、删除等操作
- **showPostDetail**：显示帖子详情页面
- **showCreatePostPage**：显示发布帖子页面
- **showEditPostPage**：显示编辑帖子页面
- **showPostList**：显示帖子列表（分页）
- **searchPosts**：搜索帖子
- **handleCreatePost**：处理发布帖子请求
- **handleEditPost**：处理编辑帖子请求
- **handleDeletePost**：处理删除帖子请求

### CommentServlet.java
**作用**：处理所有评论相关请求
**方法**：
- **doGet**：显示评论列表
- **doPost**：处理评论发布、编辑、删除操作
- **showComments**：显示帖子评论列表
- **handleAddComment**：处理添加评论请求
- **handleReplyComment**：处理回复评论请求
- **handleEditComment**：处理编辑评论请求
- **handleDeleteComment**：处理删除评论请求

### LikeServlet.java
**作用**：处理点赞相关请求
**方法**：
- **doPost**：处理点赞/取消点赞请求
- **handleLikePost**：处理点赞帖子请求
- **handleUnlikePost**：处理取消点赞帖子请求
- **handleLikeComment**：处理点赞评论请求
- **handleUnlikeComment**：处理取消点赞评论请求

### FavoriteServlet.java
**作用**：处理收藏相关请求
**方法**：
- **doGet**：显示用户收藏列表
- **doPost**：处理收藏/取消收藏操作
- **showFavorites**：显示用户收藏的帖子列表
- **handleAddFavorite**：处理收藏帖子请求
- **handleRemoveFavorite**：处理取消收藏请求

### PointsServlet.java
**作用**：处理积分相关请求
**方法**：
- **doGet**：显示积分详情、排行榜、兑换页面
- **doPost**：处理积分兑换请求
- **showPoints**：显示用户积分详情
- **showRanking**：显示积分排行榜
- **showExchange**：显示积分兑换页面
- **handleExchange**：处理虚拟道具兑换请求

### FileServlet.java
**作用**：处理文件上传下载请求
**方法**：
- **doGet**：提供文件下载功能
- **doPost**：处理文件上传请求
- **handleUpload**：处理文件上传
- **handleDownload**：处理文件下载
- **handleDeleteFile**：处理文件删除
- **handleAvatarUpload**：专门处理头像上传

### AdminServlet.java
**作用**：处理管理员后台所有请求
**方法**：
- **doGet**：显示管理员页面（用户管理、内容管理、日志查看等）
- **doPost**：处理管理员操作请求
- **showDashboard**：显示管理员仪表板
- **showUserManage**：显示用户管理页面
- **showPostManage**：显示帖子管理页面
- **showCommentManage**：显示评论管理页面
- **handleBanUser**：处理封禁用户请求
- **handleUnbanUser**：处理解封用户请求
- **handleDeletePost**：处理删除帖子请求（管理员权限）
- **handleDeleteComment**：处理删除评论请求（管理员权限）

### HomeServlet.java
**作用**：处理首页和全局搜索请求
**方法**：
- **doGet**：显示首页内容
- **showHomePage**：显示首页（最新帖子、热门帖子、推荐内容）
- **handleSearch**：处理全站搜索请求

### SectionServlet.java
**作用**：处理版块相关请求
**方法**：
- **doGet**：显示版块列表和版块详情
- **showSections**：显示所有版块列表
- **showSectionDetail**：显示特定版块的帖子和信息

### NotificationServlet.java
**作用**：处理通知相关请求
**方法**：
- **doGet**：显示用户通知列表
- **doPost**：处理通知标记已读等操作
- **showNotifications**：显示用户所有通知
- **showUnreadNotifications**：显示未读通知
- **handleMarkAsRead**：处理标记通知为已读请求

### CaptchaServlet.java
**作用**：生成验证码图片
**方法**：
- **doGet**：生成验证码图片并输出到响应
- **generateCaptcha**：生成验证码字符串和图片
- **setCaptchaInSession**：将验证码存入Session

### LogoutServlet.java
**作用**：处理用户退出登录
**方法**：
- **doGet**：销毁Session，重定向到首页
- **doPost**：处理退出登录请求

## 三、service包 - 业务逻辑层

### UserService.java（接口）
**作用**：用户业务接口定义
**方法**：
- **register**：用户注册
- **login**：用户登录验证
- **updateProfile**：更新个人资料
- **changePassword**：修改密码
- **getUserById**：根据ID获取用户
- **getUserByUsername**：根据用户名获取用户
- **recordLogin**：记录用户登录日志

### UserServiceImpl.java
**作用**：用户业务实现
**方法**：
- 实现UserService接口所有方法
- **validateRegisterData**：验证注册数据合法性
- **encryptPassword**：密码加密处理
- **checkUsernameExists**：检查用户名是否存在
- **checkEmailExists**：检查邮箱是否存在
- **generateDefaultAvatar**：生成默认头像路径

### PostService.java（接口）
**作用**：帖子业务接口定义
**方法**：
- **createPost**：创建新帖子
- **updatePost**：更新帖子内容
- **deletePost**：删除帖子
- **getPostById**：根据ID获取帖子
- **getPostsBySection**：获取版块下的帖子
- **getPostsByUser**：获取用户发布的帖子
- **searchPosts**：搜索帖子
- **increaseViewCount**：增加帖子浏览数

### PostServiceImpl.java
**作用**：帖子业务实现
**方法**：
- 实现PostService接口所有方法
- **validatePostData**：验证帖子数据合法性
- **checkPermission**：检查用户对帖子的操作权限
- **updatePostStats**：更新帖子统计信息（浏览、点赞、评论数）

### CommentService.java（接口）
**作用**：评论业务接口定义
**方法**：
- **addComment**：添加评论
- **updateComment**：更新评论
- **deleteComment**：删除评论
- **getCommentsByPost**：获取帖子的评论
- **getCommentsByUser**：获取用户的评论

### CommentServiceImpl.java
**作用**：评论业务实现
**方法**：
- 实现CommentService接口所有方法
- **buildCommentTree**：构建评论树结构
- **generateNotification**：生成评论通知
- **updatePostCommentCount**：更新帖子评论数

### LikeService.java（接口）
**作用**：点赞业务接口定义
**方法**：
- **likePost**：点赞帖子
- **unlikePost**：取消点赞帖子
- **likeComment**：点赞评论
- **unlikeComment**：取消点赞评论
- **isLiked**：检查用户是否已点赞

### LikeServiceImpl.java
**作用**：点赞业务实现
**方法**：
- 实现LikeService接口所有方法
- **checkDuplicateLike**：检查重复点赞
- **updateLikeCount**：更新点赞计数
- **generateLikeNotification**：生成点赞通知

### PointsService.java（接口）
**作用**：积分业务接口定义
**方法**：
- **addPoints**：增加用户积分
- **deductPoints**：扣除用户积分
- **getUserPoints**：获取用户积分
- **getUserLevel**：获取用户等级
- **exchangeGoods**：兑换虚拟道具
- **getRanking**：获取积分排行榜

### PointsServiceImpl.java
**作用**：积分业务实现
**方法**：
- 实现PointsService接口所有方法
- **calculateLevel**：计算用户等级
- **checkDailyLimit**：检查每日积分上限
- **recordPointsChange**：记录积分变动
- **validateExchange**：验证兑换条件

## 四、dao包 - 数据访问层

### UserDao.java（接口）
**作用**：用户数据访问接口
**方法**：
- **insert**：插入新用户
- **update**：更新用户信息
- **delete**：删除用户
- **findById**：根据ID查询用户
- **findByUsername**：根据用户名查询用户
- **findByEmail**：根据邮箱查询用户
- **findAll**：查询所有用户（分页）
- **count**：统计用户数量

### UserDaoImpl.java
**作用**：用户数据访问实现
**方法**：
- 实现UserDao接口所有方法
- 使用JDBC进行数据库操作
- 处理SQL异常
- 转换ResultSet为User对象

### PostDao.java（接口）
**作用**：帖子数据访问接口
**方法**：
- **insert**：插入新帖子
- **update**：更新帖子
- **delete**：删除帖子
- **findById**：根据ID查询帖子
- **findBySectionId**：查询版块下的帖子
- **findByUserId**：查询用户发布的帖子
- **findHotPosts**：查询热门帖子
- **findLatestPosts**：查询最新帖子
- **search**：搜索帖子

### PostDaoImpl.java
**作用**：帖子数据访问实现
**方法**：
- 实现PostDao接口所有方法
- 执行SQL查询和更新
- 处理帖子关联的用户和版块信息
- 实现分页查询逻辑

### CommentDao.java（接口）
**作用**：评论数据访问接口
**方法**：
- **insert**：插入新评论
- **update**：更新评论
- **delete**：删除评论
- **findById**：根据ID查询评论
- **findByPostId**：查询帖子的评论
- **findByUserId**：查询用户的评论
- **countByPostId**：统计帖子评论数

### CommentDaoImpl.java
**作用**：评论数据访问实现
**方法**：
- 实现CommentDao接口所有方法
- 处理评论的父子关系
- 关联查询评论者用户信息

## 五、model包 - 实体类

### User.java
**作用**：用户实体类
**属性/方法**：
- id、username、password、email、avatar等属性
- getter/setter方法
- toString、equals、hashCode方法

### Post.java
**作用**：帖子实体类
**属性/方法**：
- id、title、content、userId、sectionId等属性
- viewCount、likeCount、commentCount等统计属性
- createTime、updateTime等时间属性
- 关联的User和Section对象

### Comment.java
**作用**：评论实体类
**属性/方法**：
- id、content、userId、postId、parentId等属性
- likeCount、createTime等属性
- 关联的User对象
- 子评论列表

### Like.java
**作用**：点赞实体类
**属性/方法**：
- id、userId、targetType、targetId等属性
- createTime属性
- 枚举点赞目标类型（帖子、评论）

### Favorite.java
**作用**：收藏实体类
**属性/方法**：
- id、userId、postId、createTime等属性

## 六、util包 - 工具类

### DBUtil.java
**作用**：数据库连接工具
**方法**：
- **getConnection**：获取数据库连接
- **close**：关闭数据库资源（Connection、Statement、ResultSet）
- **executeQuery**：执行查询操作
- **executeUpdate**：执行更新操作

### StringUtil.java
**作用**：字符串处理工具
**方法**：
- **isEmpty**：判断字符串是否为空
- **isEmail**：验证邮箱格式
- **isPhone**：验证手机号格式
- **escapeHtml**：转义HTML特殊字符
- **trim**：去除空格

### DateUtil.java
**作用**：日期处理工具
**方法**：
- **formatDate**：格式化日期为字符串
- **parseDate**：解析字符串为日期
- **getCurrentTime**：获取当前时间
- **calculateDateDiff**：计算日期差

### FileUtil.java
**作用**：文件处理工具
**方法**：
- **getFileExtension**：获取文件扩展名
- **validateFileType**：验证文件类型
- **saveFile**：保存文件
- **deleteFile**：删除文件
- **getFileSize**：获取文件大小

### JsonUtil.java
**作用**：JSON处理工具
**方法**：
- **toJson**：对象转为JSON字符串
- **fromJson**：JSON字符串转为对象
- **parseJsonArray**：解析JSON数组

### PageUtil.java
**作用**：分页处理工具
**方法**：
- **calculateTotalPages**：计算总页数
- **getPaginationData**：获取分页数据
- **generatePageInfo**：生成分页信息对象

### ImageUtil.java
**作用**：图片处理工具
**方法**：
- **resizeImage**：调整图片尺寸
- **compressImage**：压缩图片
- **generateThumbnail**：生成缩略图
- **cropImage**：裁剪图片

### CaptchaUtil.java
**作用**：验证码生成工具
**方法**：
- **generateCaptcha**：生成验证码字符串
- **createCaptchaImage**：创建验证码图片
- **verifyCaptcha**：验证验证码

## 七、filter包 - 过滤器

### CharacterEncodingFilter.java
**作用**：字符编码过滤器
**方法**：
- **init**：初始化过滤器
- **doFilter**：设置请求和响应编码为UTF-8
- **destroy**：销毁过滤器

### LoginFilter.java
**作用**：登录验证过滤器
**方法**：
- **doFilter**：检查用户是否登录，未登录则跳转到登录页面
- **isExcludedUrl**：判断URL是否需要登录验证
- **checkSession**：检查Session中用户信息

### AdminFilter.java
**作用**：管理员权限过滤器
**方法**：
- **doFilter**：检查用户是否具有管理员权限
- **checkAdminRole**：验证用户角色是否为管理员

### LogFilter.java
**作用**：日志记录过滤器
**方法**：
- **doFilter**：记录请求URL、参数、执行时间
- **logRequestInfo**：记录请求信息
- **logResponseInfo**：记录响应信息

## 八、listener包 - 监听器

### ContextListener.java
**作用**：应用上下文监听器
**方法**：
- **contextInitialized**：应用启动时初始化数据库连接等资源
- **contextDestroyed**：应用关闭时释放资源

### SessionListener.java
**作用**：会话监听器
**方法**：
- **sessionCreated**：会话创建时记录
- **sessionDestroyed**：会话销毁时清理相关数据
- **attributeAdded**：监听Session属性变化

### RequestListener.java
**作用**：请求监听器
**方法**：
- **requestInitialized**：请求开始时记录
- **requestDestroyed**：请求结束时统计执行时间
- **incrementRequestCount**：增加请求计数

## 九、exception包 - 异常类

### BusinessException.java
**作用**：业务异常类
**方法**：
- 继承RuntimeException
- 构造方法支持错误消息和原因
- 用于处理业务逻辑错误

### AuthenticationException.java
**作用**：认证异常类
**方法**：
- 继承BusinessException
- 用于处理用户认证失败情况

### AuthorizationException.java
**作用**：授权异常类
**方法**：
- 继承BusinessException
- 用于处理用户权限不足情况

## 十、测试包 - 测试类

### UserDaoTest.java
**作用**：测试UserDao实现
**方法**：
- **testInsert**：测试插入用户
- **testFindById**：测试根据ID查询用户
- **testFindByUsername**：测试根据用户名查询用户
- **testUpdate**：测试更新用户信息

### PostDaoTest.java
**作用**：测试PostDao实现
**方法**：
- **testInsertPost**：测试插入帖子
- **testFindBySection**：测试查询版块帖子
- **testUpdateViewCount**：测试更新浏览数
- **testDeletePost**：测试删除帖子

### UserServiceTest.java
**作用**：测试UserService业务逻辑
**方法**：
- **testRegister**：测试用户注册
- **testLogin**：测试用户登录
- **testUpdateProfile**：测试更新个人资料
- **testChangePassword**：测试修改密码

### StringUtilTest.java
**作用**：测试字符串工具类
**方法**：
- **testIsEmpty**：测试判空方法
- **testIsEmail**：测试邮箱验证
- **testEscapeHtml**：测试HTML转义
- **testTrim**：测试去除空格