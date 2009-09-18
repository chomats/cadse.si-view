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
package fede.workspace.model.manager.properties.impl.ic;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import fede.workspace.model.manager.properties.IInteractionControllerForList;
import fr.imag.adele.cadse.core.CompactUUID;

/** */
// attribut
// - string select-title;
public abstract class IC_AbstractForList extends IC_Abstract implements IInteractionControllerForList {

	protected String	title;
	protected String	message;

	public IC_AbstractForList(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public IC_AbstractForList(CompactUUID id) {
		super(id);
	}

	public String canAddObject(Object[] object) {
		return null;
	}

	public Object[] transAndAddObject(Object[] object) {
		return object;
	}

	public String canRemoveObject(Object[] object) {
		return null;
	}

	public Object[] removeObject(Object[] object) {
		return object;
	}

	protected abstract Object[] getValues();

	public Object[] selectOrCreateValues(Shell parentShell) {
		ListSelectionDialog lsd = new ListSelectionDialog(parentShell, getValues(), new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				}
				return null;
			}

			public void dispose() {
			}

			public void inputChanged(@SuppressWarnings("unused")
			Viewer viewer, @SuppressWarnings("unused")
			Object oldInput, @SuppressWarnings("unused")
			Object newInput) {
			}
		}, getLabelProvider(), message);
		if (title != null) {
			lsd.setTitle(title);
		}
		lsd.open();
		if (lsd.getReturnCode() == Window.OK) {
			return lsd.getResult(); // after call canAddObject and
			// transAndAddObject
		}
		return null;
	}

	public IContentProvider getContentProvider() {
		return new ObjectArrayContentProvider();
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public boolean moveDown(Object[] object) {
		return false;
	}

	public boolean moveUp(Object[] object) {
		return false;
	}

	public Object edit(Shell shell, Object value, int index) {
		return null;
	}
}