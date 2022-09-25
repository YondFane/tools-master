# tools-master
# 工具库

## 已有工具
##### 1、项目构建工具

##### 2、代码生成器



## 1、项目构建工具

为了解决ssm项目在tomcat上增量部署而生，工作中项目迭代时，某次迭代代码过多，在生产环境中部署时需要一一从工作空间对比拉出修改的文件，文件繁多的话特别耗费时间和精力，并且需要一一比较以防漏文件导致生产生产环境部署后崩溃的情况。
此工具工作直接通过比较，直接复制工作空间编译后的文件到指定的目录下，能够大大节约时间成本和犯错几率。

### 准备阶段

通过一以下git命令获取修改文件



**git archive -o {保存目录}\test.zip {分支} $(git diff --diff-filter=ACMRTUXB --name-only {开始commmitid} {结束commmitid})**



**注意！！！：不包含{开始commmitid}，包含{结束commmitid}**



#### 配置项

```properties
# 项目名
projectName = gdjd-xunjia

# 公共组件名
commonModules = dao,service,entity,common,security

# 源码目录
sourcePath = C:\Users\xxx\Desktop\xxx

# 工作空间
workspacePath = C:\Users\xxx\Desktop\xxxx-xxx

# 保存目录
savePath = C:\Users\xxx\xxx\xxx
```

## 启动

## 2、代码生成器
### 生成实体类
### 支持MySQL、Oracle