/*
 * Copyright (C) 2014 The Calrissian Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.calrissian.flowmix.example.support;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import org.calrissian.flowmix.FlowmixFactory;
import org.calrissian.flowmix.bolt.PrinterBolt;
import org.calrissian.flowmix.model.kryo.EventSerializer;
import org.calrissian.flowmix.spout.MockEventGeneratorSpout;
import org.calrissian.flowmix.spout.MockFlowLoaderSpout;
import org.calrissian.mango.domain.event.BaseEvent;
import org.calrissian.mango.domain.event.Event;

public class ExampleRunner {

  FlowProvider provider;

  public ExampleRunner(FlowProvider provider) {
    this.provider = provider;
  }

  public void run() {

    StormTopology topology = new FlowmixFactory(
        new MockFlowLoaderSpout(provider.getFlows(), 60000),
        new MockEventGeneratorSpout(10),
        new PrinterBolt(), 6)
      .create()
    .createTopology();

    Config conf = new Config();
    conf.setNumWorkers(20);
    conf.setMaxSpoutPending(5000);
    conf.setDebug(false);
    conf.registerSerialization(BaseEvent.class, EventSerializer.class);
    conf.setSkipMissingKryoRegistrations(false);

    LocalCluster cluster = new LocalCluster();
    cluster.submitTopology("example-topology", conf, topology);
  }
}
