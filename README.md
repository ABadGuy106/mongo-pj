## linux安装

#### 解压

```sh
tar -xzvf mongodb-linux-x86_64-ubuntu2004-5.0.14.tar.gz
```

新建目录

```sh
#新建数据目录
mkdir -p /home/hadoop/soft/mongodb-linux-x86_64-ubuntu2004-5.0.14/data/db
#新建数据存储目录
mkdir -p /home/hadoop/soft/mongodb-linux-x86_64-ubuntu2004-5.0.14/log
```

创建配置文件

vim mongod.conf

```
systemLog
    destination: file
    path: "/home/hadoop/soft/mongodb-linux-x86_64-ubuntu2004-5.0.14/log/mongod.log"
    logAppend: true
storage:
    dbPath: "/home/hadoop/soft/mongodb-linux-x86_64-ubuntu2004-5.0.14/data/db"
    journal:
        enabled: true
processManagement:
    fork: true
net:
    bindIp: localhost,192.168.8.30
    port: 27017
```

启动mongo

```sh
cd /soft/mongo
bin/mongod -f conf/mongod.conf
```

## 基本常用命令

需求案例，存放文章评论的数据存放到MongoDB中，数据结构参考如下：

数据库: articledb

| 专栏文章评论   | comment        |                    |                           |
| -------------- | -------------- | ------------------ | ------------------------- |
| 字段名称       | 字段含义       | 字段类型           | 备注                      |
| _id            | ID             | ObjectId或者String | Mongo的主键               |
| articleid      | 文章id         | String             |                           |
| content        | 评论内容       | String             |                           |
| userid         | 评论人id       | String             |                           |
| nickname       | 评论人昵称     | String             |                           |
| createdatetime | 评论的日期时间 | Date               |                           |
| likenum        | 点赞数量       | Int32              |                           |
| replynum       | 回复数量       | Int32              |                           |
| state          | 状态           | String             | 0:不可见，1：可见         |
| parentid       | 上级ID         | String             | 如果为0表示文章的顶级评论 |

### 选择和创建数据库

选择和创建数据库语法格式：

```sql
use 数据库名称
```

如果数据库不存在则自动创建

查看有选择查看的所有数据库命令

```sql
show dbs
或者
show databases;
```

查看当前正在使用的数据库命令

```sql
db
```

### 保留数据库

**admin**: 从权限角度来看，这是"root"数据库。要是将一个用户添加到这个数据库，这个用户自动继承所有数据库的权限。一些特定的服务器端命令也只能从这个数据库运行，比如列出所有的数据库或者关闭服务器

**local**: 这个数据永远不会被复制，可以用来存储限于本地单台服务器的任意集合

**config**: 当mongo用于分片设置时，config数据库在内部使用，用于保存分片的相关信息

### 数据库删除

MongoDB删除数据库的语法格式如下：

```js
> db.dropDatabase()
{ "ok" : 1 }
```

### 集合操作

集合，类似于关系型数据库中的表，可以显式创建，也可以隐式创建

#### 集合创建

基本语法格式

```js
db.createCollection(name)
```

name: 创建的集合名称

```sh
> db.createCollection("my-collection")
{ "ok" : 1 }
```

#### 查看数据库下的所有集合

```sh
> show collections
> show tables
```

#### 集合删除

集合删除语法格式

```
db.collection.drop()
或者
db.集合.drop()
```

## 文档基本CRUD

### 单个文档插入

使用insert()或save()方法向集合插入文档，语法如下：

```sql
db.collection.insert(
	<document or array of decuments>,
	{
		witeConcern: <document>,
		ordered: <boolean>
	}
)
```

#### 参数

| Parameter   | Type              | Description                              |
| ----------- | ----------------- | ---------------------------------------- |
| document    | document or array | 要插入到集合中的文档或文档组。(json格式) |
| witeConcern | document          |                                          |
| ordered     | boolean           | 是否排序，默认为ture                     |

示例：

```js
db.comment.insert({"articleId":"tb00122","content":"这篇文章还可以吧","userId":"12312s233","nickName":"巴拉巴拉","createDateTime":new Date(),"likeNum": NumberInt(10),"state":null})
```

### 多个文档插入

```js
db.comment.insertMany([{"articleId":"tb1222","content":"测试文章评论1","userId":"132323","nickName":"test_a","createDateTime":new Date(),"likeNum": NumberInt(50),"state":null},{"articleId":"tb00211A2","content":"测试文章评论2","userId":"f121222","nickName":"测试名称","createDateTime":new Date(),"likeNum": NumberInt(100),"state":null}])
```



