/*
 * Copyright © 2017-2019 Cask Data, Inc.
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
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.Token;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.UsageDefinition.Builder;
import io.cdap.wrangler.api.parser.UsageDefinition.ArgumentsDefinition;
import io.cdap.wrangler.api.parser.UsageDefinition.ArgumentsDefinition.Builder;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TimeDuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Directive that aggregates byte sizes and time durations from columns.
 */
public class AggregateStats implements Directive {
  private String sourceSizeColumn;
  private String sourceTimeColumn;
  private String targetSizeColumn;
  private String targetTimeColumn;
  private String outputSizeUnit;
  private String outputTimeUnit;
  private String aggregationType;

  @Override
  public UsageDefinition define() {
    Builder builder = UsageDefinition.builder("aggregate-stats");
    ArgumentsDefinition args = builder.defineArguments();

    args.define("source-size-column", TokenType.COLUMN_NAME)
        .define("source-time-column", TokenType.COLUMN_NAME)
        .define("target-size-column", TokenType.COLUMN_NAME)
        .define("target-time-column", TokenType.COLUMN_NAME)
        .define("output-size-unit", TokenType.TEXT, "MB")
        .define("output-time-unit", TokenType.TEXT, "s")
        .define("aggregation-type", TokenType.TEXT, "total");

    return builder.build();
  }

  @Override
  public void initialize(Arguments args) throws DirectiveExecutionException {
    sourceSizeColumn = args.value("source-size-column").toString();
    sourceTimeColumn = args.value("source-time-column").toString();
    targetSizeColumn = args.value("target-size-column").toString();
    targetTimeColumn = args.value("target-time-column").toString();
    outputSizeUnit = args.value("output-size-unit").toString();
    outputTimeUnit = args.value("output-time-unit").toString();
    aggregationType = args.value("aggregation-type").toString();

    if (!aggregationType.equals("total") && !aggregationType.equals("average")) {
      throw new DirectiveExecutionException("Aggregation type must be either 'total' or 'average'");
    }
  }

  @Override
  public void destroy() {
    // No cleanup needed
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    if (rows.isEmpty()) {
      return rows;
    }

    long totalBytes = 0;
    long totalTimeNs = 0;
    int rowCount = rows.size();

    for (Row row : rows) {
      Object sizeValue = row.getValue(sourceSizeColumn);
      Object timeValue = row.getValue(sourceTimeColumn);

      if (sizeValue != null) {
        if (sizeValue instanceof Token) {
          Token token = (Token) sizeValue;
          if (token.type() == TokenType.BYTE_SIZE) {
            totalBytes += ((ByteSize) token).getBytes();
          }
        }
      }

      if (timeValue != null) {
        if (timeValue instanceof Token) {
          Token token = (Token) timeValue;
          if (token.type() == TokenType.TIME_DURATION) {
            totalTimeNs += ((TimeDuration) token).getNanoseconds();
          }
        }
      }
    }

    // Convert to requested output units
    double outputSize = convertBytesToUnit(totalBytes, outputSizeUnit);
    double outputTime = convertNanosecondsToUnit(totalTimeNs, outputTimeUnit);

    // If aggregation type is average, divide by row count
    if (aggregationType.equals("average")) {
      outputSize /= rowCount;
      outputTime /= rowCount;
    }

    // Create a new row with the aggregated values
    Row result = new Row();
    result.put(targetSizeColumn, outputSize);
    result.put(targetTimeColumn, outputTime);

    // Clear the input rows and add the result
    rows.clear();
    rows.add(result);

    return rows;
  }

  private double convertBytesToUnit(long bytes, String unit) {
    unit = unit.toUpperCase();
    switch (unit) {
      case "B":
        return bytes;
      case "KB":
        return bytes / 1024.0;
      case "MB":
        return bytes / (1024.0 * 1024.0);
      case "GB":
        return bytes / (1024.0 * 1024.0 * 1024.0);
      case "TB":
        return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
      default:
        throw new DirectiveExecutionException("Unsupported size unit: " + unit);
    }
  }

  private double convertNanosecondsToUnit(long nanoseconds, String unit) {
    unit = unit.toLowerCase();
    switch (unit) {
      case "ns":
        return nanoseconds;
      case "us":
        return nanoseconds / 1000.0;
      case "ms":
        return nanoseconds / (1000.0 * 1000.0);
      case "s":
        return nanoseconds / (1000.0 * 1000.0 * 1000.0);
      case "m":
        return nanoseconds / (1000.0 * 1000.0 * 1000.0 * 60.0);
      case "h":
        return nanoseconds / (1000.0 * 1000.0 * 1000.0 * 60.0 * 60.0);
      case "d":
        return nanoseconds / (1000.0 * 1000.0 * 1000.0 * 60.0 * 60.0 * 24.0);
      default:
        throw new DirectiveExecutionException("Unsupported time unit: " + unit);
    }
  }
}
