package com.fmi110.hadoop.com.fmi110.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;


public class StreamCompressor {
    public static void main(String[] args) throws Exception {
        // 接收压缩算法实现类的类名
        String           codecClassName = args[0];
        Class<?>         clazz          = Class.forName(codecClassName);
        Configuration    conf           = new Configuration();
        CompressionCodec codec          = (CompressionCodec) ReflectionUtils.newInstance(clazz, conf);
        CompressionOutputStream out = codec.createOutputStream(System.out);
        IOUtils.copyBytes(System.in,out,4096,false);
        out.flush();
    }
}
