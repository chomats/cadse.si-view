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

package fede.workspace.tool.eclipse;

import java.net.URI;

import org.eclipse.core.internal.resources.Container;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import fr.imag.adele.cadse.core.Item;


/**
 * The Class ItemResource.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class ItemResource extends Container  {

	/** The item. */
	private Item	 item;
	
	/** The parent. */
	private IContainer parent;
		
	/**
	 * Instantiates a new item resource.
	 * 
	 * @param parent
	 *            the parent
	 * @param item
	 *            the item
	 * @param path
	 *            the path
	 * @param workspace
	 *            the workspace
	 */
	public ItemResource(IContainer parent, Item item, IPath path, Workspace workspace) {
		super(path,workspace);
		this.item = item;
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#accept(org.eclipse.core.resources.IResourceVisitor)
	 */
	@Override
	public void accept(IResourceVisitor visitor) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
	 */
	@Override
	public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
	 */
	@Override
	public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
	 */
	@Override
	public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#exists()
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	@Override
	public boolean contains(ISchedulingRule rule) {
		if (rule instanceof ItemResource) {
			return this.item.containsPartChild(((ItemResource)rule).item);
		}
		return false;
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#createMarker(java.lang.String)
	 */
	@Override
	public IMarker createMarker(String type) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#createProxy()
	 */
	@Override
	public IResourceProxy createProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#deleteMarkers(java.lang.String, boolean, int)
	 */
	@Override
	public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#findMarkers(java.lang.String, boolean, int)
	 */
	@Override
	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getFileExtension()
	 */
	@Override
	public String getFileExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getFullPath()
	 */
	@Override
	public IPath getFullPath() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getLocalTimeStamp()
	 */
	@Override
	public long getLocalTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getLocation()
	 */
	@Override
	public IPath getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getLocationURI()
	 */
	@Override
	public URI getLocationURI() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getMarker(long)
	 */
	@Override
	public IMarker getMarker(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getModificationStamp()
	 */
	@Override
	public long getModificationStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getName()
	 */
	@Override
	public String getName() {
		return item.getId().toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getParent()
	 */
	@Override
	public IContainer getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
	 */
	@Override
	public String getPersistentProperty(QualifiedName key) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getProject()
	 */
	@Override
	public IProject getProject() {
		return parent.getProject();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getProjectRelativePath()
	 */
	@Override
	public IPath getProjectRelativePath() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getRawLocation()
	 */
	@Override
	public IPath getRawLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getRawLocationURI()
	 */
	@Override
	public URI getRawLocationURI() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getResourceAttributes()
	 */
	@Override
	public ResourceAttributes getResourceAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
	 */
	@Override
	public Object getSessionProperty(QualifiedName key) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getType()
	 */
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#getWorkspace()
	 */
	@Override
	public IWorkspace getWorkspace() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isAccessible()
	 */
	@Override
	public boolean isAccessible() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isDerived()
	 */
	@Override
	public boolean isDerived() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isLinked()
	 */
	@Override
	public boolean isLinked() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isLinked(int)
	 */
	@Override
	public boolean isLinked(int options) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isLocal(int)
	 */
	@Override
	public boolean isLocal(int depth) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isPhantom()
	 */
	@Override
	public boolean isPhantom() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isSynchronized(int)
	 */
	@Override
	public boolean isSynchronized(int depth) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isTeamPrivateMember()
	 */
	@Override
	public boolean isTeamPrivateMember() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#revertModificationStamp(long)
	 */
	@Override
	public void revertModificationStamp(long value) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setDerived(boolean)
	 */
	@Override
	public void setDerived(boolean isDerived) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setLocalTimeStamp(long)
	 */
	@Override
	public long setLocalTimeStamp(long value) throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
	 */
	@Override
	public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setReadOnly(boolean)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void setReadOnly(boolean readOnly) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setResourceAttributes(org.eclipse.core.resources.ResourceAttributes)
	 */
	@Override
	public void setResourceAttributes(ResourceAttributes attributes) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
	 */
	@Override
	public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#setTeamPrivateMember(boolean)
	 */
	@Override
	public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#touch(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void touch(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	@Override
	public boolean isConflicting(ISchedulingRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#requestName()
	 */
	@Override
	public String requestName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.resources.Resource#requestPath()
	 */
	@Override
	public IPath requestPath() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IContainer#getDefaultCharset(boolean)
	 */
	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		return null;
	}

	

	

}
