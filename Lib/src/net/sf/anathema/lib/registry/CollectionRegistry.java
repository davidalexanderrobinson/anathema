package net.sf.anathema.lib.registry;

import java.util.Collection;
import java.util.Set;

import net.sf.anathema.lib.collection.ListOrderedSet;

public class CollectionRegistry<E> implements ICollectionRegistry<E> {

  private Set<E> elements = new ListOrderedSet<E>();

  public void add(E element) {
    elements.add(element);
  }

  public void add(E[] newElements) {
    for (E element : newElements) {
      add(element);
    }
  }

  public final Collection<E> getAll() {
    return elements;
  }

  public E[] getAllElements(E[] array) {
    return elements.toArray(array);
  }
}