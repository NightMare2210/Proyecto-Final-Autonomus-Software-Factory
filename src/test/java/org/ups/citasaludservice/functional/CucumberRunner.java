package org.ups.citasaludservice.functional;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME,
    value = "org.ups.citasaludservice.functional")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME,
    value = "src/test/resources/features")
public class CucumberRunner {}
