package net.sf.anathema.hero.charms.template;

import net.sf.anathema.hero.concept.CasteType;

public interface CharmRules {

  boolean canLearnCharms();

  boolean isAllowedAlienCharms(CasteType caste);

  MartialArtsRules getMartialArtsRules();
}