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
package fede.workspace.tool.view.actions;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.Link;
import fr.imag.adele.fede.workspace.si.view.View;

@Deprecated
public class LoadJob extends Job {
	private Link	link;

	LoadJob(Link l) {
		super("Load " + l);
		this.link = l;
		setUser(true);
		setRule(ResourcesPlugin.getWorkspace().getRoot());
		setPriority(INTERACTIVE);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			View.setDefaultMonitor(monitor);
			link.restore();
			View.unsetDefaultMonitor();

			return Status.OK_STATUS;
		} catch (CadseException e) {
			return new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0, "Cannot import the item "
					+ link.getDestinationName(), e);
		} catch (Throwable e) {
			return new Status(Status.ERROR, WSPlugin.PLUGIN_ID, 0, "Cannot import the item "
					+ link.getDestinationName(), e);
		}
	}

}
