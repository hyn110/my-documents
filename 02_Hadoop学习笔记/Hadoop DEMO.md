# Hadoop DEMO

## 1 mapreduce 入门

​	 [案例源代码地址](https://github.com/hyn110/hadoop-learn/tree/master/01_hdfs_io)

​	[windows环境运行map-reduce出现的问题](https://blog.csdn.net/darkdragonking/article/details/72636917)

**windows 环境下运行程序需要做的准备:**

1. 解压一个hadoop的二进制文件到本地磁盘,这里我是解压  `hadoop-2.7.2.tar.gz` 到 `C:\00_my_program\hadoop-2.7.2` 目录


2. 配置环境变量 `HADOOP_HOME` , 值为解压的路径 `C:\00_my_program\hadoop-2.7.2` , 并在 `PATH` 中追加 `%HADOOP_HOME%\bin`

3. 下载 https://download.csdn.net/download/fmi110/10329691 文件, 并将解压得到的文件复制到 `%HADOOP_HOME%\bin` 目录下 

   > 否则报错
   >
   >  `java.io.IOException: (null) entry in command string: null chmod 0700 C:\tmp\hadoop-huangyunning\mapred\staging\huangyunning1997364178\.staging`
   >
   > 或
   >
   > ` Exception in thread "main" java.lang.UnsatisfiedLinkError: org.apache.hadoop.io.nativeio.NativeIO$Windows.createDirectoryWithMode0(Ljava/lang/String;I)V`

4. 修改 hdfs 文件系统权限 ,允许任一用户进行读写文件(这里只是为了保证windows下能够运行hadoop程序,并未考虑生产环境下的安全问题!!!)  `hadoop fs -chmod 777 /`    **(如果不是远程访问Linux 的hadoop 这步可以不用)**

   >  否则报错`org.apache.hadoop.ipc.RemoteException(org.apache.hadoop.security.AccessControlException): Permission denied: user=huangyunning, access=WRITE, inode="/output":fmi110:supergroup:drwxr-xr-x`

   依赖:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.fmi110</groupId>
    <artifactId>hadoop</artifactId>
    <version>1.0-SNAPSHOT</version>
    <modules>
        <module>../01_hdfs_mr</module>
        <module>../02_hdfs_filesystem</module>
    </modules>
    <packaging>pom</packaging>

<dependencyManagement>
    <dependencies>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.24</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
    </dependencies>
</dependencyManagement>

    <dependencies>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>2.7.3</version>
            <!--<exclusions>-->
                <!--<exclusion>-->
                    <!--<artifactId>netty</artifactId>-->
                    <!--<groupId>io.netty</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>jsr305</artifactId>-->
                    <!--<groupId>com.google.code.findbugs</groupId>-->
                <!--</exclusion>-->
            <!--</exclusions>-->
        </dependency>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>2.7.3</version>
            <!--<exclusions>-->
                <!--<exclusion>-->
                    <!--<artifactId>commons-logging</artifactId>-->
                    <!--<groupId>commons-logging</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>guava</artifactId>-->
                    <!--<groupId>com.google.guava</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>slf4j-api</artifactId>-->
                    <!--<groupId>org.slf4j</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>slf4j-log4j12</artifactId>-->
                    <!--<groupId>org.slf4j</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>jackson-core-asl</artifactId>-->
                    <!--<groupId>org.codehaus.jackson</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>log4j</artifactId>-->
                    <!--<groupId>log4j</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>jackson-mapper-asl</artifactId>-->
                    <!--<groupId>org.codehaus.jackson</groupId>-->
                <!--</exclusion>-->

                <!--<exclusion>-->
                    <!--<artifactId>commons-codec</artifactId>-->
                    <!--<groupId>commons-codec</groupId>-->
                <!--</exclusion>-->
                <!--<exclusion>-->
                    <!--<artifactId>commons-lang</artifactId>-->
                    <!--<groupId>commons-lang</groupId>-->
                <!--</exclusion>-->
            <!--</exclusions>-->
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>


        <!-- ZK -->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.4.9</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.101tec</groupId>
            <artifactId>zkclient</artifactId>
            <version>0.2</version>
            <exclusions>
                <exclusion>
                    <artifactId>slf4j-api</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>log4j</artifactId>
                    <groupId>log4j</groupId>
                </exclusion>
                <exclusion>
                    <artifactId>slf4j-log4j12</artifactId>
                    <groupId>org.slf4j</groupId>
                </exclusion>
            </exclusions>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.16.18</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <!--<version>1.7.24</version>-->
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <!--<version>1.2.17</version>-->
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.40</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.2</version>
                <configuration>
                    <!-- 配置使用的 jdk 版本 -->
                    <target>1.8</target>
                    <source>1.8</source>
                    <encoding>UTF-8</encoding>
                    <!--项目中在 lib 目录下放置 jar 时需声明,否则打包时jar丢失-->
                    <compilerArguments>
                        <extdirs>${project.basedir}/src/main/webapp/WEB-INF/lib</extdirs>
                    </compilerArguments>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.tomcat.maven</groupId>
                <artifactId>tomcat7-maven-plugin</artifactId>
                <version>2.2</version>
                <configuration>
                    <!-- 配置上下文路径和端口号 -->
                    <path>/</path>
                    <port>8083</port>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>2.6</version>
                <configuration>
                    <!-- 配置后项目中没有web.xml文件时,项目不提示错误 -->
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>


        </plugins>
    </build>
</project>
```

​	天气数据如下

```js
0067011990999991950051507004+68750+023550FM-12+038299999V0203301N00671220001CN9999999N9+00001+99999999999
0043011990999991950051512004+68750+023550FM-12+038299999V0203201N00671220001CN9999999N9+00221+99999999999
0043011990999991950051518004+68750+023550FM-12+038299999V0203201N00261220001CN9999999N9-00111+99999999999
0043012650999991949032412004+62300+010750FM-12+048599999V0202701N00461220001CN0500001N9+01111+99999999999
0043012650999991949032418004+62300+010750FM-12+048599999V0202701N00461220001CN0500001N9+00781+99999999999
```

​	数据流 :

![](img/2-1.png)



```java
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * @author fmi110
 * @Description:
 * @Date 2018/4/6 11:08
 */
@Slf4j
public class MaxTemperature {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        if (args.length < 2) {
            log.error("必须指定输入文件和输出路径...");
            System.exit(-1);
        }
        //1 创建配置文件 conf
        //2 创建 job , 指定输入文件类型
        //3 设置 mapper , 并设置k-v类型
        //4 设置 reducer , 并设置k-v类型
        //5 设置应用的输入
        //6 设置应用的输出
        //7 运行

        Configuration conf = new Configuration();
        // conf.set("fs.defaultFS", "file:///");

        // 删除输出目录,这里只是为了方便程序运行,生产中不能这么干
        FileSystem.get(conf)
                  .delete(new Path(args[1]), true);

        Job job = Job.getInstance(conf);
        job.setInputFormatClass(TextInputFormat.class);
        job.setJobName("maxTemperature Job");
        job.setJarByClass(MaxTemperature.class);

        job.setMapperClass(MaxTemperatureMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(MaxTemperatureReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : -1);
    }
}

```
> 由于泛型擦除的原因,所以job需要显示的设置类型  `job.setXXXclass(...)`

```java
package com.fmi110.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author fmi110
 * @Description: 输入的行数据包含了年份和天气信息, 通过map提取出来
 * @Date 2018/4/6 11:13
 */
@Slf4j
public class MaxTemperatureMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        log.info("----------------- map ---------------");

        long   keyL = key.get();
        String line = value.toString();
        String year = line.substring(15, 19);
        log.info("输入 key = {},year = {}", keyL, year);

        int airTemperature;
        if (line.charAt(87) == '+') { // parseInt doesn't like leading plus signs
            airTemperature = Integer.parseInt(line.substring(88, 92));
        } else {
            airTemperature = Integer.parseInt(line.substring(87, 92));
        }
        String quality = line.substring(92, 93);
        if (quality.matches("[01459]")) {
            context.write(new Text(year), new IntWritable(airTemperature));
        }
    }
}
```

```java
package com.fmi110.reducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author fmi110
 * @Description: 获取最大的天气值
 * @Date 2018/4/6 11:39
 */
