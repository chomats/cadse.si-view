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
package fede.workspace.model.manager.properties.impl.ic;

import java.net.MalformedURLException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import fr.imag.adele.cadse.core.Item;
import fede.workspace.eclipse.MelusineProjectManager;

/**
 * Interaction Controller for select an icon.
 * The dialog shows the icons image. The icon is a file .gif or .png.
 * The default title and message is 'Select an icon';
 * @author &lt;a href=&quot;mailto:stephane.chomat@imag.fr&quot;&gt;Stephane Chomat&lt;/a&gt;
 * @version 2.0
 */
public class IC_IconResourceForBrowser_Combo_List extends IC_FileResourceForBrowser_Combo_List {
	static class ImageFilter extends ViewerFilter {

		
		
		private ImageLoader imageloader;

		/**
		 * @param patternString Excluded files will not pass the filter.
		 * <code>null</code> is allowed if no files should be excluded. 
		 * 
		 */
		
		public ImageFilter() {
			imageloader = new ImageLoader();
		}
		
		/*
		 * @see ViewerFilter#select
		 */
		@Override
		public boolean select(Viewer viewer, Object parent, Object element) {
			if (element instanceof IResource) {
				IResource f = (IResource) element;
				if (f.isDerived())
					return false;
				if (f.isTeamPrivateMember()) {
					return false;
				}
			}
			if (element instanceof IFile) {
				try {
					imageloader.load(((IFile)element).getLocation().toOSString());
					return true;
				} catch (SWTException e) {
					return false;
				} catch (Throwable e) {
					return false;
				}
			} 
			if (element instanceof IContainer) {
				IContainer c  = (IContainer) element;
				try {
					IResource[] m = c.members();
					if (m != null)
						for (IResource e : m) {
							if (select(viewer,c,e))
								return true;
						}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				return false;
			}
			
			return true;
		}
		
	}

	public IC_IconResourceForBrowser_Combo_List(String title, String message, String pattern) {
		super(title, message, 0, pattern, false);
	}
	
	/**
	 * Default contructor. 
	 * The default title and message is 'Select an icon' and
	 * The icon is a file '*.gif' or '*.png'. 
	 *
	 */
	public IC_IconResourceForBrowser_Combo_List() {
		super("Select an icon", "Select an icon", 0, ".*\\.(gif|png|ico|jpg)", false);
	}
	
	@Override
	protected ViewerFilter getFilter() {
		return new ImageFilter();
	}

	@Override
	protected IResource getRootSelect() {
		//Item manager = (Item) description.getContext();
	//	Item model = ManagerManager._getWorkspaceModel(manager);
		return MelusineProjectManager.getProject((Item) getUIField().getContext());
	}
	
	

	@Override
	public ILabelProvider getLabelProvider() {
		return new WorkbenchLabelProvider() {
			@Override
			protected ImageDescriptor decorateImage(ImageDescriptor input, Object element) {
				if (element instanceof IFile) {
					IFile f = (IFile) element;
					try {
						return ImageDescriptor.createFromURL(f.getLocationURI().toURL());
					} catch (MalformedURLException e) {
					}
				}
				
				return super.decorateImage(input, element);
			}
		};
	}
}