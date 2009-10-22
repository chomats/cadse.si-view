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

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import fede.workspace.tool.view.WSPlugin;

/**
 * Attributes :
 * 		string link-type;
 * 		string title-select ;
 */


public class IC_FileResourceForBrowser_Combo_List  extends IC_ResourceTreeDialogForBrowser_Combo_List {
	 
    
	private String pattern;
	boolean selectfolder = false;
	

	public IC_FileResourceForBrowser_Combo_List(String title, String message, int selectRoot, String pattern, boolean selectFolder) {
		super(title, message, selectRoot);
		this.pattern = pattern;
		this.selectfolder = selectFolder;
	}
	
	
	@Override
	protected ViewerFilter getFilter() {
		return new FileFilter(pattern);
	}
	
	@Override
	public IStatus validate(Object[] selection) {
		if (selection == null || selection.length != 1)
			return new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, "Select one object only", null);
		Object o = selection[0];
		if (o instanceof IFile) {
			return Status.OK_STATUS;
		}
		if (selectfolder && o instanceof IFolder) {
			return Status.OK_STATUS;
		}
		return new Status(IStatus.ERROR, WSPlugin.PLUGIN_ID, 0, "Select an icon file", null);
	}
	
	static class FileFilter extends ViewerFilter {

		
		private Pattern fIncludes;

		/**
		 * @param patternString Excluded files will not pass the filter.
		 * <code>null</code> is allowed if no files should be excluded. 
		 * 
		 */
		
		public FileFilter(String patternString) {
			if (patternString != null) {
				fIncludes = Pattern.compile(patternString);
			}
		}
		
		/*
		 * @see ViewerFilter#select
		 */
		@Override
		public boolean select(Viewer viewer, Object parent, Object element) {
			if (element instanceof IFile) {
				if (fIncludes == null || fIncludes.matcher(((IFile)element).getName()).matches()) {
					return true;
				}
				return false;
			} 
			return true;
		}
		
	}

}
