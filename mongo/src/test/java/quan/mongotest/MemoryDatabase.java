package quan.mongotest;

import java.util.Arrays;
import java.util.List;

public class MemoryDatabase {

  public List<String> load(String info) {
    System.err.println("MemoryDatabase.load()");
    return Arrays.asList(info + ": foo", info + ": bar");
  }

}