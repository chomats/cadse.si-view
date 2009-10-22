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
package fede.workspace.eclipse.exporter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.build.IBuildingContext;
import fr.imag.adele.cadse.core.build.IExportedContent;
import fr.imag.adele.cadse.core.build.IExporterTarget;
import fede.workspace.eclipse.MelusineProjectManager;
import fede.workspace.eclipse.composer.EclipseExportedContent;
import fede.workspace.eclipse.composition.CompositeBuilder;
import fede.workspace.eclipse.composition.CompositeBuildingContext;

/**
 * This class is the basic composer that handles multi level composition.
 * 
 * When an composite item is itself a component of another composite, we need to
 * propagate any modification to its components to all its containers.
 * 
 * @author vega
 *
 */
public class CompositeReExporter extends EclipseExporter {
	
	/**
	 * Creates a new multilevel composer that handles propagation of component modifications
	 * up a composition hierrachy.
	 * 
	 * Composition of the proper content of a compsite is delegated to the specified composer.
	 * 
	 * @param item
	 */
	public CompositeReExporter(ContentItem contentManager, String... exporterTypes) {
		super(contentManager, exporterTypes);
	}

	@Override
	public IExportedContent exportItem(IBuildingContext context, IExporterTarget target, String exporterType) throws CadseException {
		IContainer targetRepository = ((EclipseExporterTarget)target).getTargetContainer();
		IProgressMonitor monitor = ((CompositeBuildingContext)context).getMonitor();
		CompositeBuilder builder = ((CompositeBuildingContext)context).getBuilder();
		
		
		try {
			/*
			 * TODO Right now we have a single component repository for every eclipse project.
			 * 
			 * We need to handle mapping variants in which there are many composites in a single eclipse project,
			 * this is the case for example when a composite has parts that are themselves composites.
			 */
			IContainer repository = MelusineProjectManager.getProject(getItem());
			
			
			List<IExportedContent> components = EclipseExportedContent.getPackagedItems(repository,exporterType, monitor);
				
			/*
			 * Copy all components to the target repository
			 * 
			 * WARNING This flatens the composition hierachy in the repository, as we copy two levels in the same
			 * repository, this way we ensure that there is a single copy of a packaged item in a repository even
			 * when components are shared by several composites.
			 */
			monitor = new SubProgressMonitor(monitor,1);
			monitor.beginTask("Updating "+components.size()+" copying components from "+repository+" to "+targetRepository,components.size());
			
			List<IExportedContent> ret = new ArrayList<IExportedContent>();
			for (IExportedContent component : components) {
				
				
				try {
					/*
					 * Delete any previously existing copy of the component in the target repository
					 */
					EclipseExportedContent targetCopy = ((EclipseExportedContent) component).getCopy(targetRepository,monitor);
					
					IResourceDelta componentUpdate	=  builder.getDelta(repository.getProject());
					
					if (targetCopy == null || componentUpdate == null) {
						// full copy
						if (targetCopy != null) targetCopy.delete(monitor);
						/*
						 * Copy component to the target repository
						 */
						EclipseExportedContent pi = ((EclipseExportedContent) component).copy(targetRepository,monitor);
						if (pi != null)
							ret.add(pi);
					}
					else {
						// skip non modified projects
						monitor.subTask("refreshing "+component.getItemDisplayName());
						if (componentUpdate.getKind() == IResourceDelta.NO_CHANGE)
								continue;
						
						((EclipseExportedContent) component).update(targetCopy,componentUpdate,monitor);
						ret.add(targetCopy);
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				monitor.worked(1);
				
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return null;
		
		
	}

	public void updatePackagedItem(EclipseExportedContent targetItem, IResourceDelta projectUpdate, IProgressMonitor monitor)	throws CoreException {

		IContainer targetRepository	= targetItem.getRepository();
		
		/*
		 * TODO Right now we have a single component repository for every eclipse project.
		 * 
		 * We need to handle mapping variants in which there are many composites in a single eclipse project,
		 * this is the case for example when a composite has parts that are themselves composites.
		 */
		IContainer repository = MelusineProjectManager.getProject(getItem());
		List<EclipseExportedContent> components = EclipseExportedContent.getPackagedItems(repository,monitor);

		/*
		 * Update all components of the target repository
		 * 
		 * WARNING This flatens the composition hierachy in the repository, as we copy two levels in the same
		 * repository, this way we ensure that there is a single copy of a packaged item in a repository even
		 * when components are shared by several composites.
		 */

		monitor.beginTask("Updating "+components.size()+" components components from "+repository+" to "+targetRepository,components.size());
		
		for (EclipseExportedContent component : components) {

			EclipseExportedContent targetCopy = component.getCopy(targetRepository,monitor);
			
			
			/*
			 * Copy any non existant components to the target repository
			 */
			if (targetCopy == null) {
				component.copy(targetRepository,monitor);
				continue;
			}
			
			monitor.subTask("refreshing "+component.getItemDisplayName());
			component.update(targetCopy,projectUpdate,monitor);
			
			monitor.worked(1);
		}
		monitor.done();
		
	}

	

}
