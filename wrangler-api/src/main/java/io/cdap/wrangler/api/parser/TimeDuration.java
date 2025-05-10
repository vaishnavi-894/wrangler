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

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.cdap.wrangler.api.annotations.PublicEvolving;

/**
 * Represents a time duration value with unit (e.g., "150ms", "2s").
 */
@PublicEvolving
public class TimeDuration implements Token {
  private final long nanoseconds;
  private final String unit;

  /**
   * Supported time units and their conversion factors to nanoseconds.
   */
  private static final class Unit {
    private final String name;
    private final long factor;

    Unit(String name, long factor) {
      this.name = name;
      this.factor = factor;
    }
  }

  private static final Unit[] UNITS = {
    new Unit("ns", 1L),
    new Unit("us", 1000L),
    new Unit("ms", 1000L * 1000L),
    new Unit("s", 1000L * 1000L * 1000L),
    new Unit("m", 1000L * 1000L * 1000L * 60L),
    new Unit("h", 1000L * 1000L * 1000L * 60L * 60L),
    new Unit("d", 1000L * 1000L * 1000L * 60L * 60L * 24L)
  };

  /**
   * Parses a time duration string (e.g., "150ms") and creates a TimeDuration object.
   *
   * @param value The time duration string to parse.
   */
  public TimeDuration(String value) {
    String normalizedValue = value.trim();
    
    // Find the unit position
    int unitPos = -1;
    for (int i = 0; i < normalizedValue.length(); i++) {
      if (!Character.isDigit(normalizedValue.charAt(i)) && normalizedValue.charAt(i) != '.' && 
          normalizedValue.charAt(i) != '-') {
        unitPos = i;
        break;
      }
    }

    if (unitPos == -1) {
      throw new IllegalArgumentException("Invalid time duration format: " + value);
    }

    // Parse the number part
    String numberPart = normalizedValue.substring(0, unitPos);
    double number = Double.parseDouble(numberPart);

    // Get the unit factor
    String unit = normalizedValue.substring(unitPos).trim();
    double factor = getUnitFactor(unit);

    // Calculate the total nanoseconds
    this.nanoseconds = (long) (number * factor);
    this.unit = unit;
  }

  @Override
  public Object value() {
    return nanoseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(nanoseconds);
  }

  private double getUnitFactor(String unit) {
    String normalizedUnit = unit.toLowerCase();
    
    // Find the matching unit
    for (Unit u : UNITS) {
      if (normalizedUnit.equals(u.name)) {
        return u.factor;
      }
        return 1000.0; // 1 microsecond in nanoseconds
      case "ns":
        return 1.0; // 1 nanosecond
      default:
        throw new IllegalArgumentException("Unsupported unit: " + unit);
    }
  }

  /**
   * Returns the value in nanoseconds.
   *
   * @return The value in nanoseconds.
   */
  public long getNanoseconds() {
    return nanoseconds;
  }

  /**
   * Returns the unit (e.g., "ms", "s").
   *
   * @return The unit string.
   */
  public String getUnit() {
    return unit;
  }

  @Override
  public Object value() {
    return nanoseconds;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(nanoseconds);
  }

  @Override
  public String toString() {
    return nanoseconds + "ns";
  }
}