@Slf4j
public class MaxTemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        log.info("-------------- reduce ------------");
        log.info("key = {}",key.toString());

        int maxValue = Integer.MIN_VALUE;

        for (IntWritable v : values) {
            log.info("max = {},v = {}",maxValue,v.get());
            maxValue = Math.max(maxValue, v.get());
        }
        // 输出最后的结果
        context.write(key,new IntWritable(maxValue));
    }
}
```

![](img/2-2.png)

​	输出文件结果如下:

```
1949	111
1950	22
```

## 2 文件系统的基础操作

### 1 shell命令

1. 本地文件复制到hdfs

```sh
hadoop fs -copyFromLocal input/test.txt hdfs://localhost:8020/user/fmi110/test.txt
或
hadoop fs -copyFromLocal input/test.txt /user/fmi110/test.txt
```

2. 从hdfs复制文件到本地

```sh
hadoop fs -copyToLocal /user/fmi110/test.txt test.copy.txt
md5sum /user/fmi110/test.txt test.copy.txt         # 查看两个文件的md5值,确认文件内容一致
```

3. 在hdfs新建目录并查看当前目录内容

```sh
hadoop fs -mkdir -p /user/fmi110
hadoop fs -ls .
```

4. 列出本地文件系统下的文件

```sh
hadoop fs -ls file:///
```

> 相当于是查看linux文件系统

### 2 api

#### 1 通过URLStreamHandler实例以标准输出方式显示hdfs上的文件

```java
import org.apache.hadoop.fs.FsUrlStreamHandlerFactory;
import org.apache.hadoop.io.IOUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * @author fmi110
 * @Date 2018/4/6 20:02
 */
