# [Hibernate注解详解](http://www.cnblogs.com/haore147/p/5219821.html)

一。实体Bean

每个持久化POJO类都是一个**实体**Bean**, 通过在类的定义中使用 **@Entity 注解来进行声明。

## @Entity 声明实体

```java
@Entity
public class Flightimplements Serializable {
  Long id;
  @Id
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
}
```

**@Entity **注解将一个类声明为实体Bean, @Id 注解声明了该实体Bean的标识属性。

Hibernate 可以对类的属性或者方法进行注解。属性对应field类别，方法的getXxx()对应property类别。

## @Table  声明表

通过 **@Table **为实体Bean指定对应数据库表，目录和schema的名字。

```java
@Entity
@Table(name="tbl_sky")
public class Skyimplements Serializable {
...
```

@Table 注解包含一个 schema 和一个 catelog 属性，使用**@UniqueConstraints **可以定义表的唯一约束。

```java
@Table(name="tbl_sky",
  uniqueConstraints ={@UniqueConstraint(columnNames={"month", "day"})}
)
```

上述代码在  "month" 和 "day" 两个 field 上加上unique constrainst.

## **@Version **

注解用于支持乐观锁版本控制。

```java
@Entity
public class Flight implements Serializable {
   ...
   @Version
   @Column(name="OPTLOCK")
   public Integer getVersion() { ... }
}
```

version 属性映射到 "OPTLOCK" 列，entity manager  使用这个字段来检测冲突。一般可以用数字或者 timestamp 类型来支持 version.

**实体Bean中所有非static 非 transient 属性都可以被持久化，除非用 @Transient 注解。**

默认情况下，所有属性都用 **@Basic **注解。

```java
public transient intcounter; //transient property
private String firstname; //persistent property
@Transient
String getLengthInMeter() { ... } //transient property
String getName() {...} // persistent property
@Basic
int getLength() { ...} // persistent property

@Basic(fetch =FetchType.LAZY)
String getDetailedComment() { ... } // persistent property

@Temporal(TemporalType.TIME)
java.util.Date getDepartureTime() { ... } // persistent property

@Enumerated(EnumType.STRING)
Starred getNote() {... } //enum persisted as String in database
```

上述代码中counter, lengthInMeter 属性将忽略不被持久化，而firstname, name, length 被定义为可持久化和可获取的。

## @TemporalType.(DATE,TIME,TIMESTAMP)

**@TemporalType.(DATE,TIME,TIMESTAMP) **分别Map java.sql.(Date,Time, Timestamp).

> `@TemporalType.DATE`  映射的数据格式为  `2017-11-09`   
>
> `@TemporalType.TIME`   映射的数据格式为  `21:06:22`   
>
> `@TemporalType.TIMESTAMP`  映射的数据格式为 `2017-11-09 12:06:22`

## @Lob

**@Lob **注解属性将被持久化为 Blog 或 Clob 类型。具体的java.sql.Clob, Character[], char[] 和 java.lang.String 将被持久化为 Clob 类型. java.sql.Blob,Byte[], byte[] 和 serializable type 将被持久化为 Blob 类型。

```java
@Lob
public String getFullText() {
   return fullText;  // clob type
}

@Lob
public byte[] getFullCode() {
  return fullCode;  // blog type
}
```

## @Column

**@Column **注解将属性映射到列。

```java
@Entity
public class Flightimplements Serializable {
   ...
   @Column(updatable = false, name ="flight_name", nullable = false, length=50)
   public String getName() { ... }
// 定义 name 属性映射到 flight_name column, not null, can't update, length equal 50

@Column(
   name="columnName"; 					//  (1) 列名
   boolean unique() default false; 		//  (2) 是否在该列上设置唯一约束
   boolean nullable() default true; 	//	(3) 列可空？
   boolean insertable() default true; 	//	(4) 该列是否作为生成 insert 语句的一个列
   boolean updatable() default true; 	//	(5) 该列是否作为生成 update 语句的一个列
   String columnDefinition() default ""; //	(6)  默认值
   String table() default "";			//	(7)  定义对应的表（deault 是主表）
   int length() default 255; 			//	(8)  列长度
   int precision() default 0; // decimalprecision (9)  decimal精度
   int scale() default 0; // decimal scale        (10) decimal长度
  )
```

