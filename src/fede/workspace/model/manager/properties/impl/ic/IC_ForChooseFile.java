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

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import fr.imag.adele.cadse.core.ItemType;

public class IC_ForChooseFile extends IC_Abstract {
	
	public static final int FOLDER_EXT = 1;
	public static final int FILE_EXT = 2;
	public static final int WORKSPACE = 4;
	
	public IPath selectExternalFolder(Shell shell, String msg, IPath currPath) {
		
		File file = getFileFrom(currPath);
		
		DirectoryDialog dialog= new DirectoryDialog(shell);
		dialog.setMessage(msg);
		//dialog.setText(this.choosetext); 
		if (file != null)
			dialog.setFilterPath(file.getAbsolutePath());
		String res= dialog.open();
		if (res != null) {
			return Path.fromOSString(res).makeAbsolute();
		}
		return null;
	}
	
	public IPath selectExternalFile(Shell shell, String msg, IPath currPath) {
			
		File file = getFileFrom(currPath);
		
		FileDialog dialog= new FileDialog(shell);
		dialog.setText(msg); 
		String[] fileFilter = getFileFilter();
		if (fileFilter != null)
			dialog.setFilterExtensions(fileFilter); //$NON-NLS-1$
		if (file != null)
			dialog.setFilterPath(file.getAbsolutePath());
		String res= dialog.open();
		if (res != null) {
			return Path.fromOSString(res).makeAbsolute();
		}
		return null;
	}

	private File getFileFrom(IPath currPath) {
		if (currPath == null) return null;
		File file= currPath.toFile();
		IResource r= ResourcesPlugin.getWorkspace().getRoot().findMember(currPath);
		if (r != null && r.getLocation() != null) {
			file= r.getLocation().toFile();
		}
		if (file != null && file.isFile())
			file = file.getParentFile();
		return file;
	}
	
	/*
	 * Opens a dialog to choose an internal jar.
	 */	
	public IPath selectWorkspaceFile(Shell shell, String msg, IPath currPath) {
		//String initSelection= textWidget.getText();
		
		ViewerFilter filter= getViewerFilter();

		ILabelProvider lp= new WorkbenchLabelProvider();
		ITreeContentProvider cp= new WorkbenchContentProvider();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource initSel= root.findMember(currPath);
		
		ElementTreeSelectionDialog dialog= createTreeSelectionDialog(shell, lp, cp);
		dialog.setAllowMultiple(false);
		if (filter != null)
			dialog.addFilter(filter);
		dialog.setTitle(msg); 
	//	dialog.setMessage(NewWizardMessages.SourceAttachmentBlock_intjardialog_message); 
		dialog.setInput(root);
		dialog.setValidator(getValidator());
		dialog.setInitialSelection(initSel);
		if (dialog.open() == Window.OK) {
			IResource res= (IResource) dialog.getFirstResult();
			return res.getFullPath();
		}
		return null;
	}

	protected ISelectionStatusValidator getValidator() {
		return null;
	}

	protected ElementTreeSelectionDialog createTreeSelectionDialog(Shell shell, ILabelProvider lp, ITreeContentProvider cp) {
		return new ElementTreeSelectionDialog(shell, lp, cp);
	}

	protected ViewerFilter getViewerFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String[] getFileFilter() {
		return null; //new String[] {"*.jar;*.zip"};
	}
	
	public int getKind() {
		return FILE_EXT +FOLDER_EXT+WORKSPACE;
	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