public class URLCat {
    static {
        // 必须设置,否则URL无法识别 hdfs 协议!!!每个jvm只能调用一次该方法,
        // 所以这个方式读取文件有局限!!!
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
    }
    public static void main(String[] args) throws Exception {
        InputStream in = null;
      try{
          // 连接linux服务器时
          // in = new URL("hdfs://m01:8020/user/fmi110/1.txt").openStream();
          
          // 读取windows本地文件时  args[0] = "file://C:/input/sample.txt"
          in = new URL(args[0]).openStream();
          IOUtils.copyBytes(in,System.out,1024*4,false);
      }finally {
        IOUtils.closeStream(in);
      }
    }
}

```

![](img/2-3.png)

​	linux下运行打包出来的 jar 文件指令:

```sh
hadoop jar jar文件路径  类名  main方法参数...
```

#### 2 通过 FileSystem 对象读取 hdfs 文件

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author fmi110
 * @Description:
 * @Date 2018/4/6 21:04
 */
public class FileSystemCat {
    public static void main(String[] args) throws IOException {
        String uri = args[0]; // hdfs://m01:8020/user/fmi110/1.txt
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(uri), conf);
        InputStream in = null;
        try {
            in = fs.open(new Path(uri));
            IOUtils.copyBytes(in,System.out,1024*4,false);
        }finally {
            IOUtils.closeStream(in);
        }
    }
}
```

![](img/3-4.png)

#### 3 通过 FileDataOutputStream 写入文件到 hdfs

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;

/**
 * 写入文件,并带进度提示
 * @author fmi110
 * @Date 2018/4/6 21:23
 */
public class FileCopyWithProgress {
    public static void main(String[] args) throws IOException, InterruptedException {
        String localFile = args[0]; // C:/a.md 
        String dst = args[1]; // hdfs://m01:8020/user/fmi110/a.md

        InputStream in = new BufferedInputStream(new FileInputStream(localFile));
        FileSystem  fs = FileSystem.get(URI.create(dst), new Configuration(),"fmi110");
        // fs.create() 会自动创建父级目录!!!
        FSDataOutputStream out = fs.create(new Path(dst), new Progressable() {
            @Override
            public void progress() {
                System.out.print(".");
            }
        });
        IOUtils.copyBytes(in,out,4096,true);
    }
}
```

![](img/3-5.png)

#### 4 FileStatus 获取文件元信息和文件路径信息

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * 获取文件元信息和路径信息
 * @author fmi110
 * @Date 2018/4/6 21:43
 */
public class FileStatusDemo {
    public static void main(String[] args) throws IOException {
        // args = {"hdfs://m01:8020/user", "hdfs://m01:8020/user/fmi110"}
        String uri = args[0];
        FileSystem fs = FileSystem.get(URI.create(uri),new Configuration());

        Path[] paths = new Path[args.length];
        for(int i=0;i<paths.length;i++) {
            paths[i] = new Path(args[i]);
        }
        // 获取一组路径的 fileStauts
        FileStatus[] status = fs.listStatus(paths);
        // FileUtil 将元信息转换为路径信息
        Path[] filePaths = FileUtil.stat2Paths(status);
        Arrays.stream(filePaths).forEach(System.out::println);
    }
}
```

> 获取文件路径信息的关键是 FileUtil

![](img/3-1.png)

