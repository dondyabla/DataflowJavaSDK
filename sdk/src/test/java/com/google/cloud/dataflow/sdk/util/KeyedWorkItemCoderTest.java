/*
 * Copyright (C) 2016 Google Inc.
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
package com.google.cloud.dataflow.sdk.util;

import com.google.cloud.dataflow.sdk.coders.StringUtf8Coder;
import com.google.cloud.dataflow.sdk.coders.VarIntCoder;
import com.google.cloud.dataflow.sdk.testing.CoderProperties;
import com.google.cloud.dataflow.sdk.transforms.windowing.GlobalWindow;
import com.google.cloud.dataflow.sdk.util.TimerInternals.TimerData;
import com.google.cloud.dataflow.sdk.util.state.StateNamespaces;
import com.google.common.collect.ImmutableList;

import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link KeyedWorkItems}.
 */
@RunWith(JUnit4.class)
public class KeyedWorkItemCoderTest {
  @Test
  public void testCoderProperties() throws Exception {
    CoderProperties.coderSerializable(
        KeyedWorkItemCoder.of(StringUtf8Coder.of(), VarIntCoder.of(), GlobalWindow.Coder.INSTANCE));
  }

  @Test
  public void testEncodeDecodeEqual() throws Exception {
    Iterable<TimerData> timers =
        ImmutableList.<TimerData>of(
            TimerData.of(StateNamespaces.global(), new Instant(500L), TimeDomain.EVENT_TIME));
    Iterable<WindowedValue<Integer>> elements =
        ImmutableList.of(
            WindowedValue.valueInGlobalWindow(1),
            WindowedValue.valueInGlobalWindow(4),
            WindowedValue.valueInGlobalWindow(8));

    KeyedWorkItemCoder<String, Integer> coder =
        KeyedWorkItemCoder.of(StringUtf8Coder.of(), VarIntCoder.of(), GlobalWindow.Coder.INSTANCE);

    CoderProperties.coderDecodeEncodeEqual(coder, KeyedWorkItems.workItem("foo", timers, elements));
    CoderProperties.coderDecodeEncodeEqual(coder, KeyedWorkItems.elementsWorkItem("foo", elements));
    CoderProperties.coderDecodeEncodeEqual(
        coder, KeyedWorkItems.<String, Integer>timersWorkItem("foo", timers));
  }
}
