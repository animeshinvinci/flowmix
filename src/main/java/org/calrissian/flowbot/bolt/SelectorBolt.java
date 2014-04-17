package org.calrissian.flowbot.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.calrissian.flowbot.FlowbotTopology;
import org.calrissian.flowbot.model.Event;
import org.calrissian.flowbot.model.FilterOp;
import org.calrissian.flowbot.model.Flow;
import org.calrissian.flowbot.model.SelectOp;

import java.util.*;

import static org.calrissian.flowbot.Constants.EVENT;
import static org.calrissian.flowbot.Constants.FLOW_ID;
import static org.calrissian.flowbot.Constants.FLOW_OP_IDX;
import static org.calrissian.flowbot.spout.MockFlowLoaderSpout.FLOW_LOADER_STREAM;

public class SelectorBolt extends BaseRichBolt {

    Map<String,Flow> flows;
    OutputCollector collector;


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        flows = new HashMap<String, Flow>();
    }

    @Override
    public void execute(Tuple tuple) {

        if(FLOW_LOADER_STREAM.equals(tuple.getSourceStreamId())) {
            for(Flow flow : (Collection<Flow>)tuple.getValue(0))
                flows.put(flow.getId(), flow);
        } else {
            String flowId = tuple.getStringByField(FLOW_ID);
            Event event = (Event) tuple.getValueByField(EVENT);
            int idx = tuple.getIntegerByField(FLOW_OP_IDX);
            idx++;

            Flow flow = flows.get(flowId);

            if (flow != null) {
                SelectOp selectOp = (SelectOp) flow.getFlowOps().get(idx);

                String nextStream = idx+1 < flow.getFlowOps().size() ? flow.getFlowOps().get(idx + 1).getComponentName() : "output";

                Set<String> toRemove = new HashSet<String>();
                for(Map.Entry<String, Set<org.calrissian.flowbot.model.Tuple>> eventTuple : event.getTuples().entrySet()) {
                    if(!selectOp.getFields().contains(eventTuple.getKey()))
                        toRemove.add(eventTuple.getKey());
                }

                for(String keyToRemove : toRemove)
                    event.getTuples().remove(keyToRemove);

                collector.emit(nextStream, new Values(flowId, event, idx));
            }

            collector.ack(tuple);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        FlowbotTopology.declareOutputStreams(outputFieldsDeclarer);
    }
}