#### 5 PathFilter 文件模式(多文件匹配)

​	FileSystem 提供了通配 (globbing) 方式 , 通过一个表达式来匹配多个文件 . hadoop 为执行通配提供了两个 FileSystem 方法 :

```java
public FileStatus[] globStatus(Path pathPattern);
public FileStatus[] globStatus(Path pathPattern,PathFilter filter);

fs.globStatus(new Path(BASE_PATH + pattern), pathFilter)
```

![](img/3-2.png)

![](img/3-3.png)

​	根据文件名,截取日期,筛选指定日期范围的文件. 通常在处理日志文件时会用到

```java
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class DateRangePathFilter implements PathFilter {
  
  private final Pattern PATTERN = Pattern.compile("^.*/(\\d\\d\\d\\d/\\d\\d/\\d\\d).*$");
  
  private final Date start, end;

  public DateRangePathFilter(Date start, Date end) {
    this.start = new Date(start.getTime());
    this.end = new Date(end.getTime());
  }
  
  public boolean accept(Path path) {
    Matcher matcher = PATTERN.matcher(path.toString());
    if (matcher.matches()) {
      DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
      try {
        return inInterval(format.parse(matcher.group(1)));
      } catch (ParseException e) {
        return false;
      }
    }
    return false;
  }

  private boolean inInterval(Date date) {
    return !date.before(start) && !date.after(end);
  }
}
```

​	正则表达式 , 包含文件

```java
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class RegexPathFilter implements PathFilter {
  
  private final String regex;
  private final boolean include;

  public RegexPathFilter(String regex) {
    this(regex, true);
  }
  
  public RegexPathFilter(String regex, boolean include) {
    this.regex = regex;
    this.include = include;
  }

  public boolean accept(Path path) {
    return (path.toString().matches(regex)) ? include : !include;
  }
}
```

​	正则表达式排除文件

```java
// cc RegexExcludePathFilter A PathFilter for excluding paths that match a regular expression
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class RegexExcludePathFilter implements PathFilter {
  
  private final String regex;

  public RegexExcludePathFilter(String regex) {
    this.regex = regex;
  }

  public boolean accept(Path path) {
    return !path.toString().matches(regex);
  }
}
```



```java
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.junit.*;

public class FileSystemGlobTest {
  
  private static final String BASE_PATH = "/tmp/" +
    FileSystemGlobTest.class.getSimpleName();
  
  private FileSystem fs;
  
  @Before
  public void setUp() throws Exception {
    fs = FileSystem.get(new Configuration());
    fs.mkdirs(new Path(BASE_PATH, "2007/12/30"));
    fs.mkdirs(new Path(BASE_PATH, "2007/12/31"));
    fs.mkdirs(new Path(BASE_PATH, "2008/01/01"));
    fs.mkdirs(new Path(BASE_PATH, "2008/01/02"));
  }
  
  @After
  public void tearDown() throws Exception {
    fs.delete(new Path(BASE_PATH), true);
  }
  
  @Test
  public void glob() throws Exception {
    assertThat(glob("/*"), is(paths("/2007", "/2008")));
    assertThat(glob("/*/*"), is(paths("/2007/12", "/2008/01")));
    assertThat(glob("/*/12/*"), is(paths("/2007/12/30", "/2007/12/31")));
    assertThat(glob("/200?"), is(paths("/2007", "/2008")));
    assertThat(glob("/200[78]"), is(paths("/2007", "/2008")));
    assertThat(glob("/200[7-8]"), is(paths("/2007", "/2008")));
    assertThat(glob("/200[^01234569]"), is(paths("/2007", "/2008")));

    assertThat(glob("/*/*/{31,01}"), is(paths("/2007/12/31", "/2008/01/01")));
    assertThat(glob("/*/*/3{0,1}"), is(paths("/2007/12/30", "/2007/12/31")));

    assertThat(glob("/*/{12/31,01/01}"), is(paths("/2007/12/31", "/2008/01/01")));
  }
  
  @Test
  public void regexIncludes() throws Exception {
    assertThat(glob("/*", new RegexPathFilter("^.*/2007$")), is(paths("/2007")));
    assertThat(glob("/*/*/*", new RegexPathFilter("^.*/2007/12/31$")), is(paths("/2007/12/31")));
    assertThat(glob("/*/*/*", new RegexPathFilter("^.*/2007(/12(/31)?)?$")), is(paths("/2007/12/31")));
  }
  
  @Test
  public void regexExcludes() throws Exception {
    assertThat(glob("/*", new RegexPathFilter("^.*/2007$", false)), is(paths("/2008")));
    assertThat(glob("/2007/*/*", new RegexPathFilter("^.*/2007/12/31$", false)), is(paths("/2007/12/30")));
  }
  
  @Test
  public void regexExcludesWithRegexExcludePathFilter() throws Exception {
    assertThat(glob("/*", new RegexExcludePathFilter("^.*/2007$")), is(paths("/2008")));
    assertThat(glob("/2007/*/*", new RegexExcludePathFilter("^.*/2007/12/31$")), is(paths("/2007/12/30")));
  }

  @Test
  public void testDateRange() throws Exception {
    DateRangePathFilter filter = new DateRangePathFilter(date("2007/12/31"),
        date("2008/01/01"));
    assertThat(glob("/*/*/*", filter), is(paths("/2007/12/31", "/2008/01/01")));  
  } 
  
  private Set<Path> glob(String pattern) throws IOException {
    return new HashSet<Path>(Arrays.asList(
        FileUtil.stat2Paths(fs.globStatus(new Path(BASE_PATH + pattern)))));
  }
  
  private Set<Path> glob(String pattern, PathFilter pathFilter) throws IOException {
    return new HashSet<Path>(Arrays.asList(
        FileUtil.stat2Paths(fs.globStatus(new Path(BASE_PATH + pattern), pathFilter))));
  }
  
  private Set<Path> paths(String... pathStrings) {
    Path[] paths = new Path[pathStrings.length];
    for (int i = 0; i < paths.length; i++) {
      paths[i] = new Path("file:" + BASE_PATH + pathStrings[i]);
    }
    return new HashSet<Path>(Arrays.asList(paths));
  }
  
  private Date date(String date) throws ParseException {
    return new SimpleDateFormat("yyyy/MM/dd").parse(date);
  }
}
```

