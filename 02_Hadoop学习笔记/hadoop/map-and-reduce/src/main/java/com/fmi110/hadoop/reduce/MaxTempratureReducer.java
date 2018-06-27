package com.fmi110.hadoop.reduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class MaxTempratureReducer extends Reducer<Text,IntWritable,Text,IntWritable> {

    private static final Logger logger = LoggerFactory.getLogger(MaxTempratureReducer.class);

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
//        super.reduce(key, values, context);

        int maxValue = Integer.MIN_VALUE;
        for (IntWritable t:values) {
            maxValue = Math.max(maxValue, t.get());
        }

        context.write(key,new IntWritable(maxValue));
        logger.info("result ==== key = {} , maxValue = {}",key,maxValue);
    }
}
