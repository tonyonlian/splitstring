package com.tunyl.senence;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Time;
import backtype.storm.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
import java.util.Random;

/**
 * @author Created by Tunyl on 2018/5/3.
 * RandomSentenceSpout实现了IRichSpout接口
 * Spout需要实现的接口可以是：
 *     1,IRichSpout：最基本的Spout,继承自ISpout, IComponent,沒有任何特殊方法（一般用这个）
 *    2,IControlSpout:继承自IComponent,包括open，close，activate，deactivate，nextTuple，ack(Object msgId)，fail等方法
 */
public class RandomSentenceSpout implements IRichSpout {
    private static final long serialVersionUID = 4058847280819269954L;
    private static final Logger logger = LoggerFactory.getLogger(RandomSentenceSpout.class);

    //可以理解为JStorm的数据传输管道，通过这个对象将这个组件的数据传输到下一个组件当中
    private SpoutOutputCollector _collector;
    //随机生成对象
    private Random _rand;
    private String component;

    /**
     * Spout初始化的时候调用
     */
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this._collector = spoutOutputCollector;
        this._rand = new Random();
        this.component = topologyContext.getThisComponentId();
    }

    public void close() {

    }

    public void activate() {
        System.out.println("Active");

    }

    public void deactivate() {

    }
    /**
     * 系统框架会不断调用
     */
    public void nextTuple() {

        String[] sentences = new String[]{"Hello world! This is my first programme of JStorm",
                "Hello JStorm,Nice to meet you!", "Hi JStorm, do you have a really good proformance",
                "Goodbye JStorm,see you tomorrow"};
        String sentence = sentences[_rand.nextInt(sentences.length)];
        _collector.emit(new Values(sentence), Time.currentTimeSecs());
        Utils.sleep(1000);

    }

    public void ack(Object o) {
        System.out.println("ack");

    }

    public void fail(Object o) {
        System.out.println("fail");
    }
    /**
     * 声明框架有哪些输出的字段
     */
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //下一个组件通过word这个关键字拿到这个组件往后输出的单词sentence
        outputFieldsDeclarer.declare(new Fields("word"));

    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