## @Basic

​	@Basic标记可以指定实体属性的加载方式 , 有两种加载方式，LAZY和EAGER。LAZY 表示即时加载、EAGER表示惰性加载。默认为即时加载

```java
@Basic(fetch=FetchType.EAGER)
@Column(name="CONTACT_EMAIL")
public String getName() {
	return name;
}
```

## **@Transient**

@Transient表示该属性并非一个到数据库表的字段的映射,ORM框架将忽略该属性；
如果一个属性并非数据库表的字段映射，就务必将其标示为@Transient，否则ORM框架默认其注解为@Basic

```java
@Transient  //表示该字段在数据库表中没有
public int getAge() {
　return 1+1;
}
```



## @Embeddable

**嵌入式对象（又称组件）也就是别的对象定义的属性**

组件类必须在类一级定义 **@Embeddable **注解。在特定的实体关联属性上使用 @Embeddable 和@AttributeOverride 注解可以覆盖该属性对应的嵌入式对象的列映射。

```java
@Entity
public class Person implements Serializable {
   // Persistent component using defaults
   Address homeAddress;
  
   @Embedded
   @AttributeOverrides( {
      @AttributeOverride(name="iso2",column = @Column(name="bornIso2") ), // 覆盖属性 , 将 iso2 映射到列 bornIso2
      @AttributeOverride(name="name",column = @Column(name="bornCountryName") )
   } )    
   Country bornIn;
   ...
}

@Embeddable
public class Address implements Serializable {
   String city;
   Country nationality; //no overriding here
}

@Embeddable
public class Country implements Serializable {

   private String iso2;

   @Column(name="countryName")
   private String name;
  
   public String getIso2() { return iso2; }
   public void setIso2(String iso2) { this.iso2= iso2; }
  
   public String getName() { return name; }
   public void setName(String name) { this.name= name; }
   ...
}
```

Person 类定义了 Address 和  Country 对象，具体两个类实现见上。

**无注解属性默认值：**

•属性为简单类型，则映射为@Basic

•属性对应的类型定义了@Embeddable 注解，则映射为@Embedded

•属性对应的类型实现了Serializable,则属性被映射为@Basic并在一个列中保存该对象的serialized版本。

•属性的类型为java.sql.Clob or  java.sql.Blob, 则映射到 @Lob 对应的类型。

## @Id 

**映射主键属性**

**@Id **注解可将实体Bean中某个属性定义为主键，

## @SequenceGenerator

```java
@SequenceGenerator(name = "dep_seq",	// 声明一个序列
					sequenceName = "DEP_SEQ", // 与数据库中序列的名对应
					initialValue = 1, // 必须指定,否则 hibernate 生成的序列可能出现负数
					allocationSize=1  // 必须指定,否则 hibernate 生成的序列可能出现负数
					)
```

​	用于声明序列化生成器 , @GenerateValue 的 generator 属性引用的是 @SequenceGenerator 的name 属性

##  @GenerateValue

使用 @GenerateValue 注解可以定义该标识符的生成策略。

• `AUTO`		 	- 可以是 identity column, sequence 或者 table 类型，取决于不同底层的数据库
• `TABLE` 			- 使用table保存id值
• `IDENTITY` 		- identity column
• `SEQUENCE`	 	- sequence

```java
@Id
@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_STORE")
public Integer getId(){ ... }

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
public Long getId() {... }

// uuid作为主键
@Id
@GenericGenerator(name = "system-uuid", strategy = "uuid2")
@GeneratedValue(generator = "system-uuid")
private String id;
```

AUTO 生成器，适用与可移值的应用，多个@Id可以共享同一个identifier生成器，只要把generator属性设成相同的值就可以。通过**@SequenceGenerator ** 和  **@TableGenerator**  可以配置不同的identifier 生成器。

