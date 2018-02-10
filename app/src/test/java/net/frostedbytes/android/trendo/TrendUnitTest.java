package net.frostedbytes.android.trendo;

import java.util.Map;
import net.frostedbytes.android.trendo.models.Trend;
import org.junit.Test;

public class TrendUnitTest {

  @Test
  public void  constructorTestDefaults() {

    Trend testTrend = new Trend();
  }

  @Test
  public void toMapTest() {

    Trend testTrend = new Trend();
    Map<String, Object> mappedTrend = testTrend.toMap();

  }
}
