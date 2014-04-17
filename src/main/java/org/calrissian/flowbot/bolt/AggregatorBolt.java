package org.calrissian.flowbot.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import org.calrissian.flowbot.FlowbotTopology;

import java.util.Map;

import static org.calrissian.flowbot.Constants.*;
import static org.calrissian.flowbot.model.AggregateOp.AGGREGATE;

public class AggregatorBolt extends BaseRichBolt {


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    }

    @Override
    public void execute(Tuple tuple) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        FlowbotTopology.declareOutputStreams(outputFieldsDeclarer);
    }
}
