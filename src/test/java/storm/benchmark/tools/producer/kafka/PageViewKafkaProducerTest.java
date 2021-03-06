/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package storm.benchmark.tools.producer.kafka;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Values;
import org.testng.annotations.Test;
import storm.benchmark.tools.PageViewGenerator;

import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static storm.benchmark.tools.producer.kafka.PageViewKafkaProducer.PageViewSpout;

public class PageViewKafkaProducerTest {
  private static final Map ANY_CONF = new HashMap();
  private static final String NEXT_CLICK_EVENT = "next click event";

  @Test
  public void spoutShouldBeKafkaPageViewSpout() {
    KafkaProducer producer = new PageViewKafkaProducer();
    producer.getTopology(new Config());
    assertThat(producer.getSpout()).isInstanceOf(PageViewSpout.class);
  }


  @Test
  public void nextTupleShouldEmitNextClickEvent() throws Exception {
    PageViewGenerator generator = mock(PageViewGenerator.class);
    PageViewSpout spout = new PageViewSpout(generator);
    TopologyContext context = mock(TopologyContext.class);
    SpoutOutputCollector collector = mock(SpoutOutputCollector.class);

    when(generator.getNextClickEvent()).thenReturn(NEXT_CLICK_EVENT);

    spout.open(ANY_CONF, context, collector);
    spout.nextTuple();

    verify(collector, times(1)).emit(any(Values.class));
  }
}
