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
package fede.workspace.model.manager.properties.impl.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;

public interface ITableUserController {

	int	getTableCollumnCount();
	String getTableCollumn(int columnIndex);
	int	getTableMinSize(int columnIndex);
	boolean getTableResizable(int columnIndex);
	
	Object[] elements(Object value);
	String	getText(Object element, int columnIndex);
	void	setText(Object element, int columnIndex, String text);
	
	CellEditor[] getCellsEditor();
	ICellModifier getCellModifier();
}
