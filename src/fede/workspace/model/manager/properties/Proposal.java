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
package fede.workspace.model.manager.properties;

import org.eclipse.jface.fieldassist.IContentProposal;

public class Proposal implements IContentProposal, Comparable<Proposal> {

	private String	_content;
	private String	_label;
	private String	_description;
	private int		_cursorPosition;
	private Object	_value	= null;

	public Proposal(String content, String label, String description, int cursorPosition) {
		_content = content;
		_label = label;
		_description = description;
		_cursorPosition = cursorPosition;
	}

	public Proposal(String content, String label, String description, int cursorPosition, Object value) {
		_content = content;
		_label = label;
		_description = description;
		_cursorPosition = cursorPosition;
		_value = value;
	}

	public String getContent() {
		return _content;
	}

	public String getLabel() {
		return _label;
	}

	public String getDescription() {
		return _description;
	}

	public int getCursorPosition() {
		return _cursorPosition;
	}

	public int compareTo(Proposal o) {
		return _label.compareTo(o._label);
	}

	public Object getValue() {
		return _value;
	}
}