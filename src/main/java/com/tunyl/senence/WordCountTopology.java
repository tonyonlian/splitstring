package com.tunyl.senence;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.alibaba.jstorm.client.ConfigExtension;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Created by Tunyl on 2018/5/7.
 * 入口文件
 */
public class WordCountTopology {
    //装载配置文件参数

    private static Map conf = new HashMap<Object,Object>();

    public static void main(String[] args){
        if (args.length == 0) {
            System.err.println("Please input configuration file");
            System.exit(-1);
        }
        //加载配置文件配置到内存
        //String path2 = WordCountTopology.class.getResource("/").getPath()+"simple.yaml";
       // LoadConf(path2);
        LoadConf(args[0]);
        //构建JStorm拓扑
        TopologyBuilder builder = setupBuilder();
        System.out.println("Topology准备提交");
        //提交任务到集群
        submitTopology(builder);
        System.out.println("Topology提交完成");
    }


    //!!!!这里通过setSpout和setBolt设置各个节点之间的连接关系，
    // 是这里把所有各自独立的节点用线连接起来，构建成一张具体的任务执行拓扑图

    private static TopologyBuilder setupBuilder(){
        TopologyBuilder builder = new TopologyBuilder();
       // ConfigExtension.setUserDefinedLog4jConf(conf, "jstorm.log4j.properties");

        /*
         * 设置spout和bolt,完整参数为
         * 1,spout的id(即name)
         * 2,spout对象
         * 3,executor数量即并发数，也就是设置多少个executor来执行spout/bolt(此项没有默认null)
         */
        //setSpout，声明Spout名称Id为sentence-spout，并行度1
        builder.setSpout("sentence-spout", new RandomSentenceSpout(), 1);
        //setBolt:SplitBolt的grouping策略是上层随机分发，CountBolt的grouping策略是按照上层字段分发
        //如果想要从多个Bolt获取数据，可以继续设置grouping
        //声明Bolt名称Id为split-bolt，并行度1

        builder.setBolt("split-bolt", new SplitBolt(), 1)
                //设置该Bolt的数据源为sentence-spout的输出
                .shuffleGrouping("sentence-spout");
        //声明Bolt名称Id为count-bolt，并行度1
        builder.setBolt("count-bolt", new CountBolt(), 1)
                //设置该Bolt的数据源为sentence-spout和split-bolt的输出,这样就可以统计到句子数计数与切分单词的计数
                //fieldsGrouping保证相同word对应的值发送到同一个Task节点，这是单词计数业务需要
                .fieldsGrouping("split-bolt", new Fields("word"))
                //统计随机取出的句子计数，如果不想计数句子可注释掉本行代码
               .fieldsGrouping("sentence-spout", new Fields("word"));


        return builder;

    }


    //提交任务到JStorm集群

    private static void submitTopology(TopologyBuilder builder){
        try {
            if (local_mode(conf)) {//本地模式，需要有本地JStorm环境支持
                LocalCluster cluster = new LocalCluster();
                cluster.submitTopology(
                        String.valueOf(conf.get("topology.name")), conf,
                        builder.createTopology());

                Thread.sleep(200000);
                cluster.shutdown();
            } else {
                StormSubmitter.submitTopology(
                        String.valueOf(conf.get("topology.name")), conf,
                        builder.createTopology());
            }
        } catch (Exception e) {
            System.out.println(e);

        }

    }

    //加载Properties配置文件
    private static void LoadProperty(String prop) {
        Properties properties = new Properties();
        try {
            InputStream stream = new FileInputStream(prop);
            properties.load(stream);
        } catch (FileNotFoundException e) {
            System.out.println("No such file " + prop);
        } catch (Exception e1) {
            e1.printStackTrace();

            return;
        }
        conf.putAll(properties);
    }

    //加载Yaml配置文件
    private static void LoadYaml(String confPath) {
        Yaml yaml = new Yaml();

        try {
            InputStream stream = new FileInputStream(confPath);
            conf = (Map) yaml.load(stream);
            if (conf == null || conf.isEmpty() == true) {
                throw new RuntimeException("Failed to read config file");
            }
        } catch (FileNotFoundException e) {
            System.out.println("No such file " + confPath);
            throw new RuntimeException("No config file");
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException("Failed to read config file");
        }
    }

    //根据后缀名选择加载配置文件方案
    private static void LoadConf(String arg) {
        if (arg.endsWith("yaml")) {
            LoadYaml(arg);
        } else {
            LoadProperty(arg);
        }
    }

    public static boolean local_mode(Map conf) {
        String mode = (String) conf.get(Config.STORM_CLUSTER_MODE);
        if (mode != null) {
            if (mode.equals("local")) {
                return true;
            }
        }
        return false;
    }

}
