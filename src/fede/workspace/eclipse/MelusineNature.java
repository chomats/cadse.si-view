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
package fede.workspace.eclipse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import fede.workspace.eclipse.composition.ComponentBuilder;
import fede.workspace.eclipse.composition.CompositeBuilder;
import fede.workspace.eclipse.validation.ValidationBuilder;
import fede.workspace.tool.view.WSPlugin;

/**
 * This class associates a nature with eclipse projects handled by the Melsuine
 * Environment.
 * 
 * @author vega
 *
 */
public class MelusineNature implements IProjectNature {

	public final static String ID 				= WSPlugin.NAMESPACE_ID+".Melusine";
	
	private IProject project;
	
	public void configure() throws CoreException {
		
		IProjectDescription description = getProject().getDescription();
		
		List<ICommand> builders = new ArrayList<ICommand>(Arrays.asList(description.getBuildSpec()));
		boolean updateBuilders = false;
		
		/*
		 *	Add composition builder at the beginning of the builder list.
		 *
		 *	This ensures that all the components of an item will be present when the Java 
		 *	or AspectJ builder is triggered
		 */
		ICommand compositionBuilder	= description.newCommand();
		compositionBuilder.setBuilderName(CompositeBuilder.ID);

		if (!builders.contains(compositionBuilder)) {
			builders.add(0,compositionBuilder);
			updateBuilders = true;
		}

		/*
		 *	Add validation builder at the end of the builder list.
		 *
		 * 	This increases the visibility of validation errors, as they will be more
		 * 	recent than compilation erros 
		 */
		ICommand validationBuilder	= description.newCommand();
		validationBuilder.setBuilderName(ValidationBuilder.ID);
		
		if (!builders.contains(validationBuilder)) {
			builders.add(validationBuilder);
			updateBuilders = true;
		}

		/*
		 *	Add packaging builder at the end of the builder list.
		 *
		 * 	This is necessary to be sure that the packaged classes are the latest
		 *	compiled versions.
		 */
		ICommand packagingBuilder	= description.newCommand();
		packagingBuilder.setBuilderName(ComponentBuilder.ID);

		if (!builders.contains(packagingBuilder)) {
			builders.add(packagingBuilder);
			updateBuilders = true;
		}

		
		if (updateBuilders)  {
			description.setBuildSpec(builders.toArray(new ICommand[builders.size()]));
			project.setDescription(description,new NullProgressMonitor());
		}
		
	}

	public void deconfigure() throws CoreException {
		IProjectDescription description = getProject().getDescription();
		
		// Remove Validation and composition builders from project
		
		List<ICommand> builders = new ArrayList<ICommand>(Arrays.asList(description.getBuildSpec()));
		boolean updateBuilders = false;
		
		ICommand validationBuilder	= description.newCommand();
		validationBuilder.setBuilderName(ValidationBuilder.ID);

		ICommand classpathBuilder	= description.newCommand();
		classpathBuilder.setBuilderName(ComponentBuilder.ID);
		
		if (builders.contains(validationBuilder)) {
			builders.remove(validationBuilder);
			updateBuilders = true;
		}

		if (builders.contains(classpathBuilder)) {
			builders.remove(classpathBuilder);
			updateBuilders = true;
		}

		if (updateBuilders) {
			description.setBuildSpec(builders.toArray(new ICommand[builders.size()]));
			project.setDescription(description,new NullProgressMonitor());
		}
		
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
