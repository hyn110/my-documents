package com.fmi110.hadoop;

import com.fmi110.hadoop.mapper.MaxTempratureMapper;
import com.fmi110.hadoop.reduce.MaxTempratureReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MaxTemprature {
    private static final Logger logger = LoggerFactory.getLogger(MaxTemprature.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        if(args.length!=2){
            logger.error("必须指定输入文件路径和输出文件路径");
             System.exit(-1);
        }

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///"); // 设置为本地模式 , 覆盖 core-site.xml 中的配置


        // 删除已存在的输出目录 , 实际中不能这么干!!
        FileSystem.get(conf).delete(new Path(args[1]),true);

        Job job = Job.getInstance(conf);
        job.setJobName("maxTemprature job");

        job.setJarByClass(MaxTemprature.class);

        job.setInputFormatClass(TextInputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // 设置输出文件

        // 设置 mapper , 指定 k-v 泛型
        job.setMapperClass(MaxTempratureMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 设置 reducer
        job.setNumReduceTasks(1);
        job.setReducerClass(MaxTempratureReducer.class);

        // 设置输出的 k-v
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        boolean success = job.waitForCompletion(true);

        logger.info("job is success : {}", success);

    }
}
//===========数据=============//
//  01012017xxee+12888888
//  01012017xxee+24888888
//  01012015xxee-76888888
//  01012015xxee+19888888
//  01012017xxee-09888888
//  01012016xxee+21888888
//  01012016xxee-37888888
//========================//

//========================================运行结果==========================================================//
//        INFO  c.f.h.mapper.MaxTempratureMapper - ============ 01012017xxee+12888888 , key = 0
//        INFO  c.f.h.mapper.MaxTempratureMapper - ======== temp = 12
//        INFO  c.f.h.mapper.MaxTempratureMapper - ============ 01012017xxee+24888888 , key = 22
//        INFO  c.f.h.mapper.MaxTempratureMapper - ======== temp = 24
//        INFO  c.f.h.mapper.MaxTempratureMapper - ============ 01012015xxee-76888888 , key = 44
//        INFO  c.f.h.mapper.MaxTempratureMapper - ======== temp = -76
//        INFO  c.f.h.mapper.MaxTempratureMapper - ============ 01012015xxee+19888888 , key = 66
//        INFO  c.f.h.mapper.MaxTempratureMapper - ======== temp = 19
//        INFO  c.f.h.mapper.MaxTempratureMapper - ============ 01012017xxee-09888888 , key = 88
//        INFO  c.f.h.mapper.MaxTempratureMapper - ======== temp = -9
//        INFO  c.f.h.mapper.MaxTempratureMapper - ============ 01012016xxee+21888888 , key = 110
//        INFO  c.f.h.mapper.MaxTempratureMapper - ======== temp = 21
//        INFO  c.f.h.mapper.MaxTempratureMapper - ============ 01012016xxee-37888888 , key = 132
//        INFO  c.f.h.mapper.MaxTempratureMapper - ======== temp = -37
//        INFO  c.f.h.reduce.MaxTempratureReducer - result ==== key = 2015 , maxValue = 19
//        INFO  c.f.h.reduce.MaxTempratureReducer - result ==== key = 2016 , maxValue = 21
//        INFO  c.f.h.reduce.MaxTempratureReducer - result ==== key = 2017 , maxValue = 24