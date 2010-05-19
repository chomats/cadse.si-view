package fede.workspace.tool.view.node;

import fr.imag.adele.cadse.core.LinkType;
import fede.workspace.tool.view.node.FilteredItemNode.Category;

/**
 * Node representing a link type category.
 * 
 * 
 * @author Thomas
 * 
 */
public class LinkTypeCategoryNode extends CategoryNode {

	private LinkType	_linkType;

	public LinkTypeCategoryNode(CadseViewModelController ctl, AbstractCadseViewNode node2, Category category,
			LinkType linkType) {
		super(ctl, node2, category);
		_linkType = linkType;
	}

	@Override
	public LinkType getLinkType() {
		return _linkType;
	}

	@Override
	public Object getElementModel() {
		return _linkType;
	}
}