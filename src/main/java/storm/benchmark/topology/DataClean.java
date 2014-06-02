package storm.benchmark.topology;

import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.benchmark.IBenchmark;
import storm.benchmark.StormBenchmark;
import storm.benchmark.bolt.FilterBolt;
import storm.benchmark.bolt.PageViewBolt;
import storm.benchmark.util.BenchmarkUtils;
import storm.benchmark.util.KafkaUtils;
import storm.kafka.KafkaSpout;
import storm.kafka.StringScheme;

import java.util.Map;

import static storm.benchmark.tools.PageView.Item;

public class DataClean extends StormBenchmark {
  public final static String SPOUT_ID = "spout";
  public final static String SPOUT_NUM = "topology.component.spout_num";
  public final static String VIEW_ID = "view";
  public final static String VIEW_NUM = "topology.component.view_bolt_num";
  public final static String FILTER_ID = "filter";
  public final static String FILTER_NUM = "topology.component.filter_bolt_num";

  // number of Spouts to run in parallel
  private int spoutNum = 4;
  // number of PageViewBolts to run in parallel
  private int pvBoltNum = 4;
  // number of FilterBolts to run in parallel
  private int filterBoltNum = 4;

  private IRichSpout spout;


  @Override
  public IBenchmark parseOptions(Map options) {
    super.parseOptions(options);

    spoutNum = BenchmarkUtils.getInt(options, SPOUT_NUM, spoutNum);
    pvBoltNum = BenchmarkUtils.getInt(options, VIEW_NUM, pvBoltNum);
    filterBoltNum = BenchmarkUtils.getInt(options, FILTER_NUM, filterBoltNum);
    spout = new KafkaSpout(KafkaUtils.getSpoutConfig(
            options, new SchemeAsMultiScheme(new StringScheme())));

    return this;
  }

  @Override
  public IBenchmark buildTopology() {
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout(SPOUT_ID, spout, spoutNum);
    builder.setBolt(VIEW_ID, new PageViewBolt(Item.STATUS, Item.ALL), pvBoltNum)
            .localOrShuffleGrouping(SPOUT_ID);
    builder.setBolt(FILTER_ID, new FilterBolt<Integer>(404), filterBoltNum)
            .fieldsGrouping(VIEW_ID, new Fields(Item.STATUS.toString()));
    topology = builder.createTopology();
    return this;
  }
}
