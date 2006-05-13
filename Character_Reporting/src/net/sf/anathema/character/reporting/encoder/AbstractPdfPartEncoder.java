package net.sf.anathema.character.reporting.encoder;

import java.awt.Point;
import java.io.IOException;

import net.disy.commons.core.geometry.SmartRectangle;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.generic.template.abilities.IGroupedTraitType;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.lib.resources.IResources;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;

public abstract class AbstractPdfPartEncoder extends AbstractPdfEncoder implements IPdfPartEncoder {

  private final BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
  private final IResources resources;
  private final PdfTraitEncoder traitEncoder;
  private final int essenceMax;

  public AbstractPdfPartEncoder(IResources resources, int essenceMax) throws DocumentException, IOException {
    this.essenceMax = essenceMax;
    this.resources = resources;
    this.traitEncoder = new PdfTraitEncoder(baseFont);
  }

  public final BaseFont getBaseFont() {
    return baseFont;
  }

  public final void encodeAttributes(
      PdfContentByte directContent,
      SmartRectangle contentBounds,
      IGroupedTraitType[] attributeGroups,
      IGenericTraitCollection traitCollection) {
    int groupSpacing = traitEncoder.getTraitHeight() / 2;
    int y = (int) contentBounds.getMaxY() - groupSpacing;
    String groupId = null;
    for (IGroupedTraitType groupedTraitType : attributeGroups) {
      if (!groupedTraitType.getGroupId().equals(groupId)) {
        groupId = groupedTraitType.getGroupId();
        y -= groupSpacing;
      }
      ITraitType traitType = groupedTraitType.getTraitType();
      String traitLabel = getResources().getString("AttributeType.Name." + traitType.getId());
      int value = traitCollection.getTrait(traitType).getCurrentValue();
      Point position = new Point(contentBounds.x, y);
      y -= traitEncoder.encodeWithText(directContent, traitLabel, position, contentBounds.width, value, essenceMax);
    }
  }

  public final IResources getResources() {
    return resources;
  }
}