#### 6 删除文件

```java
public boolean delete(Path path,boolean recursive)
```

> recursive = true 时 非空目录才会被删除,否则抛异常

```java
FileSystem.get(conf)
                  .delete(new Path(args[1]), true);
```

#### 7 文件系统一致模型(coherency model)

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CoherencyModelTest {
    private MiniDFSCluster cluster; // use an in-process HDFS cluster for testing
    private FileSystem     fs;

    @Before
    public void setUp() throws IOException {
        Configuration conf = new Configuration();
        if (System.getProperty("test.build.data") == null) {
            System.setProperty("test.build.data", "/tmp");
        }
        cluster = new MiniDFSCluster.Builder(conf).build();
        fs = cluster.getFileSystem();
    }

    @After
    public void tearDown() throws IOException {
        fs.close();
        cluster.shutdown();
    }

    /**
     * 文件create后在 namenode 节点立即可见
     */
    @Test
    public void fileExistsImmediatelyAfterCreation() throws IOException {
        Path p = new Path("p");
        fs.create(p);
        assertThat(fs.exists(p), is(true));
        assertThat(fs.delete(p, true), is(true));
    }

    /**
     * 文件内容即使调用了 flush 也不能保证立即可见(当前正在写入的块对其他 reader 不可见)
     */
    @Test
    public void fileContentIsNotVisibleAfterFlush() throws IOException {
        Path         p   = new Path("p");
        OutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));
        out.flush();

        assertThat(fs.getFileStatus(p)
                     .getLen(), is(0L)); // flush后文件内容长度仍为0

        out.close();
        assertThat(fs.delete(p, true), is(true));
    }

    /**
     * 调用 hflush() 方法强行同步所有缓存和数据节点 , 这样能保证写入的文件均到达所有
     * datanode 的写入管道并且对所有新的 reader 可见 . 这种方法有许多额外的开销,所以
     * 应用中需要权衡好鲁棒性和吞吐量 , 确定合适的调用频率
     */
    @Test
    public void fileContentIsVisibleAfterHFlush() throws IOException {
        Path               p   = new Path("p");
        FSDataOutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));
        out.hflush();

        assertThat(fs.getFileStatus(p)
                     .getLen(), is(((long) "content".length())));
