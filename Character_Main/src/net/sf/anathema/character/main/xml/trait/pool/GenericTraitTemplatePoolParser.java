package net.sf.anathema.character.main.xml.trait.pool;

import net.sf.anathema.character.main.xml.core.AbstractXmlTemplateParser;
import net.sf.anathema.character.main.xml.registry.IXmlTemplateRegistry;
import net.sf.anathema.character.main.xml.trait.GenericRestrictedTraitTemplate;
import net.sf.anathema.character.main.xml.trait.GenericTraitTemplate;
import net.sf.anathema.character.main.xml.trait.GenericTraitTemplateParser;
import net.sf.anathema.character.main.xml.trait.IClonableTraitTemplate;
import net.sf.anathema.character.main.xml.trait.IMinimumRestriction;
import net.sf.anathema.character.main.xml.trait.allocation.AllocationMinimumRestriction;
import net.sf.anathema.character.main.xml.trait.allocation.AllocationMinimumTraitTemplateParser;
import net.sf.anathema.character.main.xml.trait.alternate.AlternateMinimumTraitTemplateParser;
import net.sf.anathema.character.main.xml.trait.caste.CasteMinimumTraitTemplateParser;
import net.sf.anathema.character.main.traits.ITraitTemplate;
import net.sf.anathema.character.main.traits.TraitType;
import net.sf.anathema.character.main.traits.groups.ITraitTypeGroup;
import net.sf.anathema.lib.exception.PersistenceException;
import net.sf.anathema.lib.xml.ElementUtilities;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class GenericTraitTemplatePoolParser extends AbstractXmlTemplateParser<GenericTraitTemplatePool> {

  private static final String TAG_DEFAULT_TRAIT = "defaultTrait";
  private static final String TAG_SPECIAL_TRAIT = "specialTrait";
  private static final String TAG_ALLOCATION_MINIMUM_TRAITS = "allocationMinimumTraits";
  private static final String TAG_ALTERNATE_MINMUM_TRAITS = "alternateMinimumTraits";
  private static final String TAG_CASTE_MINIMUM_TRAITS = "casteMinimumTraits";
  private final ITraitTypeGroup traitTypeGroup;
  private final List<AllocationMinimumRestriction> family = new ArrayList<>();

  public GenericTraitTemplatePoolParser(IXmlTemplateRegistry<GenericTraitTemplatePool> templateRegistry, ITraitTypeGroup traitTypeGroup) {
    super(templateRegistry);
    this.traitTypeGroup = traitTypeGroup;
  }

  @Override
  protected GenericTraitTemplatePool createNewBasicTemplate() {
    return new GenericTraitTemplatePool();
  }

  @Override
  public GenericTraitTemplatePool parseTemplate(Element element) throws PersistenceException {
    GenericTraitTemplatePool pool = getBasicTemplate(element);
    parseDefaultTraitTemplate(pool, element);
    parseSpecialTraitTemplates(pool, element);
    parseAllocationMinimumTraitTemplates(pool, element);
    parseAlternateMinimumTraitTemplates(pool, element);
    parseCasteMinimumTraitTemplates(pool, element);
    return pool;
  }

  private void parseDefaultTraitTemplate(GenericTraitTemplatePool pool, Element element) throws PersistenceException {
    Element defaultTraitElement = element.element(TAG_DEFAULT_TRAIT);
    if (defaultTraitElement == null) {
      return;
    }
    IClonableTraitTemplate defaultTraitTemplate = GenericTraitTemplateParser.parseTraitTemplate(defaultTraitElement);
    pool.setDefaultTemplate((GenericTraitTemplate) defaultTraitTemplate);
  }

  private void parseSpecialTraitTemplates(GenericTraitTemplatePool pool, Element element) throws PersistenceException {
    for (Element specialTraitElement : ElementUtilities.elements(element, TAG_SPECIAL_TRAIT)) {
      TraitType traitType;
      String traitTypeId = ElementUtilities.getRequiredAttrib(specialTraitElement, "id");
      traitType = traitTypeGroup.getById(traitTypeId);
      IClonableTraitTemplate specialTraitTemplate = GenericTraitTemplateParser.parseTraitTemplate(specialTraitElement, traitType);
      pool.setSpecialTemplate(traitType, specialTraitTemplate);
    }
  }

  private void parseAlternateMinimumTraitTemplates(GenericTraitTemplatePool pool, Element element) throws PersistenceException {
    AlternateMinimumTraitTemplateParser parser = new AlternateMinimumTraitTemplateParser(traitTypeGroup);
    for (Element specialTraitElement : ElementUtilities.elements(element, TAG_ALTERNATE_MINMUM_TRAITS)) {
      for (GenericRestrictedTraitTemplate template : parser.parseAlternateMinimumTraits(specialTraitElement)) {
        pool.setSpecialTemplate(template.getTraitType(), template);
      }
    }
  }

  private void parseAllocationMinimumTraitTemplates(GenericTraitTemplatePool pool, Element element) throws PersistenceException {
    AllocationMinimumTraitTemplateParser parser = new AllocationMinimumTraitTemplateParser(traitTypeGroup);
    for (Element specialTraitElement : ElementUtilities.elements(element, TAG_ALLOCATION_MINIMUM_TRAITS)) {
      for (GenericRestrictedTraitTemplate template : parser.parseAllocationMinimumTraits(specialTraitElement, family)) {
        pool.setSpecialTemplate(template.getTraitType(), template);
      }
    }
  }

  private void parseCasteMinimumTraitTemplates(GenericTraitTemplatePool pool, Element element) {
    CasteMinimumTraitTemplateParser parser = new CasteMinimumTraitTemplateParser(traitTypeGroup);
    for (Element specialTraitElement : ElementUtilities.elements(element, TAG_CASTE_MINIMUM_TRAITS)) {
      for (GenericRestrictedTraitTemplate casteTemplate : parser.parseCasteMinimumTraits(specialTraitElement, family)) {
        ITraitTemplate existingTemplate = pool.getTemplate(casteTemplate.getTraitType());

        if (existingTemplate instanceof GenericRestrictedTraitTemplate) {
          GenericRestrictedTraitTemplate restrictedTemplate = (GenericRestrictedTraitTemplate) existingTemplate;
          for (IMinimumRestriction restriction : casteTemplate.getRestrictions()) {
            restrictedTemplate.addRestriction(restriction);
          }

	    		/*GenericTraitTemplate baseTemplate = (GenericTraitTemplate) restrictedTemplate.getTemplate();
                baseTemplate.setMinimumValue(casteTemplate.getStartValue() )*/
        } else {
          casteTemplate.setTemplate((IClonableTraitTemplate) existingTemplate);
          pool.setSpecialTemplate(casteTemplate.getTraitType(), casteTemplate);
        }

      }
    }
  }
}