### 基本查询

#### 查询集合中所有文档

```sh
db.comment.find()
```

#### 根据字段查询

```sh
 db.comment.find({字段名称:"字段值"})
```

#### 限制返回行数-只返回第一行

```sh
db.comment.findOne()
```

#### 投影查询(Projection Query)

如果要查询结果返回部分字段，则需要使用投影查询(不显示所有字段，只显示指定字段)

例如： 查询结果只显示 _id、userId、nickName:

```js
> db.comment.find({articleId:"tb00122"},{userId:1,nickName:1})
{ "_id" : ObjectId("63abe2a2d70455151465d256"), "userId" : "12312s233", "nickName" : "巴拉巴拉" }
{ "_id" : ObjectId("63abe2bdd70455151465d257"), "userId" : "12312s233", "nickName" : "巴拉巴拉" }
```

查询结果不显示 _id字段

```js
> db.comment.find({articleId:"tb00122"},{userId:1,nickName:1,_id:0})
{ "userId" : "12312s233", "nickName" : "巴拉巴拉" }
{ "userId" : "12312s233", "nickName" : "巴拉巴拉" }
```

### 文档更新

更新文档语法

```js
db.collection.update(query,update,options)
//或者
db.collection.update(
	<query>,
	<update>,
	{
		upsert: <boolean>,
		multi: <boolean>,
		writeConcern: <document>,
		collation: <document>,
		arrayFilters: [<filterdocument1>,...],
		hint: <document|string>  //available starting in MongoDB 4.2
	}
)
```

参数





#### 示例

##### 覆盖修改

将id为 63b4db5917bccdfd7e5b7679的记录，点赞量修改为1101

```js
> db.comment.update({articleId:"tb1222"},{likeNum:NumberInt(1101)})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
> db.comment.find()
{ "_id" : ObjectId("63abe2a2d70455151465d256"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "巴拉巴拉", "createDateTime" : ISODate("2022-12-28T06:30:58.833Z"), "likeNum" : 10, "state" : null }
{ "_id" : ObjectId("63abe2bdd70455151465d257"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "巴拉巴拉", "createDateTime" : ISODate("2022-12-28T06:31:25.996Z"), "likeNum" : 10, "state" : null }
{ "_id" : ObjectId("63b4db5917bccdfd7e5b7679"), "likeNum" : 1101 }
```

**注意**

执行完成后，这条文档除了likeNum字段其他的字段都不见了

##### 局部修改

将articleId为”tb00211A2“的记录“likeNum”字段修改为200

```js
> db.comment.update({articleId:"tb00211A2"},{$set:{likeNum:NumberInt(200)}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
```

##### 批量修改

更新"userId"为“12312s233”的 nickName为“凯撒”

```js
//默认只修改第一条
> db.comment.update({userId:"12312s233"},{$set:{nickName:"凯撒"}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
//修改所有符合条件的数据
> db.comment.update({userId:"12312s233"},{$set:{nickName:"凯撒"}},{multi:true})
WriteResult({ "nMatched" : 2, "nUpserted" : 0, "nModified" : 2 })
```

列值增长修改

更新"userId"为“12312s233”的 点赞数量递增1

```js
> db.comment.update({userId:"12312s233"},{$inc:{likeNum:NumberInt(1)}})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
```



### 删除文档

#### 语法

```
db.集合名称.remove(条件)
```

删除所有数据

```
db.集合名称.remove()
```

### 文档分页查询

#### 统计查询

语法

```js
db.collection.count(query,option)
```

示例：

统计记录数

```js
> db.employee.count()
7
```

#### 分页查询

查询第一页

```js
> db.employee.find().limit(3)
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb0"), "employee_id" : 100, "first_name" : "Steven", "last_name" : "King", "email" : "SKING", "phone_number" : "515.123.4567", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_PRES", "salary" : 2000 }
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb1"), "employee_id" : 101, "first_name" : "Neena", "last_name" : "Kochhar", "email" : "NKOCHHAR", "phone_number" : "515.123.4568", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb2"), "employee_id" : 102, "first_name" : "Lex", "last_name" : "De Haan", "email" : "LDEHAAN", "phone_number" : "515.123.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "AD_VP", "salary" : 1700 }
```

查询第二页