```java
<table-generatorname="EMP_GEN"
     table="GENERATOR_TABLE"
     pk-column-name="key"
     value-column-name="hi"
     pk-column-value="EMP"
     allocation-size="20"/>

//and the annotationequivalent

@javax.persistence.TableGenerator(
     name="EMP_GEN",
     table="GENERATOR_TABLE",
     pkColumnName = "key",
     valueColumnName = "hi"
     pkColumnValue="EMP",
     allocationSize=20
)
------------------------------------------------------------------------------------------
<sequence-generatorname="SEQ_GEN"
     sequence-name="my_sequence"
  		
     allocation-size="20"/>

//and the annotationequivalent

@javax.persistence.SequenceGenerator(
     name="SEQ_GEN",
     sequenceName="my_sequence",
     initialValue = 1,  // 不指定初始值为1的话,hibernate 生成的序列值会从负数开始...
     allocationSize=20
)
-----------------------------------------------------------------------------------------
The next example shows the definition of a sequence generator in a class scope:
-----------------------------------------------------------------------------------------
@Entity
@javax.persistence.SequenceGenerator(
    name="SEQ_STORE",
    sequenceName="my_sequence",
  	initialValue = 1
)
public class Store implements Serializable {

   private Long id;

   @Id
   @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="SEQ_STORE")
   public Long getId() { return id; }
}
```

Store类使用名为 my_sequence 的 sequence，并且 SEQ_STORE 生成器对于其他类是不可见的。

**通过下面语法，你可以定义组合键。**

•将组件类注解为@Embeddable，并将组件的属性注解为 @Id
•将组件的属性注解为@EmbeddedId
•将类注解为@IdClass，并将该实体中所有主键的属性都注解为 @Id

```java
@Entity
@IdClass(FootballerPk.class)
public classFootballer {

  //part of the id key

  @Id 
  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
     this.firstname = firstname;
  }

  //part of the id key

  @Id 
  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getClub() {
  	return club;
  }
  
  public void setClub(String club) {
   	this.club = club;
  }

  //appropriate equals() and hashCode() implementation

}

@Embeddable
public classFootballerPk implements Serializable {

  //same name and type as in Footballer

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    	this.firstname = firstname;
  }

  //same name and type as in Footballer

  public String getLastname() {
    	return lastname;
  }

  public void setLastname(String lastname) {
   		this.lastname = lastname;
  }

  //appropriate equals() and hashCode()implementation

}

 
@Entity
@AssociationOverride(name="id.channel", joinColumns =@JoinColumn(name="chan_id") )
public class TvMagazin{

   @EmbeddedId 
   public TvMagazinPk id;

   @Temporal(TemporalType.TIME) 
   Date time;

}

@Embeddable
public class TvMagazinPk implements Serializable {

   @ManyToOne
   public Channel channel;

   public String name;

   @ManyToOne
   public Presenter presenter;

}

```

## @Inheritance

**映射继承关系**

**EJB支持3种类型的继承。**

• Table per Class Strategy : the <union-class> element inHibernate 每个类一张表
• Single Table per Class Hierarchy Strategy : the<subclass> element in Hibernate 每个类层次结构一张表
• Joined Subclass Strategy : the <joined-subclass>element in Hibernate 连接的子类策略

**@Inheritance **注解来定义所选的子类策略。

**每个类一张表**

```java
@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public class Flight implements Serializable {}
```

有缺点，如多态查询或关联。Hibernate使用 SQL Union 查询来实现这种策略。这种策略支持双向的一对多关联，但不支持IDENTIFY 生成器策略，因为ID必须在多个表间共享。一旦使用就不能使用AUTO和IDENTIFY生成器。

**每个类层次结构一张表**

```java
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="planetype",
    discriminatorType=DiscriminatorType.STRING
)

@DiscriminatorValue("Plane")
public class Plane {... }

@Entity
@DiscriminatorValue("A320")
public class A320 extends Plane { ... }

```

