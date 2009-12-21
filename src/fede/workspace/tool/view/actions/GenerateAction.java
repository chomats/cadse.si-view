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
package fede.workspace.tool.view.actions;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.IGenerateContent;
import fr.imag.adele.cadse.core.IItemNode;
import fr.imag.adele.cadse.core.Item;
import fr.imag.adele.cadse.core.var.ContextVariable;
import fede.workspace.tool.view.WSPlugin;

public class GenerateAction extends AbstractEclipseMenuAction {

	List<Item> arrayOfgenerateObj;

	public GenerateAction(List<Item> arrayOfgenerateObj) {
		this.arrayOfgenerateObj = arrayOfgenerateObj;
	}
	
	
	@Override
	public String getLabel() {
		if ( arrayOfgenerateObj.size() == 1 ) {
			return "Generate "+arrayOfgenerateObj.get(0).getDisplayName();
		}
		return "Generate ...";
	}

	@Override
	public void run(IItemNode[] selection) throws CadseException {
		for (Item aItem : arrayOfgenerateObj) {
			try {
				((IGenerateContent) aItem.getContentItem()).generate(ContextVariable.DEFAULT);
			} catch (Throwable e) {
		    	String id = aItem.getName();
		        WSPlugin.log(new Status(Status.ERROR,"Tool.Workspace.View",0,
		        		MessageFormat.format("Cannot generate {0} : {1}.",id,e.getMessage()),e));
		    } 
		}
	}
	

}
