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

1. SchedulerFactory : 获取 scheduler 对象的工厂. StdSchedulerFactory 使用最多.

2. Scheduler : 任务调度器

 undefinedTrigger : 触发器 , 定义任务被执行的时间规则(定时规则)

          > ​	**SimpleTrigger** 一般用于实现每隔一定时间执行任务，以及重复多少次，如每 2 小时执行一次，重复执行 5 次。SimpleTrigger 内部实现机制是通过计算间隔时间来计算下次的执行时间，这就导致其不适合调度定时的任务。例如我们想每天的 1：00AM 执行任务，如果使用 SimpleTrigger 的话间隔时间就是一天。注意这里就会有一个问题，即当有 misfired 的任务并且恢复执行时，该执行时间是随机的（取决于何时执行 misfired 的任务，例如某天的 3：00PM）。这会导致之后每天的执行时间都会变成 3：00PM，而不是我们原来期望的 1：00AM。
          >
          > ​	**CronTirgger** 类似于 LINUX 上的任务调度命令 crontab，即利用一个包含 7 个字段的表达式来表示时间调度方式。例如，"0 15 10 * * ? *" 表示每天的 10：15AM 执行任务。对于涉及到星期和月份的调度，CronTirgger 是最适合的，甚至某些情况下是唯一选择。[cron 表达式在线生成](http://www.pdtools.net/tools/becron.jsp)
          >
          > ​	**DateIntervalTrigger** 是 Quartz 1.7 之后的版本加入的，其最适合调度类似每 N（1, 2, 3...）小时，每 N 天，每 N 周等的任务。虽然 SimpleTrigger 也能实现类似的任务，但是 DateIntervalTrigger 不会受到我们上面说到的 misfired 任务的影响。另外，DateIntervalTrigger 也不会受到 DST（Daylight Saving Time， 即中国的夏令时）调整的影响。如果使用 SimpleTrigger，本来设定的调度时间就会由于 DST 的调整而提前或延迟一个小时，而 DateIntervalTrigger 不会受此影响。
          >
          > ​	**NthIncludedDayTrigger** 的用途比较简单明确，即用于每隔一个周期的第几天调度任务，例如，每个月的第 3 天执行指定的任务。
          >
          > ​	 **org.quartz.Calendar** 这个 Calendar 与 Trigger 一起使用，但是它们的作用相反，它是用于排除任务不被执行的情况。例如，按照 Trigger 的规则在 10 月 1 号需要执行任务，但是 Calendar 指定了 10 月 1 号是节日（国庆），所以任务在这一天将不会被执行。通常来说，Calendar 用于排除节假日的任务调度，从而使任务只在工作日执行。

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

​	类似于 LINUX 上的任务调度命令 crontab，即利用一个包含 7 个字段的表达式来表示时间调度方式。例如，"0 15 10 * * ? *" 表示每天的 10：15AM 执行任务。对于涉及到星期和月份的调度，CronTirgger 是最适合的，甚至某些情况下是唯一选择。

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

## 5 数据传递

​	quartz 每次执行 job 对象时,会重新创建 job , 这时如果希望把某些数据传递到下一个 job 对象可以通过 JobMap 对象进行传递 . 步骤:

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