整个层次结构中的所有父类和子类属性都映射到同一个表中，他们的实例通过一个辨别符列（discriminator）来区分。

Plane 是父类**。**@DiscriminatorColumn  注解定义了辨别符列。对于继承层次结构中的每个类, @DiscriminatorValue  注解指定了用来辨别该类的值。辨别符列名字默认为DTYPE，其默认值为实体名。其类型为 DiscriminatorType.STRING。

**连接的子类**

```java
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Boat implements Serializable { ... }

@Entity
public class Ferry extends Boat { ... }

@Entity
@PrimaryKeyJoinColumn(name="BOAT_ID")
public class AmericaCupClass extends Boat { ... }

```

以上所有实体使用 JOINED  策略 Ferry 和 Boat class 使用同名的主键关联(eg:Boat.id = Ferry.id)， AmericaCupClass和 Boat 关联的条件为Boat.id = AmericaCupClass.BOAT_ID.

**从父类继承的属性**

```java
@MappedSuperclass
public class BaseEntity {
  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  public Date getLastUpdate() { ... }
  
  public String getLastUpdater() { ... }
  ...
}

@Entity 
class Order extends BaseEntity {

  @Id 
  public Integer getId() { ... }
  ...
}
```

继承父类的一些属性，但不用父类作为映射实体，这时候需要 **@MappedSuperclass **注解。上述实体映射到数据库中的时候对应Order 实体Bean, 其具有 id, lastUpdate, lastUpdater 三个属性。**如果没有@MappedSuperclass 注解,则父类中属性忽略**，这是Order 实体 Bean 只有 id 一个属性。

**映射实体Bean的关联关系**

## @OneToOne

**一对一**

使用 **@OneToOne **注解可以建立实体Bean之间的一对一关系。一对一关系有3种情况。

•关联的实体都共享同样的主键。

```java
@Entity
public class Body {
  @Id
  public Long getId() { return id; }

  @OneToOne(cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  public Heart getHeart() {
     return heart;
  }
  ...

}

@Entity
public class Heart {
  @Id
  public Long getId() { ...}

}
```

通过**@PrimaryKeyJoinColumn **注解定义了一对一的关联关系。

•其中一个实体通过外键关联到另一个实体的主键。**注：一对一，则外键必须为唯一约束。**

```java
@Entity
public class Customer implements Serializable {

   @OneToOne(cascade = CascadeType.ALL)
   @JoinColumn(name="passport_fk")
   public Passport getPassport() {

   ...

}

@Entity
public class Passport implements Serializable {

   @Id
   public Long getId() { ...}
  
   @OneToOne(mappedBy = "passport")
   public Customer getOwner() {

   ...

}
```

通过**@JoinColumn**注解定义一对一的关联关系。**如果没有@JoinColumn注解，则系统自动处理，在主表中将创建连接列，列名为：主表的关联属性名 + 下划线 ~~+ 被关联端的主键列名~~**。上例为 passport_id, 因为Customer 中关联属性为passport, Passport 的主键为 id.

•通过关联表来保存两个实体之间的关联关系。注：一对一，则关联表每个外键都必须是唯一约束。

```java
@Entity
public class Customer implements Serializable {

   @OneToOne(cascade = CascadeType.ALL)
   @JoinTable(name= "CustomerPassports",  						// 中间表的表名
        joinColumns= @JoinColumn(name="customer_fk"),			// 当前表的列对应中间表的列 customer_fk
        inverseJoinColumns= @JoinColumn(name="passport_fk")
   )
   public Passport getPassport() {

   ...

}

@Entity 
 public class Passport implements Serializable {

   @OneToOne(mappedBy = "passport")
   public Customer getOwner() {

   ...

}

```

Customer 通过 CustomerPassports 关联表和 Passport 关联。该关联表通过passport_fk 外键指向Passport 表，该信心定义为inverseJoinColumns 的属性值。通过 customer_fk 外键指向Customer 表，该信息定义为joinColumns 属性值。

使用了 @JoinTable 后 , 会出现联合主键

