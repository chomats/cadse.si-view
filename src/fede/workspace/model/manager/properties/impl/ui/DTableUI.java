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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fede.workspace.tool.view.dialog.create.InteractifTreeController;
import fr.imag.adele.cadse.core.ItemType;
import fr.imag.adele.cadse.core.ui.EPosLabel;
import fr.imag.adele.cadse.core.ui.IFedeFormToolkit;
import fr.imag.adele.cadse.core.ui.IModelController;
import fr.imag.adele.cadse.core.ui.IPageController;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (f).
 */

public class DTableUI extends DAbstractField {
	private String[]	columns;

	public DTableUI(String key, String label, EPosLabel poslabel, IModelController mc, InteractifTreeController ic,
			String[] columns) {
		super(key, label, poslabel, mc, ic);
		action = ic;
		this.columns = columns;
	}

	public static final String	TABLE_COLUMNS	= "table-columns";
	private Table				table;
	private Object[]			root;
	InteractifTreeController	action;
	TableViewer					viewer;
	Map<String, String>			values			= new HashMap<String, String>();
	int							newValueIndex	= 0;
	private TableEditor			fTableEditor;

	ITableUserController		uctable;

	public Object createControl(final IPageController globalUIController, IFedeFormToolkit toolkit, Object container,
			int hspan) {
		table = new Table((Composite) container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumHeight = 200;
		table.setLayoutData(gd);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		if (columns != null) {
			uctable = new ITableUserController() {

				public String getTableCollumn(int index) {
					return columns[index];
				}

				public int getTableCollumnCount() {
					return columns.length;
				}

				public int getTableMinSize(int index) {
					return 20;
				}

				public boolean getTableResizable(int index) {
					return true;
				}

				public Object[] elements(Object value) {
					return ((Map) value).entrySet().toArray();
				}

				public String getText(Object element, int indexCol) {
					if (indexCol == 0) {
						return ((Map.Entry<String, String>) element).getKey();
					}
					if (indexCol == 1) {
						return ((Map.Entry<String, String>) element).getValue();
					}
					return "";
				}

				public void setText(Object element, int indexCol, String text) {
					if (indexCol == 0) {
						Entry<String, String> e = ((Map.Entry<String, String>) element);
						values.remove(e.getKey());
						values.put(text, e.getValue());
					}
					if (indexCol == 1) {
						((Map.Entry<String, String>) element).setValue(text);
					}
				}

				public ICellModifier getCellModifier() {
					// TODO Auto-generated method stub
					return new ICellModifier() {

						public boolean canModify(Object element, String property) {
							return true;
						}

						public Object getValue(Object element, String property) {
							// TODO Auto-generated method stub
							return null;
						}

						public void modify(Object element, String property, Object value) {
							// TODO Auto-generated method stub

						}

					};
				}

				public CellEditor[] getCellsEditor() {
					return new CellEditor[] { new TextCellEditor(), new TextCellEditor() };
				}

			};
		}
		int count = uctable.getTableCollumnCount();
		for (int i = 0; i < count; i++) {
			TableColumn tc = new TableColumn(table, SWT.LEFT);
			tc.setText(uctable.getTableCollumn(i));
			tc.setResizable(uctable.getTableResizable(i));
			layout.addColumnData(new ColumnWeightData(uctable.getTableMinSize(i), true));
		}

		table.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] sel = table.getSelection();
				if (sel.length == 1) {
					TableItem ti = sel[0];
					select(ti);
				}

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		// table.addMouseListener(new MouseListener() {
		// public void mouseDoubleClick(MouseEvent e) {
		// values.put("key"+newValueIndex, "value"+newValueIndex);
		// newValueIndex++;
		// viewer.refresh();
		// }
		//
		// public void mouseDown(MouseEvent e) {
		// // Clean up any previous editor control
		// Control oldEditor = fTableEditor.getEditor();
		// if (oldEditor != null) oldEditor.dispose();
		//
		// // Identify the selected row
		// TableItem item = table.getItem(new Point(e.x,e.y));
		// if (item == null) return;
		// int w = 0;
		// int column = -1;
		// for (int i = 0; i < table.getColumnCount(); i++) {
		// TableColumn tc = table.getColumn(i);
		// if (w+tc.getWidth() > e.x) {
		// column = i; break;
		// }
		// w+=tc.getWidth();
		// }
		// // The control that will be the editor must be a child of the Table
		// Text newEditor = new Text(table, SWT.NONE);
		// newEditor.setText(item.getText(column));
		// final int finalColumn = column;
		// newEditor.addModifyListener(new ModifyListener() {
		// public void modifyText(ModifyEvent e) {
		// TableItem ti = fTableEditor.getItem();
		// viewer.getElementAt(viewer.getCellEditors());
		//
		// Text text = (Text)fTableEditor.getEditor();
		// fTableEditor.getItem().setText(finalColumn, text.getText());
		// }
		// });
		// newEditor.addKeyListener(new KeyListener() {
		//
		// public void keyPressed(KeyEvent e) {
		// if (e.keyCode == 13) {
		// Text text = (Text)fTableEditor.getEditor();
		// fTableEditor.getItem().setText(finalColumn, text.getText());
		// text.setVisible(false);
		// text.dispose();
		// fTableEditor.setEditor(null);
		// e.doit = true;
		// }
		// }
		//
		// public void keyReleased(KeyEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
		// newEditor.selectAll();
		// newEditor.setFocus();
		// fTableEditor.setEditor(newEditor, item, column);
		// }
		//
		// public void mouseUp(MouseEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		fTableEditor = new TableEditor(table);
		fTableEditor.horizontalAlignment = SWT.LEFT;
		fTableEditor.grabHorizontal = true;
		fTableEditor.minimumWidth = 50;
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		table.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
					Text text = (Text) fTableEditor.getEditor();
					if (text != null) {
						fTableEditor.getItem().setText(fTableEditor.getColumn(), text.getText());
						text.setVisible(false);
						text.dispose();
						fTableEditor.setEditor(null);
						e.doit = true;
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		viewer = new TableViewer(table);
		viewer.setLabelProvider(getLabelProvider());
		viewer.setContentProvider(getContentProvider());
		viewer.setInput(values);
		viewer.setCellEditors(uctable.getCellsEditor());
		viewer.setCellModifier(uctable.getCellModifier());

		return container;
	}

	IStructuredContentProvider getContentProvider() {
		return new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return uctable.elements(inputElement);
			}

			public void dispose() {

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			}

		};
	}

	ITableLabelProvider getLabelProvider() {
		return new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				return uctable.getText(element, columnIndex);
			}

			public void addListener(ILabelProviderListener listener) {

			}

			public void dispose() {

			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {

			}

		};
	}

	/**
	 * Returns the single selected object contained in the passed
	 * selectionEvent, or <code>null</code> if the selectionEvent contains
	 * either 0 or 2+ selected objects.
	 */
	protected Object getSingleSelection(IStructuredSelection selection) {
		return selection.size() == 1 ? selection.getFirstElement() : null;
	}

	public Object getVisualValue() {
		return root;
	}

	public void setVisualValue(Object visualValue, boolean sendNotification) {
		// root = (Object[]) visualValue;
		// table.removeAll();
		// createTree(root,table);
	}

	private String getText(Object obj) {
		return action.getText(obj);
	}

	private Image getImage(Object obj) {
		return action.getImage(obj);
	}

	private Object[] getChildren(Object obj) {
		return action.getChildren(obj);
	}

	public int getHSpan() {
		return 1;
	}

	public void setEnabled(boolean v) {
		this.table.setEnabled(v);
	}

	public void internalSetEditable(boolean v) {
	}

	public void internalSetVisible(boolean v) {
		super.internalSetVisible(v);
		this.table.setVisible(v);
	}

	public Object getUIObject(int index) {
		return table;
	}

	void select(TableItem ti) {

	}

	public ItemType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Control getMainControl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getSelectedObjects() {
		// TODO Auto-generated method stub
		return null;
	}

}
