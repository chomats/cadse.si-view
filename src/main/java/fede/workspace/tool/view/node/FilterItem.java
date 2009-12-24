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
package fede.workspace.tool.view.node;

import fr.imag.adele.cadse.core.Item;

public interface FilterItem {

	public static class TypeHiddenFilter implements FilterItem {
		public boolean accept(Item item) {
			return item.getType() != null && item.getType().isHidden();
		}
	}

	public static class ItemHiddenFilter implements FilterItem {
		public boolean accept(Item item) {
			return item.isHidden();
		}
	}

	public static class TypeRootFilter implements FilterItem {
		public boolean accept(Item item) {
			return item.getType() != null && item.getType().isRootElement();
		}
	}

	public static class TypeNotHiddenFilter implements FilterItem {
		public boolean accept(Item item) {
			return item.getType() != null && !item.getType().isHidden();
		}
	}

	public static class ItemNotStaticFilter implements FilterItem {
		public boolean accept(Item item) {
			return item.isStatic();
		}
	}

	public static class ItemNotHiddenFilter implements FilterItem {
		public boolean accept(Item item) {
			return !item.isHidden();
		}
	}

	public static class TypeNotRootFilter implements FilterItem {
		public boolean accept(Item item) {
			return item.getType() != null && !item.getType().isRootElement();
		}
	}

	public static class OrFilter implements FilterItem {
		private FilterItem[]	_filters;

		public OrFilter(FilterItem... filters) {
			_filters = filters;
			assert _filters.length > 1;
		}

		public boolean accept(Item item) {
			for (int i = 0; i < _filters.length; i++) {
				if (_filters[i].accept(item)) {
					return true;
				}
			}
			return false;
		}
	}

	public static class NotFilter implements FilterItem {
		private FilterItem	_filter;

		public NotFilter(FilterItem filter) {
			_filter = filter;
			assert _filter != null;
		}

		public boolean accept(Item item) {
			return !_filter.accept(item);
		}
	}

	public static class AndFilter implements FilterItem {
		private FilterItem[]	_filters;

		public AndFilter(FilterItem... filters) {
			_filters = filters;
			assert _filters.length > 1;
		}

		public boolean accept(Item item) {
			for (int i = 0; i < _filters.length; i++) {
				if (!_filters[i].accept(item)) {
					return false;
				}
			}
			return true;
		}
	}

	boolean accept(Item item);
}