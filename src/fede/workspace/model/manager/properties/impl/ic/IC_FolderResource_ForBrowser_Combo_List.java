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

import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Attributes :
 * 		string link-type;
 * 		string title-select ;
 */


public class IC_FolderResource_ForBrowser_Combo_List  extends IC_ResourceTreeDialogForBrowser_Combo_List {
	 
    
	
	
	public IC_FolderResource_ForBrowser_Combo_List(String title, String message, int selectRoot) {
		super(title, message, selectRoot);
	}

	@Override
	public IStatus validate(Object[] selection) {
		return Status.OK_STATUS;
	}
	
	@Override
	protected ViewerFilter getFilter() {
		return new FolderFileFilter(null);
	}
	
	static class FolderFileFilter extends ViewerFilter {

		private List<IFolder> fExcludes;
		
		/**
		 * @param excludedFiles Excluded files will not pass the filter.
		 * <code>null</code> is allowed if no files should be excluded. 
		 * 
		 */
		
		public FolderFileFilter(List<IFolder> excludedFiles) {
			fExcludes= excludedFiles;
		}
		
		/*
		 * @see ViewerFilter#select
		 */
		@Override
		public boolean select(Viewer viewer, Object parent, Object element) {
			if (element instanceof IFolder) {
				if (fExcludes != null && fExcludes.contains(element)) {
					return false;
				}
				return true;
			} 
			return false;
		}
		
		
		
		
				
	}

}
