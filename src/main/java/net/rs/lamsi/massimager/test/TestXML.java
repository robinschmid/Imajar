package net.rs.lamsi.massimager.test;

import java.io.File;
import java.io.IOException;

import net.rs.lamsi.general.settings.image.visualisation.SettingsPaintScale;

public class TestXML {

	public static void main(String[] args) {
		File f = new File("C:\\DATA\\test.xml");
		SettingsPaintScale sett = new SettingsPaintScale();
		sett.setMax(500);
		sett.setMin(100);
		sett.setLevels(5);
		try {
			sett.saveToXML(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done");
		

		SettingsPaintScale sett3 = new SettingsPaintScale();
		SettingsPaintScale sett2 = new SettingsPaintScale();
		try {
			sett2.loadFromXML(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done");
	}

	
	/*
	public static void loadConfiguration(File file) throws IOException {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document configuration = dBuilder.parse(file);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();

			logger.finest("Loading desktop configuration");

			XPathExpression expr = xpath.compile("//configuration/preferences");
			NodeList nodes = (NodeList) expr.evaluate(configuration,
					XPathConstants.NODESET);
			if (nodes.getLength() == 1) {
				Element preferencesElement = (Element) nodes.item(0);
				preferences.loadValuesFromXML(preferencesElement);
			}

			logger.finest("Loading modules configuration");

			for (MZmineModule module : WindowMZMine.getAllModules()) {

				String className = module.getClass().getName();
				expr = xpath.compile("//configuration/modules/module[@class='"
						+ className + "']/parameters");
				nodes = (NodeList) expr.evaluate(configuration,
						XPathConstants.NODESET);
				if (nodes.getLength() != 1)
					continue;

				Element moduleElement = (Element) nodes.item(0);

				ParameterSet moduleParameters = getModuleParameters(module
						.getClass());
				moduleParameters.loadValuesFromXML(moduleElement);
			}

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	
	public static void saveConfiguration(File file) throws IOException {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document configuration = dBuilder.newDocument();
			Element configRoot = configuration.createElement("configuration");
			configuration.appendChild(configRoot);

			Element prefElement = configuration.createElement("preferences");
			configRoot.appendChild(prefElement);
			preferences.saveValuesToXML(prefElement);

			Element modulesElement = configuration.createElement("modules");
			configRoot.appendChild(modulesElement);

			// traverse modules
			for (MZmineModule module : WindowMZMine.getAllModules()) {

				String className = module.getClass().getName();

				Element moduleElement = configuration.createElement("module");
				moduleElement.setAttribute("class", className);
				modulesElement.appendChild(moduleElement);

				Element paramElement = configuration
						.createElement("parameters");
				moduleElement.appendChild(paramElement);

				ParameterSet moduleParameters = getModuleParameters(module
						.getClass());
				moduleParameters.saveValuesToXML(paramElement);

			}

			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer transformer = transfac.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			StreamResult result = new StreamResult(new FileOutputStream(file));
			DOMSource source = new DOMSource(configuration);
			transformer.transform(source, result);

		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	*/
}
