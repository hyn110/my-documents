package com.fmi110.hadoop.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * The type Hdfs demo test.
 */
public class HdfsDemoTest {
    /**
     * Test 1.
     *
     * @throws Exception the exception
     */
    @Test
    public void test1() throws Exception {

        Configuration conf = new Configuration();
        FileSystem    fs   = FileSystem.get(conf);

        FSDataInputStream fis = fs.open(new Path("hdfs://m01/user/fmi110/hello.txt"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copyBytes(fis, out, 1024);
        System.out.println(new String(out.toByteArray()));
    }

    /**
     * Test write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testWrite() throws Exception {


        FileSystem fileSystem = FileSystem.get(new Configuration());

        Path path = new Path("hdfs://10.211.55.12/user/fmi110/hello.txt");


        FSDataOutputStream fos = fileSystem.create(path, true);
        fos.write("this is my hadoop test".getBytes());
        fos.close();


    }
}
//    @Test
//    public void testRead() throws Exception {
//        Configuration         conf = new Configuration();
//        FileSystem            fs   = FileSystem.get(conf);
//        Path                  path = new Path("hdfs://s201/user/centos/hello.txt") ;
//        FSDataInputStream     fis  = fs.open(path);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        IOUtils.copyBytes(fis,baos,1024);
//        fis.close();
//        System.out.println(new String(baos.toByteArray()));
//
//        testWrite();
//    }