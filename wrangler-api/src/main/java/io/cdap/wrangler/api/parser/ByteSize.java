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
 * Represents a byte size value with unit (e.g., "10KB", "5MB").
 */
@PublicEvolving
public class ByteSize implements Token {
  private final long bytes;
  private final String unit;

  /**
   * Supported byte size units and their conversion factors to bytes.
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
    new Unit("B", 1L),
    new Unit("KiB", 1024L),
    new Unit("MiB", 1024L * 1024L),
    new Unit("GiB", 1024L * 1024L * 1024L),
    new Unit("TiB", 1024L * 1024L * 1024L * 1024L)
  };

  /**
   * Parses a byte size string (e.g., "10KB") and creates a ByteSize object.
   *
   * @param value The byte size string to parse.
   */
  public ByteSize(String value) {
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
      throw new IllegalArgumentException("Invalid byte size format: " + value);
    }

    // Parse the number part
    String numberPart = normalizedValue.substring(0, unitPos);
    double number = Double.parseDouble(numberPart);

    // Get the unit factor
    String unit = normalizedValue.substring(unitPos).trim();
    long factor = getUnitFactor(unit);

    // Calculate the total bytes
    this.bytes = (long) (number * factor);
    this.unit = unit;
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(bytes);
  }

  private long getUnitFactor(String unit) {
    String normalizedUnit = unit.toUpperCase();
    
    // Find the matching unit
      }
    } else {
      // Check decimal units (KB, MB, etc.)
      if (normalizedUnit.equals("K")) {
        return 1000;
      } else if (normalizedUnit.equals("M")) {
        return 1000 * 1000;
      } else if (normalizedUnit.equals("G")) {
        return 1000 * 1000 * 1000;
      } else if (normalizedUnit.equals("T")) {
        return 1000L * 1000 * 1000 * 1000;
      } else if (normalizedUnit.equals("B")) {
        return 1;
      } else {
        throw new IllegalArgumentException("Unknown unit: " + unit);
      }
    }
  }

  /**
   * Returns the value in bytes.
   *
   * @return The value in bytes.
   */
  public long getBytes() {
    return bytes;
  }

  /**
   * Returns the unit (e.g., "KB", "MB").
   *
   * @return The unit string.
   */
  public String getUnit() {
    return unit;
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    return new JsonPrimitive(bytes);
  }

  @Override
  public String toString() {
    return bytes + "B";
  }
}
