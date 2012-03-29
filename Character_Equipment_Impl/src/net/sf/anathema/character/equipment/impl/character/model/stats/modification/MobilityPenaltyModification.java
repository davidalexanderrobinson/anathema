package net.sf.anathema.character.equipment.impl.character.model.stats.modification;

public class MobilityPenaltyModification implements StatsModification {

  private final StatModifier modifier;

  public MobilityPenaltyModification(StatModifier modifier) {
    this.modifier = modifier;
  }

  @Override
  public int getModifiedValue(int original) {
    int bonus = modifier.getModifier();
    return Math.max(0, original - bonus);
  }
}