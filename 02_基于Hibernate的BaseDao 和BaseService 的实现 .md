# 基于Hibernate的通过BaseDao和BaseService的实现 

​	数据库通用操作包含如下操作 : 

1. 保存一个对象 save
2. 查询数据库的总条数 findCount
3. 分页查询 findPage
4. 带条件的分页查询 findPage
5. 查询所有数据 findAll
6. 根据主键值查询一条数据
7. 更新一条数据 update
8. 删除一条数据 delete

## 1 Dao 层

### 1 BaseDao接口

```java
package com.fmi110.dao;

import org.hibernate.criterion.DetachedCriteria;

import java.io.Serializable;
import java.util.List;

/**
 * BaseDao 定义了常用的查询方法
 *
 * @param <T>
 */
public interface BaseDao<T> {
    /**
     * 保存
     *
     * @param t
     */
    void save(T t);

    /**
     * 查询总数
     *
     * @return
     */
    long findCount();

    /**
     * 分页查找
     *
     * @param firstResult
     * @param maxResults
     * @return
     */
    List<T> findPage(int firstResult, int maxResults);

    /**
     * 条件查询的分页查询
     * @param criteria
     * @param firstResult
     * @param maxResult
     * @return
     */
    List<T> findPage(DetachedCriteria criteria, int firstResult, int maxResult);

    /**
     * 查询所有
     *
     * @return
     */
    List<T> findAll();

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    T findById(Serializable id);

    /**
     * 更新
     *
     * @param t
     */
    void update(T t);

    /**
     * 删除
     *
     * @param t
     */
    void delete(T t);

}

```

### 2 BaseDaoImpl 实现类

```java
package com.fmi110.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import com.fmi110.dao.BaseDao;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * 带泛型,实现对象的增删改查
 */
public abstract class BaseDaoImpl<T> extends HibernateDaoSupport implements BaseDao<T> {

	private Class<?> clazz;// 代表T的类型

	public BaseDaoImpl() {
		// 获取当前的泛型类型
		Class<?> currentClazz = this.getClass();
		// 取值是 <T,A,B,C>
		Type type = currentClazz.getGenericSuperclass();
		// 1.泛型出现的位置
		ParameterizedType t = (ParameterizedType) type;
		// 2.泛型的多少 T
		Type actualType = t.getActualTypeArguments()[0];
		clazz = (Class<?>) actualType;
		System.out.println("BaseDaoImpl 泛型参数的类为 : " + clazz);
		// clazz = ?;
	}
	@Autowired
	public void injectSessionFactory(SessionFactory sessionFactory){
		super.setSessionFactory(sessionFactory);
	}

	@Override
	public void save(T t) {
		getHibernateTemplate().save(t);
	}

	@Override
	public long findCount() {
		DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
		// 查询条数(count(*))
		criteria.setProjection(Projections.rowCount());
		HibernateTemplate template = getHibernateTemplate();
		List<Long> results = (List<Long>) template.findByCriteria(criteria);
		 if (results != null && results.size() > 0) {
            return (long) results.get(0);
        } else {
            return 0;
        }
	}

	@Override
	public List<T> findPage(int firstResult, int maxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
		HibernateTemplate template = getHibernateTemplate();
		return (List<T>) template.findByCriteria(criteria, firstResult, maxResults);
	}

	@Override
	public List<T> findPage(DetachedCriteria criteria, int firstResult, int maxResult) {
		return (List<T>) super.getHibernateTemplate()
							  .findByCriteria(criteria, firstResult, maxResult);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() {
		DetachedCriteria criteria = DetachedCriteria.forClass(clazz);
		HibernateTemplate template = getHibernateTemplate();
		return (List<T>) template.findByCriteria(criteria);
	}

	@Override
	public void update(T t) {
		getHibernateTemplate().update(t);
	}

	@Override
	public void delete(T t) {
		getHibernateTemplate().delete(t);
	}


	@Override
	public T findById(Serializable id) {
		return (T) getHibernateTemplate().get(clazz, id);
	}
}
```

## 2 Service 层

​	通过 Service 层是简单的调用通用 dao 来实现一些基本的curd 操作

