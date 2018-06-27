package com.fmi110.hadoop.mapper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MaxTempratureMapper extends Mapper<LongWritable,Text,Text,IntWritable>{

    private static final Logger logger = LoggerFactory.getLogger(MaxTempratureMapper.class);

    /**
     *
     * @param key  输入的 key
     * @param value 输入的值
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
//        super.map(key, value, context);
        String line = value.toString();
        logger.info("============ {} , key = {}",line,key);

        // 开始处理数据 , 获取数据 mapper
        String year = line.substring(4, 8);
        int temp ;
        if(line.charAt(12)=='+'){
            temp = Integer.parseInt(line.substring(13,15));
        }else{
            temp = Integer.parseInt(line.substring(12,15));
        }
        logger.info("======== temp = {}",temp);

        // 输出到 reduce
        context.write(new Text(year),new IntWritable(temp));
    }
}
