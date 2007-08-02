/**
 * Copyright 2007 The Apache Software Foundation
 *
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
package org.apache.hadoop.hbase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test HClient.
 */
@Deprecated
public class TestHClient extends HBaseClusterTestCase {
  private Log LOG = LogFactory.getLog(this.getClass().getName());
  private HClient client;

  /** {@inheritDoc} */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    this.client = new HClient(this.conf);
  }
  
  /** the test
   * @throws Exception
   */
  public void testCommandline() throws Exception {
    final String m =  "--master=" + this.conf.get(HConstants.MASTER_ADDRESS);
    LOG.info("Creating table");
    // Assert each of below returns 0: i.e. success.
    assertEquals("create table", 0,
      this.client.doCommandLine(
        new String [] {m, "createTable", getName(), "family:", "1"}));
    assertEquals("list tables", 0,
      this.client.doCommandLine(new String [] {m, "listTables"}));
    assertEquals("delete table", 0,
      this.client.doCommandLine(new String [] {m, "deleteTable", getName()}));
  }
}