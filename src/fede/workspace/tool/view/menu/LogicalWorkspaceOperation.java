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
/**
 * 
 */
package fede.workspace.tool.view.menu;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableWithProgress;

import fr.imag.adele.cadse.core.impl.CadseCore;

public final class LogicalWorkspaceOperation implements IRunnableWithProgress {
	/**
	 * 
	 */
	private final String				operationName;
	private final IWorkspaceRunnable	action;

	public LogicalWorkspaceOperation(String operationName, IWorkspaceRunnable action) {
		this.operationName = operationName;
		this.action = action;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			CadseCore.getCadseDomain().beginOperation(operationName);
			ResourcesPlugin.getWorkspace().run(action, monitor);
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} catch (OperationCanceledException e) {
			throw new InterruptedException(e.getMessage());
		} finally {
			CadseCore.getCadseDomain().endOperation();
		}
	}
}