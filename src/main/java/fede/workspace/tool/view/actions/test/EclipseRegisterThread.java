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
package fede.workspace.tool.view.actions.test;

import java.util.Map;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IElementStateListener;

public class EclipseRegisterThread {
	class EditorEclipseTest {
		IWorkbenchPartReference ref;
		public IEditorPart editor;
		public ElementSateListener elementSateListener;
		
		void close() {
			
		}

		public void editorBeginChange() {
			// TODO Auto-generated method stub
			
		}

		public void unregister() {
			if (editor != null && elementSateListener != null) {
				((AbstractTextEditor)editor).getDocumentProvider().removeElementStateListener(elementSateListener);
			}
		}

		public void editorFinishChange() {
			// TODO Auto-generated method stub
			
		}
	}
	
	Map<IWorkbenchPartReference, EditorEclipseTest> map;
	
	private final class PartListener implements IPartListener2 {
		
		public void partActivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		public void partClosed(IWorkbenchPartReference partRef) {
			EditorEclipseTest eclipseTest = map.get(partRef);
			if (eclipseTest != null)
				eclipseTest.close();
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		public void partHidden(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
			
		}

		public void partOpened(IWorkbenchPartReference ref) {
			register(ref);
		}

		public void partVisible(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub
			
		}

	}

	private final class ElementSateListener implements IElementStateListener {
		private EditorEclipseTest editor;

		public ElementSateListener(EditorEclipseTest editor) {
			this.editor = editor;
		}
		public void elementContentAboutToBeReplaced(Object element) {
			// TODO Auto-generated method stub
			
		}

		public void elementContentReplaced(Object element) {
			// TODO Auto-generated method stub
			
		}

		public void elementDeleted(Object element) {
			// TODO Auto-generated method stub
			
		}

		public void elementDirtyStateChanged(Object element,
				boolean isDirty) {
			if (isDirty) {
				editor.editorBeginChange();
			} else {
				editor.editorFinishChange();
			}
			
		}

		public void elementMoved(Object originalElement,
				Object movedElement) {
			// TODO Auto-generated method stub
			
		}
	}

	public void register(IWorkbenchPartReference ref) {
		if (ref instanceof IEditorReference) {
			IEditorReference editorReference = (IEditorReference) ref;
			IEditorPart editor = editorReference .getEditor(false);
			if (editor == null) return;
			EditorEclipseTest  et = new EditorEclipseTest();
			et.ref = ref;
			et.editor = editor;
			map.put(ref, et);
			et.elementSateListener = null;
			
			if (editor instanceof AbstractTextEditor) {
				AbstractTextEditor textEditor = (AbstractTextEditor) editor;
				et.elementSateListener = new ElementSateListener(et);
				textEditor.getDocumentProvider().addElementStateListener(
						et.elementSateListener
				);
			}
			
		}
	}
	public EclipseRegisterThread() {
		start();
		
	}
	
	public void start() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
		
		activePage.addPartListener(new PartListener());
		IEditorReference[] editors = activePage.getEditorReferences();
		for (IEditorReference editorReference : editors) {
			register(editorReference);
		}
	}
	
	public void stop() {
		for (EditorEclipseTest et : map.values()) {
			et.unregister();
		}
	}
	
	
}
