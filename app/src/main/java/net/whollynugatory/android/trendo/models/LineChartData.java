package net.whollynugatory.android.trendo.models;

import java.io.Serializable;
import java.util.HashMap;

public class LineChartData implements Serializable {

  public HashMap<String, Long> CenterDataSet;

  public String CenterLabel;

  public HashMap<String, Long> LeftDataSet;

  public String LeftLabel;

  public HashMap<String, Long> RightDataSet;

  public String RightLabel;

  /**
   * Year this represents.
   */
  public int Year;

  /**
   * Constructs a new LineChartData object with default values.
   */
  public LineChartData() {

    this.CenterDataSet = new HashMap<>();
    this.CenterLabel = "";
    this.LeftDataSet = new HashMap<>();
    this.LeftLabel = "";
    this.RightDataSet = new HashMap<>();
    this.RightLabel = "";
    this.Year = 0;
  }
}
