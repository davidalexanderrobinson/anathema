package net.sf.anathema.hero.sheet.pdf.content;

import net.sf.anathema.character.main.type.CharacterType;
import net.sf.anathema.hero.model.Hero;

public class BasicContent implements SubContent {

  private Hero hero;

  public BasicContent(Hero hero) {
    this.hero = hero;
  }

  public boolean isEssenceUser() {
    return getCharacterType().isEssenceUser();
  }

  private CharacterType getCharacterType() {
    return hero.getTemplate().getTemplateType().getCharacterType();
  }

  public boolean isOfType(CharacterType type) {
    return getCharacterType().equals(type);
  }

  @Override
  public boolean hasContent() {
    return true;
  }
}
