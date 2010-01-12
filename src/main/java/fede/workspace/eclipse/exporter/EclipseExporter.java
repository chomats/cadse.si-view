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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.ContentItem;
import fr.imag.adele.cadse.core.build.IBuildingContext;
import fr.imag.adele.cadse.core.build.IExportedContent;
import fr.imag.adele.cadse.core.build.IExporterTarget;
import fr.imag.adele.cadse.core.build.Exporter;
import fede.workspace.eclipse.MelusineProjectManager;
import fede.workspace.eclipse.composer.EclipseExportedContent;
import fede.workspace.eclipse.composition.CompositeBuilder;
import fede.workspace.eclipse.composition.CompositeBuildingContext;

public class EclipseExporter extends Exporter {

	protected EclipseExporter(ContentItem contentManager, String... exporterTypes) {
		super(contentManager, exporterTypes);
	}

	@Override
	public IExportedContent exportItem(IBuildingContext context, IExporterTarget target, String exporterType) throws CadseException {
		IContainer repository = ((EclipseExporterTarget)target).getTargetContainer();
		IProgressMonitor monitor = ((CompositeBuildingContext)context).getMonitor();
		CompositeBuilder builder = ((CompositeBuildingContext)context).getBuilder();
		
		/*
		 * Get the packaged item in the target repository, create it if needed.
		 */
		EclipseExportedContent eclipseExportedContent = null;
		try {
			
			eclipseExportedContent = EclipseExportedContent.getPackagedItem(repository, getItem(), exporterType, monitor );
			IProject componentProject = MelusineProjectManager.getProject(getItem());;
			IResourceDelta componentUpdate	=  builder.getDelta(componentProject );
			boolean fullCopy = eclipseExportedContent == null || componentUpdate == null;
			
			
			/*
			 * Remove any previous content in the folder associated with this composer
			 */
			
			
			if (fullCopy) {
				if (eclipseExportedContent != null) eclipseExportedContent.delete(monitor);
				eclipseExportedContent = new EclipseExportedContent(repository,getItem(),exporterType,monitor);
				componentUpdate = null;
			}
			else { //eclipseExportedContent != null && componentUpdate != null
				// skip non modified projects
				if (componentUpdate != null && componentUpdate.getKind() == IResourceDelta.NO_CHANGE)
					return eclipseExportedContent;
			}
			/*
			 * package folder content
			 */
			exportItem(eclipseExportedContent,componentUpdate ,monitor, exporterType);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return eclipseExportedContent ;
	}

	protected void exportItem(EclipseExportedContent eclipseExportedContent, IResourceDelta componentUpdate, IProgressMonitor monitor, String exporterType) throws CoreException {
	}

	
}
