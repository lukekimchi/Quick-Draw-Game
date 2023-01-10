package nz.ac.auckland.se206.words;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionFinder {

  private Map<String, String> definitionMap;

  /**
   * constructor that reads all categories with their definitions. save definitions in a hashmap
   * dependent on their category.
   *
   * @throws IOException file not found
   * @throws CsvException csv problem
   * @throws URISyntaxException uri propblem
   */
  public DefinitionFinder() throws IOException, CsvException, URISyntaxException {
    definitionMap = new HashMap<>();

    // for every category
    for (String[] line : getLines()) {
      String category = line[0];
      String definition = line[1];
      // add category with definition to map
      definitionMap.put(category, definition);
    }
  }

  /**
   * method to return definition of a specified word from the definition map
   *
   * @param String word to search definition for
   * @return String definition of word
   */
  public String getDefinition(String word) {
    String definition = definitionMap.get(word);
    return definition;
  }

  /**
   * This method reads the lines of category_definitions.csv file
   *
   * @return list of strings with the first element being the category, and the second element is
   *     the difficulty
   * @throws IOException file not found
   * @throws CsvException csv problem
   * @throws URISyntaxException uri problem
   */
  protected List<String[]> getLines() throws IOException, CsvException, URISyntaxException {
    // find file
    File file = new File(CategorySelector.class.getResource("/category_definitions.csv").toURI());
    // read and return all lines in the file
    try (FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
        CSVReader reader = new CSVReader(fr)) {
      return reader.readAll();
    }
  }
}
