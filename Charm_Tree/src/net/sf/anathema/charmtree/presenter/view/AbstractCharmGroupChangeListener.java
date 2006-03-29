package net.sf.anathema.charmtree.presenter.view;

import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Set;

import net.disy.commons.core.util.Ensure;
import net.sf.anathema.character.generic.magic.ICharm;
import net.sf.anathema.character.generic.magic.charms.ICharmGroup;
import net.sf.anathema.character.generic.rules.IExaltedEdition;
import net.sf.anathema.character.generic.template.ITemplateRegistry;
import net.sf.anathema.character.generic.template.presentation.ICharmPresentationProperties;
import net.sf.anathema.charmtree.provider.CharmTreeProvider;
import net.sf.anathema.lib.collection.ListOrderedSet;
import net.sf.anathema.lib.util.IIdentificate;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

public abstract class AbstractCharmGroupChangeListener implements ICharmGroupChangeListener {

  private final CharmTreeProvider provider = new CharmTreeProvider();
  private final ICharmTreeViewProperties viewProperties;
  private final ITemplateRegistry templateRegistry;
  private final ICharmTreeView charmTreeView;
  private ICharmGroup currentGroup;

  public AbstractCharmGroupChangeListener(
      ICharmTreeView charmTreeView,
      ICharmTreeViewProperties viewProperties,
      ITemplateRegistry templateRegistry) {
    this.charmTreeView = charmTreeView;
    Ensure.ensureNotNull("View Properties must not be null", viewProperties); //$NON-NLS-1$
    this.viewProperties = viewProperties;
    this.templateRegistry = templateRegistry;
  }

  public final void valueChanged(Object cascade, IIdentificate type, IExaltedEdition edition) {
    charmTreeView.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      loadCharmTree((ICharmGroup) cascade, type, edition);
      return;
    }
    catch (DocumentException e) {
      e.printStackTrace();
    }
    finally {
      charmTreeView.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  private Set<ICharm> getDisplayCharms(ICharmGroup charmGroup) {
    ICharm[] allCharms = charmGroup.getAllCharms();
    Set<ICharm> charmsToDisplay = new ListOrderedSet<ICharm>();
    for (ICharm charm : allCharms) {
      charmsToDisplay.add(charm);
      for (ICharm prerequisite : charm.getRenderingPrerequisiteCharms()) {
        if (charmGroup.getId().equals(prerequisite.getGroupId())) {
          charmsToDisplay.add(prerequisite);
        }
      }
    }
    return charmsToDisplay;
  }

  private void loadCharmTree(ICharmGroup charmGroup, IIdentificate type, IExaltedEdition edition) throws DocumentException {
    currentGroup = charmGroup;
    modifyCharmVisuals(type, edition);
    if (charmGroup == null) {
      charmTreeView.loadCascade(null);
    }
    else {
      Set<ICharm> displayCharms = getDisplayCharms(charmGroup);
      ICharmPresentationProperties presentationProperties = templateRegistry.getDefaultTemplate(
          charmGroup.getCharacterType(),
          edition).getPresentationProperties().getCharmPresentationProperties();
      Dimension dimension = presentationProperties.getCharmDimension();
      viewProperties.setDimension(dimension);
      charmTreeView.setProperties(viewProperties);
      ICharm[] charms = displayCharms.toArray(new ICharm[displayCharms.size()]);
      Document dom4jDocument = provider.createCascadeDocument(charms, presentationProperties);
      DOMImplementation implementation = SVGDOMImplementation.getDOMImplementation();
      SVGDocument svgDocument = (SVGDocument) new DOMWriter().write(dom4jDocument, implementation);
      charmTreeView.loadCascade(svgDocument);
    }
  }

  protected final ICharmGroup getCurrentGroup() {
    return currentGroup;
  }

  protected abstract void modifyCharmVisuals(IIdentificate type, IExaltedEdition edition);

  public abstract void updateColors();
}