## @ManyToOne

**多对一**

使用 **@ManyToOne **注解定义多对一关系。

```java
@Entity()
public class Flight implements Serializable {

  @ManyToOne( cascade = {CascadeType.PERSIST,CascadeType.MERGE} )
  @JoinColumn(name="COMP_ID")
  public Company getCompany() {
    return company;
  }
  ...

}

```

其中@JoinColumn 注解是可选的，关键字段默认值和一对一关联的情况相似。列名为：主题的关联属性名 + 下划线 + 被关联端的主键列名。本例中为company_id,因为关联的属性是company,Company的主键为 id.

**@ManyToOne **注解有个targetEntity属性，该参数定义了目标实体名。通常不需要定义，大部分情况为默认值。但下面这种情况则需要targetEntity 定义（使用接口作为返回值，而不是常用的实体）。

```java
@Entity()
public class Flight implements Serializable {

   @ManyToOne(cascade=  {CascadeType.PERSIST,CascadeType.MERGE},targetEntity= CompanyImpl.class)
   @JoinColumn(name="COMP_ID")
   public Company getCompany() {
     return company;
   }

   ...

}

public interface Company {
   ...

```

多对一也可以通过关联表的方式来映射，通过 @JoinTable 注解可定义关联表。该关联表包含指回实体的外键（通过@JoinTable.joinColumns）以及指向**目标实体表的外键**（通过@JoinTable.inverseJoinColumns）.

```java
@Entity()
public class Flight implements Serializable {

   @ManyToOne( cascade = {CascadeType.PERSIST,CascadeType.MERGE} )
   @JoinTable(name="Flight_Company",
       joinColumns =@JoinColumn(name="FLIGHT_ID"),
       inverseJoinColumns =@JoinColumn(name="COMP_ID")
   )
   public Company getCompany() {
       return company;
   }

   ...

}

```

集合类型

##  @OneToMany

** 一对多**

**@OneToMany **注解可定义一对多关联。一对多关联可以是双向的。

**双向**

规范中多对一端几乎总是双向关联中的主体（owner）端，而一对多的关联注解为 @OneToMany(mappedBy=)

```java
@Entity
public class Troop {

  @OneToMany(mappedBy="troop")   // 集合端的注解不能定义任何物理映射
  public Set<Soldier> getSoldiers() {

  ...

}

@Entity
public class Soldier {

  @ManyToOne
  @JoinColumn(name="troop_fk")
  public Troop getTroop() {
  ...

  }

```

Troop 通过troop属性和Soldier建立了一对多的双向关联。**在 mappedBy 端不必也不能定义任何物理映射**。

**单向**

```java
@Entity
public class Customer implements Serializable {

   @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
   @JoinColumn(name="CUST_ID")
   public Set<Ticket> getTickets() {
      ...
   }

@Entity
public class Ticket implements Serializable {
   ... //no bidir

}

```

一般通过连接表来实现这种关联，可以通过**@JoinColumn**注解来描述这种单向关联关系。上例 Customer 通过CUST_ID列和 Ticket 建立了单向关联关系。

**通过关联表来处理单向关联**

```java
@Entity
public class Trainer {

  @OneToMany
  @JoinTable(
     name="TrainedMonkeys",
     joinColumns = @JoinColumn(name="trainer_id"),
     inverseJoinColumns = @JoinColumn(name="monkey_id")
  )
  public Set<Monkey> getTrainedMonkeys(){
     ...
  }

@Entity
public class Monkey {
  ... //no bidir
}

```

通过**关联表来处理单向一对多关系是首选**，这种关联通过 @JoinTable 注解来进行描述。上例子中Trainer 通过TrainedMonkeys表和Monkey建立了单向关联关系。其中外键trainer_id关联到Trainer(joinColumns)  而 外键monkey_id关联到Monkey(inverseJoinColumns).

**默认处理机制**