```js
> db.employee.find().skip(3).limit(3)
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb3"), "employee_id" : 103, "first_name" : "Alexander", "last_name" : "Hunold", "email" : "AHUNOLD", "phone_number" : "590.423.4567", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 900 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb4"), "employee_id" : 104, "first_name" : "Bruce", "last_name" : "Ernst", "email" : "BERNST", "phone_number" : "590.423.4568", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 600 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb5"), "employee_id" : 105, "first_name" : "David", "last_name" : "Austin", "email" : "DAUSTIN", "phone_number" : "590.423.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
```

查询第三页(最后一页)

```js
> db.employee.find().skip(6).limit(3)
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb6"), "employee_id" : 106, "first_name" : "Valli", "last_name" : "Pataballa", "email" : "VPATABAL", "phone_number" : "590.423.4560", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
```

#### 排序查询

语法

```js
db.collection.find().sort({KEY:1})
//或
db.collection.find().sort(排序方式)
```

示例：

对 salary  降序排列 对employee_id 升序排列

```js
> db.employee.find().sort({salary:-1,employee_id:1})
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb5"), "employee_id" : 105, "first_name" : "David", "last_name" : "Austin", "email" : "DAUSTIN", "phone_number" : "590.423.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb6"), "employee_id" : 106, "first_name" : "Valli", "last_name" : "Pataballa", "email" : "VPATABAL", "phone_number" : "590.423.4560", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb0"), "employee_id" : 100, "first_name" : "Steven", "last_name" : "King", "email" : "SKING", "phone_number" : "515.123.4567", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_PRES", "salary" : 2000 }
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb1"), "employee_id" : 101, "first_name" : "Neena", "last_name" : "Kochhar", "email" : "NKOCHHAR", "phone_number" : "515.123.4568", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb2"), "employee_id" : 102, "first_name" : "Lex", "last_name" : "De Haan", "email" : "LDEHAAN", "phone_number" : "515.123.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb3"), "employee_id" : 103, "first_name" : "Alexander", "last_name" : "Hunold", "email" : "AHUNOLD", "phone_number" : "590.423.4567", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 900 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb4"), "employee_id" : 104, "first_name" : "Bruce", "last_name" : "Ernst", "email" : "BERNST", "phone_number" : "590.423.4568", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 600 }
```

**提示：**

skip(),limit(),sort() 三个放在一起执行的时候，现象的顺序是 sort() -> skip() -> limit() 和命令的顺序无关

### 复杂查询

#### 正则的复杂查询

Mongodb的模糊查询是通过正则表达式实现的，格式为

```
db.collection.find({fileld:/正则表达式/})
//或
db.collection.find({字段:/正则表达式/})
```

**提示**

正则表达式是js的语法，直接量的写法

**示例**

查询 评论内容包含“可以”的所有文档

```js
> db.comment.find({content:/可以/})
{ "_id" : ObjectId("63abe2a2d70455151465d256"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:30:58.833Z"), "likeNum" : 11, "state" : null }
{ "_id" : ObjectId("63abe2bdd70455151465d257"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:31:25.996Z"), "likeNum" : 10, "state" : null }
```

查询评论内容以“这篇”开头的所有文档

```js
> db.comment.find({content:/^这/})
{ "_id" : ObjectId("63abe2a2d70455151465d256"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:30:58.833Z"), "likeNum" : 11, "state" : null }
{ "_id" : ObjectId("63abe2bdd70455151465d257"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:31:25.996Z"), "likeNum" : 10, "state" : null }
```

#### 比较查询

语法

```js
db.集合名称.find({field:{$gt:value}}) //大于
db.集合名称.find({field:{$lt:value}}) //小于
db.集合名称.find({field:{$gte:value}}) //大于等于
db.集合名称.find({field:{$lte:value}}) //小于等于
db.集合名称.find({field:{$ne:value}}) //不等于
```

查询 工资大于 1000 的员工信息

```js
> db.employee.find({salary:{$gt:1000}})
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb0"), "employee_id" : 100, "first_name" : "Steven", "last_name" : "King", "email" : "SKING", "phone_number" : "515.123.4567", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_PRES", "salary" : 2000 }
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb1"), "employee_id" : 101, "first_name" : "Neena", "last_name" : "Kochhar", "email" : "NKOCHHAR", "phone_number" : "515.123.4568", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb2"), "employee_id" : 102, "first_name" : "Lex", "last_name" : "De Haan", "email" : "LDEHAAN", "phone_number" : "515.123.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb5"), "employee_id" : 105, "first_name" : "David", "last_name" : "Austin", "email" : "DAUSTIN", "phone_number" : "590.423.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb6"), "employee_id" : 106, "first_name" : "Valli", "last_name" : "Pataballa", "email" : "VPATABAL", "phone_number" : "590.423.4560", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
```

