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

import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {

  @Test
  public void testParseValidValues() {
    // Test hours
    TimeDuration td1 = new TimeDuration("1h");
    Assert.assertEquals(3600000000000L, td1.getNanoseconds());

    TimeDuration td2 = new TimeDuration("2.5h");
    Assert.assertEquals(9000000000000L, td2.getNanoseconds());

    // Test minutes
    TimeDuration td3 = new TimeDuration("30m");
    Assert.assertEquals(1800000000000L, td3.getNanoseconds());

    // Test seconds
    TimeDuration td4 = new TimeDuration("45s");
    Assert.assertEquals(45000000000L, td4.getNanoseconds());

    // Test milliseconds
    TimeDuration td5 = new TimeDuration("100ms");
    Assert.assertEquals(100000000L, td5.getNanoseconds());

    // Test microseconds
    TimeDuration td6 = new TimeDuration("50us");
    Assert.assertEquals(50000L, td6.getNanoseconds());

    // Test nanoseconds
    TimeDuration td7 = new TimeDuration("10ns");
    Assert.assertEquals(10L, td7.getNanoseconds());

    // Test case insensitivity
    TimeDuration td8 = new TimeDuration("1H");
    Assert.assertEquals(3600000000000L, td8.getNanoseconds());

    // Test decimal values with minutes
    TimeDuration td9 = new TimeDuration("1.5m");
    Assert.assertEquals(90000000000L, td9.getNanoseconds());

    // Test decimal values with seconds
    TimeDuration td10 = new TimeDuration("0.5s");
    Assert.assertEquals(500000000L, td10.getNanoseconds());

    // Test case insensitivity
    TimeDuration td11 = new TimeDuration("150MS");
    Assert.assertEquals(150_000_000, td11.getNanoseconds());

    // Test negative durations
    TimeDuration td12 = new TimeDuration("-1h");
    Assert.assertEquals(-3600000000000L, td12.getNanoseconds());

    // Test negative milliseconds
    TimeDuration td13 = new TimeDuration("-150ms");
    Assert.assertEquals(-150_000_000, td13.getNanoseconds());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidUnit() {
    new TimeDuration("150xs");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidFormat() {
    new TimeDuration("150ms 2s");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidNumber() {
    new TimeDuration("abcms");
  }

  @Test
  public void testToJson() {
    TimeDuration td = new TimeDuration("150ms");
    Assert.assertEquals("150000000", td.toJson().getAsString());
  }
}
