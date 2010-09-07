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
 *
 * Copyright (C) 2006-2010 Adele Team/LIG/Grenoble University, France
 */
package fede.workspace.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;

public class CopyItem extends Task {
	
	private String item;
	private File   destFileParent = null;
	private List<FileSet> filesets = new ArrayList<FileSet>();
	boolean includeSrc = false;
	final FileSet defaultFileSet = new FileSet();
	
	public void setItem(String item) {
		this.item = item;
		log("Item : "+item);
	}
	
	public void setDestfileparent(File outFile) {
		this.destFileParent = outFile;
		log("outFile : "+outFile);
	}
	
	public void setDest(File outFile) {
		this.destFileParent = outFile;
		log("outFile : "+outFile);
	}
//	private File srcFile = null;
//	public void setSrcfile(File srcFile) {
//		this.srcFile = srcFile;
//		log("srcFile : "+srcFile);
//	}
	
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
    
    /**
     * Add a name entry to the exclude list.
     * @return <code>PatternSet.NameEntry</code>.
     */
    public PatternSet.NameEntry createExclude() {
        return defaultFileSet.createExclude();
    }
	
	@Override
	public void execute() throws BuildException {
		try {
			
			IPath jrt_lib = JavaCore.getClasspathVariable("ASPECTJRT_LIB");
			log("project : "+jrt_lib);
			if (item == null) 
				throw new BuildException("L'attribut item est obligatoire.");
			
			IProject theProject = ResourcesPlugin.getWorkspace().getRoot().getProject(item);
			if (theProject == null) 
				throw new BuildException("L'item doit etre un projet eclipse.");
			log("project : "+theProject);
			
			/*
			 * <mkdir dir="${idname}"/>
	    	<copy todir="${dest}${idname}">
	    		<fileset dir="../${idname}">
	    			<exclude name=".svn"/>
	    			<exclude name=".melusine"/>
	    			<exclude name=".project"/>
	    			<exclude name=".classpath"/>
	    		</fileset>
	    	</copy>
			 */
			File destDir = new File(destFileParent,item);
			destDir.mkdirs();
				
			Copy copytask;
			copytask = new Copy();
			copytask.setProject(getProject());
			copytask.setTaskName(getTaskName());
			
			copytask.setTodir(destDir);
			copytask.setOverwrite(true);
			defaultFileSet.setDir(theProject.getLocation().toFile());
			copytask.addFileset(defaultFileSet);
			defaultFileSet.createExclude().setName(".svn");
			defaultFileSet.createExclude().setName(".melusine");
			defaultFileSet.createExclude().setName(".project");
			defaultFileSet.createExclude().setName(".classpath");
			if (!includeSrc) {
				defaultFileSet.createExclude().setName("src");
				defaultFileSet.createExclude().setName("sources");
			}
			
			copytask.execute();
			
		} catch (BuildException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new BuildException(e.getClass().getName()+e.getMessage(),e);
		}
		
		
	}

}