//                     .getLen(), is(0L));

        out.close();
        assertThat(fs.delete(p, true), is(true));
    }

    /**
     * 调用 hsync() 强行同步
     */
    @Test
    public void fileContentIsVisibleAfterHSync() throws IOException {
        Path               p   = new Path("p");
        FSDataOutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));
        out.hsync(); // 强行同步

        assertThat(fs.getFileStatus(p)
                     .getLen(), is(((long) "content".length())));
        out.close();
        assertThat(fs.delete(p, true), is(true));
    }

    /**
     * 调用 sync() 方法进行同步, 该api据说要被遗弃???
     */
    @Test
    public void localFileContentIsVisibleAfterFlushAndSync() throws IOException {
        File localFile = File.createTempFile("tmp", "");
        assertThat(localFile.exists(), is(true));

        FileOutputStream out = new FileOutputStream(localFile);
        out.write("content".getBytes("UTF-8"));
        out.flush(); // flush to operating system
        out.getFD()
           .sync(); // sync to disk
        assertThat(localFile.length(), is(((long) "content".length())));

        out.close();
        assertThat(localFile.delete(), is(true));
    }

    @Test
    public void fileContentIsVisibleAfterClose() throws IOException {
        Path         p   = new Path("p");
        OutputStream out = fs.create(p);
        out.write("content".getBytes("UTF-8"));

        out.close();
        assertThat(fs.getFileStatus(p)
                     .getLen(), is(((long) "content".length())));

        assertThat(fs.delete(p, true), is(true));
    }

}
```

## 3 Hadoop 的IO操作

### 1 数据完整性

1. hdfs 客户端在写入或读取文件时,都会计算(写入)和验证(读取时)校验和(CRC-32校验) . 默认情况下检验单位是 512 字节,生成的校验和为 4 个字节,所以存储校验和的开销小于 1% , 是可以接受的

   > `io.bytes.per.checksum`   指定的校验数据块的大小

2. 每个 datanode 节点会在后台运行一个 DataBlockScanner , 定期验证存储在当前节点的数据块 , 并尝试处理损坏的数据块(通过数据副本复制新的块,然后删除损坏的块,保证数据副本因子(replication factor)在期望水平)

### 2 压缩

​	压缩的好处:

	1. 减少存储文件所需要的磁盘空间
	2. 加速数据在网络和磁盘上的传输

![](img/3-7.png)

> 压缩工具都提供了 9 个不同的选项  , 例如 : `gzip -1 file`
>
> `1 --> 速度最优`
>
> `9 --> 体积最优`

​	Codec 实现了压缩-解压缩算法 , Hadoop 实现的 codec 例举如下 :

| 压缩格式 | HadoopCompressionCodec                     | 是否可切分 |
| -------- | ------------------------------------------ | ---------- |
| DEFLATE  | org.apache.hadoop.io.compress.DefaultCodec |            |
| gzip     | org.apache.hadoop.io.compress.GzipCodec    |            |
| bzip2    | org.apache.hadoop.io.compress.BZip2Codec   | 是         |
| LZO      | com.hadoop.compression.lzo.LzopCodec       |            |
| LZ4      | org.apache.hadoop.io.compress.Lz4Codec     |            |
| Snappy   | org.apache.hadoop.io.compress.SnappyCodec  |            |

​	使用方式 : 使用 codec对象对输入/输出流进行包裹即可:

```java
InputStream in   = codec.createInputStream(fs.open(inPath));   // 创建一个带压缩算法的输入流
OutputStream out = codec.createOutputStream(System.out);	   // 带压缩算法的输出流	
```

1. 1. 根据文件拓展名推断 CompressionCodec 并解压文件

```java
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * 根据输入文件名的拓展名推断codec 并解压文件,输出到控制台
 *
 * @author fmi110
 * @Date 2018/4/8 20:52
 */
@Slf4j
public class FileDecompressor {
    public static void main(String[] args) throws IOException {
        String        uri  = args[0];
        Configuration conf = new Configuration();
        FileSystem    fs   = FileSystem.get(URI.create(uri), conf);

        Path                    inputPath = new Path(uri);
        CompressionCodecFactory factory   = new CompressionCodecFactory(conf);
        CompressionCodec        codec     = factory.getCodec(inputPath);

        if (null == codec) {
            log.error("输入的文件后缀名有错...path = {}", uri);
            System.exit(-1);
        }

        // 获取输出文件名
        String outputUri = CompressionCodecFactory.removeSuffix(uri, codec.getDefaultExtension());

        InputStream  in  = null;
//        OutputStream out = null;
        try {
            in = codec.createInputStream(fs.open(inputPath));
//            out = fs.create(new Path(outputUri));
            IOUtils.copyBytes(in,System.out,4096,false);
        }finally {
            IOUtils.closeStream(in);
//            IOUtils.closeStream(out);
        }
    }
}
```

> 核心 api :
>
> `InputStream in = FileSystem.get(URI.create(uri),conf).open(new Path(uri));`
>
> `CompressionCodec = new CompressionCodecFactory(conf).getCodec(new Path(uri));`

2. 对查找气温作业所产生的输出进行压缩

```java
import com.fmi110.mapper.MaxTemperatureMapper;
import com.fmi110.reducer.MaxTemperatureReducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/**
 * @author fmi110
 * @Date 2018/4/8 21:31
 */
