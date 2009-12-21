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
package fede.workspace.tool.view.readonly;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.team.FileModificationValidationContext;
import org.eclipse.core.resources.team.FileModificationValidator;
import org.eclipse.core.resources.team.IMoveDeleteHook;
import org.eclipse.core.resources.team.IResourceTree;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.CadseDomain;
import fede.workspace.tool.view.WSPlugin;

public class NotifyContent extends FileModificationValidator implements IMoveDeleteHook {

    public NotifyContent() {
        
    }
    
    CadseDomain wd;
    
    @Override
	public IStatus validateEdit(IFile[] files, FileModificationValidationContext context) {
        IFile[] filesReadOnly = getReadOnly(files);
        if (filesReadOnly.length == 0)
            return Status.OK_STATUS;
        return new Status(IStatus.ERROR,WSPlugin.PLUGIN_ID,0,"Somes files is readonly.",null);
    }
    
    private IFile[] getReadOnly(IFile[] files) {
        List<IFile> result = new ArrayList<IFile>(files.length);
        for (int i = 0; i < files.length; i++) {
            IFile file = files[i];
            if (isReadOnly(file)) {
                result.add(file);
            }
        }
        return  result.toArray(new IFile[result.size()]);
    }

    public IStatus validateSave(IFile file) {
        if ( isReadOnly(file)) {
            return new Status(IStatus.ERROR,WSPlugin.PLUGIN_ID,0,"The file is readonly.",null);
        } 
        return Status.OK_STATUS;
    }

    public boolean deleteFile(
            @SuppressWarnings("unused") IResourceTree tree, 
            IFile file, 
            @SuppressWarnings("unused") int updateFlags, 
            @SuppressWarnings("unused") IProgressMonitor monitor) {
        return isReadOnly(file);
    }

    public boolean deleteFolder(
            @SuppressWarnings("unused") IResourceTree tree, 
            IFolder folder, 
            @SuppressWarnings("unused") int updateFlags, 
            @SuppressWarnings("unused") IProgressMonitor monitor) {
        return isReadOnly(folder);
    }

    public boolean deleteProject(
            @SuppressWarnings("unused") IResourceTree tree, 
            IProject project, 
            @SuppressWarnings("unused") int updateFlags, 
            @SuppressWarnings("unused") IProgressMonitor monitor) {
        return isReadOnly(project);
    }

    public boolean moveFile(
            @SuppressWarnings("unused") IResourceTree tree, 
            IFile source, 
            IFile destination, 
            @SuppressWarnings("unused") int updateFlags, 
            @SuppressWarnings("unused") IProgressMonitor monitor) {
        return isReadOnly(source) || isReadOnly(destination);
    }

    public boolean moveFolder(
            @SuppressWarnings("unused") IResourceTree tree, 
            IFolder source, 
            IFolder destination, 
            @SuppressWarnings("unused") int updateFlags, 
            @SuppressWarnings("unused") IProgressMonitor monitor) {
        return isReadOnly(source) || isReadOnly(destination);
    }

    public boolean moveProject(@SuppressWarnings("unused") IResourceTree tree, 
            IProject source, 
            @SuppressWarnings("unused") IProjectDescription description, 
            @SuppressWarnings("unused") int updateFlags, 
            @SuppressWarnings("unused") IProgressMonitor monitor) {
        return isReadOnly(source);
    }

    private boolean isReadOnly(IResource res) {
        Item item = getItem(res);
        if (item != null && item.isReadOnly())
            return true;
        return false;
    }

    Item getItem(IResource resource) {
        IProject project = resource.getProject();
        
        IContainer container;
        if (!(resource instanceof IContainer)) {
            container = resource.getParent();
        }
        else {
            container = (IContainer) resource;
        }
        while (project != container) {
            if (container instanceof IFolder && container.getParent() == project) {
                Item contentItem = wd.getLogicalWorkspace().getItem(container.getFullPath().toPortableString());
                if (contentItem != null)
                    return contentItem;
            }
            container = resource.getParent();
        }
        return wd.getLogicalWorkspace().getItem(project.getName());
    }

	
    
}