#### 包含查询

包含查询使用$in操作符

查询评论的集合中 articleId 字段中包含  tb00122 或者 tb00211A2 的文档

```js
> db.comment.find({articleId:{$in:["tb00122","tb00211A2"]}})
{ "_id" : ObjectId("63abe2a2d70455151465d256"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:30:58.833Z"), "likeNum" : 11, "state" : null }
{ "_id" : ObjectId("63abe2bdd70455151465d257"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:31:25.996Z"), "likeNum" : 10, "state" : null }
{ "_id" : ObjectId("63b4db5917bccdfd7e5b767a"), "articleId" : "tb00211A2", "content" : "测试文章评论2", "userId" : "f121222", "nickName" : "测试名称", "createDateTime" : ISODate("2023-01-04T01:50:17.288Z"), "likeNum" : 200, "state" : null }
```

不包含查询使用$nin操作符

查询评论集合中 articleId 字段中不包含  tb00211A2 的文档

```js
> db.comment.find({articleId:{$nin:["tb00211A2"]}})
{ "_id" : ObjectId("63abe2a2d70455151465d256"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:30:58.833Z"), "likeNum" : 11, "state" : null }
{ "_id" : ObjectId("63abe2bdd70455151465d257"), "articleId" : "tb00122", "content" : "这篇文章还可以吧", "userId" : "12312s233", "nickName" : "凯撒6", "createDateTime" : ISODate("2022-12-28T06:31:25.996Z"), "likeNum" : 10, "state" : null }
```

### 条件连接查询

如果需要查询同时满足两个以上条件，需要使用$and操作符号进行关联(相当于SQL的and)

格式:

```js
$and:[{},{},{}]
```

示例： 查询员工集合工资大于900并且小于4000的文档

```js
> db.employee.find({$and:[{salary:{$gte:NumberInt(900)}},{salary:{$lte:NumberInt(4000)}}]})
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb0"), "employee_id" : 100, "first_name" : "Steven", "last_name" : "King", "email" : "SKING", "phone_number" : "515.123.4567", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_PRES", "salary" : 2000 }
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb1"), "employee_id" : 101, "first_name" : "Neena", "last_name" : "Kochhar", "email" : "NKOCHHAR", "phone_number" : "515.123.4568", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb2"), "employee_id" : 102, "first_name" : "Lex", "last_name" : "De Haan", "email" : "LDEHAAN", "phone_number" : "515.123.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb3"), "employee_id" : 103, "first_name" : "Alexander", "last_name" : "Hunold", "email" : "AHUNOLD", "phone_number" : "590.423.4567", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 900 }
```

如果两个以上条件之间是或者的关系，可以使$or操作符进行关联

格式

```
$or:[{},{},{}]
```

示例：查询员工id大于103或者 工资小于等于 2000

```js
> db.employee.find({$or:[{employee_id:{$gt:NumberInt(103)}},{salary:{$lte:NumberInt(2000)}}]})
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb0"), "employee_id" : 100, "first_name" : "Steven", "last_name" : "King", "email" : "SKING", "phone_number" : "515.123.4567", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_PRES", "salary" : 2000 }
{ "_id" : ObjectId("63b4f375f0ad541b3e96bfb1"), "employee_id" : 101, "first_name" : "Neena", "last_name" : "Kochhar", "email" : "NKOCHHAR", "phone_number" : "515.123.4568", "hire_date" : ISODate("2023-01-04T03:33:09.355Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb2"), "employee_id" : 102, "first_name" : "Lex", "last_name" : "De Haan", "email" : "LDEHAAN", "phone_number" : "515.123.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "AD_VP", "salary" : 1700 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb3"), "employee_id" : 103, "first_name" : "Alexander", "last_name" : "Hunold", "email" : "AHUNOLD", "phone_number" : "590.423.4567", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 900 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb4"), "employee_id" : 104, "first_name" : "Bruce", "last_name" : "Ernst", "email" : "BERNST", "phone_number" : "590.423.4568", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 600 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb5"), "employee_id" : 105, "first_name" : "David", "last_name" : "Austin", "email" : "DAUSTIN", "phone_number" : "590.423.4569", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
{ "_id" : ObjectId("63b4f3d5f0ad541b3e96bfb6"), "employee_id" : 106, "first_name" : "Valli", "last_name" : "Pataballa", "email" : "VPATABAL", "phone_number" : "590.423.4560", "hire_date" : ISODate("2023-01-04T03:34:45.156Z"), "job_id" : "IT_PROG", "salary" : 4800 }
```