@Slf4j
public class MaxTemperatureWithCompression {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            log.error("输出参数少于2...");
            System.exit(-1);
        }

        Configuration conf = new Configuration() ;
        Job           job = Job.getInstance(conf);
        job.setJarByClass(MaxTemperatureWithCompression.class);

        FileInputFormat.addInputPath(job,new Path(args[0]));
        FileOutputFormat.setOutputPath(job,new Path(args[1]));

        // 设置输出使用的压缩算法
        FileOutputFormat.setOutputCompressorClass(job, GzipCodec.class);

        job.setMapperClass(MaxTemperatureMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(MaxTemperatureReducer.class);
        job.setCombinerClass(MaxTemperatureReducer.class);

        System.exit(job.waitForCompletion(true)?0:1);
    }
}
```

> `FileOutputFormat.setOutputCompressor(job,XXXCodec.class);`



### 3 java数据类型的 writable 类

![](img/3-6.png)

```java
public class WritableTestBase {
  
  // 序列化一个 Writable 对象 --> byte[]
  public static byte[] serialize(Writable writable) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(out);
    writable.write(dataOut);
    dataOut.close();
    return out.toByteArray();
  }
  // ^^ WritableTestBase
  
  // 反序列化 byte[] --> Writable 对象 
  public static byte[] deserialize(Writable writable, byte[] bytes)
      throws IOException {
    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
    DataInputStream dataIn = new DataInputStream(in);
    writable.readFields(dataIn);
    dataIn.close();
    return bytes;
  }

  // Writable --> byte[] --> String
  public static String serializeToString(Writable src) throws IOException {
    return StringUtils.byteToHexString(serialize(src));
  }
  // dest对象 (序列化)--> byte[] (反序列化)--> src 对象
  public static String writeTo(Writable src, Writable dest) throws IOException {
    byte[] data = deserialize(dest, serialize(src));
    return StringUtils.byteToHexString(data);
  }

}
```



#### 1 ArrayWritable

```java
    ArrayWritable writable = new ArrayWritable(Text.class);
    
    writable.set(new Text[] { new Text("cat"), new Text("dog") });
    
    TextArrayWritable dest = new TextArrayWritable();
    WritableUtils.cloneInto(dest, writable);

    assertThat(dest.get().length, is(2));
    // TODO: fix cast, also use single assert
    assertThat((Text) dest.get()[0], is(new Text("cat")));
    assertThat((Text) dest.get()[1], is(new Text("dog")));
    
    Text[] copy = (Text[]) dest.toArray();
    assertThat(copy[0], is(new Text("cat")));
    assertThat(copy[1], is(new Text("dog")));
```

#### 2 BooleanWritable

```java
    BooleanWritable src = new BooleanWritable(true);
    BooleanWritable dest = new BooleanWritable();

    assertThat(writeTo(src, dest), is("01"));
    assertThat(dest.get(), is(src.get()));
```

#### 3 MapWritable

```java
// == MapWritableTest
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import org.apache.hadoop.io.*;
import org.junit.Test;

public class MapWritableTest extends WritableTestBase {
  
  @Test
  public void mapWritable() throws IOException {
    // vv MapWritableTest
    MapWritable src = new MapWritable();
    src.put(new IntWritable(1), new Text("cat"));
    src.put(new VIntWritable(2), new LongWritable(163));
    
    MapWritable dest = new MapWritable();
    WritableUtils.cloneInto(dest, src);
    assertThat((Text) dest.get(new IntWritable(1)), is(new Text("cat")));
    assertThat((LongWritable) dest.get(new VIntWritable(2)),
        is(new LongWritable(163)));
    // ^^ MapWritableTest
  }

