/*
 * Copyright © 2023 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.directives;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AggregateStatsTest {

  @Test
  public void testAggregateStats() throws Exception {
    // Create test data
    List<Row> rows = new ArrayList<>();
    rows.add(new Row().put("size", "10KB").put("duration", "150ms"));
    rows.add(new Row().put("size", "5MB").put("duration", "2s"));
    rows.add(new Row().put("size", "2GB").put("duration", "5m"));

    // Create directive
    AggregateStats directive = new AggregateStats();
    Arguments args = new Arguments();
    args.put("source-size-column", "size");
    args.put("source-time-column", "duration");
    args.put("target-size-column", "total_size");
    args.put("target-time-column", "total_duration");
    args.put("output-size-unit", "MB");
    args.put("output-time-unit", "s");
    args.put("aggregation-type", "total");

    directive.initialize(args);

    // Execute directive
    List<Row> result = directive.execute(rows, null);

    // Verify results
    Assert.assertEquals(1, result.size());
    Row output = result.get(0);

    // Verify size aggregation (10KB + 5MB + 2GB)
    double totalSize = output.getValue("total_size");
    Assert.assertEquals(2005.01, totalSize, 0.001);

    // Verify duration aggregation (150ms + 2s + 5m)
    double totalDuration = output.getValue("total_duration");
    Assert.assertEquals(302.15, totalDuration, 0.001);
  }

  @Test
  public void testAverageAggregation() throws Exception {
    // Create test data
    List<Row> rows = new ArrayList<>();
    rows.add(new Row().put("size", "10KB").put("duration", "150ms"));
    rows.add(new Row().put("size", "5MB").put("duration", "2s"));
    rows.add(new Row().put("size", "2GB").put("duration", "5m"));

    // Create directive
    AggregateStats directive = new AggregateStats();
    Arguments args = new Arguments();
    args.put("source-size-column", "size");
    args.put("source-time-column", "duration");
    args.put("target-size-column", "avg_size");
    args.put("target-time-column", "avg_duration");
    args.put("output-size-unit", "GB");
    args.put("output-time-unit", "m");
    args.put("aggregation-type", "average");

    directive.initialize(args);

    // Execute directive
    List<Row> result = directive.execute(rows, null);

    // Verify results
    Assert.assertEquals(1, result.size());
    Row output = result.get(0);

    // Verify size average (2005.01 MB / 3)
    double avgSize = output.getValue("avg_size");
    Assert.assertEquals(0.6683, avgSize, 0.001);

    // Verify duration average (302.15s / 3)
    double avgDuration = output.getValue("avg_duration");
    Assert.assertEquals(5.0358, avgDuration, 0.001);
  }

  @Test(expected = Exception.class)
  public void testInvalidAggregationType() throws Exception {
    AggregateStats directive = new AggregateStats();
    Arguments args = new Arguments();
    args.put("source-size-column", "size");
    args.put("source-time-column", "duration");
    args.put("target-size-column", "total_size");
    args.put("target-time-column", "total_duration");
    args.put("output-size-unit", "MB");
    args.put("output-time-unit", "s");
    args.put("aggregation-type", "invalid");

    directive.initialize(args);
  }
}
