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

public class ByteSizeTest {

  @Test
  public void testParseValidValues() {
    // Test binary units
    ByteSize bs1 = new ByteSize("10KiB");
    Assert.assertEquals(10 * 1024, bs1.getBytes());

    ByteSize bs2 = new ByteSize("5MiB");
    Assert.assertEquals(5 * 1024 * 1024, bs2.getBytes());

    ByteSize bs3 = new ByteSize("2GiB");
    Assert.assertEquals(2 * 1024 * 1024 * 1024, bs3.getBytes());

    ByteSize bs4 = new ByteSize("1.5TiB");
    Assert.assertEquals(1.5 * 1024 * 1024 * 1024 * 1024, bs4.getBytes(), 0.001);

    // Test case insensitivity
    ByteSize bs5 = new ByteSize("10kiB");
    Assert.assertEquals(10 * 1024, bs5.getBytes());

    // Test negative values
    ByteSize bs6 = new ByteSize("-10KiB");
    Assert.assertEquals(-10 * 1024, bs6.getBytes());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidUnit() {
    new ByteSize("10XB");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidFormat() {
    new ByteSize("10KB 5MB");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseInvalidNumber() {
    new ByteSize("abcKB");
  }

  @Test
  public void testToJson() {
    ByteSize bs = new ByteSize("10KB");
    Assert.assertEquals("10240", bs.toJson().getAsString());
  }
}