  @Test
  public void setWritableEmulation() throws IOException {
    MapWritable src = new MapWritable();
    src.put(new IntWritable(1), NullWritable.get());
    src.put(new IntWritable(2), NullWritable.get());
    
    MapWritable dest = new MapWritable();
    WritableUtils.cloneInto(dest, src);
    assertThat(dest.containsKey(new IntWritable(1)), is(true));
  }
}
```

### 4 基于文件的数据结构

​	对于基于mapreduce 的数据处理 , 将每个二进制数据大对象(blob)单独放在各自的文件中不能实现可拓展性 , 所以 Hadoop 为次开发了很多更高层次的容器 , 比如 SequenceFile  , MapFile 等

![](img/3-8.png)

![](img/3-9.png)

#### 1 SequenceFile

​	SequenceFile(顺序文件)是以键值形式存储数据的 , 可以作为小文件的容器 .

```
hadoop fd -text 顺序文件
```

> 该命令能以文本的形式显示顺序文件, 如果顺序文件包含自定义的 键或值的类,需要保证这些类位于 Hadoop 类路径下	

1. 写操作

   > `SequenceFile.Writer writer = SequenceFile.createWriter();`
   >
   > `writer.appen(key,value);`

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;
import java.net.URI;


/**
 * 顺序文件的写操作
 *
 * @author fmi110
 * @Date 2018/4/8 22:28
 */
public class SequenceFileWriterDemo {
    public static final String[] DATA = {
            "窗前明月光",
            "疑是地上霜",
            "举头望明月",
            "低头思故乡"
    };

    public static void main(String[] args) throws IOException {
        String outPath = "00TT/squencefile.seq";

        Configuration conf = new Configuration();
        FileSystem    fs   = FileSystem.get(URI.create(outPath), conf);
        Path          path = new Path(outPath);

        IntWritable key   = new IntWritable(); // 顺序文件的key
        Text        value = new Text();        // 顺序文件的value

        SequenceFile.Writer writer = null;
        try {
            writer = SequenceFile.createWriter(fs, conf, path, key.getClass(), value.getClass());
            for (int i = 0; i < 100; i++) {
                key.set(i);
                value.set(DATA[i % DATA.length]);
                System.out.printf("[%s]\t%s\t%s\n", writer.getLength(), key.get(), value.toString());

                writer.append(key, value); // 内容写入顺序文件
            }
        } finally {
            IOUtils.closeStream(writer);
        }

    }
}
```

2. 读操作

   > SequenceFile.Reader.next() 进行迭代 , 非空时循环

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;
import java.net.URI;


/**
 * 顺序文件的读操作
 *
 * @author fmi110
 * @Date 2018/4/8 22:28
 */
public class SequenceFileReaderDemo {


    public static void main(String[] args) throws IOException {
        String inPath = "00TT/squencefile.seq";

        Configuration conf = new Configuration();
        FileSystem    fs   = FileSystem.get(URI.create(inPath), conf);
        Path          path = new Path(inPath);

        SequenceFile.Reader reader = null;
        try {
            reader = new SequenceFile.Reader(fs, path, conf);
            Writable key   = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
            Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), conf);

            long position = reader.getPosition();
            while (reader.next(key, value)) {
                String syncSeen = reader.syncSeen() ? "*" : ""; // 是否是同步点

                System.out.printf("[%s%s]\t%s\t%s\n", position, syncSeen, key, value);
                
                position = reader.getPosition(); // 获取下一次起始的位置
            }
        } finally {
            IOUtils.closeStream(reader);
        }

    }
}
```

​	输出如下:

```properties
[1793]	45	疑是地上霜
[1830]	46	举头望明月
[1867]	47	低头思故乡
[1904]	48	窗前明月光
[1941]	49	疑是地上霜
[1978]	50	举头望明月
[2015*]	51	低头思故乡
[2072]	52	窗前明月光
[2109]	53	疑是地上霜
[2146]	54	举头望明月
[2183]	55	低头思故乡
```

> 可以看到 2015 处是一个同步点
>
> 同步点 : 指数据读取迷路(lost)后能够再一次与记录边界同步的数据流的某个位置 , 同步点由SequenceFile.Writer 记录 , 其在顺序文件写入过程中插入一个特殊项以便每隔几个记录便有一个同步标识.这样的特殊项存储开销很小,不到1%

​	在文件中查找记录边界的方法

```
SequenceFile.Reader.sync(long position);
```

> 该方法将读取位置定位到下一个同步点 , 如果 position 之后没有同步点 , 则读取位置将指向文件末尾

​	手动插入同步点的方法

```
SequenceFile.Writer.sync();
```

> 在当前位置插入同步点