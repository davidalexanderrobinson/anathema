package net.sf.anathema.character.main.magic.parser.charms;

import net.sf.anathema.lib.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class IdentificateRegistry<E extends Identifier> implements
        IIdentificateRegistry<E> {

  private final Set<E> elements = new LinkedHashSet<>();

  @Override
  public void add(E... newElements) {
    Collections.addAll(elements, newElements);
  }

  @Override
  public final Collection<E> getAll() {
    return elements;
  }

  @Override
  public boolean idRegistered(String id) {
    for (E identificate : getAll()) {
      if (identificate.getId().equals(id)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public E getById(String id) {
    for (E identificate : getAll()) {
      if (identificate.getId().equals(id)) {
        return identificate;
      }
    }
    return null;
  }
}