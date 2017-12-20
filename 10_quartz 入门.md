# 10_quartz 入门

​	参考资料 : [基于 Quartz 开发企业级任务调度应用](https://www.ibm.com/developerworks/cn/opensource/os-cn-quartz/) , [quartz 官网](http://www.quartz-scheduler.org/)

## 1 maven 依赖

```Xml
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.2.1</version>
</dependency>
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz-jobs</artifactId>
    <version>2.2.1</version>
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.24</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
<version>1.2.17</version>
```

## 2 相关概念

### 1 quartz 线程

​	在 Quartz 中，有两类线程，Scheduler 调度线程和任务执行线程，其中任务执行线程通常使用一个线程池维护一组线程.Scheduler 调度线程主要有两个： 

> ​	1 执行常规调度的线程，该线程轮询存储的所有 trigger，如果有需要触发的 trigger，即到达了下一次触发的时间，则从任务执行线程池获取一个空闲线程，执行与该 trigger 关联的任务。
>
> ​	2 Misfire 线程是扫描所有的 trigger，查看是否有 misfired trigger，如果有的话根据 misfire 的策略分别处理

![](https://www.ibm.com/developerworks/cn/opensource/os-cn-quartz/image003.png)

### 2 核心元素

1. SchedulerFactory : 获取 scheduler 对象的工厂. StdSchedulerFactory 使用最多. StdSchdulerFactory的配置例子， 更多配置，参考[Quartz配置指南](http://quartz-scheduler.org/documentation/quartz-2.2.x/configuration/)：

   ```properties
   org.quartz.scheduler.instanceName = DefaultQuartzScheduler
   org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
   org.quartz.threadPool.threadCount = 10 
   org.quartz.threadPool.threadPriority = 5
   org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread = true
   org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
   ```

2. Scheduler : 任务调度器

   > Scheduler就是Quartz的大脑，所有任务都是由它来设施。
   >
   > Schduelr包含一个两个重要组件: JobStore和ThreadPool。
   >
   > JobStore是会来存储运行时信息的，包括Trigger,Schduler,JobDetail，业务锁等。它有多种实现RAMJob(内存实现)，JobStoreTX(JDBC，事务由Quartz管理），JobStoreCMT(JDBC，使用容器事务)，ClusteredJobStore(集群实现)、TerracottaJobStore([什么是Terractta](http://yale.iteye.com/blog/1541612))。
   >
   > ThreadPool就是线程池，Quartz有自己的线程池实现。所有任务的都会由线程池执行。

3. Trigger : 触发器 , 定义任务被执行的时间规则(定时规则)

   > **SimpleTrigger** 一般用于实现每隔一定时间执行任务，以及重复多少次，如每 2 小时执行一次，重复执行 5 次。SimpleTrigger 内部实现机制是通过计算间隔时间来计算下次的执行时间，这就导致其不适合调度定时的任务。例如我们想每天的 1：00AM 执行任务，如果使用 SimpleTrigger 的话间隔时间就是一天。注意这里就会有一个问题，即当有 misfired 的任务并且恢复执行时，该执行时间是随机的（取决于何时执行 misfired 的任务，例如某天的 3：00PM）。这会导致之后每天的执行时间都会变成 3：00PM，而不是我们原来期望的 1：00AM。
   >
   >  **CronTirgger** 类似于 LINUX 上的任务调度命令 crontab，即利用一个包含 7 个字段的表达式来表示时间调度方式。例如，"0 15 10 * * ? *" 表示每天的 10：15AM 执行任务。对于涉及到星期和月份的调度，CronTirgger 是最适合的，甚至某些情况下是唯一选择。[cron 表达式在线生成](http://www.pdtools.net/tools/becron.jsp)
   >
   > **DateIntervalTrigger** 是 Quartz 1.7 之后的版本加入的，其最适合调度类似每 N（1, 2, 3...）小时，每 N 天，每 N 周等的任务。虽然 SimpleTrigger 也能实现类似的任务，但是 DateIntervalTrigger 不会受到我们上面说到的 misfired 任务的影响。另外，DateIntervalTrigger 也不会受到 DST（Daylight Saving Time， 即中国的夏令时）调整的影响。如果使用 SimpleTrigger，本来设定的调度时间就会由于 DST 的调整而提前或延迟一个小时，而 DateIntervalTrigger 不会受此影响。
   >
   > **NthIncludedDayTrigger** 的用途比较简单明确，即用于每隔一个周期的第几天调度任务，例如，每个月的第 3 天执行指定的任务。
   >
   > **org.quartz.Calendar** 这个 Calendar 与 Trigger 一起使用，但是它们的作用相反，它是用于排除任务不被执行的情况。例如，按照 Trigger 的规则在 10 月 1 号需要执行任务，但是 Calendar 指定了 10 月 1 号是节日（国庆），所以任务在这一天将不会被执行。通常来说，Calendar 用于排除节假日的任务调度，从而使任务只在工作日执行。

4. Job : 任务接口 , 定义具体要做的事情

### 3 数据存储

​	Quartz 中的 trigger 和 job 需要存储下来才能被使用。Quartz 中有两种存储方式：RAMJobStore, JobStoreSupport，其中 RAMJobStore 是将 trigger 和 job 存储在内存中，而 JobStoreSupport 是基于 jdbc 将 trigger 和 job 存储到数据库中。RAMJobStore 的存取速度非常快，但是由于其在系统被停止后所有的数据都会丢失，所以在通常应用中，都是使用 JobStoreSupport。

​	在 Quartz 中，JobStoreSupport 使用一个驱动代理来操作 trigger 和 job 的数据存储：StdJDBCDelegate。StdJDBCDelegate 实现了大部分基于标准 JDBC 的功能接口，但是对于各种数据库来说，需要根据其具体实现的特点做某些特殊处理，因此各种数据库需要扩展 StdJDBCDelegate 以实现这些特殊处理。Quartz 已经自带了一些数据库的扩展实现，可以直接使用

## 3 Hello World

1. 实现 Job 接口,定义定时任务具体要做的事情

```Java
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Hello world...");
    }
}
```

2. 测试

```Java
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

public class SimpleExample {

    public static void main(String[] args) throws Exception {

        SimpleExample example = new SimpleExample();
        example.run();

    }

    public void run() throws SchedulerException {
        /**
         * 1 创建 StdSchedulerFactory
         * 2 JobBuilder 创建任务,即 JobDetail 示例
         * 3 TriggerBuilder创建触发器 trigger
         * 4 关联 jobDetail 和 trigger
         * 5 执行定时任务
         * 6 关闭定时任务
         */
        StdSchedulerFactory sf        = new StdSchedulerFactory();
        Scheduler           scheduler = sf.getScheduler();
        // 延时10秒
        Date runtime = DateBuilder.nextGivenSecondDate(null, 10);
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                                  .withIdentity("hellojob", "group1")
                                  .build();

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                                        .withIdentity("trigger1", "group1")
                                        .startAt(runtime)
                                         // 重复执行2次,间隔3秒(总共运行3次!!!)
                                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                                                   .withIntervalInSeconds(3)
                                                                   .withRepeatCount(2))
                                        .build();
        // 作业任务和触发器关联
        Date ft = scheduler.scheduleJob(job, trigger); 
        // 只有调用了 start() 方法,定时任务才启动,类似线程的 Thread.start()
        scheduler.start();

        System.out.println(job.getKey() +" 将在 "+ft +" 运行,重复执行 "+ trigger.getRepeatCount()+" 次,间隔 : "+trigger.getRepeatInterval()/1000);
      
      Thread.sleep(15*1000);// 睡眠15秒,让job 被调用多次
      scheduler.shutdown(true); // 等待当前任务执行完成后关闭
      SchedulerMetaData metaData = scheduler.getMetaData();
      System.out.println("hellojob定时任务的被执行的次数 : "+metaData.getNumberOfJobsExecuted());
    }
}
```

> 注意 : 只有调用了 scheduler.start() ,定时任务才会启动

## 4 cronTrigger

​	使用频率最高的触发器。对于涉及到星期和月份的调度，CronTirgger 是最适合的，甚至某些情况下是唯一选择。

​	[cron 表达式在线生成](http://www.pdtools.net/tools/becron.jsp)

​	**Cron表达式的格式：`秒 分 时 日 月 周 年(可选)`**

| 字段名   | 允许的值              | 允许的特殊字符         |
| ----- | ----------------- | --------------- |
| 秒     | 0-59              | , - * /         |
| 分     | 0-59              | , - * /         |
| 小时    | 0-23              | , - * /         |
| 日     | 1-31              | , - * ? / L W C |
| 月     | 1-12 或 JAN - DEC  | , - * /         |
| 周几    | 1-7 或 SUN - SAT   | , - * ? / L C # |
| 年(可选) | Empty , 1970-2099 | , - * /         |

> `*` 字符: 		表示匹配该域的任意值，假如在Minutes域使用*, 即表示每分钟都会触发事件
>
> `?`  字符：	只能用在`Day of Month`和`Day of Week`两个域。它也匹配域的任意值，但实际不会。因为`DayofMonth`和`DayofWeek`会相互影响。例如想在每月的20日触发调度，不管20日到底是星期几，则只能使用如下写法： `13 13 15 20 * ?`, 其中最后一位只能用 `？`，而不能使用`*`，如果使用 `*` 表示不管星期几都会触发，实际上并不是这样 (`可认为是占位符?? 当 DayofMonth 和 DayofWeek 同时出现 * 时,要让一个字段生效,则另一个字段用 ? 替换掉 * ,表示这个字段的值为任意(失效)`)
>
> `,`  字符：	表示列出枚举值值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次
>
> `-`  字符：	表示范围，例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次
>
> `/`  字符：	指定一个值的增加幅度。n/m表示从n开始，每次增加m
>
> `L`  字符：	表示最后，只能出现在DayofWeek和DayofMonth域，如果在DayofWeek域使用5L,意味着在最后的一个星期四触发`(西方一个星期的最后一天是 星期6)`
>
> `W`  字符：	表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份
>
> `LW` 字符 :	这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。
>
> `#`  字符：	表示该月第几个周X。6#3表示该月第3个周五 

```Java
// 每20秒执行依次
CronTrigger trigger = TriggerBuilder.newTrigger()
                                    .withIdentity("trigger1", "group1")
                                    .withSchedule(cronSchedule("0/20 * * * * ?"))
                                    .build();
String cronExpression = trigger.getCronExpression(); // 获取触发器的 cron 表达式
```

## 5 JobDataMap 数据传递

​	`@PersistJobDataAfterExecution ` : 这个代表该任务可以支持在任务间使用JobDataMap传递信息，在任务结束时保存信息，不必设置为串行。但如果并行任务使用该注解可能会让 JobDataMap 中的内容产生不可预知的结果，所以还是强烈建议使用该注解的同时使用 `@DisallowConcurrentExecution` 注解。如果 Job 上不使用 `@PersistJobDataAfterExecution` 将导致数据不能共享!!!

​	quartz 每次执行 job 对象时,会重新创建 job , 这时如果希望把某些数据传递到下一个 job 对象可以通过 JobDataMap 对象进行传递 . 步骤:

1. 初始化,在构建 jobdetail 对象时存初始值

```Java
		// job1 运行5次,间隔4秒
        JobDetail job1 = newJob(ColorJob.class).withIdentity("job1", "group1")
                                               .build();
        // 通过 JobDataMap 传递k-v数据
        job1.getJobDataMap()
            .put("color", "Green");
        job1.getJobDataMap()
            .put("count", 1);

        // schedule the job to run
        Date scheduleTime1 = sched.scheduleJob(job1, trigger1);

        // job2 运行5次,间隔3秒
        JobDetail job2 = newJob(ColorJob.class).withIdentity("job2", "group1")
                                               .build();

        // 通过 JobDataMap 传递k-v数据
        job2.getJobDataMap()
            .put("color", "Red");
        job2.getJobDataMap()
            .put("count", 1);
```

> job 对象通过withIdentity指定了唯一的 Identity , 具有相同的 Identity 的 job 访问的是同一 JobDataMap!!!

2. 在 job 中通过 context 对象访问可以获取到 jobDataMap

```Java
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ColorJob implements Job {

    /**
     * quartz 每次调用 jobdetail 对象时,都会重新实例化,所以非静态
     * 变量每次都会重新初始化
     */
    private int _counter = 1;

    public ColorJob() {
    }

    public void execute(JobExecutionContext context)
        throws JobExecutionException {
      
        // 获取 jobDataMap 对象
        JobDataMap data = context.getJobDetail().getJobDataMap();
      
       // 通过 key 获取数据
        String favoriteColor = data.getString("color");
        int count = data.getInt("count");
      
        data.put("count", count++); // 修改数据并重新放回 map
    }

}
```

## 6 misfire 处理

​	misfire 是指job指定时刻应该被执行,但没有被执行,并在超过系统设置的misfireThreshold 时间也没有被执行时,被quartz 认定为 misfire . quartz中默认的misfireThreshold是60000，也就是60秒

​	 job 的触发时刻在 quartz 第一次调用job对象时就计算好了,比如 job1从 13:10:00 开始执行,间隔3秒,重复2次 , 则 job1 被触发的时刻分别为 : 

> 13:10:00   13:10:03  13:10:06 

所有MisFire的策略实际上都是解答两个问题：

1. 已经MisFire的任务还要重新触发吗？
2. 如果发生MisFire，要调整现有的调度时间吗？

### 1 misfire 判定

​	如果某触发器设置为，10:15首次激活，然后每隔3秒激活一次，无限次重复。然而该任务每次运行需要10秒钟的时间。可见，每次任务的执行都会超时，那么究竟是否会引起misfire，就取决于misfireThreshold的值了。以第二次任务来说，它的运行时间已经比预定晚了7秒，那么如果misfireThreshold>7000，说明该偏差可容忍，则不算misfire，该任务立刻执行；如果misfireThreshold<=7000，则判定为misfire，根据相关配置策略进行处理。**注意，任务的延迟是有累计的。在前面的例子中，假设misfireThreshold设置为20000，即20秒。那么每次任务的延迟量即是否misfire计算如下：**

| 任务编号 | 预定运行时刻 | 实际运行时刻 | 延迟量（秒） | 备注      |
| ---- | ------ | ------ | ------ | ------- |
| 1    | 10:15  | 10:15  | 0      |         |
| 2    | 10:18  | 10:25  | 7      |         |
| 3    | 10:21  | 10:35  | 14     |         |
| 4    | 10:24  | 10:45  | 21     | misfire |

从表中可以看到，每一次任务执行都会带来7秒的延迟量，该延迟量不断被与misfireThreshold比较，直到达到该值后，在10:24分发生misfire。那么在10:24第10次任务会不会在10:45准时执行呢？答案是不一定，取决于 misfireInstruction 配置。这里说明一下,当对 misfire 做出处理后 , 延迟时间即被清0,重新计算

### 2 misfire 处理策略

​	激活失败指令（Misfire Instructions）是触发器的一个重要属性，它指定了misfire发生时调度器应当如何处理。所有类型的触发器都有一个默认的指令，叫做Trigger.MISFIRE_INSTRUCTION_SMART_POLICY，但是这个这个“聪明策略”对于不同类型的触发器其具体行为是不同的。

​	对于`SimpleTrigger`，这个“聪明策略”将根据触发器实例的状态和配置来决定其行为:

> 如果RepeatCount=0：
>
> 	misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW;
>
> 如果RepeatCount=REPEAT_INDEFINITELY：
> 	misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT;
>
> 如果Repeat Count>0：
> 	misfireInstruction = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;

​	对于`CronTrigger`，该“聪明策略”默认选择 `MISFIRE_INSTRUCTION_FIRE_ONCE_NOW` 以指导其行为

常见策略说明 :

**SimpleTrigger**

> - `MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY` : 这个不是忽略已经错失的触发的意思，而是说忽略MisFire策略。它会在资源合适的时候，重新触发所有的MisFire任务，并且不会影响现有的调度时间。
>
>   比如，SimpleTrigger每15秒执行一次，而中间有5分钟时间它都MisFire了，一共错失了20个，5分钟后，假设资源充足了，并且任务允许并发，它会被一次性触发。
>
>   这个属性是所有Trigger都适用。
>
> - `MISFIRE_INSTRUCTION_FIRE_NOW` : 忽略已经MisFire的任务，并且立即执行调度。这通常只适用于只执行一次的任务
>
> - `MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT` : 将startTime设置当前时间，立即重新调度任务，包括的MisFire的
>
> - `MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT` : 忽略已经MisFire的任务, 将startTime设置当前时间，立即重新调度任务
>
> - `MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT` :  在下一次调度时间点，重新开始调度任务，包括的MisFire的
>
> - `MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT` : 忽略已经MisFire的任务,下一次调度时间点，重新开始调度任务
>
> - `MISFIRE_INSTRUCTION_SMART_POLICY` : 

**CronTrigger:**

| 值                                 | 说明                                       |
| --------------------------------- | ---------------------------------------- |
| MISFIRE_INSTRUCTION_FIRE_ONCE_NOW | 立刻执行一次，然后就按照正常的计划执行                      |
| MISFIRE_INSTRUCTION_DO_NOTHING    | 目前不执行，然后就按照正常的计划执行(执行下一次)。这意味着如果下次执行时间超过了end time，实际上就没有执行机会了 |

[参考资料](http://www.cnblogs.com/pzy4447/p/5201674.html)

```java
// 构建 Simple 触发器并指定misfire策略
TriggerBuilder.newTrigger()
              .withIdentity("simpe", "group1")
              .usingJobData("key", "data")
              .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                                 .repeatForever()                                              .withMisfireHandlingInstructionIgnoreMisfires())
              .build();

// 构建 Cron 触发器并指定misfire策略
TriggerBuilder.newTrigger()
              .withIdentity("key2", "group2")
              .withSchedule(CronScheduleBuilder.cronSchedule("0/20 * * * * ?")
                                .withMisfireHandlingInstructionDoNothing())
              .build();
```

## 7 异常处理

​	Job.execute()方法是不允许抛出除JobExecutionException之外的所有异常的（包括RuntimeException)，所以编码的时候，最好是try-catch住所有的Throwable，小心处理.

​	当 job 内部执行出现异常时,可以通过 JobExecutionException 来指定是否要重新执行job任务,如下:

```java
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class BadJob1 implements Job {

  // Logging
  private static Logger _log = LoggerFactory.getLogger(BadJob1.class);
  private int calculation;

  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobKey jobKey = context.getJobDetail().getKey();
    JobDataMap dataMap = context.getJobDetail().getJobDataMap();

    int denominator = dataMap.getInt("denominator");
    
    // 只有第一次被调用时会抛异常
    try {
      calculation = 4815 / denominator;
    } catch (Exception e) {
     
      JobExecutionException e2 = new JobExecutionException(e);

      // 除数变成1 , 下次运行不会出现再异常
      dataMap.put("denominator", "1");

      // 立刻重新触发一次job
      e2.setRefireImmediately(true);
      
      /**
      * Quartz will automatically unschedule
      * all triggers associated with this job
      * so that it does not run again
      */
      // e2.setUnscheduleAllTriggers(true);
      
      throw e2;
    }
  }

}
```

​	在 execute 方法内部捕获异常后, 新建一个 JobExecutionException 对象 , 并指定重试策略:

- setRefireImmediately(true) : 立刻重新执行job , 抛出异常的任务不算在任务总数里.比如 job 应该被执行三次 , 第一次调用时出异常 , 设置了 立即触发 , 则job还会被调用三次.

- setUnscheduleAllTriggers(true) : 移除所有跟当前job相关的触发器,即该job任务不会再被执行!

  ​

  `@DisallowConcurrentExecution` : 这个代表设置该任务为串行执行。

  `@PersistJobDataAfterExecution `: 这个代表该任务可以支持在任务间使用JobDataMap传递信息，在任务结束时保存信息，不必设置为串行。但如果并行任务使用该注解可能会让JobDataMap中的内容产生不可预知的结果，所以还是强烈建议使用该注解的同时使用@DisallowConcurrentExecution注解。

## 8 JobListener 任务监听器

​	[jobListener详解](https://yq.aliyun.com/articles/29120)

​	jobListener实现类必须实现其以下方法：

| 方法                   | 说明                                       |
| -------------------- | ---------------------------------------- |
| getName()            | getName() 方法返回一个字符串用以说明 JobListener 的名称。对于注册为全局的监听器，getName() 主要用于记录日志，对于由特定 Job 引用的 JobListener，注册在 JobDetail 上的监听器名称必须匹配从监听器上 getName() 方法的返回值。 |
| jobToBeExecuted()    | Scheduler 在 JobDetail 将要被执行时调用这个方法。      |
| jobExecutionVetoed() | Scheduler 在 JobDetail 即将被执行，但又被 TriggerListener 否决了时调用这个方法。 |
| jobWasExecuted()     | Scheduler 在 JobDetail 被执行之后调用这个方法。       |

​	对 2.x 版本时通过 `org.quartz.ListenerManager` 和 `org.quartz.Matcher` 
来对我们的监听器进行管理配置:

1. ListenerManager

> `public void addJobListener(JobListener jobListener)` 
> ​		添加全局监听器，即所有JobDetail都会被此监听器监听 
>
> `public void addJobListener(JobListener jobListener, Matcher matcher)` 
> ​		添加带条件匹配的监听器，在matcher中声明我们的匹配条件 
>
> `public void addJobListener(JobListener jobListener, Matcher … matchers)` 
> ​		添加附带不定参条件陪陪的监听器 
>
> `public boolean removeJobListener(String name)` 
> ​		根据名字移除JobListener 
>
> `public List getJobListeners()` 
> ​		获取所有的监听器 
>
> `public JobListener getJobListener(String name)` 
> ​		根据名字获取监听器

2. Matcher

> `KeyMatcher<JobKey>` 
>
> ​	根据JobKey进行匹配，每个JobDetail都有一个对应的JobKey,里面存储了JobName和JobGroup来定位唯一的JobDetail , 常用的方法有 
>
> `GroupMatcher`
>
> ​	根据组名进行匹配 .常用方法有 : `groupEndsWith()`   `groupEquals()`  `groupStartsWith`
>
> and so on....

​	JobListener 示例:

```java
public class MyJobListener implements JobListener {

    @Override//相当于为我们的监听器命名
    public String getName() {
        return "myJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println(getName() + "触发对"+context.getJobDetail().getJobClass()+"的开始执行的监听工作，这里可以完成任务前的一些资源准备工作或日志记录");
    }

    @Override//“否决JobDetail”是在Triiger被其相应的监听器监听时才具备的能力
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println("被否决执行了，可以做些日志记录。");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context,
            JobExecutionException jobException) {
        System.out.println(getName() + "触发对"+context.getJobDetail().getJobClass()+"结束执行的监听工作，这里可以进行资源销毁工作或做一些新闻扒取结果的统计工作");

    }

}
```

​	添加监听器:

```java
// Set up the listener
JobListener listener = new MyJobListener();
Matcher<JobKey> matcher = KeyMatcher.keyEquals(job.getKey());
// 添加监听器 , 监听器匹配规则由 matcher 确定
sched.getListenerManager().addJobListener(listener, matcher);
```

## 9 Job 并发

​	job是有可能并发执行的，比如一个任务要执行10秒中，而调度算法是每秒中触发1次，那么就有可能多个任务被并发执行。

有时候我们并不想任务并发执行，比如这个任务要去”获得数据库中所有未发送邮件的名单“，如果是并发执行，就需要一个数据库锁去避免一个数据被多次处理。这个时候可使用 `@DisallowConcurrentExecution` 解决这个问题。