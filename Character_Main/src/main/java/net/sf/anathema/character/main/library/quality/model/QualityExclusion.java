package net.sf.anathema.character.main.library.quality.model;

import net.sf.anathema.character.main.library.quality.presenter.IQuality;
import net.sf.anathema.character.main.library.quality.presenter.IQualityPredicate;
import net.sf.anathema.character.main.library.quality.presenter.IQualitySelection;

public class QualityExclusion implements IQualityPredicate {

  private final IQuality prerequisite;

  public QualityExclusion(IQuality prerequisite) {
    this.prerequisite = prerequisite;
  }

  @Override
  public boolean isFulfilled(IQualitySelection<? extends IQuality>[] selectedQualities) {
    for (IQualitySelection<? extends IQuality> selection : selectedQualities) {
      if (selection.getQuality() == prerequisite) {
        return false;
      }
    }
    return true;
  }
}