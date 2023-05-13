package org.example.dummytest;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = { "src/test/resources/Features"},
        glue = { "org.example.dummytest.ui.steps" },
        plugin = { "pretty", "html:target/cucumber-report/report.html"}
)
public class RunTest {
}