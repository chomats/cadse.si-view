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
package fede.workspace.tool.view.addlink;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import fr.imag.adele.cadse.core.ILinkTypeManager;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.LinkType;
import fr.imag.adele.cadse.core.oper.WSOCreateLink;
import fede.workspace.tool.view.WSPlugin;
import fr.imag.adele.cadse.eclipse.view.IViewLinkManager;
import fr.imag.adele.fede.workspace.as.test.TestException;
import fr.imag.adele.fede.workspace.si.view.View;

public class ImportItemOrCreateLinkStatusDialog extends StatusDialog {
	private final class ValidatorThread extends Thread {
		public ValidatorThread() {
			super("ValidatorThread");
		}

		@Override
		public void run() {
			while (validatornotClose) {
				try {
					IStatus currentstate = getStatus();
					final IStatus newstate = computeStatus();
					if (newstate != currentstate) {
						getShell().getDisplay().syncExec(new UpdateStatusOperation(newstate));
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private final class UpdateStatusOperation implements Runnable {
		private final IStatus	newstate;

		private UpdateStatusOperation(IStatus newstate) {
			this.newstate = newstate;
		}

		public void run() {
			updateStatus(newstate);
		}
	}

	private static final Status	STATUS_ERROR_2		= new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0,
															"Select an item for the destination.", null);
	private static final Status	STATUS_ERROR_1		= new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0,
															"Select a link type or an item type.", null);
	LinkSelect					inner;
	private Thread				validator;
	private boolean				validatornotClose	= true;
	private IStatus				lasterror;
	private IViewLinkManager	viewLinkManager;

	public ImportItemOrCreateLinkStatusDialog(Shell parent, IViewLinkManager viewLinkManager) {
		super(parent);
		this.viewLinkManager = viewLinkManager;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		inner = new LinkSelect(composite, SWT.NONE);
		inner.setFont(composite.getFont());

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		inner.setLayout(layout);
		inner.setLayoutData(new GridData(GridData.FILL_BOTH));
		inner.setLinkViewManager(viewLinkManager);
		applyDialogFont(composite);
		return composite;
	}

	private IStatus computeStatus() {
		LinkType selectedLinkType = inner.getSelectedLinkType();
		if (selectedLinkType == null) {
			return STATUS_ERROR_1;
		}

		Item selectedItemDestination = inner.getSelectedDest();
		if (selectedItemDestination == null) {
			return STATUS_ERROR_2;
		}

		if (!selectedItemDestination.isInstanceOf(selectedLinkType.getDestination())) {
			return STATUS_ERROR_2;
		}

		ILinkTypeManager manager = selectedLinkType.getManager();

		String error = manager.canCreateLink(inner.getItemParent(), selectedItemDestination, selectedLinkType);
		if (error != null) {
			if (lasterror == null || !lasterror.getMessage().equals(error)) {
				lasterror = new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, error, null);
			}

			return lasterror;
		}

		return Status.OK_STATUS;
	}

	@Override
	public void create() {
		super.create();
		this.getShell().setSize(new Point(800, 400));
		this.getShell().layout();
		validator = new ValidatorThread();
		validator.start();
	}

	public void setItemParent(Item itemParent) {
		inner.setItemParent(itemParent);
	}

	@Override
	public boolean close() {
		boolean ret = super.close();
		validatornotClose = false;
		return ret;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
		Item parent = inner.getItemParent();
		Item dest = inner.getSelectedDest();
		LinkType lt = inner.getSelectedLinkType();
		WSOCreateLink createLink = new WSOCreateLink(parent, lt, dest);
		createLink.execute();
		if (createLink.getEx() != null) {
			WSPlugin.log(new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, MessageFormat.format(
					"Cannot create a link from {0} to {1} with link type {2}.", parent.getDisplayName(), dest
							.getDisplayName(), lt.getName()), createLink.getEx()));

		}
		try {
			View.getInstance().getTestService().registerIfNeed(createLink);
		} catch (TestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