通过连接表来建立单向一对多关联不需要描述任何物理映射，表名由一下3个部分组成，主表(ownertable)表名 + 下划线 + 从表(the other side table)表名。指向主表的外键名:主表表名+下划线+主表主键列名指向从表的外键定义为唯一约束，用来表示一对多的关联关系。

```java
@Entity
public class Trainer {

  @OneToMany
  public Set<Tiger> getTrainedTigers(){
  ...

}

@Entity
public class Tiger {
  ... //no bidir
}

```

上述例子中Trainer 和 Tiger 通过 Trainer_Tiger 连接表建立单向关联关系。其中外键 trainer_id 关联到Trainer表，而外键 **trainedTigers**_id 关联到Tiger 表。

## @ManyToMany

**多对多**

通过 @ManyToMany  注解定义多对多关系，同时通过 @JoinTable 注解描述关联表和关联条件。其中一端定义为owner, 另一段定义为inverse(对关联表进行更新操作，这段被忽略)。

```java
@Entity
public class Employer implements Serializable {

  @ManyToMany(
    targetEntity=org.hibernate.test.metadata.manytomany.Employee.class,
    cascade={CascadeType.PERSIST,CascadeType.MERGE}
  )

  @JoinTable(
   name="EMPLOYER_EMPLOYEE",
   joinColumns=@JoinColumn(name="EMPER_ID"),
   inverseJoinColumns=@JoinColumn(name="EMPEE_ID")
  )
  public Collection getEmployees() {
    return employees;
  }
  ...
}

@Entity
public class Employee implements Serializable {

  @ManyToMany(
    cascade = {CascadeType.PERSIST,CascadeType.MERGE},
    mappedBy = "employees",
    targetEntity = Employer.class
  )

  public Collection getEmployers() {
    return employers;
  }

}

```

**默认值：**

关联表名：主表表名 + 下划线 + 从表表名；关联表到主表的外键：主表表名 + 下划线 + 主表中主键列名；关联表到从表的外键名：*主表中用于关联的属性名* + 下划线 + 从表的主键列名。

## @CascadeType

**用cascading 实现传播持久化（Transitivepersistence）**

cascade 属性接受值为 CascadeType 数组，其类型如下：

• **CascadeType.PERSIST**:cascades the persist (create) operation to associated entities persist() iscalled or if the entity is managed 如果一个实体是受管状态，或者当 persist() 函数被调用时，触发级联创建(create)操作。

• **CascadeType.MERGE**:cascades the merge operation to associated entities if merge() is called or if the entity is managed 如果一个实体是受管状态，或者当merge() 函数被调用时，触发级联合并(merge)操作。

• **CascadeType.REMOVE**:cascades the remove operation to associated entities if delete() is called 当 delete()函数被调用时，触发级联删除(remove)操作。

• **CascadeType.REFRESH**:cascades the refresh operation to associated entities if refresh() iscalled  当refresh() 函数被调用时，出发级联更新(refresh)操作。

• **CascadeType.ALL**:all of the above  以上全部



**CascadeType.SAVE_UPDATE** : **貌似用JPA的CascadeType注解对save方法无效。可以用Hibernate的注解 @Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)**



## @SecondaryTables 和 @SecondaryTable

**映射二级列表**

使用类一级的 **@SecondaryTable **和 @SecondaryTables 注解可以实现单个实体到多个表的映射。使用@Column或者@JoinColumn 注解中的table 参数可以指定某个列所属的特定表。

```java
@Entity
@Table(name="MainCat")
@SecondaryTables({
    @SecondaryTable(name="Cat1",pkJoinColumns={
    @PrimaryKeyJoinColumn(name="cat_id",referencedColumnName="id")}),
    @SecondaryTable(name="Cat2",uniqueConstraints={
                       								 @UniqueConstraint(columnNames={"storyPart2"})
                       							 })
 })
public class Cat implements Serializable {

  private Integer id;
  private String name;
  private String storyPart1;
  private String storyPart2;

  @Id 
  @GeneratedValue
  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Column(table="Cat1")
  public String getStoryPart1() {
    return storyPart1;
  }

  @Column(table="Cat2")
  public String getStoryPart2() {
    return storyPart2;
  }

```

