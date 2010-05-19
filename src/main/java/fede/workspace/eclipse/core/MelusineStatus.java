/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package fede.workspace.eclipse.core;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * The Class MelusineStatus.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class MelusineStatus extends Status implements IStatus {

	/**
	 * Instantiates a new melusine status.
	 * 
	 * @param severity
	 *            the severity
	 * @param pluginId
	 *            the plugin id
	 * @param code
	 *            the code
	 * @param message
	 *            the message
	 * @param exception
	 *            the exception
	 * @param objects
	 *            the objects
	 */
	public MelusineStatus(int severity, String pluginId, int code, String message, Throwable exception, Object...objects) {
		super(severity, pluginId, code, MessageFormat.format(message, objects), exception);
	}
	
	/**
	 * Instantiates a new melusine status.
	 * 
	 * @param severity
	 *            the severity
	 * @param pluginId
	 *            the plugin id
	 * @param message
	 *            the message
	 * @param exception
	 *            the exception
	 * @param objects
	 *            the objects
	 */
	public MelusineStatus(int severity, String pluginId, String message, Throwable exception, Object...objects) {
		super(severity, pluginId, 0, MessageFormat.format(message, objects), exception);
	}
}