### 1 BaseService 接口

```java
package com.fmi110.service;
import org.hibernate.criterion.DetachedCriteria;
import java.io.Serializable;
import java.util.List;

/**
 * 业务层的基类
 * Created by huangyunning on 2017/12/4.
 */
public interface BaseService<T> {
    /**
     * 保存
     *
     * @param t
     */
    void save(T t);

    /**
     * 查询总数
     *
     * @return
     */
    long findCount();

    /**
     * 分页查找
     *
     * @param firstResult
     * @param maxResults
     * @return
     */
    List<T> findPage(int firstResult, int maxResults);

    /**
     * 条件查询的分页查询
     * @param criteria
     * @param firstResult
     * @param maxResult
     * @return
     */
    List<T> findPage(DetachedCriteria criteria, int firstResult, int maxResult);

    /**
     * 查询所有
     *
     * @return
     */
    List<T> findAll();

    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    T findById(Serializable id);

    /**
     * 更新
     *
     * @param t
     */
    void update(T t);

    /**
     * 删除
     *
     * @param t
     */
    void delete(T t);
}
```

### 2 BaseServiceImpl 实现类

```java
package com.fmi110.biz.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

/**
 * 业务层的基类实现类,因为要解决 dao 依赖的注入问题,所以使用抽象方法
 * getBaseDao()  从子类中获取到 dao 对象
 * Created by huangyunning on 2017/12/4.
 */
@Transactional  // 基类也需要开启事务否则报没事务异常
public abstract class BaseServiceImpl<T> implements BaseService<T> {

    private BaseDao<T> baseDao;
    @PostConstruct
    public void initBaseDao(){
        this.baseDao = getBaseDao();
        System.out.println("=====BaseBizImpl 业务层注入 dao : "+baseDao);
    }

    /**
     * 获取basedao,要求子类必须实现
     */
    public abstract BaseDao<T> getBaseDao() ;

    public void setBaseDao(BaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public void save(T t) {
        baseDao.save(t);
    }

    @Override
    public long findCount() {
        return baseDao.findCount();
    }

    @Override
    public List<T> findPage(int firstResult, int maxResults) {
        return baseDao.findPage(firstResult,maxResults);
    }

    @Override
    public List<T> findPage(DetachedCriteria criteria, int firstResult, int maxResult) {
        return baseDao.findPage(criteria,firstResult,maxResult);
    }

    @Override
    public List<T> findAll() {
        return this.baseDao.findAll();
    }

    @Override
    public T findById(Serializable id) {
        return baseDao.findById(id);
    }

    @Override
    public void update(T t) {
        baseDao.update(t);
    }

    @Override
    public void delete(T t) {
        baseDao.delete(t);
    }
}

```

## 3 使用

1 新建一个接口DepDao继承 BaseDao , 并指定泛型的具体类型

```java
package com.fmi110.dao;

import com.fmi110.entity.Dep;

import java.util.List;

/**
 * Created by huangyunning on 2017/12/2.
 */
public interface DepDao extends BaseDao<Dep>{

}

```

2 新建一个 dao 实现类 , 继承baseDaoImpl , 实现DepDao

```java
package com.fmi110.dao.impl;

import com.fmi110.dao.DepDao;
import com.fmi110.entity.Dep;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by huangyunning on 2017/12/2.
 */
@Repository
public class DepDaoImpl extends BaseDaoImpl<Dep> implements DepDao {
  
}
```

3 新建一个 Service 接口 DepService , 继承 BaseService 接口 , 并指定具体的泛型信息

```java
import java.util.List;
import java.util.Map;

/**
 * Created by huangyunning on 2017/12/2.
 */
public interface DepService extends BaseService<Dep>{

}

```

4 新建一个 Service 实现类 , 继承BaseServiceImpl, 实现DepService接口

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangyunning on 2017/12/2.
 */
@Service
@Transactional
public class DepServiceImpl extends BaseServiceImpl<Dep> implements DepService {

    @Autowired
    private DepDao dao;

    @Override
    public BaseDao<Dep> getBaseDao() {
        System.out.println("======给父类返回dao======");
        return this.dao;
    }
}
```