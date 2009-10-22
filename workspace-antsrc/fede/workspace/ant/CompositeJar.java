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
package fede.workspace.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class CompositeJar extends Task {
	
	private String item;
	private File   outFile = null;
	private File srcFile = null;
	private List<FileSet> filesets = new ArrayList<FileSet>();
	boolean includeSrc = false;
	
	public void setItem(String item) {
		this.item = item;
		log("Item : "+item);
	}
	
	public void setOutfile(File outFile) {
		this.outFile = outFile;
		log("outFile : "+outFile);
	}
	
	
	public void setSrcfile(File srcFile) {
		this.srcFile = srcFile;
		log("srcFile : "+srcFile);
	}
	
	public void setIncludesrc(boolean i) {
		includeSrc = i;
	}
	/**
     * Adds a set of files to copy.
     * @param set a set of files to copy
     */
    public void addFileset(FileSet set) {
        filesets.add(set);
    }
	
	@Override
	public void execute() throws BuildException {
		try {
			if (item == null) 
				throw new BuildException("L'attribut item est obligatoire.");
			
			IProject theProject = ResourcesPlugin.getWorkspace().getRoot().getProject(item);
			if (theProject == null) 
				throw new BuildException("L'item doit etre un projet eclipse.");
			log("project : "+theProject);
			IFolder components = theProject.getFolder("components");
			if (components == null) 
				throw new BuildException("L'item doit etre un projet composite.");
			
			createOutFile(theProject, components);
			
			createSrcFile(components);
			
		} catch (BuildException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new BuildException(e.getClass().getName()+e.getMessage(),e);
		}
		
		
	}

	private void createSrcFile(IFolder components) {
		File tempDir;
		if (srcFile != null) {
			tempDir = createTmpFile();
			
			copySource(components, tempDir);
			
			copyExtra(tempDir);
			
			createJarFile(tempDir, srcFile);
			
			//delete temp file
			tempDir.delete();
		}
	}

	private void createOutFile(IProject theProject, IFolder components) {
		
		
		File tempDir;
		if (outFile != null) {
			// create temp dir
			
			tempDir = createTmpFile();
			
			copyComponentsClassesAndAspects(components, tempDir);
			
			/// classes folder.
			copyClasses(theProject, tempDir);
			copyExtra(tempDir);
			
			if (includeSrc)
				copySource(components,tempDir);
			
			createJarFile(tempDir, outFile);
			
			//delete temp file
			tempDir.delete();
		}
	}

	private File createTmpFile() {
		FileUtils utils = FileUtils.newFileUtils();
		File tempDir;
		tempDir = utils.createTempFile("pre","pos",null);
		tempDir.mkdir();
		return tempDir;
	}

	private void copyClasses(IProject theProject, File tempDir) {
		FileSet setOfFile;
		Copy copytask;
		IFolder classesFolder = theProject.getFolder("classes");
		if (classesFolder != null && classesFolder.exists()) {
			copytask = new Copy();
			copytask.setProject(getProject());
			copytask.setTaskName(getTaskName());
			
			copytask.setTodir(tempDir);
			copytask.setOverwrite(true);
			setOfFile = new FileSet();
			setOfFile.setDir(classesFolder.getLocation().toFile());
			copytask.addFileset(setOfFile);
			log("fileset : "+classesFolder.getLocation().toFile());
			
			copytask.execute();
		}
	}

	private void copyComponentsClassesAndAspects(IFolder components, File tempDir) {
		FileSet setOfFile;
		Copy copytask = new Copy();
		copytask.setProject(getProject());
		copytask.setTaskName(getTaskName());
		
		copytask.setTodir(tempDir);
		log("temp : "+tempDir);
		
		try {
			IResource[] Components = components.members();
			for (int i = 0; i < Components.length; i++) {
				if (!(Components[i] instanceof IFolder)) continue;
				IFolder fComponents = (IFolder) Components[i];
				IFolder classesFolder = fComponents.getFolder("classes");
				if (classesFolder != null && classesFolder.exists()) {
					setOfFile = new FileSet();
					setOfFile.setDir(classesFolder.getLocation().toFile());
					copytask.addFileset(setOfFile);
					log("fileset : "+classesFolder.getLocation().toFile());
				}
				IFolder aspectFolder = fComponents.getFolder("aspects");
				if (aspectFolder != null && aspectFolder.exists()) {
					setOfFile = new FileSet();
					setOfFile.setDir(aspectFolder.getLocation().toFile());
					copytask.addFileset(setOfFile);
					log("fileset : "+aspectFolder.getLocation().toFile());
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		copytask.execute();
	}

	private void createJarFile(File tempDir, File thefile) {
		FileSet setOfFile;
		Zip jarTask = new Zip();
		jarTask.setProject(getProject());
		jarTask.setTaskName(getTaskName());
		
		jarTask.setDestFile(thefile);
		
		setOfFile = new FileSet();
		setOfFile.setDir(tempDir);
		jarTask.addFileset(setOfFile);
		jarTask.execute();
	}

	private void copyExtra(File tempDir) {
		if (filesets.size() != 0) {
			Copy copytask = new Copy();
			copytask.setProject(getProject());
			copytask.setTaskName(getTaskName());
			
			copytask.setTodir(tempDir);
			copytask.setOverwrite(true);
			for (FileSet fs : filesets) {
				copytask.addFileset(fs);
			}
			copytask.execute();
		}
	}

	private void copySource(IFolder components, File tempDir) {
		FileSet setOfFile;
		Copy copytask = new Copy();
		copytask.setProject(getProject());
		copytask.setTaskName(getTaskName());
		
		copytask.setTodir(tempDir);
		try {
			IResource[] Components = components.members();
			for (int i = 0; i < Components.length; i++) {
				if (!(Components[i] instanceof IFolder)) continue;
				setOfFile = new FileSet();
				IProject componentsProject = ResourcesPlugin.getWorkspace().getRoot().getProject(Components[i].getName());
				IFolder sources = componentsProject.getFolder("sources");
				if (sources.exists()) {
					setOfFile.setDir(sources.getLocation().toFile());
					copytask.addFileset(setOfFile);
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		copytask.execute();
	}
	
	public static void main(String[] args) {
		CompositeJar cj = new CompositeJar();
		cj.setOutfile(new File("/home/chomats/Fede/svn/fede-repos/trunk/Deploy.Activity.Editor/Config.Activity.Editor2.jar"));
		cj.setItem("Config.Activity.Editor");
	}

}