## 索引

#### 查看索引

```js
> db.comment.getIndexes()
[
        {
                "v" : 2,
                "key" : {
                        "_id" : 1   //表示索引排序方式是升序
                },
                "name" : "_id_", //索引名称
                "ns" : "articledb.comment"
        }
]
```

#### 创建索引

语法

```js
db.collection.createIndex(kyes,options)
```

options类型

| Parameter  | Type    | Description                                                  |
| ---------- | ------- | ------------------------------------------------------------ |
| background | Boolean | 建索引过程会阻塞其他数据库操作，background可以指定以后台的方式创建索引。默认值为false |
| unique     | Boolean | 建立的索引是否唯一。指定为true创建唯一索引。默认值是false    |
| name       | String  | 索引名称                                                     |

示例:

给userId创建索引，降序

```js
> db.commnet.createIndex({userId:-1},{background:true})
{
        "createdCollectionAutomatically" : true,
        "numIndexesBefore" : 1,
        "numIndexesAfter" : 2,
        "ok" : 1
}
```

创建复合索引

```js
> db.comment.createIndex({userId:1,articleId:1})
{
        "createdCollectionAutomatically" : false,
        "numIndexesBefore" : 1,
        "numIndexesAfter" : 2,
        "ok" : 1
}
```

#### 索引移除

语法

```
db.collection.dropIndex(idex)
```

根据索引名称删除索引

```
> db.comment.dropIndex("userId_1_articleId_1")
```

删除 userId 为降序的索引

```
> db.comment.dropIndex({userId:-1})
```

删除所有的索引

```
> db.comment.dropIndexes()
```

### 索引的使用

#### 执行计划

语法

```
db.collection.find(query,options).explain(options)
```

查看根据员工id查询数据的情况

```js
> db.employee.find({employee_id:NumberInt(100)}).explain()
{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "articledb.employee",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "employee_id" : {
                                "$eq" : 100
                        }
                },
                "queryHash" : "5361BE73",
                "planCacheKey" : "5361BE73",
                "winningPlan" : {
                        "stage" : "COLLSCAN", //表示查询使用的是集合扫描，并未使用索引
                        "filter" : {
                                "employee_id" : {
                                        "$eq" : 100
                                }
                        },
                        "direction" : "forward"
                },
                "rejectedPlans" : [ ]
        },
        "serverInfo" : {
                "host" : "DESKTOP-0V43945",
                "port" : 27017,
                "version" : "4.2.23",
                "gitVersion" : "f4e6602d3a4c5b22e9d8bcf0722d0afd0ec01ea2"
        },
        "ok" : 1
}
> db.employee.createIndex({employee_id:1})  //创建索引
{
        "createdCollectionAutomatically" : false,
        "numIndexesBefore" : 1,
        "numIndexesAfter" : 2,
        "ok" : 1
}
> db.employee.find({employee_id:NumberInt(100)}).explain()
{
        "queryPlanner" : {
                "plannerVersion" : 1,
                "namespace" : "articledb.employee",
                "indexFilterSet" : false,
                "parsedQuery" : {
                        "employee_id" : {
                                "$eq" : 100
                        }
                },
                "queryHash" : "5361BE73",
                "planCacheKey" : "D88DBAE8",
                "winningPlan" : {
                        "stage" : "FETCH",  //抓取，使用了索引
                        "inputStage" : {
                                "stage" : "IXSCAN",
                                "keyPattern" : {
                                        "employee_id" : 1
                                },
                                "indexName" : "employee_id_1",
                                "isMultiKey" : false,
                                "multiKeyPaths" : {
                                        "employee_id" : [ ]
                                },
                                "isUnique" : false,
                                "isSparse" : false,
                                "isPartial" : false,
                                "indexVersion" : 2,
                                "direction" : "forward",
                                "indexBounds" : {
                                        "employee_id" : [
                                                "[100, 100]"
                                        ]
                                }
                        }
                },
                "rejectedPlans" : [ ]
        },
        "serverInfo" : {
                "host" : "DESKTOP-0V43945",
                "port" : 27017,
                "version" : "4.2.23",
                "gitVersion" : "f4e6602d3a4c5b22e9d8bcf0722d0afd0ec01ea2"
        },
        "ok" : 1
}
```

