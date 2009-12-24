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
package fede.workspace.tool.view.menu;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.progress.ProgressMonitorJobsDialog;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.ui.MenuAction;

public abstract class ProgressMenuAction extends MenuAction {
	protected static final class WorkspaceRunnable implements IWorkspaceRunnable {
		private final IItemNode[]			selection;
		private final ProgressMenuAction	action;

		private WorkspaceRunnable(ProgressMenuAction action, IItemNode[] selection) {
			this.selection = selection;
			this.action = action;
		}

		public void run(IProgressMonitor monitor) throws CoreException {
			action.run(selection, monitor);
		}
	}

	final String	operationName;

	protected ProgressMenuAction(String operationName) {
		super();
		this.operationName = operationName;
	}

	@Override
	final public void run(final IItemNode[] selection) throws CadseException {
		try {
			new ProgressMonitorJobsDialog(getShell()).run(true, true, createRunnable(operationName, selection));
		} catch (InvocationTargetException e) {
			displayError("Error " + operationName, NLS.bind("{1}", e.getTargetException().getMessage()), e
					.getTargetException());
		} catch (InterruptedException e) {
			// ignored
		}
	}

	protected IRunnableWithProgress createRunnable(final String operationName, IItemNode[] selection) {
		final IWorkspaceRunnable action = new WorkspaceRunnable(this, selection);
		IRunnableWithProgress op = new LogicalWorkspaceOperation(operationName, action);
		return op;
	}

	public Shell getShell() {
		return ((IShellProvider) getViewDescription().getWindowProvider()).getShell();
	}

	/**
	 * Opens an error dialog to display the given message.
	 * <p>
	 * Note that this method must be called from UI thread.
	 * </p>
	 * 
	 * @param message
	 *            the message
	 * @param error
	 */
	public void displayError(String title, String message, Throwable error) {
		MessageDialog.openError(getShell(), title, message);
		error.printStackTrace();
	}

	public abstract void run(final IItemNode[] selection, IProgressMonitor monitor) throws CoreException;
}
