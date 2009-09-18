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
package fede.workspace.model.manager.properties.type;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.osgi.framework.Bundle;

public class ClassType implements FieldAttributeType {

	public Object stringToObject(String value) {
		String className = value;
		String bundleId = null;
		int index;
		if ((index = value.indexOf(":")) != -1) {
			className = value.substring(index + 1);
			bundleId = value.substring(0, index - 1);
		}
		return createExecutableExtension(bundleId, className);
	}

	public String objectToString(Object value) {
		throw new UnsupportedOperationException();
	}

	private Object createExecutableExtension(String pluginName, String className) {
		Bundle bundle = null;
		if (pluginName != null && !pluginName.equals("")) { //$NON-NLS-1$
			bundle = InternalPlatform.getDefault().getBundle(pluginName);
		}

		// load the requested class from this plugin
		Class classInstance = null;
		try {
			if (bundle == null) {
				classInstance = getClass().getClassLoader().loadClass(className);
			} else {
				classInstance = bundle.loadClass(className);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
			// throwException(NLS.bind(Messages.plugin_loadClassError,
			// bundle.getSymbolicName(), className), e1);
		} catch (LinkageError e) {
			e.printStackTrace();
			return null;
			// throwException(NLS.bind(Messages.plugin_loadClassError,
			// bundle.getSymbolicName(), className), e);
		}

		// create a new instance
		Object result = null;
		try {
			result = classInstance.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			// throwException(NLS.bind(Messages.plugin_instantiateClassError,
			// bundle.getSymbolicName(), className), e);
		}

		return result;
	}

}
