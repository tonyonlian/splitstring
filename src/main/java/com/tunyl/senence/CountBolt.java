package com.tunyl.senence;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.alibaba.jstorm.callback.AsyncLoopThread;
import com.alibaba.jstorm.callback.RunnableCallback;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Created by Tunyl on 2018/5/3.
 */
public class CountBolt extends BaseBasicBolt {
    private static final long serialVersionUID = 7104767103420386799L;
    private static final Logger LOG   = LoggerFactory.getLogger(CountBolt.class);

    private Integer id;
    private String name;
    private Map<String,Integer> counters;
    private String component;
    //异步输出结果集的子线程
    private AsyncLoopThread statThread;
    //接收消息之后被调用的方法
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        String str = tuple.getStringByField("word");
        if(!counters.containsKey(str)){
            counters.put(str,1);
        }else{
            Integer c = counters.get(str) + 1;
            counters.put(str, c);
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word", "count"));
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        this.counters = new HashMap<String, Integer>();
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
        this.statThread = new AsyncLoopThread(new statRunnable());
        component = context.getThisComponentId();
    }
    /**
     * 定义内部类
     * 异步输出结果集的死循环子线程
     */
    class statRunnable extends RunnableCallback {

        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                LOG.info("===================count start===========================");
                LOG.info("\n-- Word Counter [" + name + "-" + id + "] --");
                for (Map.Entry<String, Integer> entry : counters.entrySet()) {
                   // System.out.println("=====================================");
                    //System.out.println(entry.getKey() + ": " + entry.getValue());
                    LOG.info(entry.getKey() + ": " + entry.getValue());
                }
                LOG.info("");
                LOG.info("===================count end===========================");

            }
        }
    }

}
