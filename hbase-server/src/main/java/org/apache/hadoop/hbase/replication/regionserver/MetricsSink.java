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
 * limitations under the License.
 */
package org.apache.hadoop.hbase.replication.regionserver;

import org.apache.hadoop.hbase.CompatibilitySingletonFactory;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.yetus.audience.InterfaceAudience;

/**
 * This class is for maintaining the various replication statistics for a sink and publishing them
 * through the metrics interfaces.
 */
@InterfaceAudience.Private
public class MetricsSink {

  private long lastTimestampForAge = EnvironmentEdgeManager.currentTime();
  private long startTimestamp = EnvironmentEdgeManager.currentTime();
  private final MetricsReplicationSinkSource mss;

  public MetricsSink() {
    mss =
      CompatibilitySingletonFactory.getInstance(MetricsReplicationSourceFactory.class).getSink();
  }

  /**
   * Set the age of the last applied operation
   * @param timestamp The timestamp of the last operation applied.
   * @return the age that was set
   */
  public long setAgeOfLastAppliedOp(long timestamp) {
    long age = 0;
    if (lastTimestampForAge != timestamp) {
      lastTimestampForAge = timestamp;
      age = EnvironmentEdgeManager.currentTime() - lastTimestampForAge;
    }
    mss.setLastAppliedOpAge(age);
    return age;
  }

  /**
   * Refreshing the age makes sure the value returned is the actual one and not the one set a
   * replication time
   * @return refreshed age
   */
  public long refreshAgeOfLastAppliedOp() {
    return setAgeOfLastAppliedOp(lastTimestampForAge);
  }

  /**
   * Convience method to change metrics when a batch of operations are applied. n
   */
  public void applyBatch(long batchSize) {
    mss.incrAppliedBatches(1);
    mss.incrAppliedOps(batchSize);
  }

  /**
   * Convience method to change metrics when a batch of operations are applied.
   * @param batchSize total number of mutations that are applied/replicated
   * @param hfileSize total number of hfiles that are applied/replicated
   */
  public void applyBatch(long batchSize, long hfileSize) {
    applyBatch(batchSize);
    mss.incrAppliedHFiles(hfileSize);
  }

  /**
   * Convenience method to update metrics when batch of operations has failed.
   */
  public void incrementFailedBatches() {
    mss.incrFailedBatches();
  }

  /**
   * Get the count of the failed bathes n
   */
  protected long getFailedBatches() {
    return mss.getFailedBatches();
  }

  /**
   * Get the Age of Last Applied Op n
   */
  public long getAgeOfLastAppliedOp() {
    return mss.getLastAppliedOpAge();
  }

  /**
   * Get the TimestampOfLastAppliedOp. If no replication Op applied yet, the value is the timestamp
   * at which hbase instance starts
   * @return timeStampsOfLastAppliedOp;
   */
  public long getTimestampOfLastAppliedOp() {
    return this.lastTimestampForAge;
  }

  /**
   * Gets the time stamp from when the Sink was initialized. n
   */
  public long getStartTimestamp() {
    return this.startTimestamp;
  }

  /**
   * Gets the total number of OPs delivered to this sink. n
   */
  public long getAppliedOps() {
    return this.mss.getSinkAppliedOps();
  }

}
