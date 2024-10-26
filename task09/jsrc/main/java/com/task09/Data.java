package com.task09;

import java.util.List;

@lombok.Data
public class Data {

  private double latitude;
  private double longitude;
  private double generationtime_ms;
  private int utc_offset_seconds;
  private String timezone;
  private String timezone_abbreviation;
  private double elevation;
  private Units current_units;
  private Current current;
  private Units hourly_units;
  private Hourly hourly;

  // Getters and Setters
  // Constructor
}

@lombok.Data
class Units {

  private String time;
  private String interval;
  private String temperature_2m;
  private String wind_speed_10m;

  // Getters and Setters
  // Constructor
}

@lombok.Data
class Current {

  private String time;
  private int interval;
  private double temperature_2m;
  private double wind_speed_10m;

  // Getters and Setters
  // Constructor
}

@lombok.Data
class Hourly {

  private List<String> time;
  private List<Double> temperature_2m;
  private List<Integer> relative_humidity_2m;
  private List<Double> wind_speed_10m;

  // Getters and Setters
  // Constructor
}