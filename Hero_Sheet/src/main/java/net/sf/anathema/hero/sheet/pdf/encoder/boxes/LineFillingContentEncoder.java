package net.sf.anathema.hero.sheet.pdf.encoder.boxes;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import net.sf.anathema.hero.sheet.pdf.content.ListSubBoxContent;
import net.sf.anathema.hero.sheet.pdf.encoder.general.Bounds;
import net.sf.anathema.hero.sheet.pdf.encoder.general.Position;
import net.sf.anathema.hero.sheet.pdf.encoder.graphics.SheetGraphics;
import net.sf.anathema.hero.sheet.pdf.session.ReportSession;

import java.util.Iterator;

import static net.sf.anathema.hero.sheet.pdf.page.IVoidStateFormatConstants.REDUCED_LINE_HEIGHT;

public class LineFillingContentEncoder<C extends ListSubBoxContent> extends AbstractContentEncoder<C> {

  protected LineFillingContentEncoder(Class<C> contentClass) {
    super(contentClass);
  }

  @Override
  public void encode(SheetGraphics graphics, ReportSession reportSession, Bounds bounds) throws DocumentException {
    Phrase phrase = new Phrase();
    addToPhrase(createContent(reportSession), createDefaultFont(graphics), phrase);
    Bounds textBounds = new Bounds(bounds.x, bounds.y, bounds.width, bounds.height - 2);
    float yPosition = graphics.createSimpleColumn(textBounds).withLeading(REDUCED_LINE_HEIGHT).andTextPart(phrase).encode().getYLine();
    yPosition -= REDUCED_LINE_HEIGHT;
    while (yPosition > bounds.y) {
      graphics.createHorizontalLineByCoordinate(new Position(bounds.x, yPosition), bounds.getMaxX()).encode();
      yPosition -= REDUCED_LINE_HEIGHT;
    }
  }

  private Font createDefaultFont(SheetGraphics graphics) {
    return graphics.createTableFont();
  }

  private void addToPhrase(C content, Font font, Phrase phrase) {
    for (Iterator<String> entry = content.getPrintEntries().iterator(); entry.hasNext(); ) {
      String text = entry.next();
      text += entry.hasNext() ? ", " : "";
      phrase.add(new Chunk(text, font));
    }
  }
}