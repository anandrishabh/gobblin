/*
 * Copyright (C) 2014-2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

package gobblin.hive;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Order;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import gobblin.annotation.Alpha;
import gobblin.configuration.State;


/**
 * A class for managing the {@link StorageDescriptor} for Hive tables and partitions.
 *
 * @author ziliu
 */
@Alpha
public class HiveStorageDescriptorManager {

  public static final String HIVE_ROW_FORMAT = "hive.row.format";
  public static final String HIVE_INPUT_FORMAT = "hive.input.format";
  public static final String HIVE_OUTPUT_FORMAT = "hive.output.format";

  protected final HiveRegProps props;
  protected final HiveSerDeWrapper serDeWrapper;
  protected final String rowFormat;
  protected final String tableName;

  public HiveStorageDescriptorManager(State props, String tableName) {
    Preconditions.checkArgument(props.contains(HIVE_ROW_FORMAT), "Missing required property " + HIVE_ROW_FORMAT);

    this.props = new HiveRegProps(props);
    this.rowFormat = this.props.getProp(HIVE_ROW_FORMAT);
    this.serDeWrapper = HiveSerDeWrapper.get(this.rowFormat);
    this.tableName = tableName;
  }

  /**
   * Create a {@link StorageDescriptor} for the given {@link Path}.
   */
  public StorageDescriptor getStorageDescriptor(Path path) throws IOException {
    StorageDescriptor sd = new StorageDescriptor();
    sd.setCols(Lists.<FieldSchema> newArrayList());
    sd.setSortCols(Lists.<Order> newArrayList());
    sd.setNumBuckets(-1);
    sd.setInputFormat(this.serDeWrapper.getInputFormatClassName());
    sd.setOutputFormat(this.serDeWrapper.getOutputFormatClassName());
    sd.setLocation(path.toString());

    SerDeInfo si = new SerDeInfo();
    si.setName(this.tableName);
    si.setParameters(Maps.<String, String> newHashMap());
    setParameters(si);
    si.setSerializationLib(this.serDeWrapper.getSerDe().getClass().getName());
    HiveSchemaManager.getInstance(this.rowFormat, this.props).addSchemaProperties(si, path);

    sd.setSerdeInfo(si);
    return sd;
  }

  /**
   * Create a {@link StorageDescriptor} for the given {@link Path} using the given location.
   */
  public StorageDescriptor getStorageDescriptor(Path path, String location) throws IOException {
    StorageDescriptor sd = getStorageDescriptor(path);
    sd.setLocation(location);
    return sd;
  }

  private void setParameters(SerDeInfo si) {
    State tableProps = this.props.getTableProps();
    for (String tablePropName : tableProps.getPropertyNames()) {
      si.putToParameters(tablePropName, tableProps.getProp(tablePropName));
    }
  }

}
