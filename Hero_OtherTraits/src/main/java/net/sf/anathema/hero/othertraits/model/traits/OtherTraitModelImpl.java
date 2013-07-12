package net.sf.anathema.hero.othertraits.model.traits;

import net.sf.anathema.character.main.template.HeroTemplate;
import net.sf.anathema.character.main.template.ITraitLimitation;
import net.sf.anathema.character.main.template.ITraitTemplateCollection;
import net.sf.anathema.character.main.traits.types.OtherTraitType;
import net.sf.anathema.character.main.traits.types.VirtueType;
import net.sf.anathema.character.main.library.trait.Trait;
import net.sf.anathema.hero.othertraits.OtherTraitModel;
import net.sf.anathema.hero.traits.DefaultTraitMap;
import net.sf.anathema.hero.traits.TraitModel;
import net.sf.anathema.hero.traits.TraitModelFetcher;
import net.sf.anathema.character.main.traits.EssenceLimitationListener;
import net.sf.anathema.character.main.traits.EssenceTemplateFactory;
import net.sf.anathema.character.main.traits.VirtueTemplateFactory;
import net.sf.anathema.character.main.traits.WillpowerTemplateFactory;
import net.sf.anathema.character.main.traits.creation.DefaultTraitFactory;
import net.sf.anathema.character.main.traits.creation.TypedTraitTemplateFactory;
import net.sf.anathema.hero.change.ChangeAnnouncer;
import net.sf.anathema.hero.model.Hero;
import net.sf.anathema.hero.model.HeroModel;
import net.sf.anathema.hero.model.InitializationContext;
import net.sf.anathema.hero.othertraits.template.OtherTraitsTemplate;
import net.sf.anathema.hero.traits.model.event.TraitValueChangedListener;
import net.sf.anathema.lib.util.Identifier;

public class OtherTraitModelImpl extends DefaultTraitMap implements OtherTraitModel, HeroModel {

  private HeroTemplate heroTemplate;
  private OtherTraitsTemplate template;

  public OtherTraitModelImpl(OtherTraitsTemplate template) {
    this.template = template;
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public void initialize(InitializationContext context, Hero hero) {
    this.heroTemplate = hero.getTemplate();
    addEssence(hero);
    addVirtues(hero);
    addWillpower(hero);
    connectWillpowerAndVirtues();
    TraitModel traitModel = TraitModelFetcher.fetch(hero);
    getTrait(OtherTraitType.Essence).addCurrentValueListener(new EssenceLimitationListener(traitModel, hero));
    traitModel.addTraits(getAll());
  }

  @Override
  public void initializeListening(ChangeAnnouncer announcer) {
    for (Trait trait : getAll()) {
      trait.addCurrentValueListener(new TraitValueChangedListener(announcer, trait));
    }
  }

  private void connectWillpowerAndVirtues() {
    Trait willpower = getTrait(OtherTraitType.Willpower);
    Trait[] virtues = getTraits(VirtueType.values());
    if (template.willpower.isVirtueBased) {
      new WillpowerListening().initListening(willpower, virtues);
    } else {
      willpower.setModifiedCreationRange(5, 10);
    }
  }

  private void addEssence(Hero hero) {
    TypedTraitTemplateFactory templateFactory = new EssenceTemplateFactory(getTemplateCollection().getTraitTemplateFactory());
    DefaultTraitFactory traitFactory = new DefaultTraitFactory(hero, templateFactory);
    addTraits(traitFactory.createTrait(OtherTraitType.Essence));
  }

  private void addVirtues(Hero hero) {
    TypedTraitTemplateFactory templateFactory = new VirtueTemplateFactory(getTemplateCollection().getTraitTemplateFactory());
    DefaultTraitFactory traitFactory = new DefaultTraitFactory(hero, templateFactory);
    addTraits(traitFactory.createTraits(VirtueType.values()));
  }

  private void addWillpower(Hero hero) {
    TypedTraitTemplateFactory templateFactory = new WillpowerTemplateFactory(getTemplateCollection().getTraitTemplateFactory());
    DefaultTraitFactory traitFactory = new DefaultTraitFactory(hero, templateFactory);
    addTraits(traitFactory.createTrait(OtherTraitType.Willpower));
  }

  private ITraitTemplateCollection getTemplateCollection() {
    return heroTemplate.getTraitTemplateCollection();
  }

  @Override
  public int getEssenceCap(boolean modified) {
    Trait essence = getTrait(OtherTraitType.Essence);
    return modified ? essence.getModifiedMaximalValue() : essence.getUnmodifiedMaximalValue();
  }

  @Override
  public ITraitLimitation getEssenceLimitation() {
    return heroTemplate.getTraitTemplateCollection().getTraitTemplate(OtherTraitType.Essence).getLimitation();
  }
}