上述例子中，name 保存在MainCat 表中，storyPart1保存在 Cat1 表中，storyPart2保存在Cat2 表中。Cat1 表通过外键cat_id 和 MainCat 表关联， Cat2 表通过 id 列和 MainCat 表关联。对storyPart2列还定义了唯一约束。

## @NamedQuery和 @NamedQueries

**映射查询**

使用注解可以映射EJBQL/HQL 查询，@NamedQuery和 @NamedQueries 是可以使用在类级别或者JPA的XML文件中的注解。

```JAVA
<entity-mappings>
  
 <named-queryname="plane.getAll">
  	<query>select p from Planep</query>
 </named-query>
  
 ...
</entity-mappings>

...

@Entity
@NamedQuery(name="night.moreRecentThan",query="select n from Night n where n.date >= :date")
public class Night {
 ...
}

public class MyDao {
 doStuff() {
   Query q =s.getNamedQuery("night.moreRecentThan");
   q.setDate( "date", aMonthAgo );
   List results = q.list();
   ...
 }
 ...
}

```

可以通过定义QueryHint 数组的hints 属性为查询提供一些hint 信息。下图是一些Hibernate hints:

 

## @SqlResultSetMapping

**映射本地化查询**

通过@SqlResultSetMapping 注解来描述 SQL 的 resultset 结构。如果定义多个结果集映射，则用@SqlResultSetMappings。

```JAVA
@NamedNativeQuery(name="night&area",query="select night.id nid, night.night_duration, "
     + " night.night_date, area.id aid,night.area_id, area.name "
     + "from Night night, Area area wherenight.area_id = area.id", resultSetMapping="joinMapping")
@SqlResultSetMapping(name="joinMapping", entities={
   @EntityResult(entityClass=org.hibernate.test.annotations.query.Night.class,fields = {
   @FieldResult(name="id",column="nid"),
   @FieldResult(name="duration",column="night_duration"),
   @FieldResult(name="date",column="night_date"),
   @FieldResult(name="area",column="area_id"),
   discriminatorColumn="disc"
  }),

@EntityResult(entityClass=org.hibernate.test.annotations.query.Area.class,fields = {
   @FieldResult(name="id",column="aid"),
   @FieldResult(name="name",column="name")
  })
 }
)

```

上面的例子，名为“night&area”的查询和 "joinMapping"结果集映射对应，该映射返回两个实体，分别为 Night 和Area, 其中每个属性都和一个列关联，列名通过查询获取。

```JAVA
@Entity
@SqlResultSetMapping(name="implicit",
  entities=@EntityResult(
      entityClass=org.hibernate.test.annotations.@NamedNativeQuery(
      name="implicitSample",query="select * from SpaceShip", 
      resultSetMapping="implicit")

public class SpaceShip{

 private String name;

 private String model;

 private double speed;

 @Id
 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 @Column(name="model_txt")
 public String getModel() {
  return model;
 }

 public void setModel(String model) {
  this.model = model;
 }

 public double getSpeed() {
  return speed;
 }

 public void setSpeed(double speed) {
  this.speed = speed;
 }
}

```

上例中model1 属性绑定到model_txt 列，如果和相关实体关联设计到组合主键，那么应该使用 **@FieldResult **注解来定义每个外键列。@FieldResult的名字组成：定义这种关系的属性名字 +"." + 主键名或主键列或主键属性。

```JAVA
@Entity
@SqlResultSetMapping(name="compositekey",
 entities=@EntityResult(entityClass=org.hibernate.test.annotations.query.SpaceShip.class,
  fields = {
   @FieldResult(name="name", column ="name"),
   @FieldResult(name="model", column= "model"),
   @FieldResult(name="speed", column= "speed"),
   @FieldResult(name="captain.firstname",column = "firstn"),  @FieldResult(name="captain.lastname", column ="lastn"),
   @FieldResult(name="dimensions.length", column ="length"),
   @FieldResult(name="dimensions.width", column ="width")
  }),

 columns = { @ColumnResult(name ="surface"),
 	@ColumnResult(name ="volume") } )
 	@NamedNativeQuery(name="compositekey",
 query="select name, model, speed, lnameas lastn, fname as firstn, length, width, length * width 			  asresultSetMapping="compositekey")

})

```

