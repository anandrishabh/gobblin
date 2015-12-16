/*
 * Copyright (C) 2014-2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */

package gobblin.yarn.event;

import org.apache.hadoop.yarn.api.records.ApplicationReport;

import com.google.common.base.Preconditions;


/**
 * A type of events for the arrival of an {@link ApplicationReport} to be used with a
 * {@link com.google.common.eventbus.EventBus}.
 *
 * @author ynli
 */
public class ApplicationReportArrivalEvent {

  private final ApplicationReport applicationReport;

  public ApplicationReportArrivalEvent(ApplicationReport applicationReport) {
    Preconditions.checkNotNull(applicationReport);
    this.applicationReport = applicationReport;
  }

  /**
   * Get the {@link ApplicationReport} this event carries.
   *
   * @return the {@link ApplicationReport} this event carries
   */
  public ApplicationReport getApplicationReport() {
    return this.applicationReport;
  }
}