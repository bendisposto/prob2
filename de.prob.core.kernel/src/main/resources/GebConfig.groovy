//import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver

System.setProperty("geb.env", "firefox")

reportsDir = "target/geb-reports"


waiting {
	timeout = 2
}

driver = {
	new HtmlUnitDriver(true)
}

//environments {
//	firefox {
//		driver = { new FirefoxDriver() }
//	}
//}