如果查询返回的是单个实体，或者打算用系统默认的映射，这种情况下可以不使用 resultSetMapping，而使用resultClass属性，例如：

```JAVA
@NamedNativeQuery(name="implicitSample", query="select * fromSpaceShip",
                   resultClass=SpaceShip.class)
public class SpaceShip{

```

## **Hibernate 独有的注解扩展**

Hibernate 提供了与其自身特性想吻合的注解，org.hibernate.annotations package包含了这些注解。

**实体**

org.hibernate.annotations.Entity 定义了  Hibernate 实体需要的信息。

• **mutable**: whetherthis entity is mutable or not  此实体是否可变

• **dynamicInsert**:allow dynamic SQL for inserts   用动态SQL新增

• **dynamicUpdate**: allow dynamic SQL forupdates   用动态SQL更新

• **selectBeforeUpdate**: Specifies that Hibernate should never perform an SQL UPDATE unless it is certain that an object is actually modified.指明Hibernate从不运行SQL Update，除非能确定对象已经被修改

• **polymorphism**:whether the entity polymorphism is of PolymorphismType.IMPLICIT (default) or PolymorphismType.EXPLICIT 指出实体多态是PolymorphismType.IMPLICIT(默认)还是PolymorphismType.EXPLICIT

• **optimisticLock**:optimistic locking strategy (OptimisticLockType.VERSION, OptimisticLockType.NONE,OptimisticLockType.DIRTY or OptimisticLockType.ALL) 乐观锁策略

### @GenericGenerator 和 @GenericGenerators

**标识符**

@org.hibernate.annotations.GenericGenerator 和 @org.hibernate.annotations.GenericGenerators 允许你定义hibernate特有的标识符。

```java
@Id
@GeneratedValue(generator="system-uuid")
@GenericGenerator(name="system-uuid",strategy = "uuid")
public String getId(){
	@Id
  	@GeneratedValue(generator="hibseq")
	@GenericGenerator(name="hibseq",strategy = "seqhilo",
   		parameters = {
     		@Parameter(name="max_lo", value= "5"),
    	 	@Parameter(name="sequence",value="heybabyhey")
   		}
	)
	public Integer getId(){...}
}
```

新例子

```java
@GenericGenerators(
 	{
      @GenericGenerator(
          name="hibseq",
          strategy = "seqhilo",
          parameters = {
              @Parameter(name="max_lo", value ="5"),
              @Parameter(name="sequence", value="heybabyhey")
   	}
  ),
  @GenericGenerator(...)
 }
)

```

## @NaturalId

自然ID

用 **@NaturalId **注解标识

## @Formula

### **公式**

让数据库而不是JVM进行计算。

```java
@Formula("obj_length* obj_height * obj_width")
public longgetObjectVolume()

```

## @Index

索引

通过在列属性(property)上使用@Index 注解，可以指定特定列的索引，columnNames属性(attribute)将随之被忽略。

```java
@Column(secondaryTable="Cat1")
@Index(name="story1index")
public StringgetStoryPart1() {
  return storyPart1;
}
```

## @DiscriminatorFormula

辨别符

```java
@Entity
@DiscriminatorFormula("casewhen forest_type is null then 0 else forest_type end")
public class Forest {... }
```



在单向关系中没有mappedBy,主控方相当于拥有指向另一方的外键的一方。

1. 一对一和多对一的@JoinColumn注解的都是在“主控方”，都是本表指向**外表的外键名称**。
2. 一对多的@JoinColumn注解在“被控方”，**即一的一方**，指的是**外表的外键的名称**。
3. 多对多中，joinColumns写的都是**本表在中间表的外键名称**， inverseJoinColumns写的是**另一个表在中间表的外键名称**。