package rst.pdfbox.layout.text.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * Pins down the regex that drives the underline ({@code __...__}) annotation
 * markup. Previously verified by hand via a {@code main()} on
 * {@link AnnotationCharacters}.
 */
public class AnnotationCharactersTest
{

  private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*(__(\\{(-?\\d+(\\.\\d*)?)?\\:(-?\\d+(\\.\\d*)?)?\\})?)");

  @Test
  public void testUnderlineMarkerMatchesBareUnderscores ()
  {
    final Matcher matcher = PATTERN.matcher ("__");
    assertTrue ("expected pattern to match '__'", matcher.find ());
    matcher.reset ();
    assertTrue ("expected full pattern to match '__'", matcher.matches ());
    assertEquals (0, matcher.start ());
    assertEquals (2, matcher.end ());
  }
}
