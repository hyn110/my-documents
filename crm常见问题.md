## 1 登录页面登录失败后,再次登录一直显示404

- **出现问题的前提:**

  - `action`中使用了 `addActionError("账号或者密码错误!");` 或者 `addXXXError()` 之类的api
  - `action` 未配置多例

- 原因:

  ​	

## 2 新增客户点击保存客户后出现404

- **出现问题的前提:**
  - 表单提交的数据`key` 跟 `model` 属性名一致,但是`model`中对应的属性是引用数据类型
  - 表单对应字段赋值,触发`action` 的属性驱动或模型驱动
- 原因:
  - `XXX`驱动封装数据时出现数据类型不匹配,导致转换异常



## 3 新增客户页面,行业,来源,级别的下拉框数据出现串位

​	即,行业下拉数据显示的确实 来源或者级别的选项...

```java
=======setDict_type_code = 002
=======setDict_type_code = 001
=======setDict_type_code = 006
=======执行查询  Dict_type_code = 006
Hibernate: select basedict0_.dict_id as dict_id1_0_, basedict0_.dict_type_code as dict_typ2_0_, basedict0_.dict_type_name as dict_typ3_0_, basedict0_.dict_item_name as dict_ite4_0_, basedict0_.dict_item_code as dict_ite5_0_, basedict0_.dict_sort as dict_sor6_0_, basedict0_.dict_enable as dict_ena7_0_, basedict0_.dict_memo as dict_mem8_0_ from base_dict basedict0_ where basedict0_.dict_type_code=?
=======执行查询  Dict_type_code = 006
Hibernate: select basedict0_.dict_id as dict_id1_0_, basedict0_.dict_type_code as dict_typ2_0_, basedict0_.dict_type_name as dict_typ3_0_, basedict0_.dict_item_name as dict_ite4_0_, basedict0_.dict_item_code as dict_ite5_0_, basedict0_.dict_sort as dict_sor6_0_, basedict0_.dict_enable as dict_ena7_0_, basedict0_.dict_memo as dict_mem8_0_ from base_dict basedict0_ where basedict0_.dict_type_code=?
=======执行查询  Dict_type_code = 006
Hibernate: select basedict0_.dict_id as dict_id1_0_, basedict0_.dict_type_code as dict_typ2_0_, basedict0_.dict_type_name as dict_typ3_0_, basedict0_.dict_item_name as dict_ite4_0_, basedict0_.dict_item_code as dict_ite5_0_, basedict0_.dict_sort as dict_sor6_0_, basedict0_.dict_enable as dict_ena7_0_, basedict0_.dict_memo as dict_mem8_0_ from base_dict basedict0_ where basedict0_.dict_type_code=?
```

​	**原因:**

​		Action 没有设置多例 , 导致出现并发访问修改变量  Dict_type_code .

## 4 客户列表条件筛选时,什么条件也不设置时查询不到数据

​	**原因 :**  条件判断出问题 . 可能原因 : 

​		1 if 句话判断的是 对象为空  而不是 关联对象的主键为空 

​		2 if 语句判断的是关联对象的主键为空, 而关联对象定义的主键使用的是基本数据类型 long , 浏览器不传值时 ,初始化值就为 0 ,不为空,导致 if 条件失效

## 5 客户列表页面,点击筛选查询会报错,提示不能保存瞬时态对象

​	**原因 :** action 中的查询条件 if 判断语句有问题 , 前端未提交关联对象的数据到后台 ,但是仍然进入if 条件语句 ... 底层原因待分析...

## 6 新增客户保存成功后,跳转列表页面,只显示新增的客户

​	**原因:**保存成功使用的是转发,值栈顶部保存下的新增客户的信息 , 作为查询条件进行了查找,导致只找到一条数据

​	**解决 :** 使用重定向 不用转发

## 7 客户拜访列表页面 , 日期插件失效 并且 ajax 请求不能发送

​	**出现前提 :** 页面使用 datepick.js 插件 , 并且页面使用静态包含 ,在页面尾部 引入 分页的 page.jsp 页面

​	**原因 :** page.jsp 页面内声明了 引入 jquery.js 的声明 , 导致客户拜访列表页面 在页顶部声明的 jquery.js 引入失效 ,最终导致 js 文件,被编译的数序为  date.js --> jquery.js , 而 date.js 却依赖于 jquery.js . 导致 date.js 相关的代码运行失败 ,而后面的 ajax 也不能运行

​	还有一个可能的原因是 , 代码直接复制粘贴 , 控件的 id 不同 , 导致获取不到日期控件 ,运行直接报错 

​	**解决 :** 删除 page.jsp 页面的 jquery.js 引入声明

## 8 值栈 model 元素

​	action 的模型驱动在接收到请求后,就将 当前的 model 压入栈顶 , 如果后续对 model 重新赋值 ...

## 9 fetch="join" 对get,criteria等有效 , 对HQL无效

​	hql 要实现立即加载需要设置 lazy="false" , 使用 fetch="join" 无用